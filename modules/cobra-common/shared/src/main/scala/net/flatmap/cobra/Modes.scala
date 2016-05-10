package net.flatmap.cobra

import scala.util.matching.Regex

object Comments {
  def line(start: String) = new Regex(s"^\\s*$start\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$$")
  def block(start: String, end: String) = new Regex(s"^\\s*$start\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$end\\s*$$")
}