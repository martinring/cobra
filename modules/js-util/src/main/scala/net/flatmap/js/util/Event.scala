package net.flatmap.js.util

import org.scalajs.dom._

abstract class Event[+N <: raw.EventTarget, +T <: raw.Event](val name: String)

object Event {
  object Blur extends Event[Element,FocusEvent]("blur")
  object Load extends Event[Element,UIEvent]("load")

  object Mouse {
    object Click extends Event[Element,MouseEvent]("click")
    object ContextMenu extends Event[Element,MouseEvent]("contextmenu")
    object DoubleClick extends Event[Element,MouseEvent]("dblclick")
    object Enter extends Event[Element,MouseEvent]("mouseenter")
    object Leave extends Event[Element,MouseEvent]("mouseleave")
    object Down extends Event[Element,MouseEvent]("mousedown")
    object Up extends Event[Element,MouseEvent]("mouseup")
    object Over extends Event[Element,MouseEvent]("mouseover")
    object Out extends Event[Element,MouseEvent]("mouseout")
    object Move extends Event[Element,MouseEvent]("mousemove")
  }

  object Key {
    object Down extends Event[Element,KeyboardEvent]("keydown")
    object Press extends Event[Element,KeyboardEvent]("keypress")
    object Up extends Event[Element,KeyboardEvent]("keyup")
  }

  object Animation {
    object End extends Event[Element,AnimationEvent]("animationend")
    object Iteration extends Event[Element,AnimationEvent]("animationiteration")
    object Start extends Event[Element,AnimationEvent]("animationstart")
  }

  object Transition {
    object End extends Event[Element,TransitionEvent]("transitionend")
  }

  object Socket {
    object Open extends Event[WebSocket,raw.Event]("open")
    object Close extends Event[WebSocket,raw.Event]("close")
    object Error extends Event[WebSocket,ErrorEvent]("error")
    object Message extends Event[WebSocket,MessageEvent]("message")
  }

  object Window {
    object BeforeUnload extends Event[Window,BeforeUnloadEvent]("beforeunload")
  }

  object Document {
    object Ready extends Event[html.Document,raw.Event]("readystatechange")
    object Load extends Event[Document,UIEvent]("load")
  }

  object Progress {
    object Load extends Event[XMLHttpRequest,ProgressEvent]("load")
  }
}