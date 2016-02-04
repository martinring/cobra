package net.flatmap.js.util

import org.scalajs.dom._

abstract class Event[T <: raw.Event](val name: String)

object Event {
  object Blur extends Event[FocusEvent]("blur")

  object Load extends Event[raw.Event]("load")

  object Mouse {
    object Click extends Event[MouseEvent]("click")
    object ContextMenu extends Event[MouseEvent]("contextmenu")
    object DoubleClick extends Event[MouseEvent]("dblclick")
    object Enter extends Event[MouseEvent]("mouseenter")
    object Leave extends Event[MouseEvent]("mouseleave")
    object Down extends Event[MouseEvent]("mousedown")
    object Up extends Event[MouseEvent]("mouseup")
    object Over extends Event[MouseEvent]("mouseover")
    object Out extends Event[MouseEvent]("mouseout")
    object Move extends Event[MouseEvent]("mousemove")
  }

  object Key {
    object Down extends Event[KeyboardEvent]("keydown")
    object Press extends Event[KeyboardEvent]("keypress")
    object Up extends Event[KeyboardEvent]("keyup")
  }

  object Animation {
    object End extends Event[AnimationEvent]("animationend")
    object Iteration extends Event[AnimationEvent]("animationiteration")
    object Start extends Event[AnimationEvent]("animationstart")
  }

  object Transition {
    object End extends Event[TransitionEvent]("transitionend")
  }

  object Socket {
    object Error extends Event[ErrorEvent]("error")
    object Message extends Event[MessageEvent]("message")
    object Open extends Event[raw.Event]("open")
    object Close extends Event[raw.Event]("close")
  }

  object Ready extends Event[raw.Event]("readystatechange")
}