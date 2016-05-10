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

package net.flatmap.cobra

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
 * @author Martin Ring <martin.ring@dfki.de>
 */
case class Document[T](content: Seq[T]) extends AnyVal {
  def apply(op: Operation[T]): Try[Document[T]] = {
    @tailrec
    def loop(ops: List[Action[T]], it: Seq[T], ot: Seq[T]): Try[Seq[T]] = (ops,it,ot) match {
      case (Nil,Seq(),ot) => Success(ot)
      case (op::ops,it,ot) => op match {
        case Retain(r) => if (it.length < r)
            Failure(new Exception("operation can't be applied to the document: operation is longer than the text"))
          else {
            val (before,after) = it.splitAt(r)
            loop(ops,after,ot ++ before)
          }
        case Insert(i) => loop(ops,it,ot ++ i)
        case Delete(d) => if (d > it.length)
            Failure(new Exception("operation can't be applied to the document: operation is longer than the text"))
          else loop(ops,it.drop(d),ot)
      }
      case _ => Failure(new Exception("operation can't be applied to the document: text is longer than the operation"))
    }
    loop(op.actions,content,Seq()).map(new Document(_))
  }
}
