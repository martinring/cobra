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

/**
 * @author Martin Ring
 */
sealed trait Annotation {
  val length: Int
  def withLength(n: Int): Annotation = this match {
    case Plain(_) => Plain(n)
    case Annotate(_,c) => Annotate(n,c)
  }
}

@SerialVersionUID(1L)
case class Plain(length: Int) extends Annotation {
  override def toString = length.toString
}

object AnnotationType extends Enumeration {
  /** supported values are ... TODO */
  val Class          = Value("c")
  /** currently supports "running", "finished" and "failed" */
  val Progress      = Value("ls")
  /** can be set to arbitary content and will set a html title attribute */
  val Tooltip        = Value("t")
  /** can be html */
  val ErrorMessage   = Value("e")
  /** can be html */
  val WarningMessage = Value("w")
  /** can be html */
  val InfoMessage    = Value("i")
  /** can be html */
  val Output         = Value("o")
  /** must be an url-safe document-unique id */
  val Entity         = Value("n")
  /** must be an id of the format
   *
   *  "<id>" for local references (as marked with Entity)
   *  "/<file>/<id>" for references in the same project
   *  "//<url>" for external urls
   */
  val Ref            = Value("l")
  /** can be used to substitute a text span with some text or html. must not be overlapping. */
  val Substitution   = Value("s")
  /** not supported yet */
  val HelpRequest    = Value("h")
}

@SerialVersionUID(1L)
case class Annotate(length: Int, content: List[(AnnotationType.Value,String)]) extends Annotation {
  override def toString = length.toString + ":{" + content.map{case(k,v)=>k+": " +v}.mkString(",") + "}"
}

@SerialVersionUID(1L)
case class Annotations(annotations: List[Annotation] = Nil, responses: List[(String,String)] = Nil) {
  override def toString = annotations.mkString(";")

  def positions(tpe: (AnnotationType.Value,String)): List[Int] = {
    val (_,result) = ((0,List.empty[Int]) /: annotations) {
      case ((offset,ps),Plain(n)) => (offset+n,ps)
      case ((offset,ps),Annotate(n,c)) =>
        if (c.contains(tpe)) (offset+n,offset::ps)
        else (offset+n,ps)
    }
    result
  }

  def positions(tpe: AnnotationType.Value): List[(Int,String)] = {
    val (_,result) = ((0,List.empty[(Int,String)]) /: annotations) {
      case ((offset,ps),Plain(n)) => (offset+n,ps)
      case ((offset,ps),Annotate(n,c)) =>
        c.find(_._1 == tpe).fold(offset+n,ps) {
          case (_,value) => (offset+n,(offset,value)::ps)
        }
    }
    result
  }

  def annotate(n: Int, c: List[(AnnotationType.Value,String)]): Annotations = if (n >= 0) {
    annotations.lastOption match {
      case Some(Annotate(m,c2)) if c == c2 => Annotations(annotations.init :+ Annotate(n+m,c), responses)
      case _ => Annotations(annotations :+ Annotate(n,c.toList), responses)
    }
  } else this

  @deprecated("use overloaded annotate with list instead", "2014-01-30")
  def annotate(n: Int, c: Set[(AnnotationType.Value,String)]): Annotations = if (n >= 0) {
    annotations.lastOption match {
      case Some(Annotate(m,c2)) if c == c2 => Annotations(annotations.init :+ Annotate(n+m,c.toList), responses)
      case _ => Annotations(annotations :+ Annotate(n,c.toList), responses)
    }
  } else this

  def plain(n: Int): Annotations = if (n > 0) {
    annotations.lastOption match {
      case Some(Plain(m)) => Annotations(annotations.init :+ Plain(n+m), responses)
      case _ => Annotations(annotations :+ Plain(n), responses)
    }
  } else this

  def respond(request: String, answer: String) =
    Annotations(annotations,responses :+ (request,answer))

  def :+ (a: Annotation): Annotations = {
    (annotations.lastOption,a) match {
      case (Some(Plain(n)),Plain(m)) => Annotations(annotations.init :+ Plain(n+m), responses)
      case (Some(Annotate(n,c)),Annotate(m,d)) if c == d => Annotations(annotations.init :+ Annotate(n+m,c), responses)
      case _ => Annotations(annotations :+ a, responses)
    }
  }

  def ++ (a: Annotations): Annotations = {
    (annotations.lastOption, a.annotations.headOption) match {
      case (Some(Plain(n)),Some(Plain(m))) => Annotations(annotations.init ++ (Plain(n+m) +: a.annotations.tail), responses)
      case (Some(Annotate(n,c)),Some(Annotate(m,d))) if c == d => Annotations(annotations.init ++ (Annotate(n+m,c) +: a.annotations.tail), responses)
      case _ => Annotations(annotations ++ a.annotations, responses)
    }
  }

  def length = annotations.map(_.length).reduceOption(_ + _).getOrElse(0)

  def compose(other: Annotations): Try[Annotations] = Annotations.compose(this,other)
  def transform[T](op: Operation[T]): Try[Annotations] = Annotations.transform(this, op)
}

object Annotations {
  private def addPlain(n: Int, as: List[Annotation]): List[Annotation] = as match {
    case Plain(m)::xs => Plain(n+m)::xs
    case xs if n > 0 => Plain(n)::xs
    case _ => as
  }

  private def addAnnotate(n: Int, c: List[(AnnotationType.Value,String)], as: List[Annotation]): List[Annotation] = as match {
    case Annotate(m,c2)::xs if c2 == c => Annotate(n+m,c)::xs
    case xs => Annotate(n,c)::xs
  }

  private def add(a: Annotation, as: List[Annotation]): List[Annotation] = a match {
    case Plain(n) => addPlain(n,as)
    case Annotate(n,c) => addAnnotate(n,c,as)
  }

  private def addWithLength(n: Int, a: Annotation, as: List[Annotation]): List[Annotation] = a match {
    case Plain(_)      => addPlain(n,as)
    case Annotate(_,c) => addAnnotate(n,c,as)
  }

  def transform[T](a: Annotations, o: Operation[T]): Try[Annotations] = {
    @tailrec
    def loop(as: List[Annotation], bs: List[Action[T]], xs: List[Annotation]): Try[List[Annotation]] = (as,bs,xs) match {
      case (Nil,Nil,xs) => Success(xs)
      case (aa@(a::as),bb@(b::bs),xs) => (a,b) match {
        case (a,Insert(i)) => loop(aa,bs,addWithLength(i.length,a,xs))
        case (a,Retain(m)) =>
          if (a.length < m)       loop(as,Retain(m-a.length)::bs,add(a,xs))
          else if (a.length == m) loop(as,bs,add(a,xs))
          else                    loop(addWithLength(a.length-m,a,as),bs,addWithLength(m,a,xs))        
        case (a,Delete(d)) => 
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

  /** TODO */
  def compose(a: Annotations, b: Annotations): Try[Annotations] = {
    @tailrec
    def loop(as: List[Annotation], bs: List[Annotation], xs: List[Annotation]): Try[List[Annotation]] = (as,bs,xs) match {
      case (Nil,Nil,xs) => Success(xs)
      case ((a::as),(b::bs),xs) => (a,b) match {
        case (Plain(n),Plain(m)) => 
          if (n < m)       loop(as,addPlain(m-n,bs),addPlain(n,xs))
          else if (n == m) loop(as,bs,addPlain(n,xs))
          else             loop(addPlain(n-m,as),bs,addPlain(m,xs))
        case (Plain(n),Annotate(m,c)) => 
          if (n < m)       loop(as,addAnnotate(m-n,c,bs),addAnnotate(n,c,xs))
          else if (n == m) loop(as,bs,addAnnotate(n,c,xs))
          else             loop(addPlain(n-m,as),bs,addAnnotate(m,c,xs))        
        case (Annotate(n,c),Plain(m)) => 
          if (n < m)       loop(as,addPlain(m-n,bs),addAnnotate(n,c,xs))
          else if (n == m) loop(as,bs,addAnnotate(n,c,xs))
          else             loop(addAnnotate(n-m,c,as),bs,addAnnotate(m,c,xs))        
        case (Annotate(n,c),Annotate(m,c2)) => 
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
