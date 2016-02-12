package net.flatmap.cobra

import net.flatmap.js.codemirror.CodeMirror
import net.flatmap.js.reveal.{RevealEvents, RevealOptions, Reveal}
import net.flatmap.js.util._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

/**
  * Created by martin on 04.02.16.
  */
object CobraJS extends SocketApp[ServerMessage,ClientMessage]("/socket","cobra")(
  deserialize = ServerMessage.read,
  serialize = ClientMessage.write
) {
  val editors = collection.mutable.Buffer.empty[CodeMirror]

  def receive = {
    case _ => //
  }

  override def preStart(): Unit = {
    whenReady {
      val slides = $"#slides"
      for {
        _ <- slides.loadFrom("slides.html")
        _ <- Code.loadDelayed(slides)
      } {
        val snippets = Code.getCodeSnippets(slides)
        Code.injectSnippets(slides,snippets)
        val settings = RevealOptions()
        settings.history = true
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
}