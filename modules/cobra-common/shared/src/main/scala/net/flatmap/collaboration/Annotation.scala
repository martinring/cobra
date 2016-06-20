 /*            _ _     _                                                      *\
 **           | (_)   | |                                                     **
 **        ___| |_  __| | ___      clide 2                                    **
 **       / __| | |/ _` |/ _ \     (c) 2012-2014 Martin Ring                  **
 **      | (__| | | (_| |  __/     http://clide.flatmap.net                   **
 **       \___|_|_|\__,_|\___|                                                **
 **                                                                           **
 **  This file is part of Clide.                                              **
 **                                                                           **
 **  Clide is free software: you can redistribute it and/or modify            **
 **  it under the terms of the GNU General Public License as published by     **
 **  the Free Software Foundation, either version 3 of the License, or        **
 **  (at your option) any later version.                                      **
 **                                                                           **
 **  Clide is distributed in the hope that it will be useful,                 **
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of           **
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            **
 **  GNU General Public License for more details.                             **
 **                                                                           **
 **  You should have received a copy of the GNU General Public License        **
 **  along with Clide.  If not, see <http://www.gnu.org/licenses/>.           **
 \*                                                                           */

package net.flatmap.collaboration

import scala.annotation.tailrec
import scala.util._


sealed trait Annotation {
  val length: Int
  def withLength(n: Int): Annotation = this match {
    case Empty(_) => Empty(n)
    case Annotated(_,c) => Annotated(n,c)
  }
}

case class Empty(length: Int) extends Annotation {
  override def toString = length.toString
}

sealed trait AnnotationMessage
case class ErrorMessage(content: String) extends AnnotationMessage
case class WarningMessage(content: String) extends AnnotationMessage
case class InfoMessage(content: String) extends AnnotationMessage
case class OutputMessage(content: String) extends AnnotationMessage
case class StateMessage(content: String) extends AnnotationMessage


case class AnnotationOptions(
  val classes: Set[String] = Set.empty,
  val substitute: Option[String] = None,
  val messages: List[AnnotationMessage] = List.empty,
  val tooltip: Option[String] = None
) {
  def ++(other: AnnotationOptions) = AnnotationOptions(
    classes = classes ++ other.classes,
    substitute = other.substitute orElse substitute,
    messages = messages ++ other.messages,
    tooltip = other.tooltip orElse tooltip
  )
}

case class Annotated(length: Int, content: AnnotationOptions) extends Annotation

case class Annotations(annotations: List[Annotation] = Nil, responses: List[(String,String)] = Nil) {
  override def toString = annotations.mkString(";")

  def annotate(n: Int, c: AnnotationOptions): Annotations = if (n >= 0) {
    annotations.lastOption match {
      case Some(Annotated(m,c2)) if c == c2 => Annotations(annotations.init :+ Annotated(n+m,c), responses)
      case _ => Annotations(annotations :+ Annotated(n,c), responses)
    }
  } else this

  def plain(n: Int): Annotations = if (n > 0) {
    annotations.lastOption match {
      case Some(Empty(m)) => Annotations(annotations.init :+ Empty(n+m), responses)
      case _ => Annotations(annotations :+ Empty(n), responses)
    }
  } else this

  def respond(request: String, answer: String) =
    Annotations(annotations,responses :+ (request,answer))

  def :+ (a: Annotation): Annotations = {
    (annotations.lastOption,a) match {
      case (Some(Empty(n)),Empty(m)) => Annotations(annotations.init :+ Empty(n+m), responses)
      case (Some(Annotated(n,c)),Annotated(m,d)) if c == d => Annotations(annotations.init :+ Annotated(n+m,c), responses)
      case _ => Annotations(annotations :+ a, responses)
    }
  }

  def ++ (a: Annotations): Annotations = {
    (annotations.lastOption, a.annotations.headOption) match {
      case (Some(Empty(n)),Some(Empty(m))) => Annotations(annotations.init ++ (Empty(n+m) +: a.annotations.tail), responses)
      case (Some(Annotated(n,c)),Some(Annotated(m,d))) if c == d => Annotations(annotations.init ++ (Annotated(n+m,c) +: a.annotations.tail), responses)
      case _ => Annotations(annotations ++ a.annotations, responses)
    }
  }

  def length = annotations.map(_.length).reduceOption(_ + _).getOrElse(0)

  def compose(other: Annotations): Try[Annotations] = Annotations.compose(this,other)
  def transform[T](op: Operation[T]): Try[Annotations] = Annotations.transform(this, op)
}

object Annotations {
  private def addPlain(n: Int, as: List[Annotation]): List[Annotation] = as match {
    case Empty(m)::xs => Empty(n+m)::xs
    case xs if n > 0 => Empty(n)::xs
    case _ => as
  }

  private def addAnnotate(n: Int, c: AnnotationOptions, as: List[Annotation]): List[Annotation] = as match {
    case Annotated(m,c2)::xs if c2 == c => Annotated(n+m,c)::xs
    case xs => Annotated(n,c)::xs
  }

  private def add(a: Annotation, as: List[Annotation]): List[Annotation] = a match {
    case Empty(n) => addPlain(n,as)
    case Annotated(n,c) => addAnnotate(n,c,as)
  }

  private def addWithLength(n: Int, a: Annotation, as: List[Annotation]): List[Annotation] = a match {
    case Empty(_)      => addPlain(n,as)
    case Annotated(_,c) => addAnnotate(n,c,as)
  }

  def transform[T](a: Annotations, o: Operation[T]): Try[Annotations] = {
    @tailrec
    def loop(as: List[Annotation], bs: List[Action[T]], xs: List[Annotation]): Try[List[Annotation]] = (as,bs,xs) match {
      case (Nil,Nil,xs) => Success(xs)
      case (aa@(a::as),bb@(b::bs),xs) => b match {
        case Insert(i) => loop(aa,bs,addWithLength(i.length,a,xs))
        case Retain(m) =>
          if (a.length < m)       loop(as,Retain(m-a.length)::bs,add(a,xs))
          else if (a.length == m) loop(as,bs,add(a,xs))
          else                    loop(addWithLength(a.length-m,a,as),bs,addWithLength(m,a,xs))
        case Delete(d) =>
          if (a.length < d)       loop(as,Delete(d-a.length)::bs,xs)
          else if (a.length == d) loop(as,bs,xs)
          else                    loop(addWithLength(a.length-d,a,as),bs,xs)
      }
      case (Nil,Insert(i)::bs,xs) => loop(Nil,bs,addPlain(i.length,xs))
      case _ =>
        Success(xs)
    }
    loop(a.annotations.reverse,o.actions.reverse,Nil).map(Annotations(_,a.responses))
  }

  def compose(a: Annotations, b: Annotations): Try[Annotations] = {
    @tailrec
    def loop(as: List[Annotation], bs: List[Annotation], xs: List[Annotation]): Try[List[Annotation]] = (as,bs,xs) match {
      case (Nil,Nil,xs) => Success(xs)
      case ((a::as),(b::bs),xs) => (a,b) match {
        case (Empty(n),Empty(m)) =>
          if (n < m)       loop(as,addPlain(m-n,bs),addPlain(n,xs))
          else if (n == m) loop(as,bs,addPlain(n,xs))
          else             loop(addPlain(n-m,as),bs,addPlain(m,xs))
        case (Empty(n),Annotated(m,c)) =>
          if (n < m)       loop(as,addAnnotate(m-n,c,bs),addAnnotate(n,c,xs))
          else if (n == m) loop(as,bs,addAnnotate(n,c,xs))
          else             loop(addPlain(n-m,as),bs,addAnnotate(m,c,xs))
        case (Annotated(n,c),Empty(m)) =>
          if (n < m)       loop(as,addPlain(m-n,bs),addAnnotate(n,c,xs))
          else if (n == m) loop(as,bs,addAnnotate(n,c,xs))
          else             loop(addAnnotate(n-m,c,as),bs,addAnnotate(m,c,xs))        
        case (Annotated(n,c),Annotated(m,c2)) =>
          if (n < m)       loop(as,addAnnotate(m-n,c2,bs),addAnnotate(n,c ++ c2,xs))
          else if (n == m) loop(as,bs,addAnnotate(n,c ++ c2,xs))
          else             loop(addAnnotate(n-m,c,as),bs,addAnnotate(m,c ++ c2,xs))        
      }
      case (a::as,Nil,xs) if a.length == 0 => loop(as,Nil,add(a,xs))
      case (Nil,b::bs,xs) if b.length == 0 => loop(Nil,bs,add(b,xs))
      case _ => Success(xs)
    }
    loop(a.annotations.reverse, b.annotations.reverse, Nil).map(Annotations(_,a.responses))
  }
}
