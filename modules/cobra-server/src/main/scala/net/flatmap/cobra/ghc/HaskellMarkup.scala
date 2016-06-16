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

package net.flatmap.cobra.ghc

import net.flatmap.collaboration._


object HaskellMarkup {
  val donts = Seq(
    "\\p{Lu}\\p{L}*\\.",
    "\"[^\"]*\"",
    "'[^']*'",
    "\\{-(?:[^-]|(?:\\-+[^-\\}]))*-+\\}",
    "--.*","\\w(?:sqrt|forall|alpha|beta|gamma|pi)","(?:sqrt|forall|alpha|beta|gamma|pi)\\w")

  val substs = Map(
    "."  -> "∘",
    "/=" -> "≠",
    "<=" -> "≤",
    ">=" -> "≥",
    "\\" -> "λ",
    "&&" -> "∧",
    "||" -> "∨",
    "forall" -> "∀",
    "alpha" -> "α",
    "beta" -> "β",
    "gamma" -> "γ",
    "pi" -> "π",
    "sqrt" -> "√")

  val r = ("(?:" + (donts ++ substs.keys.map(_.replace("\\","\\\\").replace(".", "\\.").replace("|","\\|"))).reduce(_ + "|" + _) + ")").r

  def substitutions(state: String, as: Annotations = new Annotations): Annotations = state match {
    case "" => as
    case _  => r.findFirstMatchIn(state) match {
      case Some(m) =>
        if (substs.isDefinedAt(m.matched))
          substitutions(state.drop(m.end), as.plain(m.start).annotate(m.end - m.start, AnnotationOptions(classes = Set("symbol"), substitute = Some(m.matched))))
        else
          substitutions(state.drop(m.end), as.plain(m.end))
      case None => as.plain(state.length())
    }
  }

  def toAnnotations(errors: List[((Int,Int),String,String)], state: String): Annotations = {
    var result = new Annotations
    val lines = state.split("\n").map(_.length() + 1).toList

    def offset(line: Int, ch: Int): Int =
      lines.take(line-1).reduceOption(_ + _).getOrElse(0) + ch

    var position = 0

    errors.map({
      case ((l,c),t,e) => (offset(l,c),t,e)
    }).sortBy(_._1).foreach {
      case (o,t,e) =>
        if (o > position) {
          result = result.plain(o - position)
          position = o
        }
        t match {
          case "Error" =>
            result = result.annotate(0, AnnotationOptions(messages = List(ErrorMessage(e))))
          case "Warning" =>
            result = result.annotate(0, AnnotationOptions(messages = List(WarningMessage(e))))
          case other =>
            result = result.annotate(0, AnnotationOptions(messages = List(InfoMessage(other + ": " + e))))
        }
    }
    result.plain(state.length - position)
  }
}