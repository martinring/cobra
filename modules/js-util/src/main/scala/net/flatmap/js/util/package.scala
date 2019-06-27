package net.flatmap.js

import java.util.regex.Pattern

import org.scalajs.dom.{window,document,Document,Node,EventTarget,NodeList,NamedNodeMap}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.util.matching.Regex

/**
 * Created by martin on 08.09.15.
 */
package object util {
  import net.flatmap.js.util._

  def query(selector: String): NodeSeqQuery = QueryNodeSeq(document.querySelectorAll(selector))
  def query(elem: Node*): NodeSeqQuery = elem

  implicit class BetterEventTarget[T <: EventTarget](underlying: T) extends EventSource[T] {
    def on[E](event: Event[T,E])(f: E => Unit): Subscription = {
      val g: scalajs.js.Function1[E,_] = f
      underlying.addEventListener(event.name, g)
      Subscription(underlying.removeEventListener(event.name, g.asInstanceOf[scalajs.js.Function1[E,_]]))
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
      StringContext.checkLengths(args,sc.parts.toSeq)
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

  implicit class AttributeMap(val underlying: NamedNodeMap) extends mutable.Map[String,String] {
    def subtractOne(key: String) = {
      underlying.removeNamedItem(key)
      this
    }
    def addOne(kv: (String, String)) = {
      val (key,value) = kv
      val attr = document.createAttribute(key)
      attr.value = value
      underlying.setNamedItem(attr)
      this
    }
    def get(key: String): Option[String] = {
      Option(underlying.getNamedItem(key)).filter(_ != js.undefined).map { attr =>
        attr.value
      }
    }
    def iterator: Iterator[(String,String)] = new Iterator[(String,String)] {
      private var index = 0
      def next(): (String,String) = {
        val it = underlying.item(index)
        index += 1
        (it.name,it.value)
      }
      def hasNext: Boolean = underlying.length > index
    }
  }

  def whenReady(init: => Unit) = {
    if (document.readyState != "loading") { init }
    else document.once(Event.Document.Ready) { e => init }
  }

  def loadedDocument: Future[Document] = {
    val doc = Promise[Document]
    whenReady(doc.success(document))
    doc.future
  }

  def scheduleOnce(ms: Int)(task: => Unit) = window.setTimeout(() => task, ms)
  def schedule(ms: Int)(task: => Unit) = window.setInterval(() => task, ms)
}
