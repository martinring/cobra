package net.flatmap.js.codemirror.plugins

import net.flatmap.js.codemirror.CodeMirror
import scala.scalajs.js
import scala.scalajs.js.RegExp
import net.flatmap.js.codemirror.Position

import scala.scalajs.js.annotation.JSName
import scala.language.implicitConversions

@js.native
trait SearchCursor extends js.Object {
  def getSearchCursor(query: String): Cursor[js.UndefOr[Boolean]] = js.native
  def getSearchCursor(query: String, start: Position): Cursor[js.UndefOr[Boolean]] = js.native
  def getSearchCursor(query: String, start: Position, caseFold: Boolean): Cursor[js.UndefOr[Boolean]] = js.native
  def getSearchCursor(query: RegExp): Cursor[js.UndefOr[js.Array[String]]] = js.native
  def getSearchCursor(query: RegExp, start: Position): Cursor[js.UndefOr[js.Array[String]]] = js.native
}

@js.native
trait Cursor[R] extends js.Object {
  def findNext(): R = js.native
  def findPrevious(): R = js.native
  def from(): Position = js.native
  def to(): Position = js.native
  def replace(text: String): Unit = js.native
}

object SearchCursor {
  @js.native
  implicit def SearchCursorExtension(cm: CodeMirror): SearchCursor = cm.asInstanceOf[SearchCursor]
}