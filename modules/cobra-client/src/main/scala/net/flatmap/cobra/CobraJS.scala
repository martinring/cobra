package net.flatmap.cobra

import net.flatmap.cobra.isabelle.{IsabelleMode, IsabelleModeConfig, IsabelleModeState}
import net.flatmap.js.codemirror.{CodeMirror, CodeMirrorConfiguration}
import net.flatmap.js.reveal._
import net.flatmap.js.util._
import org.scalajs.dom.raw.{HTMLElement, HTMLLinkElement}

import scala.collection.mutable
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js._
import scala.util.control.NonFatal
import org.scalajs.dom._

/**
  * Created by martin on 04.02.16.
  */
object CobraJS extends SocketApp[ServerMessage,ClientMessage]("/socket","cobra",HeartBeat,HeartBeat, autoReload = true)(
  deserialize = ServerMessage.read,
  serialize = ClientMessage.write
) {
  private val handlers = mutable.Map.empty[String,Set[ServerMessage with SnippetMessage => Unit]].withDefaultValue(Set.empty)

  def listenOn(id: String)(f: PartialFunction[ServerMessage with SnippetMessage, Unit]) = {
    handlers(id) += f
  }

  val cmTheme = RVar("default")
  var revealOps: Option[RevealOptions] = None

  val initialOptions = scala.concurrent.Promise[RevealOptions]

  def receive = {
    case msg: SnippetMessage => handlers(msg.id).foreach(_(msg))
    case RevealOptionsUpdate(options) =>
      val old = (new js.Object).asInstanceOf[js.Dynamic]
      options.foreach {
        case ("autoSlideMethod",value) => try { old.autoSlideMethod = eval(value.tail.init) } catch { case NonFatal(e) => console.warn(s"could not apply config value: autoSlideMethod = $value") }
        case (key,value) => try { old.updateDynamic(key)(eval(value)) } catch { case NonFatal(e) => console.warn(s"could not apply config value: $key = $value") }
      }
      revealOps = Some(old.asInstanceOf[RevealOptions])
      if (!initialOptions.isCompleted)
        initialOptions.success(old.asInstanceOf[RevealOptions])
      if (Reveal.isReady()) {
        Reveal.configure(old.asInstanceOf[RevealOptions])
        Reveal.sync()
      }
    case TitleUpdate(title) =>
      query("title").text = title
    case LanguageUpdate(lang) =>
      query("html").elements.head.asInstanceOf[HTMLElement].lang = lang
    case ThemeUpdate(code,slides) =>
      val slideTheme = document.getElementById("slideTheme").asInstanceOf[HTMLLinkElement]
      val codeTheme = document.getElementById("codeTheme").asInstanceOf[HTMLLinkElement]
      slideTheme.href = slides
      codeTheme.href = code
      cmTheme := code.split("/").last.dropRight(4)
    case FileUpdate("slides.html") =>
      send(ResetAllSnippets)
      initialize()
    case FileUpdate(other) => // TODO: differentiate here!
      send(ResetAllSnippets)
      initialize()
  }

  var heartBeatAcknowledged: Boolean = true

  lazy val printing = org.scalajs.dom.window.location.search.contains("print-pdf")

  var initializing = false

  def initialize(): Unit = for (document <- loadedDocument) {
    if (Reveal.isReady() && !Reveal.isPaused()) {
      Reveal.togglePause()
      initializing = true
      scheduleOnce(800) {
        initializing = false
        initialize()
      }
    } else if (!initializing) {
      handlers.clear()
      for {
        slides <- $"#slides" <<< "slides.html"
        delayedSnippets <- Code.loadDelayed(slides)
        options <- initialOptions.future
      } {
        val link = document.createElement("link").asInstanceOf[HTMLLinkElement]
        link.rel = "stylesheet"
        link.`type` = "text/css"
        link.href = if (printing) "/lib/reveal.js/css/print/pdf.css" else "/lib/reveal.js/css/print/paper.css"
        if (printing) {
          val link2 = document.createElement("link").asInstanceOf[HTMLLinkElement]
          link2.rel = "stylesheet"
          link2.`type` = "text/css"
          link2.href = "/lib/cobra-client/print.css"
          document.getElementsByTagName("head")(0).appendChild(link2)
        }
        document.getElementsByTagName("head")(0).appendChild(link)
        val documents = Code.initializeDocuments(slides)
        val editors = Code.initializeEditors(slides, documents)
        Code.attachDocuments(documents)
        val settings = revealOps getOrElse (RevealOptions())
        val mathOptions = RevealMathOptions()
        mathOptions.mathjax = "/lib/MathJax/2.6.1/MathJax.js"
        mathOptions.config = "TeX-AMS_HTML-full"
        settings.dependencies = js.Array(
          RevealDependency(src = "/lib/reveal.js/plugin/math/math.js", async = true),
          RevealDependency(src = "/lib/reveal.js/plugin/zoom-js/zoom.js", async = true),
          RevealDependency(src = "/lib/reveal.js/plugin/notes/notes.js", async = true)
        )
        settings.math = mathOptions
        if (!Reveal.isReady()) Reveal.initialize(settings)
        else {
          val pos = Reveal.getIndices()
          Reveal.initialize(settings)
          Reveal.next()
          Reveal.prev()
          Reveal.slide(pos.indexh, pos.indexv)
          scheduleOnce(500) {
            if (Reveal.isPaused) Reveal.togglePause()
          }
        }
        Reveal.on(RevealEvents.Ready) { x =>
          editors.foreach(_.refresh())
          Reveal.sync()
        }
        Reveal.on(RevealEvents.SlideChanged) { x =>
          editors.foreach(_.refresh())
          Reveal.sync()
        }
      }
    }
  }

  override def preStart(): Unit = {
    CodeMirror.defineMode[IsabelleModeState]("isabelle", IsabelleMode.apply _)
    CodeMirror.defineMIME("text/x-isabelle","isabelle")
    send(WatchFile("slides.html"))
    initialize()
  }


}