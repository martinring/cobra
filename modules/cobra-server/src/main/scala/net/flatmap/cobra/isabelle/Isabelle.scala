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

/*package net.flatmap.cobra.isabelle

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.kernel.Bootable
import isabelle.Isabelle_System
import scala.concurrent.duration._
import clide.assistants.AssistBehavior
import clide.assistants.AssistantControl
import clide.assistants.AssistantServer
import clide.models.ProjectInfo
import scala.concurrent.Future
import clide.models.OpenedFile
import scala.collection.mutable.Map
import clide.collaboration.Operation
import clide.models.SessionInfo
import isabelle.Build
import isabelle.Session
import scala.concurrent.Promise
import scala.concurrent.Await
import isabelle.XML
import isabelle.Path
import isabelle.Document
import clide.assistants.Cursor
import akka.actor.Cancellable
import isabelle.Symbol
import clide.collaboration.Annotations
import scala.io.StdIn

object Isabelle extends Isabelle

class Isabelle extends AssistantServer(IsabelleAssistBehavior) {
  override def startup() {
    Isabelle_System.init()
    super.startup()
  }

  override def shutdown() {    
    super.shutdown()
  }
}

trait Control {
  def control: AssistantControl
}

case class IsabelleAssistBehavior(control: AssistantControl) extends AssistBehavior with Control
  with IsabelleSession with IsabelleConversions {

  def mimeTypes = Set("text/x-isabelle")

  def fileOpened(file: OpenedFile) = {
    control.log.info("fileOpened({})", file.info.path)
    updateFile(file, file, initEdits(file, Nil))
  }

  def fileActivated(file: OpenedFile) = {
    control.log.info("fileActivated({})", file.info.path)
    updateFile(file, file, initEdits(file, Nil))
  }

  def fileInactivated(file: OpenedFile) = {
    updateFile(file, file, closeEdits(file))
  }

  def fileClosed(file: OpenedFile) = {
    updateFile(file, file, removeEdits(file))
  }

  def fileChanged(file: OpenedFile, delta: Operation[Char], cursors: Seq[Cursor]) = {
    control.log.info("fileChanged({},{},...)", file.info.path, delta)
    val edits = opToDocumentEdits(file, cursors, delta)
    this.cursors = cursors.toSet
    updateFile(file,file,edits)
  }

  def collaboratorJoined(who: SessionInfo) = noop
  def collaboratorLeft(who: SessionInfo) = noop

  def cursorMoved(cursor: Cursor) = {
    this.cursors += cursor
    noop
  }

  def annotationsDisregarded(file: OpenedFile, name: String) = noop
  def annotationsRequested(file: OpenedFile, name: String) = noop

  def receiveChatMessage(from: SessionInfo, msg: String, tpe: Option[String], timestamp: Long) = noop

  implicit val ec = control.executionContext

  def helpRequest(from: SessionInfo, file: OpenedFile, index: Int, id: String, request: String) = {
    isabelle.Symbol.names.foreach {
      case (sym,name) =>
        control.annotate(file, "autocompletion", (new Annotations).respond("c:" + id, "\\<" + name + ">\t" + isabelle.Symbol.decode("\\<"+name+">") + "<span class='text-muted pull-right'>" + name + "</span>"))
    }
    noop
  }

  override def refreshInterval() {
    refreshAnnotations()
  }
}

object IsabelleApp extends App {
  Isabelle.startup()
  StdIn.readLine()
  Isabelle.shutdown()
  Isabelle.system.awaitTermination()
  sys.exit()
}*/
