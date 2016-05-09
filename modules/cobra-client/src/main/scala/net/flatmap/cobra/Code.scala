package net.flatmap.cobra

import net.flatmap.js.codemirror.CodeMirror
import org.scalajs.dom.Element
import net.flatmap.js.util._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by martin on 12.02.16.
  */
object Code {
  class Snippets private[Code] (root: NodeSeqQuery) extends Map[String,Snippet] {
    def get(id: String) = root.query(s"snippet#$id").elements.headOption.map { code =>
      new Snippet(code)
    }

    def iterator = root.query(s"code").elements.iterator.collect {
      case code if code.id != null && code.id.nonEmpty =>
        (code.id, new Snippet(code))
    }

    def +[S >: Snippet](kv: (String,S)): Map[String,Snippet] = ???
    def -(k: (String)): Map[String,Snippet] = ???
  }

  class Snippet private[Code] (code: Element) {
    def content: String = stripIndentation(code.textContent)
    def content_=(value: String): Unit = code.textContent = value
  }

  def loadDelayed(root: NodeSeqQuery): Future[Seq[String]] = Future.sequence {
    root.query(s"code[src]:not([src^='#'])").elements.map { code =>
      val src = code.getAttribute("src")
      code.html = Ajax.get(src).filter(_.status == 200).map(_.responseText)
    }
  }

  def stripIndentation(raw: String): String = {
    val strippedFront = raw.lines.dropWhile(!_.exists(!_.isWhitespace)).toSeq
    strippedFront.headOption.fold("") { firstLine =>
      val s = strippedFront.map(_.stripPrefix(firstLine.takeWhile(_.isWhitespace))).mkString("\n")
      s.take(s.lastIndexWhere(!_.isWhitespace) + 1)
    }
  }

  def getCodeSnippets(root: NodeSeqQuery) = new Snippets(root)

  def injectSnippets(root: NodeSeqQuery, snippets: Snippets) = {
    root.query("code[src^='#']").elements.foreach { code =>
      val src = code.getAttribute("src").tail
      code.innerHTML = snippets.get(src).map(_.content).getOrElse(s"Error: could not resolve '$src'")
    }
  }

  def initializeEditors(root: NodeSeqQuery) = {
    root.query("section code").elements.collect {
      case code if (!code.classes.contains("hidden")) =>
        val text = code.textContent
        code.innerHTML = ""
        val editor = CodeMirror(code)
        editor.getDoc().setValue(text)
        editor.setOption("mode","text/x-scala")
        editor.setOption("scrollbarStyle","null")
        editor
    }
  }
}
