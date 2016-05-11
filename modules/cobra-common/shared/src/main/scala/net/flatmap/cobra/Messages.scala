package net.flatmap.cobra

import java.nio.ByteBuffer

import boopickle.Default._
import net.flatmap.collaboration._

import scala.util.matching.Regex



sealed trait ClientMessage
sealed trait ServerMessage
sealed trait SnippetMessage { val id: String }

case object HeartBeat extends ClientMessage with ServerMessage

case class InitDoc(id: String, content: String, mode: Mode) extends ClientMessage with SnippetMessage
case class Edit(id: String, operation: Operation[Char], revision: Long) extends ClientMessage with SnippetMessage
case class Annotate(id: String, aid: String, annotations: Annotations, revision: Long) extends ClientMessage with SnippetMessage

case class AcknowledgeEdit(id: String) extends ServerMessage with SnippetMessage
case class RemoteEdit(id: String, op: Operation[Char]) extends ServerMessage with SnippetMessage
case class RemoteAnnotations(id: String, aid: String, annotations: Annotations) extends ServerMessage with SnippetMessage
case class CombinedRemoteEdit(id: String, op: Operation[Char], revisions: Long) extends ServerMessage with SnippetMessage
case class ResetSnippet(id: String, content: String, revision: Long) extends ServerMessage with SnippetMessage

trait Picklers {
  implicit val charActionPickler: Pickler[Action[Char]] =
    compositePickler[Action[Char]]
      .addConcreteType[Retain]
      .addConcreteType[Insert[Char]]
      .addConcreteType[Delete]

  implicit val annotationPickler: Pickler[Annotation] =
    compositePickler[Annotation]
      .addConcreteType[Empty]
      .addConcreteType[Annotated]

  implicit val modePickler: Pickler[Mode] =
    compositePickler[Mode]
      .addConcreteType[Scala.type ]
      .addConcreteType[Isabelle.type ]
      .addConcreteType[Plain.type]
      .addConcreteType[Haskell.type]

  implicit val annotationTypePickler: Pickler[AnnotationType.Value] =
    transformPickler[AnnotationType.Value,Int](_.id,AnnotationType.apply)

}

object ClientMessage extends Picklers {
  def read(bytes: ByteBuffer): ClientMessage = Unpickle[ClientMessage].fromBytes(bytes)
  def write(message: ClientMessage): ByteBuffer = Pickle.intoBytes(message)
}

object ServerMessage extends Picklers {
  def read(bytes: ByteBuffer): ServerMessage = Unpickle[ServerMessage].fromBytes(bytes)
  def write(message: ServerMessage): ByteBuffer = Pickle.intoBytes(message)
}