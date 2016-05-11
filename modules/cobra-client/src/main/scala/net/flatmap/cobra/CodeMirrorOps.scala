package net.flatmap.cobra

import net.flatmap.js.codemirror.{Doc, EditorChange}

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
}
