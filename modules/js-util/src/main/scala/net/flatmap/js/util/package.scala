package net.flatmap.js

import java.util.regex.Pattern

import org.scalajs.dom._

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

  trait NodeSeqQuery extends EventSource { underlying: Seq[Node] =>
    def elements = underlying.collect { case e if e.nodeType == 1 => e.asInstanceOf[Element] }
    private def foreachElement(f: Element => Unit): Seq[Node] = {
      elements.filter(_ != null).foreach(f)
      this
    }
    private def foreachNode(f: Node => Unit): Seq[Node] = {
      filter(_ != null).foreach(f)
      this
    }
    def append(e: Node) = foreachNode(_.appendChild(e))
    def append($: Seq[Node]) = foreachNode(e => $.foreach(e.appendChild(_)))
    object data {
      def apply(name: String) = elements.head.getAttribute("data-" + name)
      def update(name: String, value: Option[String]) = value match {
        case None => elements.foreach(_.setAttribute("data-" + name, null))
        case Some(value) => elements.foreach(_.setAttribute("data-" + name, value))
      }
    }
    object classes extends collection.mutable.Set[String] {
      def contains(elem: String): Boolean = elements.headOption.exists(_.classList.contains(elem))
      def iterator: Iterator[String] = elements.headOption.toIterator.flatMap(h => Iterator.tabulate(h.classList.length)(h.classList.item))
      def += (elem: String): this.type = { elements.foreach(_.classList.add(elem)); this }
      def -= (elem: String): this.type = { elements.foreach(_.classList.remove(elem)); this }
    }
    def remove(e: Node) = foreachNode(_.removeChild(e))
    def remove($: Seq[Node]) = foreachNode(e => $.foreach(e.removeChild(_)))
    def remove() = foreachNode(e => Option(e.parentNode).foreach(_.removeChild(e)))
    def insertBefore(e: Node) = foreachNode(e.parentNode.insertBefore(_,e))
    def insertBefore($: Seq[Node]): Seq[Node] = { $.foreach(e => foreach(e.parentNode.insertBefore(_,e))); this }
    def insertAfter(e: Node) = foreachNode { n =>
      for {
        parent <- Option(n.parentNode)
      } Option(n.nextSibling).fold(parent.appendChild(e))(next => parent.insertBefore(e,next))
    }
    def insertAfter($: Seq[Node]): Seq[Node] = foreachNode { n =>
      for {
        parent <- Option(n.parentNode)
      } Option(n.nextSibling).fold($.foreach(parent.appendChild(_)))(next => $.foreach(parent.insertBefore(_,next)))
    }
    def on[T <: raw.Event](event: Event[T])(f: T => Unit): Subscription = {
      val g: scalajs.js.Function1[T,_] = f
      foreach(_.addEventListener(event.name, g))
      Subscription(foreach(_.removeEventListener(event.name, g.asInstanceOf[scalajs.js.Function1[raw.Event,_]])))
    }
    def next(): Seq[Node] = underlying.map(_.nextSibling)

    def html: String = elements.head.innerHTML
    def html_=(value: String): Unit = elements.foreach(_.innerHTML = value)


    def title: String = elements.head.getAttribute("title")
    def title_=(value: String): Unit = elements.foreach(_.setAttribute("title",value))

    def tabIndex: Int = elements.head.getAttribute("tabindex").toInt
    def tabIndex_=(value: Int): Unit = elements.foreach(_.setAttribute("tabindex", value.toString))

    def focus() = foreachElement(_.asInstanceOf[Dynamic].focus())
    def blur() = foreachElement(_.asInstanceOf[Dynamic].blur())

    def query(selector: String) = elements.flatMap(e => e.querySelectorAll(selector))

    def ==(other: Seq[Node]) =
      underlying.length == other.length &&
      underlying.zip(other).forall { case (a,b) => a == b }
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
