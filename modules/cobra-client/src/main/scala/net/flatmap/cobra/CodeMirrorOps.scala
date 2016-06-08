package net.flatmap.cobra

import net.flatmap.collaboration._
import net.flatmap.js.codemirror._
import net.flatmap.js.reveal.Reveal
import org.scalajs.dom.raw.HTMLElement

import scala.collection.mutable
import scala.scalajs.js
import net.flatmap.js.util._

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

  def applyAnnotations(doc: Doc, annotations: Annotations, id: String, mode: Mode): () => Unit = {
    val (_,markers) = annotations.annotations.foldLeft((0,Seq.empty[Clearable])) {
      case ((offset,markers),Empty(n)) => (offset + n, markers)
      case ((offset,markers),Annotated(l,c)) =>
        val from = doc.posFromIndex(offset)
        val to = doc.posFromIndex(offset + l)
        val marker = c.substitute.fold {
          val options = TextMarkerOptions()
          options.shared = true
          c.tooltip.foreach(options.title = _)
          if (l == 0) {
            options.className = c.classes.map("cm-empty-" + _)mkString(" ")
            doc.markText(from,doc.posFromIndex(offset + l + 1), options)
          } else {
            if (c.classes.nonEmpty) options.className = c.classes.map("cm-" + _)mkString(" ")
            doc.markText(from,to, options)
          }
        } { substitution =>
          val options = TextMarkerOptions()
          options.shared = true
          options.replacedWith =
            net.flatmap.js.util.HTML(s"<span class='cm-m-isabelle ${c.classes.map("cm-" + _).mkString(" ")}'>$substitution</span>").head.asInstanceOf[HTMLElement]
          c.tooltip.foreach(options.replacedWith.title = _)
          doc.markText(from,to,options)
        }
        val buf = mutable.Buffer.empty[Clearable]
        c.messages.foreach { message =>
          def widget(doc: Doc) = Option(doc.getEditor()).foreach { editor => if (editor != js.undefined && doc.firstLine() <= to.line && doc.lastLine() >= to.line) {
            if (editor.getOption("states") == true || !message.isInstanceOf[StateMessage]) {
              val elem = message match {
                case ErrorMessage(txt) =>
                  val classes = c.classes.map("cm-" + _) + "error" + s"cm-m-${mode.name}"
                  net.flatmap.js.util.HTML(s"<div class='${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
                case WarningMessage(txt) =>
                  val classes = c.classes.map("cm-" + _) + "warning" + s"cm-m-${mode.name}"
                  net.flatmap.js.util.HTML(s"<div class='${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
                case InfoMessage(txt) =>
                  val classes = c.classes.map("cm-" + _) + "info" + s"cm-m-${mode.name}"
                  net.flatmap.js.util.HTML(s"<div class='${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
                case OutputMessage(txt) =>
                  val classes = c.classes.map("cm-" + _) + "output" + s"cm-m-${mode.name}"
                  net.flatmap.js.util.HTML(s"<div class='${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
                case StateMessage(txt) =>
                  val classes = c.classes.map("cm-" + _) + "output" + s"cm-m-${mode.name}"
                  if (editor.getOption("state-fragments") == "all")
                    net.flatmap.js.util.HTML(s"<div class='all fragment ${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
                  else if (editor.getOption("state-fragments") == "single")
                    net.flatmap.js.util.HTML(s"<div class='fragment ${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
                  else
                    net.flatmap.js.util.HTML(s"<div class='${classes.mkString(" ")}'>$txt</div>").head.asInstanceOf[HTMLElement]
              }
              elem.querySelectorAll("sendback").foreach { sb =>
                sb.on(Event.Mouse.Click) { e =>
                  val attrs = sb.attributes.toMap
                  CobraJS.send(Sendback(id, attrs, sb.textContent))
                }
              }
              buf += editor.addLineWidget(to.line, elem)
            }
          } }
          widget(doc)
          doc.iterLinkedDocs { (doc: Doc, sharedHistory: Boolean) => widget(doc) }
        }
        (offset + l, markers ++ buf :+ marker)
    }
    Reveal.sync()
    () => markers.foreach(_.clear())
  }
}
