package net.flatmap.cobra

import net.flatmap.collaboration._
import net.flatmap.js.codemirror.{Doc, EditorChange, TextMarker, TextMarkerOptions}
import org.scalajs.dom.raw.HTMLElement

object CodeMirrorOps {
  def changeToOperation(doc: Doc, change: EditorChange): Operation[Char] = {
    val from = doc.indexFromPos(change.from)
    val to = doc.indexFromPos(change.to)
    val end = doc.getValue().length
    val text = change.text.mkString("\n")
    val retainPrefix = if (from > 0) Some(Retain(from)) else None
    val retainSuffix = if (end > to) Some(Retain(end - to)) else None
    val insert = if (text.nonEmpty) Some(Insert(text)) else None
    val delete = if (to > from) Some(Delete(to - from)) else None
    Operation(retainPrefix.toList ++ insert ++ delete ++ retainSuffix)
  }

  def applyOperation(doc: Doc, operation: Operation[Char]) = {
    val opLen = operation.actions.foldLeft(0) {
      case (offset,Retain(n)) =>
        offset + n
      case (offset,Insert(s)) =>
        doc.replaceRange(s.mkString,doc.posFromIndex(offset))
        offset + s.length
      case (offset,Delete(n)) =>
        doc.replaceRange("",doc.posFromIndex(offset),doc.posFromIndex(offset + n))
        offset
    }
    assert(opLen == doc.getValue().length)
  }

  def applyAnnotations(doc: Doc, annotations: Annotations): () => Unit = {
    val (_,markers) = annotations.annotations.foldLeft((0,Seq.empty[TextMarker])) {
      case ((offset,markers),Empty(n)) => (offset + n, markers)
      case ((offset,markers),Annotated(l,c)) =>
        val newMarkers = c.collect {
          case (AnnotationType.Class, cs) =>
            val options = TextMarkerOptions()
            options.shared = true
            options.className = cs
            doc.markText(doc.posFromIndex(offset),doc.posFromIndex(offset + l),options)
          case (AnnotationType.ErrorMessage, msg) =>
            val options = TextMarkerOptions()
            options.shared = true
            options.className = "error"
            options.title = msg
            doc.markText(doc.posFromIndex(offset),doc.posFromIndex(offset + l),options)
          case (AnnotationType.Substitution, msg) =>
            val options = TextMarkerOptions()
            options.shared = true
            options.replacedWith = net.flatmap.js.util.HTML(s"<span>$msg</span>").head.asInstanceOf[HTMLElement]
            doc.markText(doc.posFromIndex(offset),doc.posFromIndex(offset + l),options)
        }
        (offset + l, markers ++ newMarkers)
    }
    () => markers.foreach(_.clear())
  }
}
