package net.flatmap.cobra

import java.nio.ByteBuffer

import boopickle.Default._

import scala.util.matching.Regex

sealed trait ClientMessage
sealed trait ServerMessage
sealed trait SnippetMessage { val id: String }
case object HeartBeat extends ClientMessage with ServerMessage

sealed abstract class Mode(val name: String, val mime: String, val regex: Regex, val fileendings: Set[String])
case object Scala extends Mode("scala","text/x-scala",Comments.line("\\/\\/\\/+"), Set("scala"))
case object Haskell extends Mode("haskell","text/x-haskell",Comments.line("---+"), Set("hs"))
case object Isabelle extends Mode("isabelle","text/x-isabelle",Comments.block("\\(\\*","\\*\\)"), Set("thy"))
case object Plain extends Mode("plain","text/plain",new Regex("$^"),Set.empty)

object Mode {
  def modes = Set(Scala,Haskell,Isabelle)
}

sealed trait Action[+T]

/** Skip the next `n` positions */
case class Retain(n: Int) extends Action[Nothing] { require(n>=0) }
/** Insert the given text at the current position */
case class Insert[T](s: Seq[T]) extends Action[T]
/** Delete the next `n` characters */
case class Delete(n: Int) extends Action[Nothing] { require(n>=0) }

case class InitDoc(id: String, content: String, mode: Mode) extends ClientMessage
case class Edit(id: String, operation: Operation[Char], revision: Long) extends ClientMessage

case class AcknowledgeEdit(id: String) extends ServerMessage with SnippetMessage
case class RemoteEdit(id: String, op: Operation[Char]) extends ServerMessage with SnippetMessage
case class CombinedRemoteEdit(id: String, op: Operation[Char], revisions: Long) extends ServerMessage with SnippetMessage
case class ResetSnippet(id: String, content: String, revision: Long) extends ServerMessage with SnippetMessage

object ClientMessage {
  def read(bytes: ByteBuffer): ClientMessage = Unpickle[ClientMessage].fromBytes(bytes)
  def write(message: ClientMessage): ByteBuffer = Pickle.intoBytes(message)
}

object ServerMessage {
  def read(bytes: ByteBuffer): ServerMessage = Unpickle[ServerMessage].fromBytes(bytes)
  def write(message: ServerMessage): ByteBuffer = Pickle.intoBytes(message)
}