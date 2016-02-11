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
      $"#slides".loadFrom("slides.html").andThen {
        case Success(()) =>
          $"code.scala".elements foreach { code =>
            val content =
              code.innerHTML.lines.dropWhile(!_.exists(!_.isWhitespace)).toSeq
            code.innerHTML = ""
            val stripped = content.headOption.fold ("") { firstLine =>
              val s = content.map(_.stripPrefix(firstLine.takeWhile(_.isWhitespace))).mkString("\n")
              s.take(s.lastIndexWhere(!_.isWhitespace) + 1)
            }
            val editor = CodeMirror(code)
            editor.setOption("mode","text/x-scala")
            editor.getDoc().setValue(stripped)
            editors += editor
          }
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
        case Failure(e) => //
      }
    }
  }
}