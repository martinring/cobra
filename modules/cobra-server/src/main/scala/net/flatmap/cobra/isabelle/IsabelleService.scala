package net.flatmap.cobra.isabelle

import isabelle.Session
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import net.flatmap.cobra.{CombinedRemoteEdit, RemoteEdit, _}
import net.flatmap.collaboration._

import scala.concurrent.Await
import scala.concurrent.duration._

object IsabelleService {
  def props(env: Map[String,String]) = Props(classOf[IsabelleService],env)
}

class IsabelleService(env: Map[String,String]) extends Actor with ActorLogging with IsabelleConversions with IsabelleSession {
  implicit val dispatcher = context.dispatcher

  override def preStart: Unit = Await.ready(start(env), 20 seconds)

  def receive = {
    case ResetSnippet(id, content, rev) =>
      context.become(initialized(id,content,rev,sender()))
  }

  def initialized(id: String, content: String, rev: Long, server: ActorRef): Receive = {
    lazy val editorInterface: EditorInterface[Char] = new EditorInterface[Char] {
      def applyOperation(operation: Operation[Char]) = {
        files.get(fileToNodeName(id)).foreach { case (a,b) =>
          val f = OpenedFile(id,clientInterface,Document(b.state).apply(operation).get.content.mkString)
          updateFile(fileToNodeName(id),f,opToDocumentEdits(id,f.state,operation))
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

    Await.ready(updateFile(fileToNodeName(id),OpenedFile(id,clientInterface,content),initEdits(id,content)), 5 seconds)

    //context.system.scheduler.schedule(1 second, 1 second)(refreshAnnotations())

    {

      case AcknowledgeEdit(id2) if id == id2 => clientInterface.serverAck()
      case RemoteEdit(id2, op) if id == id2 => clientInterface.remoteEdit(op)
      case RemoteAnnotations(id2, aid, as) if id == id2 => clientInterface.remoteAnnotations(aid, as)
      case CombinedRemoteEdit(id2, op, rev) if id == id2 => clientInterface.combinedRemoteEdit(op, rev)
    }
  }
}
