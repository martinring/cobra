package net.flatmap.cobra

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}

import scala.util.{Failure, Success}

object SnippetServer {
  def props = Props[SnippetServer]
}

/**
  * Created by martin on 10.05.16.
  */
class SnippetServer extends Actor with ActorLogging {
  def receive = {
    case InitDoc(id,content,mode) =>
      context.become(initialized(mode, new Server(Document(content)), Set(sender)))
      context.watch(sender)
  }

  def initialized(mode: Mode, server: Server[Char], listeners: Set[ActorRef]): Receive = {
    case Terminated(listener) =>
      log.debug("client disconnected")
      context.become(initialized(mode,server,listeners - listener))
    case InitDoc(id,content,mode) =>
      log.debug("client connected")
      context.watch(sender)
      context.become(initialized(mode,server,listeners + sender))
      if (server.revision > 0) {
        sender ! CombinedRemoteEdit(id, server.getCombinedHistory, server.revision)
      }
    case Edit(id,op,rev) =>
      log.debug("applying edit")
      server.applyOperation(op,rev) match {
        case Success(op) =>
          sender ! AcknowledgeEdit(id)
          (listeners - sender).foreach(_ ! RemoteEdit(id,op))
        case Failure(e) =>
          log.error(e,"could not apply operation")
          sender ! ResetSnippet(id,server.text.mkString,server.revision)
      }
  }
}
