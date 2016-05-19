/*             _ _     _                                                      *\
**            | (_)   | |                                                     **
**         ___| |_  __| | ___      clide 2                                    **
**        / __| | |/ _` |/ _ \     (c) 2012-2014 Martin Ring                  **
**       | (__| | | (_| |  __/     http://clide.flatmap.net                   **
**        \___|_|_|\__,_|\___|                                                **
**                                                                            **
**   This file is part of Clide.                                              **
**                                                                            **
**   Clide is free software: you can redistribute it and/or modify            **
**   it under the terms of the GNU Lesser General Public License as           **
**   published by the Free Software Foundation, either version 3 of           **
**   the License, or (at your option) any later version.                      **
**                                                                            **
**   Clide is distributed in the hope that it will be useful,                 **
**   but WITHOUT ANY WARRANTY; without even the implied warranty of           **
**   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            **
**   GNU General Public License for more details.                             **
**                                                                            **
**   You should have received a copy of the GNU Lesser General Public         **
**   License along with Clide.                                                **
**   If not, see <http://www.gnu.org/licenses/>.                              **
\*                                                                            */

/**
 * originally adapted from Tim Baumanns Haskell OT library (MIT-License)
 * @see https://github.com/Operational-Transformation/ot.hs
 * @author Martin Ring
 */
package net.flatmap.collaboration

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

sealed trait Action[+T]

/** Skip the next `n` positions */
case class Retain(n: Int) extends Action[Nothing] { require(n>=0) }
/** Insert the given text at the current position */
case class Insert[T](s: Seq[T]) extends Action[T]
/** Delete the next `n` characters */
case class Delete(n: Int) extends Action[Nothing] { require(n>=0) }

object Action {
  implicit val charActionPickler = boopickle.Default.compositePickler[Action[Char]]
}

case class Operation[+T](actions: List[Action[T]]) {
  override def toString = "[" + actions.mkString(",") + "]"
}

object Operation {
  private def addRetain[T](n: Int, ops: List[Action[T]]): List[Action[T]] = ops match {
    case Retain(m)::xs => Retain(n+m)::xs
    case xs            => Retain(n)::xs
  }

  private def addInsert[T](s: Seq[T], ops: List[Action[T]]): List[Action[T]] = ops match {
    case Delete(d)::xs => Delete(d)::addInsert(s,xs)
    case Insert(t)::xs => Insert(t++s)::xs
    case xs            => Insert(s)::xs
  }

  private def addDelete[T](n: Int, ops: List[Action[T]]): List[Action[T]] = ops match {
    case Delete(m)::xs => Delete(n+m)::xs
    case xs            => Delete(n)::xs
  }

  private def normalize[T](ops: List[Action[T]]): List[Action[T]] = {
    @tailrec
  	def loop(as: List[Action[T]], bs: List[Action[T]]): List[Action[T]] = (as,bs) match {
  	  case (as,Nil) => as
  	  case (as,Retain(n)::bs) =>
  	    if (n == 0) loop(as,bs)
  	    else loop(addRetain(n,as),bs)
  	  case (as,Insert(i)::bs) =>
  	    if (i == "") loop(as,bs)
  	    else loop(addInsert(i,as),bs)
  	  case (as,Delete(d)::bs) =>
  	    if (d == 0) loop(as,bs)
  	    else loop(addDelete(d,as),bs)
      }
  	loop(Nil,ops.reverse).reverse
  }

  def transform[T](a: Operation[T], b: Operation[T]): Try[(Operation[T], Operation[T])] = {
    @tailrec
    def loop(as: List[Action[T]], bs: List[Action[T]], xs: List[Action[T]], ys: List[Action[T]]): Try[(List[Action[T]],List[Action[T]])] = (as,bs,xs,ys) match {
      case (Nil,Nil,xs,ys) => Success((xs,ys))
      case (aa@(a::as),bb@(b::bs),xs,ys) => (a,b) match {
        case (Insert(i),_) => loop(as,bb,addInsert(i,xs),addRetain(i.length,ys))
        case (_,Insert(i)) => loop(aa,bs,addRetain(i.length,xs),addInsert(i,ys))
        case (Retain(n),Retain(m)) => 
          if (n < m)       loop(as,Retain(m-n)::bs,addRetain(n,xs),addRetain(n,ys))
          else if (n == m) loop(as,bs,addRetain(n,xs),addRetain(n,ys))
          else             loop(Retain(n-m)::as,bs,addRetain(m,xs),addRetain(m,ys))        
        case (Delete(n),Delete(m)) => 
          if (n < m)       loop(as,Delete(m-n)::bs,xs,ys)
          else if (n == m) loop(as,bs,xs,ys)
          else             loop(Delete(n-m)::as,bs,xs,ys)        
        case (Retain(r),Delete(d)) => 
          if (r < d)       loop(as,Delete(d-r)::bs,xs,addDelete(r,ys))
          else if (r == d) loop(as,bs,xs,addDelete(d,ys))
          else             loop(Retain(r-d)::as,bs,xs,addDelete(d,ys))        
        case (Delete(d),Retain(r)) => 
          if (d < r)       loop(as,Retain(r-d)::bs,addDelete(d,xs),ys)
          else if (d == r) loop(as,bs,addDelete(d,xs),ys)
          else             loop(Delete(d-r)::as,bs,addDelete(r,xs),ys)        
      }
      case (Nil,Insert(i)::bs,xs,ys) => loop(Nil,bs,addRetain(i.length,xs),addInsert(i,ys))
      case (Insert(i)::as,Nil,xs,ys) => loop(as,Nil,addInsert(i,xs),addRetain(i.length,ys))
      case _ => Failure(new Exception("the operations cannot be transformed: input-lengths must match"))
    }
    loop(a.actions,b.actions,Nil,Nil).map { case (a,b) => (Operation(a.reverse), Operation(b.reverse)) }
  }

  def compose[T](a: Operation[T], b: Operation[T]): Try[Operation[T]] = {
    @tailrec
    def loop(as: List[Action[T]], bs: List[Action[T]], xs: List[Action[T]]): Try[List[Action[T]]] = (as,bs,xs) match {
      case (Nil,Nil,xs) => Success(xs)
      case (Delete(d)::as,bs,xs) => loop(as,bs,addDelete(d,xs))
      case (as,Insert(i)::bs,xs) => loop(as,bs,addInsert(i,xs))
      case (aa@(a::as),bb@(b::bs),xs) => (a,b) match {
        case (Retain(n),Retain(m)) =>
          if (n < m)       loop(as,Retain(m-n)::bs,addRetain(n,xs))
          else if (n == m) loop(as,bs,addRetain(n,xs))
          else             loop(Retain(n-m)::as,bs,addRetain(m,xs))
        case (Retain(r),Delete(d)) => 
          if (r < d)       loop(as,Delete(d-r)::bs,addDelete(r,xs))
          else if (r == d) loop(as,bs,addDelete(d,xs))
          else             loop(Retain(r-d)::as,bs,addDelete(d,xs))
        case (Insert(i),Retain(m)) => 
          if (i.length < m)       loop(as,Retain(m-i.length)::bs,addInsert(i,xs))
          else if (i.length == m) loop(as,bs,addInsert(i,xs))
          else {
            val (before,after) = i.splitAt(m)
            loop(Insert(after)::as,bs,addInsert(before,xs))
          }
        case (Insert(i),Delete(d)) =>
          if (i.length < d)       loop(as,Delete(d-i.length)::bs,xs)
          else if (i.length == d) loop(as,bs,xs)
          else                    loop(Insert(i.drop(d))::as,bs,xs)
        case other => Failure(new Exception("invalid state"))
      }
      case _ => Failure(new Exception(s"the operations cannot be composed: output-length of a ($a) must match input-length of b ($b)"))
    }
    loop(a.actions,b.actions,Nil).map(x => Operation(x.reverse))
  }
}
