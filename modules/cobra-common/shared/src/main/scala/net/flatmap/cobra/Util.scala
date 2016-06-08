package net.flatmap.cobra

package object util {

  implicit class BooleanOps(val boolean: Boolean) extends AnyVal {
    def ?[T](t: => T): Option[T] =
      if (boolean) Some(t) else None
  }
}