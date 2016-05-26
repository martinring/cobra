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
    "." ->  "keyword-2",
    ".." -> "keyword-2",
    "Isabelle.command" -> "keyword-2",
    "Isar.begin_document" ->  "keyword-2",
    "Isar.define_command" ->  "keyword-2",
    "Isar.edit_document" -> "keyword-2",
    "Isar.end_document" ->  "keyword-2",
    "ML" -> "keyword-2",
    "ML_command" -> "keyword-2",
    "ML_prf" -> "keyword-2",
    "ML_val" -> "keyword-2",
    "ProofGeneral.inform_file_processed" -> "keyword-2",
    "ProofGeneral.inform_file_retracted" -> "keyword-2",
    "ProofGeneral.kill_proof" ->  "keyword-2",
    "ProofGeneral.pr" ->  "keyword-2",
    "ProofGeneral.process_pgip" ->  "keyword-2",
    "ProofGeneral.restart" -> "keyword-2",
    "ProofGeneral.undo" ->  "keyword-2",
    "abbreviation" -> "keyword-2",
    "also" -> "keyword-2",
    "apply" ->  "keyword-2",
    "apply_end" ->  "keyword-2",
    "arities" ->  "keyword-2",
    "assume" -> "keyword-2",
    "atom_decl" ->  "keyword-2",
    "attribute_setup" ->  "keyword-2",
    "automaton" ->  "keyword-2",
    "ax_specification" -> "keyword-2",
    "axiomatization" -> "keyword-2",
    "axioms" -> "keyword-2",
    "back" -> "keyword-2",
    "boogie_end" -> "keyword-2",
    "boogie_open" ->  "keyword-2",
    "boogie_status" ->  "keyword-2",
    "boogie_vc" ->  "keyword-2",
    "by" -> "keyword-2",
    "cannot_undo" ->  "keyword-2",
    "case" -> "keyword-2",
    "cd" -> "keyword-2",
    "chapter" ->  "keyword-2",
    "class" ->  "keyword-2",
    "class_deps" -> "keyword-2",
    "classes" ->  "keyword-2",
    "classrel" -> "keyword-2",
    "code_abort" -> "keyword-2",
    "code_class" -> "keyword-2",
    "code_const" -> "keyword-2",
    "code_datatype" ->  "keyword-2",
    "code_deps" ->  "keyword-2",
    "code_include" -> "keyword-2",
    "code_instance" ->  "keyword-2",
    "code_library" -> "keyword-2",
    "code_module" ->  "keyword-2",
    "code_modulename" ->  "keyword-2",
    "code_monad" -> "keyword-2",
    "code_pred" ->  "keyword-2",
    "code_reflect" -> "keyword-2",
    "code_reserved" ->  "keyword-2",
    "code_thms" ->  "keyword-2",
    "code_type" ->  "keyword-2",
    "coinductive" ->  "keyword-2",
    "coinductive_set" ->  "keyword-2",
    "commit" -> "keyword-2",
    "constdefs" ->  "keyword-2",
    "consts" -> "keyword-2",
    "consts_code" ->  "keyword-2",
    "context" ->  "keyword-2",
    "corollary" ->  "keyword-2",
    "cpodef" -> "keyword-2",
    "datatype" -> "keyword-2",
    "declaration" ->  "keyword-2",
    "declare" ->  "keyword-2",
    "def" ->  "keyword-2",
    "default_sort" -> "keyword-2",
    "defer" ->  "keyword-2",
    "defer_recdef" -> "keyword-2",
    "definition" -> "keyword-2",
    "defs" -> "keyword-2",
    "disable_pr" -> "keyword-2",
    "display_drafts" -> "keyword-2",
    "domain" -> "keyword-2",
    "domain_isomorphism" -> "keyword-2",
    "done" -> "keyword-2",
    "enable_pr" ->  "keyword-2",
    "end" ->  "keyword",
    "equivariance" -> "keyword-2",
    "example_proof" ->  "keyword-2",
    "exit" -> "keyword-2",
    "export_code" ->  "keyword-2",
    "extract" ->  "keyword-2",
    "extract_type" -> "keyword-2",
    "finalconsts" ->  "keyword-2",
    "finally" ->  "keyword-2",
    "find_consts" ->  "keyword-2",
    "find_theorems" ->  "keyword-2",
    "fix" ->  "keyword-2",
    "fixpat" -> "keyword-2",
    "fixrec" -> "keyword-2",
    "from" -> "keyword-2",
    "full_prf" -> "keyword-2",
    "fun" ->  "keyword-2",
    "function" -> "keyword-2",
    "global" -> "keyword-2",
    "guess" ->  "keyword-2",
    "have" -> "keyword-2",
    "header" -> "keyword-2",
    "help" -> "keyword-2",
    "hence" ->  "keyword-2",
    "hide_class" -> "keyword-2",
    "hide_const" -> "keyword-2",
    "hide_fact" ->  "keyword-2",
    "hide_type" ->  "keyword-2",
    "inductive" ->  "keyword-2",
    "inductive_cases" ->  "keyword-2",
    "inductive_set" ->  "keyword-2",
    "init_toplevel" ->  "keyword-2",
    "instance" -> "keyword-2",
    "instantiation" ->  "keyword-2",
    "interpret" ->  "keyword-2",
    "interpretation" -> "keyword-2",
    "judgment" -> "keyword-2",
    "kill" -> "keyword-2",
    "kill_thy" -> "keyword-2",
    "lemma" ->  "keyword-2",
    "lemmas" -> "keyword-2",
    "let" ->  "keyword-2",
    "linear_undo" ->  "keyword-2",
    "local" ->  "keyword-2",
    "local_setup" ->  "keyword-2",
    "locale" -> "keyword-2",
    "method_setup" -> "keyword-2",
    "moreover" -> "keyword-2",
    "new_domain" -> "keyword-2",
    "next" -> "keyword-2",
    "nitpick" ->  "keyword-2",
    "nitpick_params" -> "keyword-2",
    "no_notation" ->  "keyword-2",
    "no_syntax" ->  "keyword-2",
    "no_translations" ->  "keyword-2",
    "no_type_notation" -> "keyword-2",
    "nominal_datatype" -> "keyword-2",
    "nominal_inductive" ->  "keyword-2",
    "nominal_inductive2" -> "keyword-2",
    "nominal_primrec" ->  "keyword-2",
    "nonterminals" -> "keyword-2",
    "normal_form" ->  "keyword-2",
    "notation" -> "keyword-2",
    "note" -> "keyword-2",
    "notepad" -> "keyword-2",
    "obtain" -> "keyword-2",
    "oops" -> "keyword-2",
    "oracle" -> "keyword-2",
    "overloading" ->  "keyword-2",
    "parse_ast_translation" ->  "keyword-2",
    "parse_translation" ->  "keyword-2",
    "pcpodef" ->  "keyword-2",
    "pr" -> "keyword-2",
    "prefer" -> "keyword-2",
    "presume" ->  "keyword-2",
    "pretty_setmargin" -> "keyword-2",
    "prf" ->  "keyword-2",
    "primrec" ->  "keyword-2",
    "print_abbrevs" ->  "keyword-2",
    "print_antiquotations" -> "keyword-2",
    "print_ast_translation" ->  "keyword-2",
    "print_attributes" -> "keyword-2",
    "print_binds" ->  "keyword-2",
    "print_cases" ->  "keyword-2",
    "print_claset" -> "keyword-2",
    "print_classes" ->  "keyword-2",
    "print_codeproc" -> "keyword-2",
    "print_codesetup" ->  "keyword-2",
    "print_commands" -> "keyword-2",
    "print_configs" ->  "keyword-2",
    "print_context" ->  "keyword-2",
    "print_drafts" -> "keyword-2",
    "print_facts" ->  "keyword-2",
    "print_induct_rules" -> "keyword-2",
    "print_interps" ->  "keyword-2",
    "print_locale" -> "keyword-2",
    "print_locales" ->  "keyword-2",
    "print_methods" ->  "keyword-2",
    "print_orders" -> "keyword-2",
    "print_quotconsts" -> "keyword-2",
    "print_quotients" ->  "keyword-2",
    "print_quotmaps" -> "keyword-2",
    "print_rules" ->  "keyword-2",
    "print_simpset" ->  "keyword-2",
    "print_statement" ->  "keyword-2",
    "print_syntax" -> "keyword-2",
    "print_theorems" -> "keyword-2",
    "print_theory" -> "keyword-2",
    "print_trans_rules" ->  "keyword-2",
    "print_translation" ->  "keyword-2",
    "proof" ->  "keyword-2",
    "prop" -> "keyword-2",
    "pwd" ->  "keyword-2",
    "qed" ->  "keyword-2",
    "quickcheck" -> "keyword-2",
    "quickcheck_params" ->  "keyword-2",
    "quit" -> "keyword-2",
    "quotient_definition" ->  "keyword-2",
    "quotient_type" ->  "keyword-2",
    "realizability" ->  "keyword-2",
    "realizers" ->  "keyword-2",
    "recdef" -> "keyword-2",
    "recdef_tc" ->  "keyword-2",
    "record" -> "keyword-2",
    "refute" -> "keyword-2",
    "refute_params" ->  "keyword-2",
    "remove_thy" -> "keyword-2",
    "rep_datatype" -> "keyword-2",
    "repdef" -> "keyword-2",
    "schematic_corollary" ->  "keyword-2",
    "schematic_lemma" ->  "keyword-2",
    "schematic_theorem" ->  "keyword-2",
    "sect" -> "keyword-2",
    "section" ->  "keyword-2",
    "setup" ->  "keyword-2",
    "show" -> "keyword-2",
    "simproc_setup" ->  "keyword-2",
    "sledgehammer" -> "keyword-2",
    "sledgehammer_params" ->  "keyword-2",
    "smt_status" -> "keyword-2",
    "sorry" ->  "keyword-2",
    "specification" ->  "keyword-2",
    "statespace" -> "keyword-2",
    "subclass" -> "keyword-2",
    "sublocale" ->  "keyword-2",
    "subsect" ->  "keyword-2",
    "subsection" -> "keyword-2",
    "subsubsect" -> "keyword-2",
    "subsubsection" ->  "keyword-2",
    "syntax" -> "keyword-2",
    "term" -> "keyword-2",
    "termination" ->  "keyword-2",
    "text" -> "keyword-2",
    "text_raw" -> "keyword-2",
    "then" -> "keyword-2",
    "theorem" ->  "keyword-2",
    "theorems" -> "keyword-2",
    "theory" -> "keyword-2",
    "thm" ->  "keyword-2",
    "thm_deps" -> "keyword-2",
    "thus" -> "keyword-2",
    "thy_deps" -> "keyword-2",
    "touch_thy" ->  "keyword-2",
    "translations" -> "keyword-2",
    "txt" ->  "keyword-2",
    "txt_raw" ->  "keyword-2",
    "typ" ->  "keyword-2",
    "type_notation" ->  "keyword-2",
    "typed_print_translation" ->  "keyword-2",
    "typedecl" -> "keyword-2",
    "typedef" ->  "keyword-2",
    "types" ->  "keyword-2",
    "types_code" -> "keyword-2",
    "type_synonym" -> "keyword-2",
    "ultimately" -> "keyword-2",
    "undo" -> "keyword-2",
    "undos_proof" ->  "keyword-2",
    "unfolding" ->  "keyword-2",
    "unused_thms" ->  "keyword-2",
    "use" ->  "keyword-2",
    "use_thy" ->  "keyword-2",
    "using" ->  "keyword-2",
    "value" ->  "keyword-2",
    "values" -> "keyword-2",
    "welcome" ->  "keyword-2",
    "with" -> "keyword-2",
    "write" ->  "keyword-2",
    "{" ->  "keyword-2",
    "}" ->  "keyword-2",
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
    else if (stream.`match`(IsabelleMode.escaped) != null) "string escaped"
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