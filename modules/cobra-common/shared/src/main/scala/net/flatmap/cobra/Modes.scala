package net.flatmap.cobra

import scala.util.matching.Regex

object Comments {
  def line(start: String) = new Regex(s"^\\s*$start\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$$")
  def block(start: String, end: String) = new Regex(s"^\\s*$start\\s*(begin|end)\\s*\\#(\\w[\\w\\d-_]*)\\s*$end\\s*$$")
}

sealed abstract class Mode(val name: String, val mime: String, val regex: Regex, val fileendings: Set[String])
case object Scala extends Mode("scala","text/x-scala",Comments.line("\\/\\/\\/+"), Set("scala"))
case object Haskell extends Mode("haskell","text/x-haskell",Comments.line("---+"), Set("hs"))
case object Isabelle extends Mode("isabelle","text/x-isabelle",Comments.block("\\(\\*","\\*\\)"), Set("thy"))
case object Plain extends Mode("plain","text/plain",new Regex("$^"),Set.empty)

object Mode {
  def modes = Set(Scala,Haskell,Isabelle)
}