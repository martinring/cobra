package net.flatmap.cobra.isabelle

import net.flatmap.js.codemirror.{CodeMirror, EditorChange, Position}
import net.flatmap.js.util._
import org.scalajs.dom.raw.HTMLDivElement

import scala.collection.SortedMap

object Symbols {

  val names = Map(
    "zero" -> 0x01d7ec,
    "one" -> 0x01d7ed,
    "two" -> 0x01d7ee,
    "three" -> 0x01d7ef,
    "four" -> 0x01d7f0,
    "five" -> 0x01d7f1,
    "six" -> 0x01d7f2,
    "seven" -> 0x01d7f3,
    "eight" -> 0x01d7f4,
    "nine" -> 0x01d7f5,
    "A" -> 0x01d49c,
    "B" -> 0x00212c,
    "C" -> 0x01d49e,
    "D" -> 0x01d49f,
    "E" -> 0x002130,
    "F" -> 0x002131,
    "G" -> 0x01d4a2,
    "H" -> 0x00210b,
    "I" -> 0x002110,
    "J" -> 0x01d4a5,
    "K" -> 0x01d4a6,
    "L" -> 0x002112,
    "M" -> 0x002133,
    "N" -> 0x01d4a9,
    "O" -> 0x01d4aa,
    "P" -> 0x01d4ab,
    "Q" -> 0x01d4ac,
    "R" -> 0x00211b,
    "S" -> 0x01d4ae,
    "T" -> 0x01d4af,
    "U" -> 0x01d4b0,
    "V" -> 0x01d4b1,
    "W" -> 0x01d4b2,
    "X" -> 0x01d4b3,
    "Y" -> 0x01d4b4,
    "Z" -> 0x01d4b5,
    "a" -> 0x01d5ba,
    "b" -> 0x01d5bb,
    "c" -> 0x01d5bc,
    "d" -> 0x01d5bd,
    "e" -> 0x01d5be,
    "f" -> 0x01d5bf,
    "g" -> 0x01d5c0,
    "h" -> 0x01d5c1,
    "i" -> 0x01d5c2,
    "j" -> 0x01d5c3,
    "k" -> 0x01d5c4,
    "l" -> 0x01d5c5,
    "m" -> 0x01d5c6,
    "n" -> 0x01d5c7,
    "o" -> 0x01d5c8,
    "p" -> 0x01d5c9,
    "q" -> 0x01d5ca,
    "r" -> 0x01d5cb,
    "s" -> 0x01d5cc,
    "t" -> 0x01d5cd,
    "u" -> 0x01d5ce,
    "v" -> 0x01d5cf,
    "w" -> 0x01d5d0,
    "x" -> 0x01d5d1,
    "y" -> 0x01d5d2,
    "z" -> 0x01d5d3,
    "AA" -> 0x01d504,
    "BB" -> 0x01d505,
    "CC" -> 0x00212d,
    "DD" -> 0x01d507,
    "EE" -> 0x01d508,
    "FF" -> 0x01d509,
    "GG" -> 0x01d50a,
    "HH" -> 0x00210c,
    "II" -> 0x002111,
    "JJ" -> 0x01d50d,
    "KK" -> 0x01d50e,
    "LL" -> 0x01d50f,
    "MM" -> 0x01d510,
    "NN" -> 0x01d511,
    "OO" -> 0x01d512,
    "PP" -> 0x01d513,
    "QQ" -> 0x01d514,
    "RR" -> 0x00211c,
    "SS" -> 0x01d516,
    "TT" -> 0x01d517,
    "UU" -> 0x01d518,
    "VV" -> 0x01d519,
    "WW" -> 0x01d51a,
    "XX" -> 0x01d51b,
    "YY" -> 0x01d51c,
    "ZZ" -> 0x002128,
    "aa" -> 0x01d51e,
    "bb" -> 0x01d51f,
    "cc" -> 0x01d520,
    "dd" -> 0x01d521,
    "ee" -> 0x01d522,
    "ff" -> 0x01d523,
    "gg" -> 0x01d524,
    "hh" -> 0x01d525,
    "ii" -> 0x01d526,
    "jj" -> 0x01d527,
    "kk" -> 0x01d528,
    "ll" -> 0x01d529,
    "mm" -> 0x01d52a,
    "nn" -> 0x01d52b,
    "oo" -> 0x01d52c,
    "pp" -> 0x01d52d,
    "qq" -> 0x01d52e,
    "rr" -> 0x01d52f,
    "ss" -> 0x01d530,
    "tt" -> 0x01d531,
    "uu" -> 0x01d532,
    "vv" -> 0x01d533,
    "ww" -> 0x01d534,
    "xx" -> 0x01d535,
    "yy" -> 0x01d536,
    "zz" -> 0x01d537,
    "alpha" -> 0x0003b1,
    "beta" -> 0x0003b2,
    "gamma" -> 0x0003b3,
    "delta" -> 0x0003b4,
    "epsilon" -> 0x0003b5,
    "zeta" -> 0x0003b6,
    "eta" -> 0x0003b7,
    "theta" -> 0x0003b8,
    "iota" -> 0x0003b9,
    "kappa" -> 0x0003ba,
    "lambda" -> 0x0003bb,
    "mu" -> 0x0003bc,
    "nu" -> 0x0003bd,
    "xi" -> 0x0003be,
    "pi" -> 0x0003c0,
    "rho" -> 0x0003c1,
    "sigma" -> 0x0003c3,
    "tau" -> 0x0003c4,
    "upsilon" -> 0x0003c5,
    "phi" -> 0x0003c6,
    "chi" -> 0x0003c7,
    "psi" -> 0x0003c8,
    "omega" -> 0x0003c9,
    "Gamma" -> 0x000393,
    "Delta" -> 0x000394,
    "Theta" -> 0x000398,
    "Lambda" -> 0x00039b,
    "Xi" -> 0x00039e,
    "Pi" -> 0x0003a0,
    "Sigma" -> 0x0003a3,
    "Upsilon" -> 0x0003a5,
    "Phi" -> 0x0003a6,
    "Psi" -> 0x0003a8,
    "Omega" -> 0x0003a9,
    "bool" -> 0x01d539,
    "complex" -> 0x002102,
    "nat" -> 0x002115,
    "rat" -> 0x00211a,
    "real" -> 0x00211d,
    "int" -> 0x002124,
    "leftarrow" -> 0x002190,
    "longleftarrow" -> 0x0027f5,
    "longlongleftarrow" -> 0x00290e,
    "longlonglongleftarrow" -> 0x0021e0,
    "rightarrow" -> 0x002192,
    "longrightarrow" -> 0x0027f6,
    "longlongrightarrow" -> 0x00290f,
    "longlonglongrightarrow" -> 0x0021e2,
    "Leftarrow" -> 0x0021d0,
    "Longleftarrow" -> 0x0027f8,
    "Lleftarrow" -> 0x0021da,
    "Rightarrow" -> 0x0021d2,
    "Longrightarrow" -> 0x0027f9,
    "Rrightarrow" -> 0x0021db,
    "leftrightarrow" -> 0x002194,
    "longleftrightarrow" -> 0x0027f7,
    "Leftrightarrow" -> 0x0021d4,
    "Longleftrightarrow" -> 0x0027fa,
    "mapsto" -> 0x0021a6,
    "longmapsto" -> 0x0027fc,
    "midarrow" -> 0x002500,
    "Midarrow" -> 0x002550,
    "hookleftarrow" -> 0x0021a9,
    "hookrightarrow" -> 0x0021aa,
    "leftharpoondown" -> 0x0021bd,
    "rightharpoondown" -> 0x0021c1,
    "leftharpoonup" -> 0x0021bc,
    "rightharpoonup" -> 0x0021c0,
    "rightleftharpoons" -> 0x0021cc,
    "leadsto" -> 0x00219d,
    "downharpoonleft" -> 0x0021c3,
    "downharpoonright" -> 0x0021c2,
    "upharpoonleft" -> 0x0021bf,
    "restriction" -> 0x0021be,
    "Colon" -> 0x002237,
    "up" -> 0x002191,
    "Up" -> 0x0021d1,
    "down" -> 0x002193,
    "Down" -> 0x0021d3,
    "updown" -> 0x002195,
    "Updown" -> 0x0021d5,
    "langle" -> 0x0027e8,
    "rangle" -> 0x0027e9,
    "lceil" -> 0x002308,
    "rceil" -> 0x002309,
    "lfloor" -> 0x00230a,
    "rfloor" -> 0x00230b,
    "lparr" -> 0x002987,
    "rparr" -> 0x002988,
    "lbrakk" -> 0x0027e6,
    "rbrakk" -> 0x0027e7,
    "lbrace" -> 0x002983,
    "rbrace" -> 0x002984,
    "guillemotleft" -> 0x0000ab,
    "guillemotright" -> 0x0000bb,
    "bottom" -> 0x0022a5,
    "top" -> 0x0022a4,
    "and" -> 0x002227,
    "And" -> 0x0022c0,
    "or" -> 0x002228,
    "Or" -> 0x0022c1,
    "forall" -> 0x002200,
    "exists" -> 0x002203,
    "nexists" -> 0x002204,
    "not" -> 0x0000ac,
    "box" -> 0x0025a1,
    "diamond" -> 0x0025c7,
    "diamondop" -> 0x0022c4,
    "turnstile" -> 0x0022a2,
    "Turnstile" -> 0x0022a8,
    "tturnstile" -> 0x0022a9,
    "TTurnstile" -> 0x0022ab,
    "stileturn" -> 0x0022a3,
    "surd" -> 0x00221a,
    "le" -> 0x002264,
    "ge" -> 0x002265,
    "lless" -> 0x00226a,
    "ggreater" -> 0x00226b,
    "lesssim" -> 0x002272,
    "greatersim" -> 0x002273,
    "lessapprox" -> 0x002a85,
    "greaterapprox" -> 0x002a86,
    "in" -> 0x002208,
    "notin" -> 0x002209,
    "subset" -> 0x002282,
    "supset" -> 0x002283,
    "subseteq" -> 0x002286,
    "supseteq" -> 0x002287,
    "sqsubset" -> 0x00228f,
    "sqsupset" -> 0x002290,
    "sqsubseteq" -> 0x002291,
    "sqsupseteq" -> 0x002292,
    "inter" -> 0x002229,
    "Inter" -> 0x0022c2,
    "union" -> 0x00222a,
    "Union" -> 0x0022c3,
    "squnion" -> 0x002294,
    "Squnion" -> 0x002a06,
    "sqinter" -> 0x002293,
    "Sqinter" -> 0x002a05,
    "setminus" -> 0x002216,
    "propto" -> 0x00221d,
    "uplus" -> 0x00228e,
    "Uplus" -> 0x002a04,
    "noteq" -> 0x002260,
    "sim" -> 0x00223c,
    "doteq" -> 0x002250,
    "simeq" -> 0x002243,
    "approx" -> 0x002248,
    "asymp" -> 0x00224d,
    "cong" -> 0x002245,
    "smile" -> 0x002323,
    "equiv" -> 0x002261,
    "frown" -> 0x002322,
    "Join" -> 0x0022c8,
    "bowtie" -> 0x002a1d,
    "prec" -> 0x00227a,
    "succ" -> 0x00227b,
    "preceq" -> 0x00227c,
    "succeq" -> 0x00227d,
    "parallel" -> 0x002225,
    "bar" -> 0x0000a6,
    "plusminus" -> 0x0000b1,
    "minusplus" -> 0x002213,
    "times" -> 0x0000d7,
    "div" -> 0x0000f7,
    "cdot" -> 0x0022c5,
    "star" -> 0x0022c6,
    "bullet" -> 0x002219,
    "circ" -> 0x002218,
    "dagger" -> 0x002020,
    "ddagger" -> 0x002021,
    "lhd" -> 0x0022b2,
    "rhd" -> 0x0022b3,
    "unlhd" -> 0x0022b4,
    "unrhd" -> 0x0022b5,
    "triangleleft" -> 0x0025c3,
    "triangleright" -> 0x0025b9,
    "triangle" -> 0x0025b3,
    "triangleq" -> 0x00225c,
    "oplus" -> 0x002295,
    "Oplus" -> 0x002a01,
    "otimes" -> 0x002297,
    "Otimes" -> 0x002a02,
    "odot" -> 0x002299,
    "Odot" -> 0x002a00,
    "ominus" -> 0x002296,
    "oslash" -> 0x002298,
    "dots" -> 0x002026,
    "cdots" -> 0x0022ef,
    "Sum" -> 0x002211,
    "Prod" -> 0x00220f,
    "Coprod" -> 0x002210,
    "infinity" -> 0x00221e,
    "integral" -> 0x00222b,
    "ointegral" -> 0x00222e,
    "clubsuit" -> 0x002663,
    "diamondsuit" -> 0x002662,
    "heartsuit" -> 0x002661,
    "spadesuit" -> 0x002660,
    "aleph" -> 0x002135,
    "emptyset" -> 0x002205,
    "nabla" -> 0x002207,
    "partial" -> 0x002202,
    "flat" -> 0x00266d,
    "natural" -> 0x00266e,
    "sharp" -> 0x00266f,
    "angle" -> 0x002220,
    "copyright" -> 0x0000a9,
    "registered" -> 0x0000ae,
    "hyphen" -> 0x0000ad,
    "inverse" -> 0x0000af,
    "onequarter" -> 0x0000bc,
    "onehalf" -> 0x0000bd,
    "threequarters" -> 0x0000be,
    "ordfeminine" -> 0x0000aa,
    "ordmasculine" -> 0x0000ba,
    "section" -> 0x0000a7,
    "paragraph" -> 0x0000b6,
    "exclamdown" -> 0x0000a1,
    "questiondown" -> 0x0000bf,
    "euro" -> 0x0020ac,
    "pounds" -> 0x0000a3,
    "yen" -> 0x0000a5,
    "cent" -> 0x0000a2,
    "currency" -> 0x0000a4,
    "degree" -> 0x0000b0,
    "amalg" -> 0x002a3f,
    "mho" -> 0x002127,
    "lozenge" -> 0x0025ca,
    "wp" -> 0x002118,
    "wrong" -> 0x002240,
    "acute" -> 0x0000b4,
    "index" -> 0x000131,
    "dieresis" -> 0x0000a8,
    "cedilla" -> 0x0000b8,
    "hungarumlaut" -> 0x0002dd,
    "bind" -> 0x00291c,
    "then" -> 0x002aa2,
    "some" -> 0x0003f5,
    "hole" -> 0x002311,
    "newline" -> 0x0023ce,
    "comment" -> 0x002015,
    "open" -> 0x002039,
    "close" -> 0x00203a,
    "here" -> 0x002302,
    "^undefined" -> 0x002756,
    "^noindent" -> 0x0021e4,
    "^smallskip" -> 0x002508,
    "^medskip" -> 0x002509,
    "^bigskip" -> 0x002501,
    "^item" -> 0x0025aa,
    "^enum" -> 0x0025b8,
    "^descr" -> 0x0027a7,
    "^footnote" -> 0x00204b,
    "^verbatim" -> 0x0025a9,
    "^theory_text" -> 0x002b1a,
    "^emph" -> 0x002217,
    "^bold" -> 0x002759,
    "^sub" -> 0x0021e9,
    "^sup" -> 0x0021e7,
    "^bsub" -> 0x0021d8,
    "^esub" -> 0x0021d9,
    "^bsup" -> 0x0021d7,
    "^esup" -> 0x0021d6
  ).mapValues(i => new String(Character.toChars(i)))

  val abbrevs = Seq(
    "<." ->("\\<leftarrow>", "\u2190"),
    "<." ->("\\<longleftarrow>", "\u27f5"),
    "<." ->("\\<longlongleftarrow>", "\u290e"),
    "<." ->("\\<longlonglongleftarrow>", "\u21e0"),
    ".>" ->("\\<rightarrow>", "\u2192"),
    "->" ->("\\<rightarrow>", "\u2192"),
    ".>" ->("\\<longrightarrow>", "\u27f6"),
    "-->" ->("\\<longrightarrow>", "\u27f6"),
    ".>" ->("\\<longlongrightarrow>", "\u290f"),
    "--->" ->("\\<longlongrightarrow>", "\u290f"),
    ".>" ->("\\<longlonglongrightarrow>", "\u21e2"),
    "--->" ->("\\<longlonglongrightarrow>", "\u21e2"),
    "<." ->("\\<Leftarrow>", "\u21d0"),
    "<." ->("\\<Longleftarrow>", "\u27f8"),
    "<." ->("\\<Lleftarrow>", "\u21da"),
    ".>" ->("\\<Rightarrow>", "\u21d2"),
    "=>" ->("\\<Rightarrow>", "\u21d2"),
    ".>" ->("\\<Longrightarrow>", "\u27f9"),
    "==>" ->("\\<Longrightarrow>", "\u27f9"),
    ".>" ->("\\<Rrightarrow>", "\u21db"),
    "<>" ->("\\<leftrightarrow>", "\u2194"),
    "<->" ->("\\<leftrightarrow>", "\u2194"),
    "<>" ->("\\<longleftrightarrow>", "\u27f7"),
    "<->" ->("\\<longleftrightarrow>", "\u27f7"),
    "<>" ->("\\<longleftrightarrow>", "\u27f7"),
    "<-->" ->("\\<longleftrightarrow>", "\u27f7"),
    "<>" ->("\\<Leftrightarrow>", "\u21d4"),
    "<>" ->("\\<Longleftrightarrow>", "\u27fa"),
    ".>" ->("\\<mapsto>", "\u21a6"),
    "|->" ->("\\<mapsto>", "\u21a6"),
    ".>" ->("\\<longmapsto>", "\u27fc"),
    "|-->" ->("\\<longmapsto>", "\u27fc"),
    "<>" ->("\\<midarrow>", "\u2500"),
    "<>" ->("\\<Midarrow>", "\u2550"),
    "<." ->("\\<hookleftarrow>", "\u21a9"),
    ".>" ->("\\<hookrightarrow>", "\u21aa"),
    "<." ->("\\<leftharpoondown>", "\u21bd"),
    ".>" ->("\\<rightharpoondown>", "\u21c1"),
    "<." ->("\\<leftharpoonup>", "\u21bc"),
    ".>" ->("\\<rightharpoonup>", "\u21c0"),
    "<>" ->("\\<rightleftharpoons>", "\u21cc"),
    "==" ->("\\<rightleftharpoons>", "\u21cc"),
    ".>" ->("\\<leadsto>", "\u219d"),
    "~>" ->("\\<leadsto>", "\u219d"),
    "<<" ->("\\<langle>", "\u27e8"),
    ">>" ->("\\<rangle>", "\u27e9"),
    "[." ->("\\<lceil>", "\u2308"),
    ".]" ->("\\<rceil>", "\u2309"),
    "[." ->("\\<lfloor>", "\u230a"),
    ".]" ->("\\<rfloor>", "\u230b"),
    "(|" ->("\\<lparr>", "\u2987"),
    "|)" ->("\\<rparr>", "\u2988"),
    "[|" ->("\\<lbrakk>", "\u27e6"),
    "|]" ->("\\<rbrakk>", "\u27e7"),
    "{|" ->("\\<lbrace>", "\u2983"),
    "|}" ->("\\<rbrace>", "\u2984"),
    "<<" ->("\\<guillemotleft>", "\u00ab"),
    ">>" ->("\\<guillemotright>", "\u00bb"),
    "/\\" ->("\\<and>", "\u2227"),
    "&" ->("\\<and>", "\u2227"),
    "!!" ->("\\<And>", "\u22c0"),
    "\\/" ->("\\<or>", "\u2228"),
    "|" ->("\\<or>", "\u2228"),
    "??" ->("\\<Or>", "\u22c1"),
    "!" ->("\\<forall>", "\u2200"),
    "ALL" ->("\\<forall>", "\u2200"),
    "?" ->("\\<exists>", "\u2203"),
    "EX" ->("\\<exists>", "\u2203"),
    "~?" ->("\\<nexists>", "\u2204"),
    "~" ->("\\<not>", "\u00ac"),
    "|-" ->("\\<turnstile>", "\u22a2"),
    "|=" ->("\\<Turnstile>", "\u22a8"),
    "|-" ->("\\<tturnstile>", "\u22a9"),
    "|=" ->("\\<TTurnstile>", "\u22ab"),
    "-|" ->("\\<stileturn>", "\u22a3"),
    "<=" ->("\\<le>", "\u2264"),
    ">=" ->("\\<ge>", "\u2265"),
    "<<" ->("\\<lless>", "\u226a"),
    ">>" ->("\\<ggreater>", "\u226b"),
    ":" ->("\\<in>", "\u2208"),
    "~:" ->("\\<notin>", "\u2209"),
    "(=" ->("\\<subseteq>", "\u2286"),
    ")=" ->("\\<supseteq>", "\u2287"),
    "[=" ->("\\<sqsubseteq>", "\u2291"),
    "]=" ->("\\<sqsupseteq>", "\u2292"),
    "Int" ->("\\<inter>", "\u2229"),
    "Inter" ->("\\<Inter>", "\u22c2"),
    "INT" ->("\\<Inter>", "\u22c2"),
    "Un" ->("\\<union>", "\u222a"),
    "Union" ->("\\<Union>", "\u22c3"),
    "UN" ->("\\<Union>", "\u22c3"),
    "SUP" ->("\\<Squnion>", "\u2a06"),
    "INF" ->("\\<Sqinter>", "\u2a05"),
    "~=" ->("\\<noteq>", "\u2260"),
    ".=" ->("\\<doteq>", "\u2250"),
    "==" ->("\\<equiv>", "\u2261"),
    "||" ->("\\<parallel>", "\u2225"),
    "||" ->("\\<bar>", "\u00a6"),
    "<*>" ->("\\<times>", "\u00d7"),
    "..." ->("\\<dots>", "\u2026"),
    "SUM" ->("\\<Sum>", "\u2211"),
    "PROD" ->("\\<Prod>", "\u220f"),
    ">>=" ->("\\<bind>", "\u291c"),
    ">>" ->("\\<then>", "\u2aa2"),
    "<<" ->("\\<open>", "\u2039"),
    ">>" ->("\\<close>", "\u203a"),
    "=_(" ->("\\<^bsub>", "\u21d8"),
    "=_)" ->("\\<^esub>", "\u21d9"),
    "=^(" ->("\\<^bsup>", "\u21d7"),
    "=^)" ->("\\<^esup>", "\u21d6")
  ).groupBy(_._1.last).mapValues(x => x.toSeq.sortBy(_._1.length)(Ordering[Int].reverse))

  println(abbrevs)

  def enable(editor: CodeMirror) = {
    val widget = HTML("<ul class='suggestion'></ul>").elements.head.asInstanceOf[HTMLDivElement]
    var current = Seq.empty[(Position, Position, String, String)]
    var backslashPosition = Option.empty[Position]
    var selectedIndex = 0
    def currentItem: Option[(Position,Position,String)] =
      current.lift(selectedIndex).map { case (from,to,char,sym) =>
        (from,to,sym)
      }

    def clear() = {
      widget.style.opacity = "0"
      if (current.isEmpty) backslashPosition = None
      current = Seq.empty
      selectedIndex = 0
    }

    def set(to: Position, items: Seq[(Position, Position, String, String)]) = {
      widget.innerHTML = items.take(10).zipWithIndex.map { case ((from,to,sym,char),index) =>
        val escapedChar = char.replaceAll("<","&lt;").replaceAll(">","&gt;")
        val liClass = if (index == selectedIndex) "item selected" else "item"
        s"<li class='$liClass'><span class='symbol'>$sym</span><span class='escape'>$escapedChar</span></li>"
      }.mkString
      current = items
      editor.addWidget(to, widget)
      widget.style.opacity = "1"
    }

    import scalajs.js

    editor.setOption("extraKeys", js.Dynamic.literal(

      Tab = { (cm: CodeMirror) =>
        currentItem.fold {
          cm.getDoc().replaceSelection("\t")
        } { case (from, to, subst) =>
          cm.getDoc().replaceRange(subst, from, to)
          clear()
        }
      }: js.Function1[CodeMirror, Unit],
      Enter = { (cm: CodeMirror) =>
        currentItem.fold {
          cm.getDoc().replaceSelection("\n")
        } { case (from, to, subst) =>
          cm.getDoc().replaceRange(subst, from, to)
          clear()
        }
      }: js.Function1[CodeMirror, Unit],
      Up = { (cm: CodeMirror) =>
        if (selectedIndex <= 0) {
          clear()
          cm.execCommand("goLineUp")
        } else {
          selectedIndex -= 1
          set(current.head._2,current)
        }
      }: js.Function1[CodeMirror, Unit],
      Down = { (cm: CodeMirror) =>
        if (selectedIndex >= current.length - 1) {
          clear()
          cm.execCommand("goLineDown")
        }
        else {
          selectedIndex += 1
          set(current.head._2,current)
        }
      }: js.Function1[CodeMirror, Unit],
      Right = { (cm: CodeMirror) =>
        clear()
        cm.execCommand("goCharRight")
      }: js.Function1[CodeMirror, Unit]
    ))

    editor.on("cursorActivity", {
      (cm: CodeMirror) => {
        currentItem.foreach { case (from, to, subst) =>
          val c = cm.getDoc().getCursor()
          if (cm.getDoc().somethingSelected() || c.line != to.line || c.ch != to.ch) {
            clear()
          }
        }
      }
    })

    editor.on[EditorChange]("change", {
      (cm: CodeMirror, change: EditorChange) => {
        val text = change.text.mkString("\n")
        org.scalajs.dom.console.log(change)
        val to = CodeMirror.Pos(
          change.from.line + Math.max(0, change.text.length - 1),
          if (change.text.length > 1) change.text.last.length
          else change.from.ch + change.text.headOption.map(_.length).getOrElse(0)
        )
        if (!cm.getDoc().somethingSelected() && cm.getDoc().getCursor().line == to.line && cm.getDoc().getCursor().ch == to.ch) {
          if (text.nonEmpty) {
            def getTextBeforeCursor(length: Int) = {
              val from = CodeMirror.Pos(
                change.from.line,
                Math.max(0, change.from.ch + change.text.headOption.map(_.length).getOrElse(0) - length)
              )
              editor.getDoc().getRange(from, to)
            }
            if (text.last == '\\' && getTextBeforeCursor(2) != "/\\") {
              clear()
              backslashPosition = Some(to)
            } else {
              val possible1 = backslashPosition.toSeq.flatMap { from =>
                val q = editor.getDoc().getRange(from,to)
                names.collect {
                  case (name,char) if name.startsWith(q) => (CodeMirror.Pos(from.line,from.ch-1), s"\\<$name>", char)
                }
              }
              if (possible1.isEmpty) {
                val possible = abbrevs.get(text.last).fold {
                  Seq.empty[(Position,String,String)]
                } { abbrevs =>
                  abbrevs.collect { case (abbrev, (sym, char)) if getTextBeforeCursor(abbrev.length) == abbrev  =>
                    val from = CodeMirror.Pos(
                      change.from.line,
                      Math.max(0, change.from.ch + change.text.headOption.map(_.length).getOrElse(0) - abbrev.length)
                    )
                    (from, sym, char)
                  }
                }
                if (possible.isEmpty) {
                  clear()
                } else set(to,possible.map { case (from, sym, char) =>
                  (from, to, char, sym)
                })
              } else set(to,possible1.map { case (from, sym, char) =>
                (from, to, char, sym)
              })
            }
          } else if (backslashPosition.exists(p => p.line == change.from.line && p.ch <= change.from.ch)) {
            val to = change.from
            val possible = backslashPosition.toSeq.flatMap { from =>
              val q = editor.getDoc().getRange(from,to)
              names.collect {
                case (name,char) if name.startsWith(q) => (CodeMirror.Pos(from.line,from.ch-1), s"\\<$name>", char)
              }
            }
            if (possible.isEmpty) {
              clear()
            } else set(to,possible.map { case (from, sym, char) =>
              (from, to, char, sym)
            })
          } else clear()
        } else clear()
      }
    })
  }
}