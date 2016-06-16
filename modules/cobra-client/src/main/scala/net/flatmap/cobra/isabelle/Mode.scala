package net.flatmap.cobra.isabelle

import net.flatmap.js.codemirror._
import scala.scalajs.js.annotation.{ScalaJSDefined, JSExport, JSExportAll}
import scala.scalajs.js.RegExp
import scala.scalajs.js
import scala.scalajs.js.|

case class IsabelleModeState(var commentLevel: Int = 0,
                             var command: String = null,
                             var tokenize: (Stream,IsabelleModeState) => String)

case class IsabelleModeConfig(commands: Set[String], keywords: Set[String])

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

  val defaultCommands = Set(
    ".", "..", "Isabelle.command", "Isar.begin_document", "Isar.define_command",
    "Isar.edit_document", "Isar.end_document", "ML", "ML_command", "ML_prf",
    "ML_val", "ProofGeneral.inform_file_processed",
    "ProofGeneral.inform_file_retracted", "ProofGeneral.kill_proof",
    "ProofGeneral.pr", "ProofGeneral.process_pgip", "ProofGeneral.restart",
    "ProofGeneral.undo", "abbreviation", "also", "apply", "apply_end",
    "arities", "assume", "atom_decl", "attribute_setup", "automaton",
    "ax_specification", "axiomatization", "axioms", "back", "boogie_end",
    "boogie_open", "boogie_status", "boogie_vc", "by", "cannot_undo", "case",
    "cd", "chapter", "class", "class_deps", "classes", "classrel", "code_abort",
    "code_class", "code_const", "code_datatype", "code_deps", "code_include",
    "code_instance", "code_library", "code_module", "code_modulename",
    "code_monad", "code_pred", "code_reflect", "code_reserved", "code_thms",
    "code_type", "coinductive", "coinductive_set", "commit", "constdefs",
    "consts", "consts_code", "context", "corollary", "cpodef", "datatype",
    "declaration", "declare", "def", "default_sort", "defer", "defer_recdef",
    "definition", "defs", "disable_pr", "display_drafts", "domain",
    "domain_isomorphism", "done", "enable_pr", "end", "equivariance",
    "example_proof", "exit", "export_code", "extract", "extract_type",
    "finalconsts", "finally", "find_consts", "find_theorems", "fix", "fixpat",
    "fixrec", "from", "full_prf", "fun", "function", "global", "guess", "have",
    "header", "help", "hence", "hide_class", "hide_const", "hide_fact",
    "hide_type", "inductive", "inductive_cases", "inductive_set",
    "init_toplevel", "instance", "instantiation", "interpret", "interpretation",
    "judgment", "kill", "kill_thy", "lemma", "lemmas", "let", "linear_undo",
    "local", "local_setup", "locale", "method_setup", "moreover", "new_domain",
    "next", "nitpick", "nitpick_params", "no_notation", "no_syntax",
    "no_translations", "no_type_notation", "nominal_datatype",
    "nominal_inductive", "nominal_inductive2", "nominal_primrec",
    "nonterminals", "normal_form", "notation", "note", "notepad", "obtain",
    "oops", "oracle", "overloading", "parse_ast_translation",
    "parse_translation", "pcpodef", "pr", "prefer", "presume",
    "pretty_setmargin", "prf", "primrec", "print_abbrevs",
    "print_antiquotations", "print_ast_translation", "print_attributes",
    "print_binds", "print_cases", "print_claset", "print_classes",
    "print_codeproc", "print_codesetup", "print_commands", "print_configs",
    "print_context", "print_drafts", "print_facts", "print_induct_rules",
    "print_interps", "print_locale", "print_locales", "print_methods",
    "print_orders", "print_quotconsts", "print_quotients", "print_quotmaps",
    "print_rules", "print_simpset", "print_statement", "print_syntax",
    "print_theorems", "print_theory", "print_trans_rules", "print_translation",
    "proof", "prop", "pwd", "qed", "quickcheck", "quickcheck_params", "quit",
    "quotient_definition", "quotient_type", "realizability", "realizers",
    "recdef", "recdef_tc", "record", "refute", "refute_params", "remove_thy",
    "rep_datatype", "repdef", "schematic_corollary", "schematic_lemma",
    "schematic_theorem", "sect", "section", "setup", "show", "simproc_setup",
    "sledgehammer", "sledgehammer_params", "smt_status", "sorry",
    "specification", "statespace", "subclass", "sublocale", "subsect",
    "subsection", "subsubsect", "subsubsection", "syntax", "term",
    "termination", "text", "text_raw", "then", "theorem", "theorems", "theory",
    "thm", "thm_deps", "thus", "thy_deps", "touch_thy", "translations", "txt",
    "txt_raw", "typ", "type_notation", "typed_print_translation", "typedecl",
    "typedef", "types", "types_code", "type_synonym", "ultimately", "undo",
    "undos_proof", "unfolding", "unused_thms", "use", "use_thy", "using",
    "value", "values", "welcome", "with", "write", "{", "}"
  )

  val defaultKeywords = Set(
    "actions", "advanced", "and", "assumes", "attach", "avoids", "begin",
    "binder", "compose", "congs", "constrains", "contains", "datatypes",
    "defines", "file", "fixes", "for", "functions", "hide_action", "hints",
    "identifier", "if", "imports", "in", "infix", "infixl", "infixr",
    "initially", "inputs", "internals", "is", "lazy", "module_name", "monos",
    "morphisms", "notes", "obtains", "open", "output", "outputs", "overloaded",
    "permissive", "pervasive", "post", "pre", "rename", "restrict", "shows",
    "signature", "states", "structure", "to", "transitions", "transrel",
    "unchecked", "uses", "where"
  )

  def apply(config: CodeMirrorConfiguration, pconfig: js.Any): Mode[IsabelleModeState] = new IsabelleMode(config, pconfig match {
    case c: IsabelleModeConfig => c
    case _ => IsabelleModeConfig(defaultCommands,defaultKeywords)
  })
}

class IsabelleMode(config: CodeMirrorConfiguration, parserConfig: IsabelleModeConfig) extends Mode[IsabelleModeState] {
  val words: PartialFunction[String,String] = {
    case command if parserConfig.commands.contains(command) => "keyword strong command"
    case keyword if parserConfig.keywords.contains(keyword) => "keyword"
  }

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
    else if (stream.`match`(IsabelleMode.longident) != null || stream.`match`(IsabelleMode.ident) != null)
      words.lift(stream.current) getOrElse "identifier"
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
    else if (stream.`match`(IsabelleMode.incomplete) != null) "invalid"
    else {
      stream.next()
      null
    }
  }

  def tokenAltString(stream: Stream, state: IsabelleModeState): String = {
    if (stream.skipTo('`') exists identity) {
      stream.next()
      state.tokenize = tokenBase
      "string-2"
    }
    else {
      stream.skipToEnd()
      "string-2"
    }
  }


  def token(stream: Stream, state: IsabelleModeState): String = {
    state.tokenize(stream,state)
  }

  override def startState() = IsabelleModeState(0,null,tokenBase)
}