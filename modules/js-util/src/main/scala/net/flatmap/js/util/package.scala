package net.flatmap.js

import java.util.regex.Pattern

import org.scalajs.dom._

import scala.concurrent.{Promise, Future}
import scala.util.matching.Regex

/**
 * Created by martin on 08.09.15.
 */
package object util {
  import net.flatmap.js.util._

  def query(selector: String): NodeSeqQuery = QueryNodeSeq(document.querySelectorAll(selector))
  def query(elem: Node*): NodeSeqQuery = elem

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

  implicit class NodeListAsSeq(u: NodeList) extends Seq[Node] {
    def length = u.length
    def apply(idx: Int) = u.apply(idx)
    def iterator: Iterator[Node] = Iterator.tabulate(length)(apply)
  }

  implicit class QueryNode(u: Node) extends NodeSeqQuery {
    def underlying = Seq(u)
  }

  implicit class QueryNodeSeq(u: Seq[Node]) extends NodeSeqQuery {
    def underlying = u
  }

  implicit class QueryHelper(val sc: StringContext) extends AnyVal {
    def $(args: Any*): NodeSeqQuery = query(sc.s(args :_*))
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
    div.childNodes
  }

  implicit class OptionExt(val underlying: Option.type) extends AnyVal {
    def when[T](b: Boolean)(f: => Option[T]) = if (b) Some(f) else None
  }

  def whenReady(init: => Unit) = {
    if (document.readyState != "loading") { init }
    else document.once(Event.Ready) { e => init }
  }

  def loadedDocument: Future[Document] = {
    val doc = Promise[Document]
    whenReady(doc.success(document))
    doc.future
  }

  def schedule(ms: Int)(task: => Unit) = window.setTimeout(() => task, ms)
}
