package net.flatmap.cobra.isabelle

import isabelle._
import net.flatmap.collaboration.{Insert, Retain, Delete}
import net.flatmap.collaboration.Operation

import scala.language.implicitConversions

trait IsabelleConversions { self: IsabelleSession =>
  def fileToNodeName(id: String): Document.Node.Name = {
    Thy_Header.thy_name(id + ".thy").map { theory =>
      Document.Node.Name(id + ".thy", "example", theory)
    }
  }.get

  def fileToNodeHeader(id: String, content: String): Document.Node.Header =
    Exn.capture {
      session.resources.check_thy_reader("", fileToNodeName(id), new scala.util.parsing.input.CharSequenceReader(content))
    } match {
      case Exn.Res(header: Document.Node.Header) => header
      case Exn.Exn(exn) => Document.Node.bad_header(Exn.message(exn))
    }

  val overlays = Document.Node.Overlays.empty

  def perspective: Document.Node.Perspective_Text =
    Document.Node.Perspective(true, Text.Perspective.full, overlays)

  def initEdits(id: String, content: String): List[(Document.Node.Name,Document.Node.Edit[Text.Edit,Text.Perspective])] = {
    val name: Document.Node.Name = fileToNodeName(id)
    val header = fileToNodeHeader(id,content)
    List(session.header_edit(name,header),
         name -> Document.Node.Clear(),
         name -> Document.Node.Edits(List(Text.Edit.insert(0,content))),
         name -> perspective)
  }

  def closeEdits(id: String, content: String): List[(Document.Node.Name,Document.Node.Edit[Text.Edit,Text.Perspective])] = {
    val name: Document.Node.Name = fileToNodeName(id)
    val header = fileToNodeHeader(id,content)
    List(session.header_edit(name, header),
         name -> Document.Node.Perspective(true, Text.Perspective.empty, overlays))
  }

  def removeEdits(id: String): List[(Document.Node.Name,Document.Node.Edit[Text.Edit,Text.Perspective])] = {
    val name: Document.Node.Name = fileToNodeName(id)
    List(name -> Document.Node.Clear(), // TODO: There must be a better way to do this
         name -> Document.Node.Perspective(true, Text.Perspective.empty, overlays))
  }

  def opToEdits(operation: Operation[Char]): List[Text.Edit] = operation.actions.foldLeft((0,Nil: List[Text.Edit])) {
    case ((i,edits),Retain(n)) => (i+n,edits)
    case ((i,edits),Delete(n)) => (i+n,Text.Edit.remove(i,Seq.fill(n)('-').mkString) :: edits)
    case ((i,edits),Insert(s)) => (i+s.length,Text.Edit.insert(i,s.mkString) :: edits)
  }._2.reverse // TODO: Do we need to reverse???

  def opToDocumentEdits(id: String, content: String, operation: Operation[Char]): List[Document.Edit_Text] = {
    val name: Document.Node.Name = fileToNodeName(id)
    val header = fileToNodeHeader(id, content)
    val edits = opToEdits(operation)
    List(session.header_edit(name, header),
      name -> Document.Node.Edits(edits),
      name -> perspective)
  }

  def commandAt(id: String, snapshot: Document.Snapshot, pos: Int): Option[Command] = {
    val node = snapshot.version.nodes(fileToNodeName(id))
    val commands = snapshot.node.command_iterator(pos)
    if (commands.hasNext) {
      val (cmd0,_) = commands.next
      node.commands.reverse.iterator(cmd0).find(cmd => !cmd.is_ignored)
    } else None
  }
}
