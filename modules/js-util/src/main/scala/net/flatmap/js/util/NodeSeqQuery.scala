package net.flatmap.js.util

import org.scalajs.dom.{Node,Element,raw}
import org.scalajs.dom.ext.Ajax
import scala.scalajs.js.Dynamic
import scala.util.{Failure, Success}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

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

  def loadFrom(file: String) = Ajax.get(file).map {
    case value =>
      elements.html = value.responseText
  }

  def ==(other: Seq[Node]) =
    underlying.length == other.length &&
      underlying.zip(other).forall { case (a,b) => a == b }
}