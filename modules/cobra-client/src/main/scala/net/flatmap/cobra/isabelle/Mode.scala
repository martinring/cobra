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
    "." ->  "keyword strong",
    ".." -> "keyword strong",
    "Isabelle.command" -> "keyword strong",
    "Isar.begin_document" ->  "keyword strong",
    "Isar.define_command" ->  "keyword strong",
    "Isar.edit_document" -> "keyword strong",
    "Isar.end_document" ->  "keyword strong",
    "ML" -> "keyword strong",
    "ML_command" -> "keyword strong",
    "ML_prf" -> "keyword strong",
    "ML_val" -> "keyword strong",
    "ProofGeneral.inform_file_processed" -> "keyword strong",
    "ProofGeneral.inform_file_retracted" -> "keyword strong",
    "ProofGeneral.kill_proof" ->  "keyword strong",
    "ProofGeneral.pr" ->  "keyword strong",
    "ProofGeneral.process_pgip" ->  "keyword strong",
    "ProofGeneral.restart" -> "keyword strong",
    "ProofGeneral.undo" ->  "keyword strong",
    "abbreviation" -> "keyword strong",
    "also" -> "keyword strong",
    "apply" ->  "keyword strong",
    "apply_end" ->  "keyword strong",
    "arities" ->  "keyword strong",
    "assume" -> "keyword strong",
    "atom_decl" ->  "keyword strong",
    "attribute_setup" ->  "keyword strong",
    "automaton" ->  "keyword strong",
    "ax_specification" -> "keyword strong",
    "axiomatization" -> "keyword strong",
    "axioms" -> "keyword strong",
    "back" -> "keyword strong",
    "boogie_end" -> "keyword strong",
    "boogie_open" ->  "keyword strong",
    "boogie_status" ->  "keyword strong",
    "boogie_vc" ->  "keyword strong",
    "by" -> "keyword strong",
    "cannot_undo" ->  "keyword strong",
    "case" -> "keyword strong",
    "cd" -> "keyword strong",
    "chapter" ->  "keyword strong",
    "class" ->  "keyword strong",
    "class_deps" -> "keyword strong",
    "classes" ->  "keyword strong",
    "classrel" -> "keyword strong",
    "code_abort" -> "keyword strong",
    "code_class" -> "keyword strong",
    "code_const" -> "keyword strong",
    "code_datatype" ->  "keyword strong",
    "code_deps" ->  "keyword strong",
    "code_include" -> "keyword strong",
    "code_instance" ->  "keyword strong",
    "code_library" -> "keyword strong",
    "code_module" ->  "keyword strong",
    "code_modulename" ->  "keyword strong",
    "code_monad" -> "keyword strong",
    "code_pred" ->  "keyword strong",
    "code_reflect" -> "keyword strong",
    "code_reserved" ->  "keyword strong",
    "code_thms" ->  "keyword strong",
    "code_type" ->  "keyword strong",
    "coinductive" ->  "keyword strong",
    "coinductive_set" ->  "keyword strong",
    "commit" -> "keyword strong",
    "constdefs" ->  "keyword strong",
    "consts" -> "keyword strong",
    "consts_code" ->  "keyword strong",
    "context" ->  "keyword strong",
    "corollary" ->  "keyword strong",
    "cpodef" -> "keyword strong",
    "datatype" -> "keyword strong",
    "declaration" ->  "keyword strong",
    "declare" ->  "keyword strong",
    "def" ->  "keyword strong",
    "default_sort" -> "keyword strong",
    "defer" ->  "keyword strong",
    "defer_recdef" -> "keyword strong",
    "definition" -> "keyword strong",
    "defs" -> "keyword strong",
    "disable_pr" -> "keyword strong",
    "display_drafts" -> "keyword strong",
    "domain" -> "keyword strong",
    "domain_isomorphism" -> "keyword strong",
    "done" -> "keyword strong",
    "enable_pr" ->  "keyword strong",
    "end" ->  "keyword strong",
    "equivariance" -> "keyword strong",
    "example_proof" ->  "keyword strong",
    "exit" -> "keyword strong",
    "export_code" ->  "keyword strong",
    "extract" ->  "keyword strong",
    "extract_type" -> "keyword strong",
    "finalconsts" ->  "keyword strong",
    "finally" ->  "keyword strong",
    "find_consts" ->  "keyword strong",
    "find_theorems" ->  "keyword strong",
    "fix" ->  "keyword strong",
    "fixpat" -> "keyword strong",
    "fixrec" -> "keyword strong",
    "from" -> "keyword strong",
    "full_prf" -> "keyword strong",
    "fun" ->  "keyword strong",
    "function" -> "keyword strong",
    "global" -> "keyword strong",
    "guess" ->  "keyword strong",
    "have" -> "keyword strong",
    "header" -> "keyword strong",
    "help" -> "keyword strong",
    "hence" ->  "keyword strong",
    "hide_class" -> "keyword strong",
    "hide_const" -> "keyword strong",
    "hide_fact" ->  "keyword strong",
    "hide_type" ->  "keyword strong",
    "inductive" ->  "keyword strong",
    "inductive_cases" ->  "keyword strong",
    "inductive_set" ->  "keyword strong",
    "init_toplevel" ->  "keyword strong",
    "instance" -> "keyword strong",
    "instantiation" ->  "keyword strong",
    "interpret" ->  "keyword strong",
    "interpretation" -> "keyword strong",
    "judgment" -> "keyword strong",
    "kill" -> "keyword strong",
    "kill_thy" -> "keyword strong",
    "lemma" ->  "keyword strong",
    "lemmas" -> "keyword strong",
    "let" ->  "keyword strong",
    "linear_undo" ->  "keyword strong",
    "local" ->  "keyword strong",
    "local_setup" ->  "keyword strong",
    "locale" -> "keyword strong",
    "method_setup" -> "keyword strong",
    "moreover" -> "keyword strong",
    "new_domain" -> "keyword strong",
    "next" -> "keyword strong",
    "nitpick" ->  "keyword strong",
    "nitpick_params" -> "keyword strong",
    "no_notation" ->  "keyword strong",
    "no_syntax" ->  "keyword strong",
    "no_translations" ->  "keyword strong",
    "no_type_notation" -> "keyword strong",
    "nominal_datatype" -> "keyword strong",
    "nominal_inductive" ->  "keyword strong",
    "nominal_inductive2" -> "keyword strong",
    "nominal_primrec" ->  "keyword strong",
    "nonterminals" -> "keyword strong",
    "normal_form" ->  "keyword strong",
    "notation" -> "keyword strong",
    "note" -> "keyword strong",
    "notepad" -> "keyword strong",
    "obtain" -> "keyword strong",
    "oops" -> "keyword strong",
    "oracle" -> "keyword strong",
    "overloading" ->  "keyword strong",
    "parse_ast_translation" ->  "keyword strong",
    "parse_translation" ->  "keyword strong",
    "pcpodef" ->  "keyword strong",
    "pr" -> "keyword strong",
    "prefer" -> "keyword strong",
    "presume" ->  "keyword strong",
    "pretty_setmargin" -> "keyword strong",
    "prf" ->  "keyword strong",
    "primrec" ->  "keyword strong",
    "print_abbrevs" ->  "keyword strong",
    "print_antiquotations" -> "keyword strong",
    "print_ast_translation" ->  "keyword strong",
    "print_attributes" -> "keyword strong",
    "print_binds" ->  "keyword strong",
    "print_cases" ->  "keyword strong",
    "print_claset" -> "keyword strong",
    "print_classes" ->  "keyword strong",
    "print_codeproc" -> "keyword strong",
    "print_codesetup" ->  "keyword strong",
    "print_commands" -> "keyword strong",
    "print_configs" ->  "keyword strong",
    "print_context" ->  "keyword strong",
    "print_drafts" -> "keyword strong",
    "print_facts" ->  "keyword strong",
    "print_induct_rules" -> "keyword strong",
    "print_interps" ->  "keyword strong",
    "print_locale" -> "keyword strong",
    "print_locales" ->  "keyword strong",
    "print_methods" ->  "keyword strong",
    "print_orders" -> "keyword strong",
    "print_quotconsts" -> "keyword strong",
    "print_quotients" ->  "keyword strong",
    "print_quotmaps" -> "keyword strong",
    "print_rules" ->  "keyword strong",
    "print_simpset" ->  "keyword strong",
    "print_statement" ->  "keyword strong",
    "print_syntax" -> "keyword strong",
    "print_theorems" -> "keyword strong",
    "print_theory" -> "keyword strong",
    "print_trans_rules" ->  "keyword strong",
    "print_translation" ->  "keyword strong",
    "proof" ->  "keyword strong",
    "prop" -> "keyword strong",
    "pwd" ->  "keyword strong",
    "qed" ->  "keyword strong",
    "quickcheck" -> "keyword strong",
    "quickcheck_params" ->  "keyword strong",
    "quit" -> "keyword strong",
    "quotient_definition" ->  "keyword strong",
    "quotient_type" ->  "keyword strong",
    "realizability" ->  "keyword strong",
    "realizers" ->  "keyword strong",
    "recdef" -> "keyword strong",
    "recdef_tc" ->  "keyword strong",
    "record" -> "keyword strong",
    "refute" -> "keyword strong",
    "refute_params" ->  "keyword strong",
    "remove_thy" -> "keyword strong",
    "rep_datatype" -> "keyword strong",
    "repdef" -> "keyword strong",
    "schematic_corollary" ->  "keyword strong",
    "schematic_lemma" ->  "keyword strong",
    "schematic_theorem" ->  "keyword strong",
    "sect" -> "keyword strong",
    "section" ->  "keyword strong",
    "setup" ->  "keyword strong",
    "show" -> "keyword strong",
    "simproc_setup" ->  "keyword strong",
    "sledgehammer" -> "keyword strong",
    "sledgehammer_params" ->  "keyword strong",
    "smt_status" -> "keyword strong",
    "sorry" ->  "keyword strong",
    "specification" ->  "keyword strong",
    "statespace" -> "keyword strong",
    "subclass" -> "keyword strong",
    "sublocale" ->  "keyword strong",
    "subsect" ->  "keyword strong",
    "subsection" -> "keyword strong",
    "subsubsect" -> "keyword strong",
    "subsubsection" ->  "keyword strong",
    "syntax" -> "keyword strong",
    "term" -> "keyword strong",
    "termination" ->  "keyword strong",
    "text" -> "keyword strong",
    "text_raw" -> "keyword strong",
    "then" -> "keyword strong",
    "theorem" ->  "keyword strong",
    "theorems" -> "keyword strong",
    "theory" -> "keyword strong",
    "thm" ->  "keyword strong",
    "thm_deps" -> "keyword strong",
    "thus" -> "keyword strong",
    "thy_deps" -> "keyword strong",
    "touch_thy" ->  "keyword strong",
    "translations" -> "keyword strong",
    "txt" ->  "keyword strong",
    "txt_raw" ->  "keyword strong",
    "typ" ->  "keyword strong",
    "type_notation" ->  "keyword strong",
    "typed_print_translation" ->  "keyword strong",
    "typedecl" -> "keyword strong",
    "typedef" ->  "keyword strong",
    "types" ->  "keyword strong",
    "types_code" -> "keyword strong",
    "type_synonym" -> "keyword strong",
    "ultimately" -> "keyword strong",
    "undo" -> "keyword strong",
    "undos_proof" ->  "keyword strong",
    "unfolding" ->  "keyword strong",
    "unused_thms" ->  "keyword strong",
    "use" ->  "keyword strong",
    "use_thy" ->  "keyword strong",
    "using" ->  "keyword strong",
    "value" ->  "keyword strong",
    "values" -> "keyword strong",
    "welcome" ->  "keyword strong",
    "with" -> "keyword strong",
    "write" ->  "keyword strong",
    "{" ->  "keyword strong",
    "}" ->  "keyword strong",
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