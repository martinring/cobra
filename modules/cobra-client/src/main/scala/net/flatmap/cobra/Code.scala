package net.flatmap.cobra

import net.flatmap.js.codemirror.{CodeMirror, Doc, EditorChange, LinkedDocOptions}
import net.flatmap.js.reveal.Reveal
import org.scalajs.dom.{Element, console, raw}
import net.flatmap.js.util._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js._
import scala.util.control.NonFatal
import scala.util.matching.Regex

/**
  * Created by martin on 12.02.16.
  */
object Code {
  def loadDelayed(root: NodeSeqQuery): Future[Seq[String]] = Future.sequence {
    root.query(s"code[src]:not([src^='#'])").elements.map { code =>
      val src = code.getAttribute("src")
      val ext = src.split("\\.").last
      Mode.modes.find(_.fileendings.contains(ext)).foreach(code.classes += _.name)
      code.text = Ajax.get(src).filter(_.status == 200).map(_.responseText).recover {
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
        val doc = root.linkedDoc(new LinkedDocOptions(from = sl, to = el, sharedHist = true))
        doc.on("beforeChange",(x: CodeMirror,y: org.scalajs.dom.Event) => {
          CodeMirror.signal(root, "beforeChange", x, y)
          ()
        })
        name -> doc
    }
  }

  def mode(code: Element): Mode =
    Mode.modes.find(mode => code.classes.contains(mode.name)).getOrElse(Plain)

  def attachDocument(id: String, doc: Doc, mode: Mode) = {
    CobraJS.send(InitDoc(id,doc.getValue(), mode))
    var client = Client[Char](0)
    var silent = false
    CobraJS.listenOn(id) {
      case AcknowledgeEdit(_) =>
        val (send,newClient) = client.ack
        send.foreach(op => CobraJS.send(Edit(id,op,newClient.rev)))
        client = newClient
      case RemoteEdit(_,op) =>
        val (apply,newClient) = client.remoteEdit(op)
        silent = true
        var offset = 0
        apply.actions.foreach {
          case Retain(n) =>
            offset += n
          case Insert(s) =>
            doc.replaceRange(s.mkString,doc.posFromIndex(offset))
            offset += s.length
          case Delete(n) => doc.replaceRange("",doc.posFromIndex(offset),doc.posFromIndex(offset + n))
        }
        assert(offset == doc.getValue().length)
        silent = false
        client = newClient
    }
    val changeHandler: js.Function2[CodeMirror,org.scalajs.dom.Event,Unit] = (cm: CodeMirror, e: org.scalajs.dom.Event) => if (!silent) {
      val change = e.asInstanceOf[EditorChange]
      val from = doc.indexFromPos(change.from)
      val to = doc.indexFromPos(change.to)
      val end = doc.getValue().length
      val text = change.text.mkString("\n")
      val retainPrefix = if (from > 0) Some(Retain(from)) else None
      val retainSuffix = if (end > to) Some(Retain(end - to)) else None
      val insert = if (text.nonEmpty) Some(Insert(text)) else None
      val delete = if (to > from) Some(Delete(to - from)) else None
      val operation = Operation(retainPrefix.toList ++ insert ++ delete ++ retainSuffix)
      val (send,newClient) = client.localEdit(operation)
      val rev = client.rev
      client = newClient
      if (send) CobraJS.send(Edit(id,operation,rev))
    }
    doc.on("beforeChange",changeHandler)
  }

  def initializeDocuments(root: NodeSeqQuery): Map[String,Doc] = {
    var lastSuccessfulSuffix = 0

    def nextIds = {
      Iterator.iterate(lastSuccessfulSuffix) { x =>
        lastSuccessfulSuffix = x + 1
        lastSuccessfulSuffix
      }.map("code_snippet_" + _).drop(1)
    }

    def nextId = nextIds.find(id => $"#$id".elements.isEmpty).get

    root.query("code").elements.flatMap { code =>
      if (code.attribute("src").exists(_.startsWith("#"))) Map.empty[String,Doc] else {
        val id = code.attribute("id").getOrElse {
          code.id = nextId
          code.id
        }
        val md = mode(code)
        val doc = CodeMirror.Doc(stripIndentation(code.textContent), md.mime)
        val subdocs = subdocuments(doc,md.regex)
        attachDocument(id,doc,md)
        Map(id -> doc) ++ subdocs
      }
    }.toMap
  }

  def initializeEditors(root: NodeSeqQuery, documents: Map[String,Doc]) = {
    root.query("section code").elements.collect {
      case code if (!code.classes.contains("hidden")) =>
        val doc = code.attribute("id").flatMap(documents.get).orElse(
        code.attribute("src").collect {
          case src if src.startsWith("#") =>
            src.tail
        }.flatMap(documents.get)).getOrElse(CodeMirror.Doc(code.textContent, mode(code).mime : String))
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
