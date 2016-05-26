package net.flatmap.cobra.isabelle

import net.flatmap.js.codemirror._
import scala.scalajs.js.annotation.{ScalaJSDefined, JSExport, JSExportAll}
import scala.scalajs.js.RegExp
import scala.scalajs.js

case class IsabelleModeState(
                              var commentLevel: Int = 0,
                              var command: String = null,
                              var tokenize: (Stream,IsabelleModeState) => String)

trait IsabelleModeConfig extends js.Object {
  var words: js.UndefOr[js.Dictionary[String]] = js.native
}

object IsabelleMode {
  val Rgreek       = "(?:\\\\<(?:alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|" +
    "mu|nu|xi|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega|Gamma|Delta|Theta|Lambda|Xi|" +
    "Pi|Sigma|Upsilon|Phi|Psi|Omega)>)"
  val Rdigit       = "[0-9]"
  val Rlatin       = "[a-zA-Z]"
  val Rsym         = "[\\!\\#\\$\\%\\&\\*\\+\\-\\/\\<\\=\\>\\?\\@\\^\\_\\|\\~]"
  val Rletter      = s"(?:$Rlatin|\\\\<$Rlatin{1,2}>|$Rgreek|\\\\<\\^isu[bp]>)"
  val Rquasiletter = s"(?:$Rletter|$Rdigit|\\_|\\')"
  val quasiletter = RegExp(Rquasiletter)
  val Rident      = s"(?:$Rletter$Rquasiletter*)"
  val ident       = RegExp(Rident)
  val longident   = RegExp(s"(?:$Rident(?:\\.$Rident)+)")
  val symident    = RegExp(s"(?:$Rsym+|\\\\<$Rident>)")
  val Rnat        = s"(?:$Rdigit+)"
  val nat         = RegExp(Rnat)
  val floating    = RegExp(s"(?:-?$Rnat}\\.$Rnat)")
  val variable    = RegExp(s"(?:\\?$Rident}(?:\\.$Rnat)?)")
  val Rtypefree   = s"'$Rident"
  val typefree    = RegExp(Rtypefree)
  val typevar     = RegExp(s"\\?$Rtypefree(?:\\.$Rnat)")
  val num         = RegExp("#?-?[0-9]+(?:\\.[0-9]+)?")
  val escaped     = RegExp("\\[\"\\]")
  val speciale    = RegExp("\\<[A-Za-z]+>")
  val control     = RegExp("\\<\\^[A-Za-z]+>")
  val incomplete  = RegExp("\\<\\^{0,1}[A-Za-z]*>?")
  val lineComment = RegExp("--.*")

  val defaultWords = js.Dictionary(
    "." ->  "keyword",
    ".." -> "keyword",
    "Isabelle.command" -> "keyword",
    "Isar.begin_document" ->  "keyword",
    "Isar.define_command" ->  "keyword",
    "Isar.edit_document" -> "keyword",
    "Isar.end_document" ->  "keyword",
    "ML" -> "keyword",
    "ML_command" -> "keyword",
    "ML_prf" -> "keyword",
    "ML_val" -> "keyword",
    "ProofGeneral.inform_file_processed" -> "keyword",
    "ProofGeneral.inform_file_retracted" -> "keyword",
    "ProofGeneral.kill_proof" ->  "keyword",
    "ProofGeneral.pr" ->  "keyword",
    "ProofGeneral.process_pgip" ->  "keyword",
    "ProofGeneral.restart" -> "keyword",
    "ProofGeneral.undo" ->  "keyword",
    "abbreviation" -> "keyword",
    "also" -> "keyword",
    "apply" ->  "keyword",
    "apply_end" ->  "keyword",
    "arities" ->  "keyword",
    "assume" -> "keyword",
    "atom_decl" ->  "keyword",
    "attribute_setup" ->  "keyword",
    "automaton" ->  "keyword",
    "ax_specification" -> "keyword",
    "axiomatization" -> "keyword",
    "axioms" -> "keyword",
    "back" -> "keyword",
    "boogie_end" -> "keyword",
    "boogie_open" ->  "keyword",
    "boogie_status" ->  "keyword",
    "boogie_vc" ->  "keyword",
    "by" -> "keyword",
    "cannot_undo" ->  "keyword",
    "case" -> "keyword",
    "cd" -> "keyword",
    "chapter" ->  "keyword",
    "class" ->  "keyword",
    "class_deps" -> "keyword",
    "classes" ->  "keyword",
    "classrel" -> "keyword",
    "code_abort" -> "keyword",
    "code_class" -> "keyword",
    "code_const" -> "keyword",
    "code_datatype" ->  "keyword",
    "code_deps" ->  "keyword",
    "code_include" -> "keyword",
    "code_instance" ->  "keyword",
    "code_library" -> "keyword",
    "code_module" ->  "keyword",
    "code_modulename" ->  "keyword",
    "code_monad" -> "keyword",
    "code_pred" ->  "keyword",
    "code_reflect" -> "keyword",
    "code_reserved" ->  "keyword",
    "code_thms" ->  "keyword",
    "code_type" ->  "keyword",
    "coinductive" ->  "keyword",
    "coinductive_set" ->  "keyword",
    "commit" -> "keyword",
    "constdefs" ->  "keyword",
    "consts" -> "keyword",
    "consts_code" ->  "keyword",
    "context" ->  "keyword",
    "corollary" ->  "keyword",
    "cpodef" -> "keyword",
    "datatype" -> "keyword",
    "declaration" ->  "keyword",
    "declare" ->  "keyword",
    "def" ->  "keyword",
    "default_sort" -> "keyword",
    "defer" ->  "keyword",
    "defer_recdef" -> "keyword",
    "definition" -> "keyword",
    "defs" -> "keyword",
    "disable_pr" -> "keyword",
    "display_drafts" -> "keyword",
    "domain" -> "keyword",
    "domain_isomorphism" -> "keyword",
    "done" -> "keyword",
    "enable_pr" ->  "keyword",
    "end" ->  "keyword",
    "equivariance" -> "keyword",
    "example_proof" ->  "keyword",
    "exit" -> "keyword",
    "export_code" ->  "keyword",
    "extract" ->  "keyword",
    "extract_type" -> "keyword",
    "finalconsts" ->  "keyword",
    "finally" ->  "keyword",
    "find_consts" ->  "keyword",
    "find_theorems" ->  "keyword",
    "fix" ->  "keyword",
    "fixpat" -> "keyword",
    "fixrec" -> "keyword",
    "from" -> "keyword",
    "full_prf" -> "keyword",
    "fun" ->  "keyword",
    "function" -> "keyword",
    "global" -> "keyword",
    "guess" ->  "keyword",
    "have" -> "keyword",
    "header" -> "keyword",
    "help" -> "keyword",
    "hence" ->  "keyword",
    "hide_class" -> "keyword",
    "hide_const" -> "keyword",
    "hide_fact" ->  "keyword",
    "hide_type" ->  "keyword",
    "inductive" ->  "keyword",
    "inductive_cases" ->  "keyword",
    "inductive_set" ->  "keyword",
    "init_toplevel" ->  "keyword",
    "instance" -> "keyword",
    "instantiation" ->  "keyword",
    "interpret" ->  "keyword",
    "interpretation" -> "keyword",
    "judgment" -> "keyword",
    "kill" -> "keyword",
    "kill_thy" -> "keyword",
    "lemma" ->  "keyword",
    "lemmas" -> "keyword",
    "let" ->  "keyword",
    "linear_undo" ->  "keyword",
    "local" ->  "keyword",
    "local_setup" ->  "keyword",
    "locale" -> "keyword",
    "method_setup" -> "keyword",
    "moreover" -> "keyword",
    "new_domain" -> "keyword",
    "next" -> "keyword",
    "nitpick" ->  "keyword",
    "nitpick_params" -> "keyword",
    "no_notation" ->  "keyword",
    "no_syntax" ->  "keyword",
    "no_translations" ->  "keyword",
    "no_type_notation" -> "keyword",
    "nominal_datatype" -> "keyword",
    "nominal_inductive" ->  "keyword",
    "nominal_inductive2" -> "keyword",
    "nominal_primrec" ->  "keyword",
    "nonterminals" -> "keyword",
    "normal_form" ->  "keyword",
    "notation" -> "keyword",
    "note" -> "keyword",
    "notepad" -> "keyword",
    "obtain" -> "keyword",
    "oops" -> "keyword",
    "oracle" -> "keyword",
    "overloading" ->  "keyword",
    "parse_ast_translation" ->  "keyword",
    "parse_translation" ->  "keyword",
    "pcpodef" ->  "keyword",
    "pr" -> "keyword",
    "prefer" -> "keyword",
    "presume" ->  "keyword",
    "pretty_setmargin" -> "keyword",
    "prf" ->  "keyword",
    "primrec" ->  "keyword",
    "print_abbrevs" ->  "keyword",
    "print_antiquotations" -> "keyword",
    "print_ast_translation" ->  "keyword",
    "print_attributes" -> "keyword",
    "print_binds" ->  "keyword",
    "print_cases" ->  "keyword",
    "print_claset" -> "keyword",
    "print_classes" ->  "keyword",
    "print_codeproc" -> "keyword",
    "print_codesetup" ->  "keyword",
    "print_commands" -> "keyword",
    "print_configs" ->  "keyword",
    "print_context" ->  "keyword",
    "print_drafts" -> "keyword",
    "print_facts" ->  "keyword",
    "print_induct_rules" -> "keyword",
    "print_interps" ->  "keyword",
    "print_locale" -> "keyword",
    "print_locales" ->  "keyword",
    "print_methods" ->  "keyword",
    "print_orders" -> "keyword",
    "print_quotconsts" -> "keyword",
    "print_quotients" ->  "keyword",
    "print_quotmaps" -> "keyword",
    "print_rules" ->  "keyword",
    "print_simpset" ->  "keyword",
    "print_statement" ->  "keyword",
    "print_syntax" -> "keyword",
    "print_theorems" -> "keyword",
    "print_theory" -> "keyword",
    "print_trans_rules" ->  "keyword",
    "print_translation" ->  "keyword",
    "proof" ->  "keyword",
    "prop" -> "keyword",
    "pwd" ->  "keyword",
    "qed" ->  "keyword",
    "quickcheck" -> "keyword",
    "quickcheck_params" ->  "keyword",
    "quit" -> "keyword",
    "quotient_definition" ->  "keyword",
    "quotient_type" ->  "keyword",
    "realizability" ->  "keyword",
    "realizers" ->  "keyword",
    "recdef" -> "keyword",
    "recdef_tc" ->  "keyword",
    "record" -> "keyword",
    "refute" -> "keyword",
    "refute_params" ->  "keyword",
    "remove_thy" -> "keyword",
    "rep_datatype" -> "keyword",
    "repdef" -> "keyword",
    "schematic_corollary" ->  "keyword",
    "schematic_lemma" ->  "keyword",
    "schematic_theorem" ->  "keyword",
    "sect" -> "keyword",
    "section" ->  "keyword",
    "setup" ->  "keyword",
    "show" -> "keyword",
    "simproc_setup" ->  "keyword",
    "sledgehammer" -> "keyword",
    "sledgehammer_params" ->  "keyword",
    "smt_status" -> "keyword",
    "sorry" ->  "keyword",
    "specification" ->  "keyword",
    "statespace" -> "keyword",
    "subclass" -> "keyword",
    "sublocale" ->  "keyword",
    "subsect" ->  "keyword",
    "subsection" -> "keyword",
    "subsubsect" -> "keyword",
    "subsubsection" ->  "keyword",
    "syntax" -> "keyword",
    "term" -> "keyword",
    "termination" ->  "keyword",
    "text" -> "keyword",
    "text_raw" -> "keyword",
    "then" -> "keyword",
    "theorem" ->  "keyword",
    "theorems" -> "keyword",
    "theory" -> "keyword",
    "thm" ->  "keyword",
    "thm_deps" -> "keyword",
    "thus" -> "keyword",
    "thy_deps" -> "keyword",
    "touch_thy" ->  "keyword",
    "translations" -> "keyword",
    "txt" ->  "keyword",
    "txt_raw" ->  "keyword",
    "typ" ->  "keyword",
    "type_notation" ->  "keyword",
    "typed_print_translation" ->  "keyword",
    "typedecl" -> "keyword",
    "typedef" ->  "keyword",
    "types" ->  "keyword",
    "types_code" -> "keyword",
    "type_synonym" -> "keyword",
    "ultimately" -> "keyword",
    "undo" -> "keyword",
    "undos_proof" ->  "keyword",
    "unfolding" ->  "keyword",
    "unused_thms" ->  "keyword",
    "use" ->  "keyword",
    "use_thy" ->  "keyword",
    "using" ->  "keyword",
    "value" ->  "keyword",
    "values" -> "keyword",
    "welcome" ->  "keyword",
    "with" -> "keyword",
    "write" ->  "keyword",
    "{" ->  "keyword",
    "}" ->  "keyword",
    "actions" -> "keyword-2",
    "advanced" -> "keyword-2",
    "and" -> "keyword-2",
    "assumes" -> "keyword-2",
    "attach" -> "keyword-2",
    "avoids" -> "keyword-2",
    "begin" -> "keyword-2",
    "binder" -> "keyword-2",
    "compose" -> "keyword-2",
    "congs" -> "keyword-2",
    "constrains" -> "keyword-2",
    "contains" -> "keyword-2",
    "datatypes" -> "keyword-2",
    "defines" -> "keyword-2",
    "file" -> "keyword-2",
    "fixes" -> "keyword-2",
    "for" -> "keyword-2",
    "functions" -> "keyword-2",
    "hide_action" -> "keyword-2",
    "hints" -> "keyword-2",
    "identifier" -> "keyword-2",
    "if" -> "keyword-2",
    "imports" -> "keyword-2",
    "in" -> "keyword-2",
    "infix" -> "keyword-2",
    "infixl" -> "keyword-2",
    "infixr" -> "keyword-2",
    "initially" -> "keyword-2",
    "inputs" -> "keyword-2",
    "internals" -> "keyword-2",
    "is" -> "keyword-2",
    "lazy" -> "keyword-2",
    "module_name" -> "keyword-2",
    "monos" -> "keyword-2",
    "morphisms" -> "keyword-2",
    "notes" -> "keyword-2",
    "obtains" -> "keyword-2",
    "open" -> "keyword-2",
    "output" -> "keyword-2",
    "outputs" -> "keyword-2",
    "overloaded" -> "keyword-2",
    "permissive" -> "keyword-2",
    "pervasive" -> "keyword-2",
    "post" -> "keyword-2",
    "pre" -> "keyword-2",
    "rename" -> "keyword-2",
    "restrict" -> "keyword-2",
    "shows" -> "keyword-2",
    "signature" -> "keyword-2",
    "states" -> "keyword-2",
    "structure" -> "keyword-2",
    "to" -> "keyword-2",
    "transitions" -> "keyword-2",
    "transrel" -> "keyword-2",
    "unchecked" -> "keyword-2",
    "uses" -> "keyword-2",
    "where" -> "keyword-2"
  )

  def apply(config: CodeMirrorConfiguration, pconfig: js.Any): Mode[IsabelleModeState] = new IsabelleMode(config,pconfig.asInstanceOf[IsabelleModeConfig])
}

class IsabelleMode(config: CodeMirrorConfiguration, parserConfig: IsabelleModeConfig) extends Mode[IsabelleModeState] {
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
    if (stream.`match`(IsabelleMode.typefree) != null) "variable-3"
    else if (stream.`match`(IsabelleMode.typevar)  != null) "variable-3"
    else if (stream.`match`(IsabelleMode.variable)  != null) "variable"
    else if (stream.`match`(IsabelleMode.longident) != null || stream.`match`(IsabelleMode.ident) != null) {
      words.getOrElse(stream.current(), "identifier") match {
        case "command" =>
          state.command = stream.current()
          "def"
        case t => t
      }
    }
    else if (stream.`match`(IsabelleMode.symident) != null) "operator"
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
    }
    else if (stream.`match`(IsabelleMode.escaped) != null) "string-2"
    else if (stream.`match`(IsabelleMode.longident) != null) null
    else if (stream.`match`(IsabelleMode.ident) != null) null
    else if (stream.`match`(IsabelleMode.typefree) != null) "variable-3"
    else if (stream.`match`(IsabelleMode.typevar) != null) "variable-2"
    else if (stream.`match`(IsabelleMode.num) != null) "atom"
    else if (stream.`match`(IsabelleMode.symident) != null) "delimiter"
    else if (stream.`match`(IsabelleMode.control) != null) "control"
    else if (stream.`match`(IsabelleMode.incomplete) != null) "incomplete"
    else {
      stream.next()
      null
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