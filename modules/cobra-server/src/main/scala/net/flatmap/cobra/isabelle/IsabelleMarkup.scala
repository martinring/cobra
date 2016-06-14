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
    Markup.KEYWORD2 -> Set("keyword","strong"),
    Markup.KEYWORD3 -> Set("keyword","em"),
    Markup.QUASI_KEYWORD -> Set("keyword","em"),
    Markup.VERBATIM -> Set("string-2"),
    Markup.LITERAL -> Set("atom"),
    Markup.DELIMITER -> Set("operator"),
    Markup.OPERATOR -> Set("builtin"),
    Markup.TFREE -> Set("variable-3"),
    Markup.TVAR -> Set("variable-2"),
    Markup.FREE -> Set("variable"),
    Markup.SKOLEM -> Set("property"),
    Markup.BOUND -> Set("qualifier"),
    Markup.VAR -> Set("variable"),
    Markup.INNER_STRING -> Set("string-2"),
    Markup.INNER_COMMENT -> Set("comment"),
    Markup.DYNAMIC_FACT -> Set("def"),
    Markup.ANTIQUOTE -> Set("meta"))

  val classesElements = Markup.Elements(classes.keySet)

  def highlighting(snapshot: Document.Snapshot): Annotations = {
    val cs: List[Text.Info[Option[(Set[String],String)]]] = snapshot.cumulate(Text.Range(0,Int.MaxValue), Option.empty[(Set[String],String)], classesElements, _ =>
      {
        case (_, Text.Info(_,elem)) => Some(classes.get(elem.name).map((_,elem.body.mkString)))
      })
    cs.foldLeft(new Annotations) {
      case (as, Text.Info(range,None))    => as.plain(range.length)
      case (as, Text.Info(range,Some((c,t)))) => as.annotate(range.length, AnnotationOptions(classes = c,tooltip = Some(t)))
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
        val (states,other) = state.flatMap(_.results.iterator.map(_._2))
          .filterNot(Protocol.is_result).toList
          .partition(Protocol.is_state)

        val outputs = (states ::: other).collect {
          case XML.Elem(markup,body) if markup.name == Markup.WRITELN_MESSAGE =>
            OutputMessage(body.mkString)
          case XML.Elem(markup,body) if markup.name == Markup.INFORMATION_MESSAGE =>
            InfoMessage(body.mkString)
          case XML.Elem(markup,body) if markup.name == Markup.LEGACY_MESSAGE =>
            StateMessage(body.mkString)
          case XML.Elem(markup,body) if markup.name == Markup.STATE_MESSAGE =>
            StateMessage(body.mkString)
          case XML.Elem(markup,body) if markup.name == Markup.ERROR_MESSAGE =>
            ErrorMessage(body.mkString)
          case XML.Elem(markup,body) if markup.name == Markup.WARNING_MESSAGE =>
            WarningMessage(body.mkString)
          case XML.Elem(markup,body) =>
            WarningMessage(body.mkString)
        }
        as.annotate(cmd.length, AnnotationOptions(messages = outputs))
      } else {
        as.plain(cmd.length)
      }
    }
  }

  private val tooltip_message_elements =
    Markup.Elements(Markup.WRITELN, Markup.INFORMATION, Markup.WARNING, Markup.LEGACY, Markup.ERROR,
      Markup.BAD)

  private val tooltip_descriptions =
    Map(
      Markup.EXPRESSION -> "expression",
      Markup.CITATION -> "citation",
      Markup.TOKEN_RANGE -> "inner syntax token",
      Markup.FREE -> "free variable",
      Markup.SKOLEM -> "skolem variable",
      Markup.BOUND -> "bound variable",
      Markup.VAR -> "schematic variable",
      Markup.TFREE -> "free type variable",
      Markup.TVAR -> "schematic type variable")

  private val tooltip_elements =
    Markup.Elements(Markup.TIMING, Markup.ENTITY, Markup.SORTING,
      Markup.TYPING, Markup.ML_TYPING, Markup.ML_BREAKPOINT, /*Markup.PATH, */Markup.DOC,
      Markup.URL, Markup.MARKDOWN_PARAGRAPH, Markup.Markdown_List.name) ++
      Markup.Elements(tooltip_descriptions.keySet)

  private def pretty_typing(kind: String, body: XML.Body): XML.Tree =
    Pretty.block(XML.Text(kind) :: Pretty.brk(1) :: body)

  def tooltip(snapshot: Document.Snapshot, range: Text.Range): Option[Text.Info[XML.Body]] =
  {
    def add(prev: Text.Info[(Timing, Vector[(Boolean, XML.Tree)])],
            r0: Text.Range, p: (Boolean, XML.Tree)): Text.Info[(Timing, Vector[(Boolean, XML.Tree)])] =
    {
      val r = snapshot.convert(r0)
      val (t, info) = prev.info
      if (prev.range == r)
        Text.Info(r, (t, info :+ p))
      else Text.Info(r, (t, Vector(p)))
    }

    val results =
      snapshot.cumulate[Text.Info[(Timing, Vector[(Boolean, XML.Tree)])]](
        range, Text.Info(range, (Timing.zero, Vector.empty)), tooltip_elements, _ =>
        {
          case (Text.Info(r, (t1, info)), Text.Info(_, XML.Elem(Markup.Timing(t2), _))) =>
            Some(Text.Info(r, (t1 + t2, info)))

          case (prev, Text.Info(r, XML.Elem(Markup.Entity(kind, name), _))) =>
            val kind1 = Word.implode(Word.explode('_', kind))
            val txt1 =
              if (name == "") kind1
              else kind1 + " " + quote(name)
            val t = prev.info._1
            val txt2 =
              if (kind == Markup.COMMAND && t.elapsed.seconds >= 5)
                "\n" + t.message
              else ""
            Some(add(prev, r, (true, XML.Text(txt1 + txt2))))

          /*case (prev, Text.Info(r, XML.Elem(Markup.Path(name), _))) =>
            val file = jedit_file(name)
            val text =
              if (name == file) "file " + quote(file)
              else "path " + quote(name) + "\nfile " + quote(file)
            Some(add(prev, r, (true, XML.Text(text))))*/

          case (prev, Text.Info(r, XML.Elem(Markup.Doc(name), _))) =>
            val text = "doc " + quote(name)
            Some(add(prev, r, (true, XML.Text(text))))

          case (prev, Text.Info(r, XML.Elem(Markup.Url(name), _))) =>
            Some(add(prev, r, (true, XML.Text("URL " + quote(name)))))

          case (prev, Text.Info(r, XML.Elem(Markup(name, _), body)))
            if name == Markup.SORTING || name == Markup.TYPING =>
            Some(add(prev, r, (true, pretty_typing("::", body))))

          case (prev, Text.Info(r, XML.Elem(Markup(Markup.ML_TYPING, _), body))) =>
            Some(add(prev, r, (false, pretty_typing("ML:", body))))

          case (prev, Text.Info(r, Protocol.ML_Breakpoint(breakpoint))) =>
            val text =
              if (Debugger.breakpoint_state(breakpoint)) "breakpoint (enabled)"
              else "breakpoint (disabled)"
            Some(add(prev, r, (true, XML.Text(text))))
          /*case (prev, Text.Info(r, XML.Elem(Markup.Language(language, _, _, _), _))) =>
            Some(add(prev, r, (true, XML.Text("language: " + language))))*/

          case (prev, Text.Info(r, XML.Elem(Markup(Markup.MARKDOWN_PARAGRAPH, _), _))) =>
            Some(add(prev, r, (true, XML.Text("Markdown: paragraph"))))
          case (prev, Text.Info(r, XML.Elem(Markup.Markdown_List(kind), _))) =>
            Some(add(prev, r, (true, XML.Text("Markdown: " + kind))))

          case (prev, Text.Info(r, XML.Elem(Markup(name, _), _))) =>
            tooltip_descriptions.get(name).
              map(descr => add(prev, r, (true, XML.Text(descr))))
        }).map(_.info)

    results.map(_.info).flatMap(res => res._2.toList) match {
      case Nil => None
      case tips =>
        val r = Text.Range(results.head.range.start, results.last.range.stop)
        val all_tips = (tips.filter(_._1) ++ tips.filter(!_._1).lastOption.toList).map(_._2)
        Some(Text.Info(r, Library.separate(Pretty.fbrk, all_tips)))
    }
  }


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
