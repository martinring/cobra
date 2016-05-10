package net.flatmap.cobra

import net.flatmap.js.codemirror.{CodeMirror, Doc, LinkedDocOptions}
import net.flatmap.js.reveal.Reveal
import org.scalajs.dom.{Element, raw}
import net.flatmap.js.util._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.control.NonFatal
import scala.util.matching.Regex

/**
  * Created by martin on 12.02.16.
  */
object Code {
  def loadDelayed(root: NodeSeqQuery): Future[Seq[String]] = Future.sequence {
    root.query(s"code[src]:not([src^='#'])").elements.map { code =>
      val src = code.getAttribute("src")
      code.removeAttribute("src")
      code.html = Ajax.get(src).filter(_.status == 200).map(_.responseText).recover {
        case NonFatal(e) => s"could not load '$src'"
      }
    }
  }

  def stripIndentation(raw: String): String = {
    val strippedFront = raw.lines.dropWhile(!_.exists(!_.isWhitespace)).toSeq
    strippedFront.headOption.fold("") { firstLine =>
      val s = strippedFront.map(_.stripPrefix(firstLine.takeWhile(_.isWhitespace))).mkString("\n")
      s.take(s.lastIndexWhere(!_.isWhitespace) + 1)
    }
  }

  def snippetRegex(comment: String = "\\/\\/\\/") = new Regex(s"^\\s*$comment\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$$")

  def subdocuments(root: Doc, Regex: Regex = snippetRegex()): Map[String,Doc] = {
    var line = root.firstLine()
    var starts = Map.empty[String,Int]
    var ends = Map.empty[String,Int]
    while (line < root.lineCount()) {
      root.getLine(line) match {
        case Regex("begin",name) =>
          starts += name -> line
          root.replaceRange("",CodeMirror.Pos(line,0),CodeMirror.Pos(line + 1, 0))
        case Regex("end",name) =>
          ends += name -> line
          root.replaceRange("",CodeMirror.Pos(line,0),CodeMirror.Pos(line + 1, 0))
        case other =>
          line += 1
      }
    }
    starts.collect {
      case (name,sl) if ends.contains(name) =>
        val el = ends(name)
        println(s"subdocument '#$name' from line $sl to $el")
        name -> root.linkedDoc(new LinkedDocOptions(
          from = sl,
          to = el
        ))
    }
  }

  def initializeDocuments(root: NodeSeqQuery): Map[String,Doc] = {
    root.query("code").elements.flatMap { code =>
      val mode =
        if (code.classes.contains("scala")) "text/x-scala"
        else if (code.classes.contains("haskell")) "text/x-haskell"
        else if (code.classes.contains("isabelle")) "text/x-isabelle"
        else "text/plain"
      val doc = CodeMirror.Doc(stripIndentation(code.textContent),mode)
      code.attribute("id").map(_ -> doc) ++ subdocuments(doc)
    }.toMap
  }

  def initializeEditors(root: NodeSeqQuery, documents: Map[String,Doc]) = {
    root.query("section code").elements.collect {
      case code if (!code.classes.contains("hidden")) =>
        val doc = code.attribute("id").flatMap(documents.get).orElse(
        code.attribute("src").collect {
          case src if src.startsWith("#") =>
            println(src.tail)
            println(documents.get(src.tail).map(_.getValue()))
            src.tail
        }.flatMap(documents.get)).getOrElse(CodeMirror.Doc(code.textContent,"text/x-scala"))
        code.innerHTML = ""
        val editor = CodeMirror(code)
        editor.swapDoc(doc)
        editor.setOption("scrollbarStyle","null")
        val handler: js.Function2[CodeMirror,raw.Event,Unit] = (instance: CodeMirror, event: raw.Event) => {
          val changes = event.asInstanceOf[js.Array[js.Dynamic]]
          if (changes.exists { change =>
            change.from.line.asInstanceOf[Int] != change.to.line.asInstanceOf[Int] ||
              change.text.asInstanceOf[js.Array[String]].length > 1
          }) {
            Reveal.sync()
          }
        }
        editor.on("changes", handler)
        editor
    }
  }
}
