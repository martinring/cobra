package net.flatmap.cobra.scalac

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import net.flatmap.cobra._
import net.flatmap.collaboration._
import scala.concurrent.duration._

object ScalaService extends LanguageService  {
  def props(env: Map[String,String]) = Props(classOf[ScalaService],env)
}

class ScalaService(env: Map[String,String]) extends Actor with ScalaCompiler with ActorLogging {
  override def preStart() = println("hello from scala")

  implicit val dispatcher = context.system.dispatcher

  def receive = {
    case ResetSnippet(id, content, rev) =>
      context.become(initialized(id,content,rev,sender()))
  }

  val files = collection.mutable.Map.empty[String,(String,ClientInterface[Char])]

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
    reset()
    compile(id,content)

    case object Refresh
    case object DelayRefresh
    var refreshDelay: Option[Cancellable] = None

    {
      case DelayRefresh =>
        refreshDelay.foreach(_.cancel())
        refreshDelay = Some(context.system.scheduler.scheduleOnce(.75 second, self, Refresh))
      case Refresh =>
        files.get(id).foreach { case (b,c) =>
          reset()
          compile(id,b)
        }
        refreshDelay = None
      case AcknowledgeEdit(id2) if id == id2 => clientInterface.serverAck()
      case RemoteEdit(id2, op) if id == id2 =>
        clientInterface.remoteEdit(op)
        self ! DelayRefresh
      case RemoteAnnotations(id2, aid, as) if id == id2 => clientInterface.remoteAnnotations(aid, as)
      case CombinedRemoteEdit(id2, op, rev) if id == id2 => clientInterface.combinedRemoteEdit(op, rev)
      case RequestInfo(id2,from,to) if id == id2 => getInfo(id,from,to).foreach(server ! _)
      case other => log.warning("unhandled message: " + other)
    }
  }
}
