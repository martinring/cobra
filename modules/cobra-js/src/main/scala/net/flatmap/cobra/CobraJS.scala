package net.flatmap.cobra

import net.flatmap.js.codemirror.CodeMirror
import net.flatmap.js.reveal.{RevealOptions, Reveal}
import org.scalajs.dom.ext.Ajax

import scala.scalajs.js.JSApp
import net.flatmap.js.util._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.{Failure, Success}

/**
  * Created by martin on 04.02.16.
  */
object CobraJS extends JSApp {
  def main(): Unit = {
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
            editor.getDoc().setValue(stripped)
          }
          val settings = RevealOptions()
          settings.history = true
          Reveal.initialize(settings)
        case Failure(e) => //
      }
    }
  }
}