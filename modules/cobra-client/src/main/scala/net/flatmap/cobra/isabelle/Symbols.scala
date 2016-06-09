package net.flatmap.cobra.isabelle

import net.flatmap.js.codemirror.{CodeMirror, EditorChange, Position}
import net.flatmap.js.util._
import org.scalajs.dom.raw.HTMLDivElement

import scala.collection.SortedMap

object Symbols {
  val abbrevs = Map(
    "<." -> ("\\<leftarrow>", "\u2190"),
    "<." -> ("\\<longleftarrow>", "\u27f5"),
    "<." -> ("\\<longlongleftarrow>", "\u290e"),
    "<." -> ("\\<longlonglongleftarrow>", "\u21e0"),
    ".>" -> ("\\<rightarrow>", "\u2192"),
    "->" -> ("\\<rightarrow>", "\u2192"),
    ".>" -> ("\\<longrightarrow>", "\u27f6"),
    "-->" -> ("\\<longrightarrow>", "\u27f6"),
    ".>" -> ("\\<longlongrightarrow>", "\u290f"),
    "--->" -> ("\\<longlongrightarrow>", "\u290f"),
    ".>" -> ("\\<longlonglongrightarrow>", "\u21e2"),
    "--->" -> ("\\<longlonglongrightarrow>", "\u21e2"),
    "<." -> ("\\<Leftarrow>", "\u21d0"),
    "<." -> ("\\<Longleftarrow>", "\u27f8"),
    "<." -> ("\\<Lleftarrow>", "\u21da"),
    ".>" -> ("\\<Rightarrow>", "\u21d2"),
    "=>" -> ("\\<Rightarrow>", "\u21d2"),
    ".>" -> ("\\<Longrightarrow>", "\u27f9"),
    "==>" -> ("\\<Longrightarrow>", "\u27f9"),
    ".>" -> ("\\<Rrightarrow>", "\u21db"),
    "<>" -> ("\\<leftrightarrow>", "\u2194"),
    "<->" -> ("\\<leftrightarrow>", "\u2194"),
    "<>" -> ("\\<longleftrightarrow>", "\u27f7"),
    "<->" -> ("\\<longleftrightarrow>", "\u27f7"),
    "<>" -> ("\\<longleftrightarrow>", "\u27f7"),
    "<-->" -> ("\\<longleftrightarrow>", "\u27f7"),
    "<>" -> ("\\<Leftrightarrow>", "\u21d4"),
    "<>" -> ("\\<Longleftrightarrow>", "\u27fa"),
    ".>" -> ("\\<mapsto>", "\u21a6"),
    "|->" -> ("\\<mapsto>", "\u21a6"),
    ".>" -> ("\\<longmapsto>", "\u27fc"),
    "|-->" -> ("\\<longmapsto>", "\u27fc"),
    "<>" -> ("\\<midarrow>", "\u2500"),
    "<>" -> ("\\<Midarrow>", "\u2550"),
    "<." -> ("\\<hookleftarrow>", "\u21a9"),
    ".>" -> ("\\<hookrightarrow>", "\u21aa"),
    "<." -> ("\\<leftharpoondown>", "\u21bd"),
    ".>" -> ("\\<rightharpoondown>", "\u21c1"),
    "<." -> ("\\<leftharpoonup>", "\u21bc"),
    ".>" -> ("\\<rightharpoonup>", "\u21c0"),
    "<>" -> ("\\<rightleftharpoons>", "\u21cc"),
    "==" -> ("\\<rightleftharpoons>", "\u21cc"),
    ".>" -> ("\\<leadsto>", "\u219d"),
    "~>" -> ("\\<leadsto>", "\u219d"),
    "<<" -> ("\\<langle>", "\u27e8"),
    ">>" -> ("\\<rangle>", "\u27e9"),
    "[." -> ("\\<lceil>", "\u2308"),
    ".]" -> ("\\<rceil>", "\u2309"),
    "[." -> ("\\<lfloor>", "\u230a"),
    ".]" -> ("\\<rfloor>", "\u230b"),
    "(|" -> ("\\<lparr>", "\u2987"),
    "|)" -> ("\\<rparr>", "\u2988"),
    "[|" -> ("\\<lbrakk>", "\u27e6"),
    "|]" -> ("\\<rbrakk>", "\u27e7"),
    "{|" -> ("\\<lbrace>", "\u2983"),
    "|}" -> ("\\<rbrace>", "\u2984"),
    "<<" -> ("\\<guillemotleft>", "\u00ab"),
    ">>" -> ("\\<guillemotright>", "\u00bb"),
    "/\\" -> ("\\<and>", "\u2227"),
    "&" -> ("\\<and>", "\u2227"),
    "!!" -> ("\\<And>", "\u22c0"),
    "\\/" -> ("\\<or>", "\u2228"),
    "|" -> ("\\<or>", "\u2228"),
    "??" -> ("\\<Or>", "\u22c1"),
    "!" -> ("\\<forall>", "\u2200"),
    "ALL" -> ("\\<forall>", "\u2200"),
    "?" -> ("\\<exists>", "\u2203"),
    "EX" -> ("\\<exists>", "\u2203"),
    "~?" -> ("\\<nexists>", "\u2204"),
    "~" -> ("\\<not>", "\u00ac"),
    "|-" -> ("\\<turnstile>", "\u22a2"),
    "|=" -> ("\\<Turnstile>", "\u22a8"),
    "|-" -> ("\\<tturnstile>", "\u22a9"),
    "|=" -> ("\\<TTurnstile>", "\u22ab"),
    "-|" -> ("\\<stileturn>", "\u22a3"),
    "<=" -> ("\\<le>", "\u2264"),
    ">=" -> ("\\<ge>", "\u2265"),
    "<<" -> ("\\<lless>", "\u226a"),
    ">>" -> ("\\<ggreater>", "\u226b"),
    ":" -> ("\\<in>", "\u2208"),
    "~:" -> ("\\<notin>", "\u2209"),
    "(=" -> ("\\<subseteq>", "\u2286"),
    ")=" -> ("\\<supseteq>", "\u2287"),
    "[=" -> ("\\<sqsubseteq>", "\u2291"),
    "]=" -> ("\\<sqsupseteq>", "\u2292"),
    "Int" -> ("\\<inter>", "\u2229"),
    "Inter" -> ("\\<Inter>", "\u22c2"),
    "INT" -> ("\\<Inter>", "\u22c2"),
    "Un" -> ("\\<union>", "\u222a"),
    "Union" -> ("\\<Union>", "\u22c3"),
    "UN" -> ("\\<Union>", "\u22c3"),
    "SUP" -> ("\\<Squnion>", "\u2a06"),
    "INF" -> ("\\<Sqinter>", "\u2a05"),
    "~=" -> ("\\<noteq>", "\u2260"),
    ".=" -> ("\\<doteq>", "\u2250"),
    "==" -> ("\\<equiv>", "\u2261"),
    "||" -> ("\\<parallel>", "\u2225"),
    "||" -> ("\\<bar>", "\u00a6"),
    "<*>" -> ("\\<times>", "\u00d7"),
    "..." -> ("\\<dots>", "\u2026"),
    "SUM" -> ("\\<Sum>", "\u2211"),
    "PROD" -> ("\\<Prod>", "\u220f"),
    ">>=" -> ("\\<bind>", "\u291c"),
    ">>" -> ("\\<then>", "\u2aa2"),
    "<<" -> ("\\<open>", "\u2039"),
    ">>" -> ("\\<close>", "\u203a"),
    "=_(" -> ("\\<^bsub>", "\u21d8"),
    "=_)" -> ("\\<^esub>", "\u21d9"),
    "=^(" -> ("\\<^bsup>", "\u21d7"),
    "=^)" -> ("\\<^esup>", "\u21d6")
  ).groupBy(_._1.last).mapValues(x => SortedMap(x.toSeq :_*)(Ordering.by[String,Int](_.length).reverse))

  def enable(editor: CodeMirror) = {
    val widget = HTML("<div class='suggestion'></div>").elements.head.asInstanceOf[HTMLDivElement]
    var current = Option.empty[(Position,Position,String)]

    def clear() = {
      widget.style.display = "none"
      current = None
    }
    def set(from: Position, to: Position, symbol: String, subst: String) = {
      widget.style.display = "block"
      widget.innerHTML = symbol
      current = Some(from,to,subst)
      editor.addWidget(to,widget)
    }

    editor.on[EditorChange]("change", {
      (cm: CodeMirror,change: EditorChange) => {
        val text = change.text.mkString("\n")
        val to = CodeMirror.Pos(
          change.from.line + Math.max(0,change.text.length - 1),
          if (change.text.length > 1) change.text.last.length
          else change.from.ch + change.text.headOption.map(_.length).getOrElse(0)
        )
        if (!cm.getDoc().somethingSelected() && cm.getDoc().getCursor().line == to.line && cm.getDoc().getCursor().ch == to.ch && text.nonEmpty) {
          abbrevs.get(text.last).fold {
            if (text == " ") current.foreach { case (from,to,subst) =>
              cm.getDoc().replaceRange(subst,from,to)
            }
            clear()
          } { abbrevs =>
            def getTextBeforeCursor(length: Int) = {
              val from = CodeMirror.Pos(
                change.from.line,
                Math.max(0, change.from.ch + change.text.headOption.map(_.length).getOrElse(0) - length)
              )
              editor.getDoc().getRange(from, to)
            }
            abbrevs.find { case (abbrev, (sym, char)) =>
              getTextBeforeCursor(abbrev.length) == abbrev
            }.fold {
              clear()
            } { case (abbrev, (sym, char)) =>
              val from = CodeMirror.Pos(
                change.from.line,
                Math.max(0, change.from.ch + change.text.headOption.map(_.length).getOrElse(0) - abbrev.length)
              )
              set(from,to,char,sym)
            }
          }
        } else clear()
      }
    })
  }
}