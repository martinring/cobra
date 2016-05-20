package net.flatmap.js.codemirror.plugins

import net.flatmap.js.codemirror.CodeMirror
import org.scalajs.dom.raw.HTMLElement

import scala.scalajs.js

/**
  * Created by martin on 20.05.16.
  */
@js.native
trait Runmode extends js.Object {
  def runMode(code: String, mode: String, output: HTMLElement)
}

object Runmode {
  implicit def runmodePlugin(cm: CodeMirror.type): Runmode = cm.asInstanceOf[Runmode]
}