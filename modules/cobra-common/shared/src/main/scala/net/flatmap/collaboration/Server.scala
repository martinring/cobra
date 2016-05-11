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

package net.flatmap.collaboration

import scala.collection.mutable.Buffer
import scala.util.{Success, Try}

class Server[T](initialState: Document[T]) {
  private val history: Buffer[Operation[T]] = Buffer.empty
  private var combinedHistory: Operation[T] = Operation(List(Retain(initialState.content.length)))
  private def appendOperation(op: Operation[T]) = {
    history.append(op)
    combinedHistory = Operation.compose(combinedHistory,op).get
  }
  private var state: Document[T] = initialState

  def text = state.content
  def revision = history.length
  def getHistory = history.view
  def getCombinedHistory = combinedHistory

  /**
   * an operation arrives from a client
    *
    * @param rev the revision the client refers to
   */
  def applyOperation(operation: Operation[T], rev: Long): Try[Operation[T]] = {
    val result = for {
	  concurrentOps <- Try {
	    require((0 to revision) contains rev, "invalid revision: " + rev)
	    history.view(rev.toInt, revision) // TODO: Long Revisions
	  }
	  operation <- concurrentOps.foldLeft(Success(operation): Try[Operation[T]]) {
	    case (a,b) => a.flatMap(a => Operation.transform(a,b).map(_._1)) }
	  nextState <- state(operation)
	} yield (nextState, operation)
	result.map {
	  case (nextState,operation) =>
	    appendOperation(operation)
	    state = nextState
	    operation
	}
  }

  /**
   * transform a client annotation to fit the most recent revision
    *
    * @param rev the revision the client refers to
   */
  def transformAnnotation(rev: Int, as: Annotations): Try[Annotations] = for {
      concurrentOps <- Try {
        require((0 to revision) contains rev, "invalid revision: " + rev)
        history.view(rev, revision)
      }
      annotation <- concurrentOps.foldLeft(Success(as): Try[Annotations]) {
        case (a,b) => a.flatMap(a => Annotations.transform(a,b)) }
  } yield annotation
}
