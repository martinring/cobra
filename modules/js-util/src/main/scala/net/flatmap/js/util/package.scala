package net.flatmap.js

import java.util.regex.Pattern

import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.util.matching.Regex

/**
 * Created by martin on 08.09.15.
 */
package object util {
  import net.flatmap.js.util._

  def query(selector: String): Seq[Node] = document.querySelectorAll(selector)
  def query(elem: Node*): Seq[Node] = elem

  trait EventSource {
    def on[T <: raw.Event](event: Event[T])(f: T => Unit): Subscription

    def once[T <: raw.Event](event: Event[T])(f: T => Unit): Subscription = {
      lazy val subscription: Subscription = on(event) { e =>
        if (!subscription.isCancelled) {
          f(e)
          subscription.cancel()
        }
      }
      subscription
    }

    def onclick(f: => Unit): Subscription = {
      on(Event.Mouse.Click)(_ => f)
    }
  }

  implicit class BetterEventTarget(underlying: EventTarget) extends EventSource{
    def on[T <: raw.Event](event: Event[T])(f: T => Unit): Subscription = {
      val g: scalajs.js.Function1[T,_] = f
      underlying.addEventListener(event.name, g)
      Subscription(underlying.removeEventListener(event.name, g.asInstanceOf[scalajs.js.Function1[raw.Event,_]]))
    }
  }

  implicit class NodeListAsSeq(underlying: NodeList) extends Seq[Node] with NodeSeqQuery {
    def length = underlying.length
    def apply(idx: Int) = underlying.apply(idx)
    def iterator: Iterator[Node] = Iterator.tabulate(length)(apply)
  }

  implicit class QueryNodeSeq(underlying: Seq[Node]) extends Seq[Node] with NodeSeqQuery {
    def length = underlying.length
    def apply(idx: Int) = underlying.apply(idx)
    def iterator: Iterator[Node] = underlying.iterator
  }

  implicit class QueryHelper(val sc: StringContext) extends AnyVal {
    def $(args: Any*): Seq[Node] = query(sc.s(args :_*))
  }

  implicit class RegexStrings(val sc: StringContext) extends AnyVal {
    def r(args: Any*): Regex = {
      sc.checkLengths(args)
      sc.parts.zip(args ++ Seq("")).map { case (s,a) =>
        s + Pattern.quote(a.toString)
      }.mkString.r
    }
  }

  def HTML(s: String): Seq[Node] = {
    val div = org.scalajs.dom.document.createElement("div")
    div.innerHTML = s
    Seq(div.childNodes :_*)
  }

  implicit class OptionExt(val underlying: Option.type) extends AnyVal {
    def when[T](b: Boolean)(f: => Option[T]) = if (b) Some(f) else None
  }

  def whenReady(init: => Unit) = {
    if (document.readyState != "loading") { init }
    else document.once(Event.Ready) { e => init }
  }

  def schedule(ms: Int)(task: => Unit) = window.setTimeout(() => task, ms)
}
