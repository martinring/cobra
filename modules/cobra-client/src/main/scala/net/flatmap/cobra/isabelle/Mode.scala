package net.flatmap.cobra.isabelle

import net.flatmap.js.codemirror._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.RegExp
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

case class IsabelleModeState(
                              var commentLevel: Int = 0,
                              var command: String = null,
                              var tokenize: (Stream,IsabelleModeState) => String)

trait IsabelleModeConfig extends js.Object {
  var words: js.UndefOr[js.Dictionary[String]] = js.native
}

@JSExport("Regexpes")
object IsabelleMode {
  @JSExport val Rgreek       = "(?:\\\\<(?:alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|" +
    "mu|nu|xi|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega|Gamma|Delta|Theta|Lambda|Xi|" +
    "Pi|Sigma|Upsilon|Phi|Psi|Omega)>)"
  @JSExport val Rdigit       = "[0-9]"
  @JSExport val Rlatin       = "[a-zA-Z]"
  @JSExport val Rsym         = "[\\!\\#\\$\\%\\&\\*\\+\\-\\/\\<\\=\\>\\?\\@\\^\\_\\|\\~]"
  @JSExport val Rletter      = s"(?:$Rlatin|\\\\<$Rlatin{1,2}>|$Rgreek|\\\\<\\^isu[bp]>)"
  @JSExport val Rquasiletter = s"(?:$Rletter|$Rdigit|\\_|\\')"
  @JSExport val quasiletter = RegExp(Rquasiletter)
  @JSExport val Rident      = s"(?:$Rletter$Rquasiletter*)"
  @JSExport val ident       = RegExp(Rident)
  @JSExport val longident   = RegExp(s"(?:$Rident(?:\\.$Rident)+)")
  @JSExport val symident    = RegExp(s"(?:$Rsym+|\\\\<$Rident>)")
  @JSExport val Rnat        = s"(?:$Rdigit+)"
  @JSExport val nat         = RegExp(Rnat)
  @JSExport val floating    = RegExp(s"(?:-?$Rnat}\\.$Rnat)")
  @JSExport val variable    = RegExp(s"(?:\\?$Rident}(?:\\.$Rnat)?)")
  @JSExport val Rtypefree   = s"'$Rident"
  @JSExport val typefree    = RegExp(Rtypefree)
  @JSExport val typevar     = RegExp(s"\\?$Rtypefree(?:\\.$Rnat)")
  @JSExport val num         = RegExp("#?-?[0-9]+(?:\\.[0-9]+)?")
  @JSExport val escaped     = RegExp("\\[\"\\]")
  @JSExport val speciale    = RegExp("\\<[A-Za-z]+>")
  @JSExport val control     = RegExp("\\<\\^[A-Za-z]+>")
  @JSExport val incomplete  = RegExp("\\<\\^{0,1}[A-Za-z]*>?")
  @JSExport val lineComment = RegExp("--.*")

  val defaultWords = js.Dictionary(
    "." ->  "command",
    ".." -> "command",
    "Isabelle.command" -> "command",
    "Isar.begin_document" ->  "command",
    "Isar.define_command" ->  "command",
    "Isar.edit_document" -> "command",
    "Isar.end_document" ->  "command",
    "ML" -> "command",
    "ML_command" -> "command",
    "ML_prf" -> "command",
    "ML_val" -> "command",
    "ProofGeneral.inform_file_processed" -> "command",
    "ProofGeneral.inform_file_retracted" -> "command",
    "ProofGeneral.kill_proof" ->  "command",
    "ProofGeneral.pr" ->  "command",
    "ProofGeneral.process_pgip" ->  "command",
    "ProofGeneral.restart" -> "command",
    "ProofGeneral.undo" ->  "command",
    "abbreviation" -> "command",
    "also" -> "command",
    "apply" ->  "command",
    "apply_end" ->  "command",
    "arities" ->  "command",
    "assume" -> "command",
    "atom_decl" ->  "command",
    "attribute_setup" ->  "command",
    "automaton" ->  "command",
    "ax_specification" -> "command",
    "axiomatization" -> "command",
    "axioms" -> "command",
    "back" -> "command",
    "boogie_end" -> "command",
    "boogie_open" ->  "command",
    "boogie_status" ->  "command",
    "boogie_vc" ->  "command",
    "by" -> "command",
    "cannot_undo" ->  "command",
    "case" -> "command",
    "cd" -> "command",
    "chapter" ->  "command",
    "class" ->  "command",
    "class_deps" -> "command",
    "classes" ->  "command",
    "classrel" -> "command",
    "code_abort" -> "command",
    "code_class" -> "command",
    "code_const" -> "command",
    "code_datatype" ->  "command",
    "code_deps" ->  "command",
    "code_include" -> "command",
    "code_instance" ->  "command",
    "code_library" -> "command",
    "code_module" ->  "command",
    "code_modulename" ->  "command",
    "code_monad" -> "command",
    "code_pred" ->  "command",
    "code_reflect" -> "command",
    "code_reserved" ->  "command",
    "code_thms" ->  "command",
    "code_type" ->  "command",
    "coinductive" ->  "command",
    "coinductive_set" ->  "command",
    "commit" -> "command",
    "constdefs" ->  "command",
    "consts" -> "command",
    "consts_code" ->  "command",
    "context" ->  "command",
    "corollary" ->  "command",
    "cpodef" -> "command",
    "datatype" -> "command",
    "declaration" ->  "command",
    "declare" ->  "command",
    "def" ->  "command",
    "default_sort" -> "command",
    "defer" ->  "command",
    "defer_recdef" -> "command",
    "definition" -> "command",
    "defs" -> "command",
    "disable_pr" -> "command",
    "display_drafts" -> "command",
    "domain" -> "command",
    "domain_isomorphism" -> "command",
    "done" -> "command",
    "enable_pr" ->  "command",
    "end" ->  "keyword",
    "equivariance" -> "command",
    "example_proof" ->  "command",
    "exit" -> "command",
    "export_code" ->  "command",
    "extract" ->  "command",
    "extract_type" -> "command",
    "finalconsts" ->  "command",
    "finally" ->  "command",
    "find_consts" ->  "command",
    "find_theorems" ->  "command",
    "fix" ->  "command",
    "fixpat" -> "command",
    "fixrec" -> "command",
    "from" -> "command",
    "full_prf" -> "command",
    "fun" ->  "command",
    "function" -> "command",
    "global" -> "command",
    "guess" ->  "command",
    "have" -> "command",
    "header" -> "command",
    "help" -> "command",
    "hence" ->  "command",
    "hide_class" -> "command",
    "hide_const" -> "command",
    "hide_fact" ->  "command",
    "hide_type" ->  "command",
    "inductive" ->  "command",
    "inductive_cases" ->  "command",
    "inductive_set" ->  "command",
    "init_toplevel" ->  "command",
    "instance" -> "command",
    "instantiation" ->  "command",
    "interpret" ->  "command",
    "interpretation" -> "command",
    "judgment" -> "command",
    "kill" -> "command",
    "kill_thy" -> "command",
    "lemma" ->  "command",
    "lemmas" -> "command",
    "let" ->  "command",
    "linear_undo" ->  "command",
    "local" ->  "command",
    "local_setup" ->  "command",
    "locale" -> "command",
    "method_setup" -> "command",
    "moreover" -> "command",
    "new_domain" -> "command",
    "next" -> "command",
    "nitpick" ->  "command",
    "nitpick_params" -> "command",
    "no_notation" ->  "command",
    "no_syntax" ->  "command",
    "no_translations" ->  "command",
    "no_type_notation" -> "command",
    "nominal_datatype" -> "command",
    "nominal_inductive" ->  "command",
    "nominal_inductive2" -> "command",
    "nominal_primrec" ->  "command",
    "nonterminals" -> "command",
    "normal_form" ->  "command",
    "notation" -> "command",
    "note" -> "command",
    "notepad" -> "command",
    "obtain" -> "command",
    "oops" -> "command",
    "oracle" -> "command",
    "overloading" ->  "command",
    "parse_ast_translation" ->  "command",
    "parse_translation" ->  "command",
    "pcpodef" ->  "command",
    "pr" -> "command",
    "prefer" -> "command",
    "presume" ->  "command",
    "pretty_setmargin" -> "command",
    "prf" ->  "command",
    "primrec" ->  "command",
    "print_abbrevs" ->  "command",
    "print_antiquotations" -> "command",
    "print_ast_translation" ->  "command",
    "print_attributes" -> "command",
    "print_binds" ->  "command",
    "print_cases" ->  "command",
    "print_claset" -> "command",
    "print_classes" ->  "command",
    "print_codeproc" -> "command",
    "print_codesetup" ->  "command",
    "print_commands" -> "command",
    "print_configs" ->  "command",
    "print_context" ->  "command",
    "print_drafts" -> "command",
    "print_facts" ->  "command",
    "print_induct_rules" -> "command",
    "print_interps" ->  "command",
    "print_locale" -> "command",
    "print_locales" ->  "command",
    "print_methods" ->  "command",
    "print_orders" -> "command",
    "print_quotconsts" -> "command",
    "print_quotients" ->  "command",
    "print_quotmaps" -> "command",
    "print_rules" ->  "command",
    "print_simpset" ->  "command",
    "print_statement" ->  "command",
    "print_syntax" -> "command",
    "print_theorems" -> "command",
    "print_theory" -> "command",
    "print_trans_rules" ->  "command",
    "print_translation" ->  "command",
    "proof" ->  "command",
    "prop" -> "command",
    "pwd" ->  "command",
    "qed" ->  "command",
    "quickcheck" -> "command",
    "quickcheck_params" ->  "command",
    "quit" -> "command",
    "quotient_definition" ->  "command",
    "quotient_type" ->  "command",
    "realizability" ->  "command",
    "realizers" ->  "command",
    "recdef" -> "command",
    "recdef_tc" ->  "command",
    "record" -> "command",
    "refute" -> "command",
    "refute_params" ->  "command",
    "remove_thy" -> "command",
    "rep_datatype" -> "command",
    "repdef" -> "command",
    "schematic_corollary" ->  "command",
    "schematic_lemma" ->  "command",
    "schematic_theorem" ->  "command",
    "sect" -> "command",
    "section" ->  "command",
    "setup" ->  "command",
    "show" -> "command",
    "simproc_setup" ->  "command",
    "sledgehammer" -> "command",
    "sledgehammer_params" ->  "command",
    "smt_status" -> "command",
    "sorry" ->  "command",
    "specification" ->  "command",
    "statespace" -> "command",
    "subclass" -> "command",
    "sublocale" ->  "command",
    "subsect" ->  "command",
    "subsection" -> "command",
    "subsubsect" -> "command",
    "subsubsection" ->  "command",
    "syntax" -> "command",
    "term" -> "command",
    "termination" ->  "command",
    "text" -> "command",
    "text_raw" -> "command",
    "then" -> "command",
    "theorem" ->  "command",
    "theorems" -> "command",
    "theory" -> "command",
    "thm" ->  "command",
    "thm_deps" -> "command",
    "thus" -> "command",
    "thy_deps" -> "command",
    "touch_thy" ->  "command",
    "translations" -> "command",
    "txt" ->  "command",
    "txt_raw" ->  "command",
    "typ" ->  "command",
    "type_notation" ->  "command",
    "typed_print_translation" ->  "command",
    "typedecl" -> "command",
    "typedef" ->  "command",
    "types" ->  "command",
    "types_code" -> "command",
    "type_synonym" -> "command",
    "ultimately" -> "command",
    "undo" -> "command",
    "undos_proof" ->  "command",
    "unfolding" ->  "command",
    "unused_thms" ->  "command",
    "use" ->  "command",
    "use_thy" ->  "command",
    "using" ->  "command",
    "value" ->  "command",
    "values" -> "command",
    "welcome" ->  "command",
    "with" -> "command",
    "write" ->  "command",
    "{" ->  "command",
    "}" ->  "command",
    "actions" -> "keyword",
    "advanced" -> "keyword",
    "and" -> "keyword",
    "assumes" -> "keyword",
    "attach" -> "keyword",
    "avoids" -> "keyword",
    "begin" -> "keyword",
    "binder" -> "keyword",
    "compose" -> "keyword",
    "congs" -> "keyword",
    "constrains" -> "keyword",
    "contains" -> "keyword",
    "datatypes" -> "keyword",
    "defines" -> "keyword",
    "file" -> "keyword",
    "fixes" -> "keyword",
    "for" -> "keyword",
    "functions" -> "keyword",
    "hide_action" -> "keyword",
    "hints" -> "keyword",
    "identifier" -> "keyword",
    "if" -> "keyword",
    "imports" -> "keyword",
    "in" -> "keyword",
    "infix" -> "keyword",
    "infixl" -> "keyword",
    "infixr" -> "keyword",
    "initially" -> "keyword",
    "inputs" -> "keyword",
    "internals" -> "keyword",
    "is" -> "keyword",
    "lazy" -> "keyword",
    "module_name" -> "keyword",
    "monos" -> "keyword",
    "morphisms" -> "keyword",
    "notes" -> "keyword",
    "obtains" -> "keyword",
    "open" -> "keyword",
    "output" -> "keyword",
    "outputs" -> "keyword",
    "overloaded" -> "keyword",
    "permissive" -> "keyword",
    "pervasive" -> "keyword",
    "post" -> "keyword",
    "pre" -> "keyword",
    "rename" -> "keyword",
    "restrict" -> "keyword",
    "shows" -> "keyword",
    "signature" -> "keyword",
    "states" -> "keyword",
    "structure" -> "keyword",
    "to" -> "keyword",
    "transitions" -> "keyword",
    "transrel" -> "keyword",
    "unchecked" -> "keyword",
    "uses" -> "keyword",
    "where" -> "keyword"
  )

  def apply(config: CodeMirrorConfiguration, pconfig: js.Any): Mode[IsabelleModeState] = new IsabelleMode(config,pconfig.asInstanceOf[IsabelleModeConfig])
}

class IsabelleMode(config: CodeMirrorConfiguration, parserConfig: IsabelleModeConfig) extends Mode[IsabelleModeState] {
  println("Hallo")

  val words = parserConfig.words.getOrElse(IsabelleMode.defaultWords)

  def tokenBase(stream: Stream, state: IsabelleModeState): String = {
    val char = stream.peek()

    if (char == "{") {
      stream.next()
      if (stream.eat("*").isDefined) {
        state.tokenize = tokenVerbatim
        return state.tokenize(stream,state)
      } else stream.backUp(1)
    }

    state.command = null

    if (char == "\"") {
      stream.next()
      state.tokenize = tokenString
      return "string"
    }

    if (char == "`") {
      stream.next()
      state.tokenize = tokenAltString
      return state.tokenize(stream, state)
    }

    if (char == "(") {
      stream.next()
      if (stream.eat("*").isDefined) {
        state.commentLevel += 1
        state.tokenize = tokenComment
        return state.tokenize(stream, state)
      } else stream.backUp(1)
    }

    if (stream.`match`(IsabelleMode.typefree) != null) "tfree"
    else if (stream.`match`(IsabelleMode.typevar)  != null) "tvar"
    else if (stream.`match`(IsabelleMode.variable)  != null) "var"
    else if (stream.`match`(IsabelleMode.longident) != null || stream.`match`(IsabelleMode.ident) != null) {
      words.getOrElse(stream.current(), "identifier") match {
        case "command" =>
          state.command = stream.current()
          "def"
        case t => t
      }
    }
    else if (stream.`match`(IsabelleMode.symident) != null) "symbol"
    else if (stream.`match`(IsabelleMode.control) != null) "control"
    else if (stream.`match`(IsabelleMode.incomplete) != null) "incomplete"
    else {
      stream.next()
      null
    }
  }

  def tokenVerbatim(stream: Stream, state: IsabelleModeState): String = {
    if (stream.skipTo('*') exists identity) {
      stream.next()
      if (stream.eat("}").isDefined) {
        state.tokenize = tokenBase
        "verbatim" + (if (state.command != null) " " + state.command else "")
      } else state.tokenize(stream,state)
    }
    else {
      stream.skipToEnd()
      "verbatim"
    }
  }

  def tokenComment(stream: Stream, state: IsabelleModeState): String = {
    if (stream.skipTo('*') exists identity) {
      stream.next()
      if (stream.eat(")").isDefined) {
        state.tokenize = tokenBase
        "comment"
      } else state.tokenize(stream,state)
    }
    else {
      stream.skipToEnd()
      "comment"
    }
  }

  def tokenString(stream: Stream, state: IsabelleModeState): String = {
    if (stream.eatSpace() exists identity) "string"
    else if (stream.eat("\"").isDefined) {
      state.tokenize = tokenBase
      "string"
    } else if (stream.`match`(IsabelleMode.escaped) != null) "string escaped"
    else if (stream.`match`(IsabelleMode.longident) != null) "string longident"
    else if (stream.`match`(IsabelleMode.ident) != null) "string ident"
    else if (stream.`match`(IsabelleMode.typefree) != null) "string tfree"
    else if (stream.`match`(IsabelleMode.typevar) != null) "string tvar"
    else if (stream.`match`(IsabelleMode.num) != null) "string num"
    else if (stream.`match`(IsabelleMode.symident) != null) "string symbol"
    else if (stream.`match`(IsabelleMode.control) != null) "string control"
    else if (stream.`match`(IsabelleMode.incomplete) != null) "string incomplete"
    else {
      stream.next()
      "string"
    }
  }

  def tokenAltString(stream: Stream, state: IsabelleModeState): String = {
    if (stream.skipTo('`') exists identity) {
      stream.next()
      state.tokenize = tokenBase
      "alt-string"
    }
    else {
      stream.skipToEnd()
      "alt-string"
    }
  }


  def token(stream: Stream, state: IsabelleModeState): String = {
    state.tokenize(stream,state)
  }

  override def startState() = IsabelleModeState(0,null,tokenBase)
}