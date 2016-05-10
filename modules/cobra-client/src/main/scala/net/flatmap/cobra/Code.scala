package net.flatmap.cobra

import net.flatmap.js.codemirror.{CodeMirror, Doc, LinkedDocOptions}
import net.flatmap.js.reveal.Reveal
import org.scalajs.dom.{Element, raw, console}
import net.flatmap.js.util._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.control.NonFatal
import scala.util.matching.Regex

object Comments {
  def line(start: String) = new Regex(s"^\\s*$start\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$$")
  def block(start: String, end: String) = new Regex(s"^\\s*$start\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$end\\s*$$")
}

sealed abstract class Mode(val name: String, val mime: String, val regex: Regex, val fileendings: Set[String])
case object Scala extends Mode("scala","text/x-scala",Comments.line("\\/\\/\\/+"), Set("scala"))
case object Haskell extends Mode("haskell","text/x-haskell",Comments.line("---+"), Set("hs"))
case object Isabelle extends Mode("isabelle","text/x-isabelle",Comments.line("---+"), Set("thy"))

/**
  * Created by martin on 12.02.16.
  */
object Code {
  val modes = Set(Scala,Haskell,Isabelle)

  def loadDelayed(root: NodeSeqQuery): Future[Seq[String]] = Future.sequence {
    root.query(s"code[src]:not([src^='#'])").elements.map { code =>
      val src = code.getAttribute("src")
      code.removeAttribute("src")
      val ext = src.split("\\.").last
      modes.find(_.fileendings.contains(ext)).foreach(code.classes += _.name)
      println(s"resolving '$src'")
      code.html = Ajax.get(src).filter(_.status == 200).map(_.responseText).recover {
        case NonFatal(e) =>
          console.error(s"could not load source from '$src'")
          s"could not load source from '$src'"
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

  def subdocuments(root: Doc, Regex: Regex): Map[String,Doc] = {
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

  def mode(code: Element): Option[Mode] =
    modes.find(mode => code.classes.contains(mode.name))

  def initializeDocuments(root: NodeSeqQuery): Map[String,Doc] = {
    root.query("code").elements.flatMap { code =>
      mode(code).fold {
        val doc = CodeMirror.Doc(stripIndentation(code.textContent),"text/plain")
        code.attribute("id").map(_ -> doc).toIterable
      } { mode =>
        val doc = CodeMirror.Doc(stripIndentation(code.textContent),mode.mime)
        code.attribute("id").map(_ -> doc) ++ subdocuments(doc,mode.regex)
      }
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
        }.flatMap(documents.get)).getOrElse(CodeMirror.Doc(code.textContent, mode(code).map(_.mime).getOrElse("text/plain") : String))
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
