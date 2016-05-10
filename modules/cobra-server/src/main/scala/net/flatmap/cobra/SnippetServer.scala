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
      log.info("client disconnected")
      context.become(initialized(mode,server,listeners - listener))
    case InitDoc(id,content,mode) =>
      log.info("client connected")
      context.watch(sender)
      context.become(initialized(mode,server,listeners + sender))
      server.getHistory.foreach(op => sender ! RemoteEdit(id,op))
    case Edit(id,op,rev) =>
      log.info("applying edit")
      server.applyOperation(op,rev) match {
        case Success(op) =>
          sender ! AcknowledgeEdit(id)
          (listeners - sender).foreach(_ ! RemoteEdit(id,op))
        case Failure(e) =>
          log.error(e,"could not apply operation")
      }
  }
}
