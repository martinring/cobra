package net.flatmap.cobra.ghc

import java.io.FileWriter

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import net.flatmap.cobra.{CombinedRemoteEdit, RemoteAnnotations, RemoteEdit, RequestInfo, _}
import net.flatmap.collaboration._
import better.files._
import scala.concurrent.duration._

object HaskellService extends LanguageService  {
  def props(env: Map[String,String]) = Props(classOf[HaskellService],env)
}

class HaskellService(env: Map[String,String]) extends Actor with ActorLogging {
  implicit val dispatcher = context.system.dispatcher

  def receive = {
    case ResetSnippet(id, content, rev) =>
      context.become(initialized(id,content,rev,sender()))
  }

  val tempDir = File.newTemporaryDirectory("cobra-hs")

  val files = collection.mutable.Map.empty[String,(String,ClientInterface[Char])]

  val Output = """^.*:([0-9]+):([0-9]+):(.*)$""".r

  def compile(id: String, content: String, clientInterface: ClientInterface[Char]) = {
    import sys.process._

    val temp = new java.io.File(s"/tmp/$id.hs")
    val name = temp.getPath()

    val write = new FileWriter(temp)
    write.write(content)
    write.close()

    val lines = {
      val outer = Seq("ghc-mod","check",name).lineStream
      if (outer.nonEmpty) outer else
        Seq("ghc-mod","lint", name).lineStream
    }

    val errs = lines.collect {
      case Output(line,ch,msg) => ((line.toInt,ch.toInt),if (msg.toLowerCase.contains("error")) "Error" else if (msg.toLowerCase.contains("warning")) "Warning" else "Info" ,msg.split('\0').mkString("<br>"))
    }

    log.info("lines: {}", errs)

    val as = HaskellMarkup.toAnnotations(errs.toList, content)

    clientInterface.localAnnotations("linting", as)
  }

  val Line = "([0-9]+) ([0-9]+) ([0-9]+) ([0-9]+) \"(.*)\"".r

  def getInfo(id: String, from: Int, to: Int, guid: String) = files.get(id).flatMap { case (state,clientInterface) =>
    import sys.process._

    val temp = tempDir / s"$id.hs"
    val name = temp.path.toAbsolutePath.toString

    temp.clear()
    temp < state

    val before = state.take(from)
    val line = before.count(_ == '\n') + 1
    val col  = before.length - before.lastIndexOf('\n')

    val it = state.drop(from).take(to - from)

    val proc1: Seq[String] = Seq("ghc-mod", "info", name, it)
    val output = proc1.lineStream.mkString("\n")
      .replaceAll("-- Defined at (.+?):(\\d+):(\\d+)", "")
      .replaceAll(" = ((\\w*\\.?)*# )+","")
      .split("\0").mkString("\n")

    if (!output.startsWith("Cannot show info"))
      Some(Information(id,from,to,output,guid))
    else {
      val proc: Seq[String] = Seq("ghc-mod", "type", name, line.toString, col.toString)
      val lines = proc.lineStream

      val lengths = state.split("\n").map(_.length + 1)
      val offsets = lines.collect {
        case Line(fl,fc,tl,tc,msg) =>
          val from = lengths.take(fl.toInt - 1).sum + fc.toInt - 1
          val to = lengths.take(tl.toInt - 1).sum + tc.toInt - 1
          (from,to,msg)
      }
      offsets.sortBy {
        case (f,t,_) => Math.abs(f - from) + Math.abs(t - to)
      }.headOption.map {
        case (from,to,msg) =>
          Information(id, from, to, msg, guid)
      }
    }
  }

  def initialized(id: String, content: String, rev: Long, server: ActorRef): Receive = {
    lazy val editorInterface: EditorInterface[Char] = new EditorInterface[Char] {
      def applyOperation(operation: Operation[Char]) = {
        files.get(id).foreach { case (b,c) =>
          val nc = Document(b).apply(operation).get.content.mkString
          files(id) = (nc,c)
        }
      }

      def sendOperation(operation: Operation[Char], revision: Long) = {
        server ! Edit(id,operation,revision)
      }

      def applyAnnotations(aid: String, annotations: Annotations) = {
        // ignore annotations
      }

      def sendAnnotations(aid: String, annotations: Annotations, revision: Long) = {
        server ! Annotate(id, aid, annotations, revision)
      }
    }

    lazy val clientInterface = ClientInterface[Char](editorInterface)

    files += id -> (content,clientInterface)
    compile(id,content,clientInterface)

    case object Refresh
    case object DelayRefresh
    var refreshDelay: Option[Cancellable] = None

    {
      case DelayRefresh =>
        refreshDelay.foreach(_.cancel())
        refreshDelay = Some(context.system.scheduler.scheduleOnce(.75 second, self, Refresh))
      case Refresh =>
        files.get(id).foreach { case (b,c) =>
          compile(id,b,clientInterface)
        }
        refreshDelay = None
      case AcknowledgeEdit(id2) if id == id2 => clientInterface.serverAck()
      case RemoteEdit(id2, op) if id == id2 =>
        clientInterface.remoteEdit(op)
        self ! DelayRefresh
      case RemoteAnnotations(id2, aid, as) if id == id2 => clientInterface.remoteAnnotations(aid, as)
      case CombinedRemoteEdit(id2, op, rev) if id == id2 => clientInterface.combinedRemoteEdit(op, rev)
      case RequestInfo(id2,from,to,guid) if id == id2 => getInfo(id,from,to,guid).foreach(server ! _)
      case other => log.warning("unhandled message: " + other)
    }
  }
}
