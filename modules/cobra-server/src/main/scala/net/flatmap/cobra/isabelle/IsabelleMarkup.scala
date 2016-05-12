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

package net.flatmap.cobra.isabelle

import isabelle._
import net.flatmap.collaboration.{Document => _, _}

object IsabelleMarkup {
  val classes = Map(
      Markup.KEYWORD1 -> "command",
      Markup.KEYWORD2 -> "keyword",
      Markup.STRING -> "string",
      Markup.ALTSTRING -> "string",
      Markup.VERBATIM -> "verbatim",
      Markup.LITERAL -> "keyword",
      Markup.DELIMITER -> "delimiter",
      Markup.TFREE -> "tfree",
      Markup.TVAR -> "tvar",
      Markup.FREE -> "free",
      Markup.SKOLEM -> "skolem",
      Markup.BOUND -> "bound",
      Markup.VAR -> "var",
      Markup.INNER_STRING -> "innerString",
      Markup.INNER_COMMENT -> "innerComment",
      Markup.DYNAMIC_FACT -> "dynamic_fact")

  val classesElements = Markup.Elements(classes.keySet)

    /* message priorities */

  private val writeln_pri = 1
  private val information_pri = 2
  private val tracing_pri = 3
  private val warning_pri = 4
  private val legacy_pri = 5
  private val error_pri = 6

  private val message_pri = Map(
    Markup.WRITELN -> writeln_pri, Markup.WRITELN_MESSAGE -> writeln_pri,
    Markup.TRACING -> tracing_pri, Markup.TRACING_MESSAGE -> tracing_pri,
    Markup.WARNING -> warning_pri, Markup.WARNING_MESSAGE -> warning_pri,
    Markup.ERROR -> error_pri, Markup.ERROR_MESSAGE -> error_pri)

  def highlighting(snapshot: Document.Snapshot): Annotations = {
    val cs: List[Text.Info[Option[String]]] = snapshot.cumulate(Text.Range(0,Int.MaxValue), Option.empty[String], classesElements, _ =>
      {
        case (_, Text.Info(_,elem)) => Some(classes.get(elem.name))
      })
    cs.foldLeft(new Annotations) {
      case (as, Text.Info(range,None))    => as.plain(range.length)
      case (as, Text.Info(range,Some(c))) => as.annotate(range.length, AnnotationOptions(classes = Set(c)))
    }
  }

  def errors(snapshot: Document.Snapshot): Annotations = {
    val es: List[Text.Info[Option[String]]] = snapshot.cumulate(Text.Range(0,Int.MaxValue), Option.empty[String], Markup.Elements(Markup.ERROR, Markup.ERROR_MESSAGE), _ =>
      {
        case (_, Text.Info(_,elem)) =>
          Some(Some(elem.toString))
      })
    es.foldLeft(new Annotations) {
      case (as, Text.Info(range,None))    => as.plain(range.length)
      case (as, Text.Info(range,_)) => as.annotate(range.length, AnnotationOptions(classes = Set("error")))
    }
  }

  def warnings(snapshot: Document.Snapshot): Annotations = {
    val ws: List[Text.Info[Option[String]]] = snapshot.cumulate(Text.Range(0,Int.MaxValue), Option.empty[String], Markup.Elements(Markup.WARNING, Markup.WARNING_MESSAGE), _ =>
      {
        case (_, Text.Info(_,elem)) =>
          Some(Some(elem.toString))
      })
    ws.foldLeft(new Annotations) {
      case (as, Text.Info(range,None))    => as.plain(range.length)
      case (as, Text.Info(range,_)) => as.annotate(range.length, AnnotationOptions(classes = Set("warning")))
    }
  }

  def output(snapshot: Document.Snapshot, positions: Set[Text.Offset]): Annotations = {
    snapshot.node.commands.foldLeft (new Annotations) {
      case (as,cmd) => if (!cmd.is_ignored) {
        val state = snapshot.state.command_states(snapshot.version, cmd)        
        val outputs = state.flatMap(_.results.iterator.map(_._2)
          .filterNot(Protocol.is_result(_))
          .collect{
            case XML.Elem(markup,body) if markup.name == Markup.WRITELN_MESSAGE =>
              OutputMessage(XML.content(body))
            case XML.Elem(markup,body) if markup.name == Markup.ERROR_MESSAGE =>
              ErrorMessage(XML.content(body)) //isabelle.Pretty.formatted(body, 120.0, isabelle.Pretty.Metric_Default).mkString("\n")
            case XML.Elem(markup,body) if markup.name == Markup.WARNING_MESSAGE =>
              WarningMessage(XML.content(body))
          })
        as.annotate(cmd.length, AnnotationOptions(messages = outputs))
      } else {
        as.plain(cmd.length)
      }
    }
  }

  private val tooltip_elements =
    Set(Markup.TIMING, Markup.ENTITY, Markup.SORTING, Markup.TYPING,
      Markup.ML_TYPING, Markup.PATH)

  private def pretty_typing(kind: String, body: XML.Body): XML.Tree =
    Pretty.block(XML.Text(kind) :: Pretty.Break(1) :: body)

  def scripts(state: String): Annotations =
    Symbol.iterator(state).foldLeft((new Annotations, false, false, false, false)){
      case ((as,sub,sup,bsub,bsup),sym) if sym.length() > 1 && Symbol.decode(sym) == Symbol.sub_decoded =>
        (as.annotate(sym.length, AnnotationOptions(substitute = Some(""))),true,false,bsub,bsup)
      case ((as,sub,sup,bsub,bsup),sym) if sym.length() > 1 && Symbol.decode(sym) == Symbol.sup_decoded =>
        (as.annotate(sym.length, AnnotationOptions(substitute = Some(""))),false,true,bsub,bsup)
      case ((as,sub,sup,bsub,bsup),sym) if sym.length() > 1 && Symbol.decode(sym) == Symbol.bsub_decoded =>
        (as.annotate(sym.length, AnnotationOptions(substitute = Some(""))),false,false,true,bsup)
      case ((as,sub,sup,bsub,bsup),sym) if sym.length() > 1 && Symbol.decode(sym) == Symbol.bsup_decoded =>
        (as.annotate(sym.length, AnnotationOptions(substitute = Some(""))),false,false,bsub,true)
      case ((as,sub,sup,bsub,bsup),sym) if sym.length() > 1 && Symbol.decode(sym) == Symbol.esub_decoded =>
        (as.annotate(sym.length, AnnotationOptions(substitute = Some(""))),false,false,false,bsup)
      case ((as,sub,sup,bsub,bsup),sym) if sym.length() > 1 && Symbol.decode(sym) == Symbol.esup_decoded =>
        (as.annotate(sym.length, AnnotationOptions(substitute = Some(""))),false,false,bsub,false)
      case ((as,true,sup,bsub,bsup),sym) =>
        (as.annotate(sym.length(), AnnotationOptions(classes = Set("sub"))),false,false,bsub,bsup)
      case ((as,sub,true,bsub,bsup),sym) =>
        (as.annotate(sym.length(), AnnotationOptions(classes = Set("sup"))),false,false,bsub,bsup)
      case ((as,sub,sup,true,bsup),sym) =>
        (as.annotate(sym.length(), AnnotationOptions(classes = Set("sub"))),false,false,true,bsup)
      case ((as,sub,sup,bsub,true),sym) =>
        (as.annotate(sym.length(), AnnotationOptions(classes = Set("sup"))),false,false,bsub,true)
      case ((as,sub,sup,bsub,bsup),sym) =>
        (as.plain(sym.length),sub,sup,bsub,bsup)
    }._1

  def substitutions(state: String): Annotations =
    Symbol.iterator(state).foldLeft(new Annotations) {
      case (as, sym) if sym.length == 1 || Symbol.decode(sym) == sym =>
        as.plain(sym.length)
      case (as, sym) =>
        as.annotate(sym.length, AnnotationOptions(classes = Set("symbol"), substitute = Some(Symbol.decode(sym))))
    }


  /*def progress(state: String, snapshot: Document.Snapshot): Annotations = {
    var offset = 0
    val it = state.linesWithSeparators
    var result = new Annotations
    while (it.hasNext) {
      val line = it.next()
      overview_class(snapshot, Text.Range(offset, offset + line.length())) match {
        case None => result = result.plain(line.length())
        case Some(c) => result = result.annotate(line.length, List(AnnotationType.Progress -> c))
      }
      offset += line.length()
    }
    result
  }*/

  private val overview_include = Markup.Elements(Markup.WARNING, Markup.ERROR, Markup.RUNNING, Markup.ACCEPTED, Markup.FAILED)

  def overview_class(snapshot: Document.Snapshot, range: Text.Range): Option[String] =
  {
    if (snapshot.is_outdated) None
    else {
      val results =
        snapshot.cumulate[List[Markup]](
          range, Nil, Protocol.liberal_status_elements, _ =>
          {
            case (status, Text.Info(_, elem)) => Some(elem.markup :: status) 
          }, status = true)
      if (results.isEmpty) None
      else {
        val status = Protocol.Status.make(results.iterator.flatMap(_.info))         
        if (status.is_running) Some("running")
        else if (status.is_failed) Some("failed")
        else if (status.is_warned) Some("warning")
        else if (status.is_unprocessed) Some("pending")        
        else None
      }
    }
  }

}
