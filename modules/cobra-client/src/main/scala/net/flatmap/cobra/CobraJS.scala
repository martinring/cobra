package net.flatmap.cobra

import net.flatmap.cobra.isabelle.{IsabelleMode, IsabelleModeConfig, IsabelleModeState}
import net.flatmap.js.codemirror.{CodeMirror, CodeMirrorConfiguration}
import net.flatmap.js.reveal.{Reveal, RevealEvents, RevealOptions}
import net.flatmap.js.util._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js._

/**
  * Created by martin on 04.02.16.
  */
object CobraJS extends SocketApp[ServerMessage,ClientMessage]("/socket","cobra",HeartBeat,HeartBeat, autoReload = true)(
  deserialize = ServerMessage.read,
  serialize = ClientMessage.write
) {
  def receive = {
    case _ =>
  }

  var heartBeatAcknowledged: Boolean = true

  override def preStart(): Unit = {
    CodeMirror.defineMode[IsabelleModeState]("isabelle", IsabelleMode.apply _)
    CodeMirror.defineMIME("text/x-isabelle","isabelle")
    for {
      document <- loadedDocument
      slides <- $"#slides" <<< "slides.html"
      delayedSnippets <- Code.loadDelayed(slides)
    } {
      val documents = Code.initializeDocuments(slides)
      val editors = Code.initializeEditors(slides, documents)
      val settings = RevealOptions()
      settings.history = true
      settings.minScale = 1
      settings.maxScale = 1
      Reveal.initialize(settings)
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