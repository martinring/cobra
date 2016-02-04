package net.flatmap.js.util

trait Subscription {
  def isCancelled: Boolean
  def cancel()
  def +(that: Subscription) = Subscription {
    this.cancel()
    that.cancel()
  }
}

object Subscription {
  def apply(f: => Unit) = {
    var cancelled = false
    new Subscription {
      def isCancelled = cancelled
      override def cancel(): Unit = if (!cancelled) {
        cancelled = true
        f
      }
    }
  }
}