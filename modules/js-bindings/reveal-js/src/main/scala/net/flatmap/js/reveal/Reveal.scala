package net.flatmap.js.reveal

import net.flatmap.js.util.Event
import org.scalajs.dom.raw.{HTMLDivElement, EventTarget}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * Created by martin on 01.02.16.
  */

@JSName("Reveal")
@js.native
object Reveal extends EventTarget {
  def initialize(options: RevealOptions): Unit = js.native

  // Navigation
  def slide( indexh: Int, indexv: Int = js.native, indexf: Int = js.native): Unit = js.native
  def left(): Unit = js.native
  def right(): Unit = js.native
  def up(): Unit = js.native
  def down(): Unit = js.native
  def prev(): Unit = js.native
  def next(): Unit = js.native
  def prevFragment(): Unit = js.native
  def nextFragment(): Unit = js.native

  // Toggle presentation states, optionally pass true/false to force on/off
  def toggleOverview(): Unit = js.native
  def togglePause(): Unit = js.native
  def toggleAutoSlide(): Unit = js.native

  // Change a config value at runtime
  def configure(config: RevealOptions): Unit = js.native

  // Returns the present configuration options
  def getConfig(): RevealOptions = js.native

  // Fetch the current scale of the presentation
  def getScale(): Double = js.native

  // Retrieves the previous and current slide elements
  def getPreviousSlide(): HTMLDivElement = js.native
  def getCurrentSlide(): HTMLDivElement = js.native

  def getIndices(): RevealPosition = js.native; // { h: 0, v: 0 } }
  def getProgress(): Double = js.native // 0-1
  def getTotalSlides(): Int = js.native

  // Returns the speaker notes for the current slide
  //def getSlideNotes()

  // State checks
  def isFirstSlide(): Boolean = js.native
  def isLastSlide(): Boolean = js.native
  def isOverview(): Boolean = js.native
  def isPaused(): Boolean = js.native
  def isAutoSliding(): Boolean = js.native

  // private stuff
  def sync(): Unit = js.native
  def isReady(): Boolean = js.native
}

@js.native
trait SlideChangedEvent extends org.scalajs.dom.Event {
  def previousSlide: HTMLDivElement = js.native
  def currentSlide: HTMLDivElement = js.native
  def indexh: Int = js.native
  def indexv: Int = js.native
}

object RevealEvents {
  object SlideChanged extends Event[SlideChangedEvent]("slidechanged")
  object Ready extends Event[org.scalajs.dom.Event]("ready")
}

object RevealMathOptions {
  def apply() = js.Object.apply().asInstanceOf[RevealMathOptions]
}

@js.native
sealed trait RevealMathOptions extends js.Object {
  var mathjax: String
  var config: String
}

@js.native
sealed trait RevealDependency extends js.Object {
  var src: String = js.native
  var condition: js.Function0[Boolean] = js.native
  var async: Boolean = js.native
  var callback: js.Function0[Unit] = js.native
}

object RevealDependency {
  def apply(src: String, condition: js.UndefOr[Function0[Boolean]] = js.undefined, async: js.UndefOr[Boolean] = js.undefined, callback: js.UndefOr[Function0[Unit]] = js.undefined) = {
    val res = js.Object.apply().asInstanceOf[RevealDependency]
    res.src = src
    condition.foreach(res.condition = _)
    async.foreach(res.async = _)
    callback.foreach(res.callback = _)
    res
  }
}

object RevealOptions {
  def apply() = js.Object.apply().asInstanceOf[RevealOptions]
}

@js.native
trait RevealPosition extends js.Object {
  @JSName("h")
  def indexh: Int = js.native
  @JSName("v")
  def indexv: Int = js.native
}

@js.native
sealed trait RevealOptions extends js.Object {
  // Display controls in the bottom right corner
  var controls: Boolean = js.native

  // Display a presentation progress bar
  var progress: Boolean = js.native

  // Display the page number of the current slide
  var slideNumber: Boolean = js.native

  // Push each slide change to the browser history
  var history: Boolean = js.native

  // Enable keyboard shortcuts for navigation
  var keyboard: Boolean = js.native

  // Enable the slide overview mode
  var overview: Boolean = js.native

  // Vertical centering of slides
  var center: Boolean = js.native

  // Enables touch navigation on devices with touch input
  var touch: Boolean = js.native

  // Loop the presentation
  var loop: Boolean = js.native

  // Change the presentation direction to be RTL
  var rtl: Boolean = js.native

  // Turns fragments on and off globally
  var fragments: Boolean = js.native

  // Flags if the presentation is running in an embedded mode,
  // i.e. contained within a limited portion of the screen
  var embedded: Boolean = js.native

  // Flags if we should show a help overlay when the questionmark
  // key is pressed
  var help: Boolean = js.native

  // Flags if speaker notes should be visible to all viewers
  var showNotes: Boolean = js.native

  // Number of milliseconds between automatically proceeding to the
  // next slide, disabled when set to 0, this value can be overwritten
  // by using a data-autoslide attribute on your slides
  var autoSlide: Int = js.native

  // Stop auto-sliding after user input
  var autoSlideStoppable: Boolean = js.native

  // Enable slide navigation via mouse wheel
  var mouseWheel: Boolean = js.native

  // Hides the address bar on mobile devices
  var hideAddressBar: Boolean = js.native

  // Opens links in an iframe preview overlay
  var previewLinks: Boolean = js.native

  // Transition style // default/none/fade/slide/convex/concave/zoom
  var transition: String = js.native

  // Transition speed// default/fast/slow
  var transitionSpeed: String = js.native

  // Transition style for full page slide backgrounds // none/fade/slide/convex/concave/zoom
  var backgroundTransition: String = js.native

  // Number of slides away from the current that are visible
  var viewDistance: Int = js.native

  // Parallax background image
  var parallaxBackgroundImage: String = js.native // e.g. "'https://s3.amazonaws.com/hakim-static/reveal-js/reveal-parallax-1.jpg'"

  // Parallax background size
  var parallaxBackgroundSize: String = js.native // CSS syntax, e.g. "2100px 900px"

  // Number of pixels to move the parallax background per slide
  // - Calculated automatically unless specified
  // - Set to 0 to disable movement along an axis
  var parallaxBackgroundHorizontal: Int = js.native
  var parallaxBackgroundVertical: Int = js.native

  var math: RevealMathOptions = js.native
  var dependencies: js.Array[RevealDependency] = js.native
}