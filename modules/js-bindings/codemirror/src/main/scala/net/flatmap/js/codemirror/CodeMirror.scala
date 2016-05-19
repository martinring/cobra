package net.flatmap.js.codemirror

import org.scalajs.dom.raw.{Element, Event, HTMLElement, HTMLTextAreaElement}

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.{RegExp, UndefOr, |}

@JSName("CodeMirror")
@js.native
object CodeMirror extends js.Object {
  def apply(place: Element): CodeMirror = js.native  
  def version: String = js.native
  def fromTextArea(host: HTMLTextAreaElement, options: CodeMirrorConfiguration = js.native): CodeMirror with FromTextArea = js.native
  def defaults: CodeMirrorConfiguration = js.native
  def defineExtension(name: String, value: js.Any): Unit = js.native
  def defineDocExtension(name: String, value: js.Any): Unit = js.native
  def defineOption(name: String, default: js.Any, updateFunc: js.Function): Unit = js.native
  def defineInitHook(func: js.Function1[CodeMirror,Unit]): Unit = js.native
  def registerHelper(typ: String, name: String, value: js.Any): Unit = js.native
  def registerGlobalHelper(typ: String, name: String, predicate: js.Function2[String,CodeMirror,Boolean], value: js.Any): Unit = js.native  
  def Pos(line: Int, ch: Int = js.native): Position = js.native
  def changeEnd(change: js.Any): Position = js.native
  def copyState[S](mode: Mode[S], state: S): S = js.native
  def signal(target: js.Any, name: String, args: js.Any*): Unit = js.native

  def Pass: Nothing = js.native
  def defineMode[S](name: String, constructor: js.Function2[CodeMirrorConfiguration, js.Any, Mode[S]]): Unit = js.native
  def defineMIME(mime: String, modeSpec: String): Unit = js.native
  def defineMIME[S](mime: String, modeSpec: Mode[S]): Unit = js.native
  def extendMode[S](mode: String, extensions: Mode[S]): Unit = js.native
  
  def commands: CodeMirrorCommands = js.native

  def Doc(test: String, mode: js.Any, firstLineNumber: Int = js.native): Doc = js.native
}

@js.native
trait FromTextArea extends js.Object {
  def save(): Unit = js.native
  def toTextArea(): Unit = js.native
  def getTextArea(): HTMLTextAreaElement = js.native
}

@JSName("CodeMirror")
@js.native
class CodeMirror protected () extends js.Object {
  def on(eventName: String, handler: js.Function1[CodeMirror, Unit]): Unit = js.native
  def off(eventName: String, handler: js.Function1[CodeMirror, Unit]): Unit = js.native
  def on[T](eventName: String, handler: js.Function2[CodeMirror, T, Unit]): Unit = js.native
  def off[T](eventName: String, handler: js.Function2[CodeMirror, T, Unit]): Unit = js.native
  def this(place: Element, options: CodeMirrorConfiguration) = this()
  def this(place: Element) = this()
  def this(place: js.Function1[Element, Unit], options: CodeMirrorConfiguration = js.native) = this()
  def hasFocus(): Boolean = js.native
  def findPosH(start: Position, amount: Int, unit: String, visually: Boolean): PositionWithHitSide = js.native
  def findPosV(start: Position, amound: Int, unit: String): PositionWithHitSide = js.native
  def findWordAt(pos: Position): Range = js.native
  
  def setOption(option: String, value: js.Any): Unit = js.native
  def getOption(option: String): js.Dynamic = js.native
  def addKeyMap(map: js.Any, bottom: Boolean = js.native): Unit = js.native
  def removeKeyMap(map: js.Any): Unit = js.native
  def addOverlay(mode: String, options: js.Any): Unit = js.native  
  def addOverlay(mode: String): Unit = js.native
  def addOverlay[S](mode: Mode[S], options: js.Any = js.native): Unit = js.native
  def removeOverlay(mode: String): Unit = js.native
  def removeOverlay[S](mode: Mode[S]): Unit = js.native  
  
  def getDoc(): Doc = js.native
  def swapDoc(doc: Doc): Doc = js.native
  def setGutterMarker(line: Int, gutterID: String, value: HTMLElement): LineHandle = js.native
  def setGutterMarker(line: LineHandle, gutterID: String, value: HTMLElement): LineHandle = js.native  
  def clearGutter(gutterID: String): Unit = js.native
  def addLineClass(line: Int, where: String, _clazz: String): LineHandle = js.native
  def addLineClass(line: LineHandle, where: String, _clazz: String): LineHandle = js.native
  def removeLineClass(line: Int, where: String, clazz: String): LineHandle = js.native
  def removeLineClass(line: LineHandle, where: String, clazz: String): LineHandle = js.native
  def lineInfo(line: Int): js.Any = js.native
  def lineInfo(line: LineHandle): js.Any = js.native
  def addWidget(pos: Position, node: HTMLElement, scrollIntoView: Boolean = js.native): Unit = js.native
  def addLineWidget(line: Int, node: HTMLElement): LineWidget = js.native
  def addLineWidget(line: Int, node: HTMLElement, options: js.Any): LineWidget = js.native
  def addLineWidget(line: LineHandle, node: HTMLElement, options: js.Any = js.native): LineWidget = js.native
  def setSize(width: Int, height: js.Any): Unit = js.native
  def setSize(width: String, height: js.Any): Unit = js.native
  def scrollTo(x: Int, y: Int): Unit = js.native
  def getScrollInfo(): js.Any = js.native
  def scrollIntoView(pos: Position, margin: Int = js.native): Unit = js.native
  def scrollIntoView(pos: FromTo, margin: Int): Unit = js.native
  def scrollIntoView(pos: FromTo): Unit = js.native
  def cursorCoords(where: Boolean, mode: String): js.Any = js.native
  def charCoords(pos: Position, mode: String): js.Any = js.native
  def coordsChar(`object`: js.Any, mode: String = js.native): Position = js.native
  def defaultTextHeight(): Int = js.native
  def defaultCharWidth(): Int = js.native
  def getViewport(): js.Any = js.native
  def refresh(): Unit = js.native
  def getTokenAt(pos: Position): js.Any = js.native
  def getStateAfter(line: Int = js.native): js.Dynamic = js.native
  def operation[T](fn: js.Function0[T]): T = js.native
  def indentLine(line: Int, dir: String = js.native): Unit = js.native
  def focus(): Unit = js.native
  def getInputField(): HTMLTextAreaElement = js.native
  def getWrapperElement(): HTMLElement = js.native
  def getScrollerElement(): HTMLElement = js.native
  def getGutterElement(): HTMLElement = js.native
}

@js.native
trait CodeMirrorOptions extends js.Object {
  var value: String = js.native
  var mode: String = js.native

  /*
    lineSeparator: string|null
  Explicitly set the line separator for the editor. By default (value null), the document will be split on CRLFs as well as lone CRs and LFs, and a single LF will be used as line separator in all output (such as getValue). When a specific string is given, lines will only be split on that string, and output will, by default, use that same separator.
  theme: string
  The theme to style the editor with. You must make sure the CSS file defining the corresponding .cm-s-[name] styles is loaded (see the theme directory in the distribution). The default is "default", for which colors are included in codemirror.css. It is possible to use multiple theming classes at once—for example "foo bar" will assign both the cm-s-foo and the cm-s-bar classes to the editor.
    indentUnit: integer
  How many spaces a block (whatever that means in the edited language) should be indented. The default is 2.
  smartIndent: boolean
  Whether to use the context-sensitive indentation that the mode provides (or just indent the same as the line before). Defaults to true.
    tabSize: integer
  The width of a tab character. Defaults to 4.
  indentWithTabs: boolean
  Whether, when indenting, the first N*tabSize spaces should be replaced by N tabs. Default is false.
    electricChars: boolean
  Configures whether the editor should re-indent the current line when a character is typed that might change its proper indentation (only works if the mode supports indentation). Default is true.
    specialChars: RegExp
  A regular expression used to determine which characters should be replaced by a special placeholder. Mostly useful for non-printing special characters. The default is /[\u0000-\u0019\u00ad\u200b-\u200f\u2028\u2029\ufeff]/.
    specialCharPlaceholder: function(char) → Element
    A function that, given a special character identified by the specialChars option, produces a DOM node that is used to represent the character. By default, a red dot (•) is shown, with a title tooltip to indicate the character code.
  rtlMoveVisually: boolean
  Determines whether horizontal cursor movement through right-to-left (Arabic, Hebrew) text is visual (pressing the left arrow moves the cursor left) or logical (pressing the left arrow moves to the next lower index in the string, which is visually right in right-to-left text). The default is false on Windows, and true on other platforms.
    keyMap: string
  Configures the key map to use. The default is "default", which is the only key map defined in codemirror.js itself. Extra key maps are found in the key map directory. See the section on key maps for more information.
  extraKeys: object
  Can be used to specify extra key bindings for the editor, alongside the ones defined by keyMap. Should be either null, or a valid key map value.
  lineWrapping: boolean
  Whether CodeMirror should scroll or wrap for long lines. Defaults to false (scroll).
    lineNumbers: boolean
  Whether to show line numbers to the left of the editor.
    firstLineNumber: integer
  At which number to start counting lines. Default is 1.
  lineNumberFormatter: function(line: integer) → string
    A function used to format line numbers. The function is passed the line number, and should return a string that will be shown in the gutter.
    gutters: array<string>
    Can be used to add extra gutters (beyond or instead of the line number gutter). Should be an array of CSS class names, each of which defines a width (and optionally a background), and which will be used to draw the background of the gutters. May include the CodeMirror-linenumbers class, in order to explicitly set the position of the line number gutter (it will default to be to the right of all other gutters). These class names are the keys passed to setGutterMarker.
    fixedGutter: boolean
  Determines whether the gutter scrolls along with the content horizontally (false) or whether it stays fixed during horizontal scrolling (true, the default).
    scrollbarStyle: string
  Chooses a scrollbar implementation. The default is "native", showing native scrollbars. The core library also provides the "null" style, which completely hides the scrollbars. Addons can implement additional scrollbar models.
  coverGutterNextToScrollbar: boolean
  When fixedGutter is on, and there is a horizontal scrollbar, by default the gutter will be visible to the left of this scrollbar. If this option is set to true, it will be covered by an element with class CodeMirror-gutter-filler.
    inputStyle: string
  Selects the way CodeMirror handles input and focus. The core library defines the "textarea" and "contenteditable" input models. On mobile browsers, the default is "contenteditable". On desktop browsers, the default is "textarea". Support for IME and screen readers is better in the "contenteditable" model. The intention is to make it the default on modern desktop browsers in the future.
    readOnly: boolean|string
  This disables editing of the editor content by the user. If the special value "nocursor" is given (instead of simply true), focusing of the editor is also disallowed.
    showCursorWhenSelecting: boolean
  Whether the cursor should be drawn when a selection is active. Defaults to false.
    lineWiseCopyCut: boolean
  When enabled, which is the default, doing copy or cut when there is no selection will copy or cut the whole lines that have cursors on them.
    undoDepth: integer
  The maximum number of undo levels that the editor stores. Note that this includes selection change events. Defaults to 200.
  historyEventDelay: integer
  The period of inactivity (in milliseconds) that will cause a new history event to be started when typing or deleting. Defaults to 1250.
  tabindex: integer
  The tab index to assign to the editor. If not given, no tab index will be assigned.
  autofocus: boolean
  Can be used to make CodeMirror focus itself on initialization. Defaults to off. When fromTextArea is used, and no explicit value is given for this option, it will be set to true when either the source textarea is focused, or it has an autofocus attribute and no other element is focused.
  Below this a few more specialized, low-level options are listed. These are only useful in very specific situations, you might want to skip them the first time you read this manual.

    dragDrop: boolean
  Controls whether drag-and-drop is enabled. On by default.
    allowDropFileTypes: array<string>
    When set (default is null) only files whose type is in the array can be dropped into the editor. The strings should be MIME types, and will be checked against the type of the File object as reported by the browser.
    cursorBlinkRate: number
  Half-period in milliseconds used for cursor blinking. The default blink rate is 530ms. By setting this to zero, blinking can be disabled. A negative value hides the cursor entirely.
    cursorScrollMargin: number
  How much extra space to always keep above and below the cursor when approaching the top or bottom of the visible view in a scrollable document. Default is 0.
  cursorHeight: number
  Determines the height of the cursor. Default is 1, meaning it spans the whole height of the line. For some fonts (and by some tastes) a smaller height (for example 0.85), which causes the cursor to not reach all the way to the bottom of the line, looks better
    resetSelectionOnContextMenu: boolean
  Controls whether, when the context menu is opened with a click outside of the current selection, the cursor is moved to the point of the click. Defaults to true.
    workTime, workDelay: number
  Highlighting is done by a pseudo background-thread that will work for workTime milliseconds, and then use timeout to sleep for workDelay milliseconds. The defaults are 200 and 300, you can change these options to make the highlighting more or less aggressive.
    pollInterval: number
  Indicates how quickly CodeMirror should poll its input textarea for changes (when focused). Most input is captured by events, but some things, like IME input on some browsers, don't generate events that allow CodeMirror to properly detect it. Thus, it polls. Default is 100 milliseconds.
  flattenSpans: boolean
  By default, CodeMirror will combine adjacent tokens into a single span if they have the same class. This will result in a simpler DOM tree, and thus perform better. With some kinds of styling (such as rounded corners), this will change the way the document looks. You can set this option to false to disable this behavior.
    addModeClass: boolean
  When enabled (off by default), an extra CSS class will be added to each token, indicating the (inner) mode that produced it, prefixed with "cm-m-". For example, tokens from the XML mode will get the cm-m-xml class.
  maxHighlightLength: number
  When highlighting long lines, in order to stay responsive, the editor will give up and simply style the rest of the line as plain text when it reaches a certain position. The default is 10 000. You can set this to Infinity to turn off this behavior.
  viewportMargin: integer
  Specifies the amount of lines that are rendered above and below the part of the document that's currently scrolled into view. This affects the amount of updates needed when scrolling, and the amount of work that such an update does. You should usually leave it at its default, 10. Can be set to Infinity to make sure the whole document is always rendered, and thus the browser's text search works on it. This will have bad effects on performance of big documents.
  */
}

@js.native
trait DocEditorCommon extends js.Object {
  def posFromIndex(index: Int): Position = js.native
  def indexFromPos(pos: Position): Int = js.native
  /** Get the current editor content. You can pass it an optional argument to specify the string to be used to separate lines (defaults to "\n"). */
  def getValue(separator: String = js.native): String = js.native
  /** Set the editor content */
  def setValue(content: String): Unit = js.native
  /** Get the text between the given points in the editor, which should be {line, ch} objects. An optional third argument can be given to indicate the line separator string to use (defaults to "\n"). */
  def getRange(from: Position, to: Position, separator: String = js.native): String = js.native
  /** Replace the part of the document between from and to with the given string. from and to must be {line, ch} objects. to can be left off to simply insert the string at position from. When origin is given, it will be passed on to "change" events, and its first letter will be used to determine whether this change can be merged with previous history events, in the way described for selection origins. */
  def replaceRange(replacement: String, from: Position, to: Position = js.native, origin: String = js.native): Unit = js.native
  /** Get the content of line n. */
  def getLine(n: Int): String = js.native
  /** Get the number of lines in the editor. */
  def lineCount(): Int = js.native
  /** Get the first line of the editor. This will usually be zero but for linked sub-views, or documents instantiated with a non-zero first line, it might return other values. */
  def firstLine(): Int = js.native
  /** Get the last line of the editor. This will usually be doc.lineCount() - 1, but for linked sub-views, it might return other values. */  
  def lastLine(): Int = js.native
  /** Fetches the line handle for the given line number. */
  def getLineHandle(num: Int): LineHandle = js.native
  /** Given a line handle, returns the current position of that line (or null when it is no longer in the document). */
  def getLineNumber(handle: LineHandle): Int = js.native
  /** Iterate over the whole document, or if start and end line numbers are given, the range from start up to (not including) end, and call f for each line, passing the line handle. This is a faster way to visit a range of line handlers than calling getLineHandle for each of them. Note that line handles have a text property containing the line's content (as a string). */
  def eachLine(f: js.Function1[LineHandle, Unit]): Unit = js.native
  /** Iterate over the whole document, or if start and end line numbers are given, the range from start up to (not including) end, and call f for each line, passing the line handle. This is a faster way to visit a range of line handlers than calling getLineHandle for each of them. Note that line handles have a text property containing the line's content (as a string). */  
  def eachLine(start: Int, end: Int, f: js.Function1[LineHandle, Unit]): Unit = js.native
  /** Set the editor content as 'clean', a flag that it will retain until it is edited, and which will be set again when such an edit is undone again. Useful to track whether the content needs to be saved. This function is deprecated in favor of changeGeneration, which allows multiple subsystems to track different notions of cleanness without interfering. */
  def markClean(): Unit = js.native
  /** Returns a number that can later be passed to isClean to test whether any edits were made (and not undone) in the meantime. If closeEvent is true, the current history event will be ‘closed’, meaning it can't be combined with further changes (rapid typing or deleting events are typically combined). */
  def changeGeneration(closeEvent: Boolean = js.native): Int = js.native
  /** Returns whether the document is currently clean — not modified since initialization or the last call to markClean if no argument is passed, or since the matching call to changeGeneration if a generation value is given. */
  def isClean(generation: Int = js.native): Boolean = js.native
  /** Get the currently selected code. Optionally pass a line separator to put between the lines in the output. When multiple selections are present, they are concatenated with instances of lineSep in between. */
  def getSelection(lineSep: String = js.native): String = js.native
  /** Returns an array containing a string for each selection, representing the content of the selections. */
  def getSelections(lineSep: String = js.native): String = js.native
  /** Replace the selection(s) with the given string. By default, the new selection ends up after the inserted text. The optional select argument can be used to change this—passing "around" will cause the new text to be selected, passing "start" will collapse the selection to the start of the inserted text. */
  def replaceSelection(replacement: String, select: String = js.native): Unit = js.native
  /** Retrieve one end of the primary selection. start is a an optional string indicating which end of the selection to return. It may be "from", "to", "head" (the side of the selection that moves when you press shift+arrow), or "anchor" (the fixed side of the selection). Omitting the argument is the same as passing "head". A {line, ch} object will be returned. */
  def getCursor(start: String = js.native): Position = js.native
  /** Retrieves a list of all current selections. These will always be sorted, and never overlap (overlapping selections are merged). Each object in the array contains anchor and head properties referring to {line, ch} objects. */ 
  def listSelections(): js.Array[Range] = js.native
  /** Return true if any text is selected */
  def somethingSelected(): Boolean = js.native
  def setCursor(pos: Position): Unit = js.native
  def setCursor(pos: Int): Unit = js.native
  def setCursor(pos: Position, ch: Int): Unit = js.native
  def setCursor(pos: Int, ch: Int): Unit = js.native  
  def setCursor(pos: Position, options: SelectionOptions): Unit = js.native
  def setCursor(pos: Int, options: SelectionOptions): Unit = js.native
  def setCursor(pos: Position, ch: Int, options: SelectionOptions): Unit = js.native
  def setCursor(pos: Int, ch: Int, options: SelectionOptions): Unit = js.native
  def setSelection(anchor: Position, head: Position = js.native, options: SelectionOptions = js.native): Unit = js.native
  def setSelections(ranges: js.Array[Range], primary: Int = js.native, options: SelectionOptions = js.native): Unit = js.native
  def addSelection(anchor: Position, head: Position = js.native): Unit = js.native
  def extendSelection(from: Position, to: Position = js.native, options: SelectionOptions = js.native): Unit = js.native
  def extendSelections(heads: js.Array[Position], options: SelectionOptions = js.native): Unit = js.native
  def extendSelectionsBy(f: js.Function1[Range,Range], options: SelectionOptions = js.native): Unit = js.native
  def setExtending(value: Boolean): Unit = js.native
  def getExtending(): Boolean = js.native
  def undo(): Unit = js.native
  def redo(): Unit = js.native
  def undoSelection(): Unit = js.native
  def redoSelection(): Unit = js.native
  def historySize(): HistorySizeInfo = js.native
  def clearHistory(): Unit = js.native
  def getHistory(): js.Any = js.native
  def setHistory(history: js.Any): Unit = js.native
  
  def markText(from: Position, to: Position, options: TextMarkerOptions = js.native): TextMarker = js.native
  def setBookmark(pos: Position, options: TextMarkerOptions = js.native): TextMarker = js.native
  def findMarks(from: Position, to: Position): js.Array[TextMarker] = js.native
  def findMarksAt(pos: Position): js.Array[TextMarker] = js.native
  def getAllMarks(): js.Array[TextMarker] = js.native
}

@js.native
trait HistorySizeInfo extends js.Object {
  val undo: Int = js.native
  val redo: Int = js.native
}

@js.native
trait SelectionOptions extends js.Object {
  /**
   * Determines whether the selection head should be scrolled into view. Defaults to true.
   */
  var scroll: Boolean = js.native
  /**
   * Detemines whether the selection history event may be merged with the previous one. When an origin starts with the character +, and the last recorded selection had the same origin and was similar (close in time, both collapsed or both non-collapsed), the new one will replace the old one. When it starts with *, it will always replace the previous event (if that had the same origin). Built-in motion uses the "+move" origin.
   */
  var origin: String = js.native
  /**
   * Determine the direction into which the selection endpoints should be adjusted when they fall inside an atomic range. Can be either -1 (backward) or 1 (forward). When not given, the bias will be based on the relative position of the old selection—the editor will try to move further away from that, to prevent getting stuck.
   */
  var bias: Int = js.native  
}

@ScalaJSDefined
class LinkedDocOptions(
  val sharedHist: UndefOr[Boolean] = js.undefined,
  val from: UndefOr[Int] = js.undefined,
  val to: UndefOr[Int] = js.undefined,
  val mode: UndefOr[String | js.Object] = js.undefined) extends js.Object

@js.native
trait Doc extends DocEditorCommon {
  def on(eventName: String, handler: js.Function1[Doc, Unit]): Unit = js.native
  def off(eventName: String, handler: js.Function1[Doc, Unit]): Unit = js.native
  def on[T](eventName: String, handler: js.Function2[Doc, T, Unit]): Unit = js.native
  def off[T](eventName: String, handler: js.Function2[Doc, T, Unit]): Unit = js.native
  def getEditor(): CodeMirror = js.native
  def copy(copyHistory: Boolean = js.native): Doc = js.native
  def linkedDoc(options: LinkedDocOptions): Doc = js.native
  /** Will call the given function for all documents linked to the target
    * document. It will be passed two arguments, the linked document and a
    * boolean indicating whether that document shares history with the target.*/
  def iterLinkedDocs(function: js.Function2[Doc,Boolean,Unit])
}

@js.native
trait Range extends js.Object {
  var anchor: Position = js.native
  var head: Position = js.native
}

@js.native
trait Coordinates extends js.Object {
  var left: Int = js.native
  var top: Int = js.native
}

@js.native
trait LineHandle extends js.Object {
  def text: String = js.native
}

@js.native
trait TextMarker extends Clearable{
  def find(): FromTo = js.native
  def getOptions(copyWidget: Boolean): TextMarkerOptions = js.native
}

@js.native
trait Clearable extends js.Object {
  def clear(): Unit = js.native
}

@js.native
trait LineWidget extends Clearable {
  def changed(): Unit = js.native
}

@js.native
trait EditorChange extends js.Object {
  def from: Position = js.native
  def to: Position = js.native
  def text: js.Array[String] = js.native
  def removed: String = js.native
}

@js.native
trait EditorChangeLinkedList extends EditorChange {
  def next: EditorChangeLinkedList = js.native
}

@js.native
trait EditorChangeCancellable extends EditorChange {
  def update(from: Position = js.native, to: Position = js.native, text: String = js.native): Unit = js.native
  def cancel(): Unit = js.native
}

/**
 * Whenever points in the document are represented, the API uses objects with line and ch properties. Both are zero-based. CodeMirror makes sure to 'clip' any positions passed by client code so that they fit inside the document, so you shouldn't worry too much about sanitizing your coordinates. If you give ch a value of null, or don't specify it, it will be replaced with the length of the specified line.
 */
@js.native
trait Position extends js.Object {
  /**
   * Zero based
   */
  var ch: Int = js.native
  var line: Int = js.native
}

@js.native
trait FromTo extends js.Object {
  var from: Position = js.native
  var to: Position = js.native
}

object Position {
  def apply(ch: Int, line: Int): Position = {
    val result = js.Object().asInstanceOf[Position]
    result.ch = ch
    result.line = line
    return result
  }
}

@js.native
trait PositionWithHitSide extends Position {
  var hitSide: js.UndefOr[Boolean] = js.native
}

@js.native
trait CodeMirrorCommands extends js.Object {
  type Command = js.Function1[CodeMirror, Unit]
  /** Select the whole content of the editor. */
  var selectAll: Command = js.native
  /** When multiple selections are present, this deselects all but the primary selection. */
  var singleSelection: Command = js.native
  /** Emacs-style line killing. Deletes the part of the line after the cursor. If that consists only of whitespace, the newline at the end of the line is also deleted. */
  var killLine: Command = js.native
  /** Deletes the whole line under the cursor, including newline at the end. */
  var deleteLine: Command = js.native
  /** Delete the part of the line before the cursor. */
  var delLineLeft: Command = js.native
  /** Delete the part of the line from the left side of the visual line the cursor is on to the cursor. */
  var delWrappedLineLeft: Command = js.native
  /** Delete the part of the line from the cursor to the right side of the visual line the cursor is on. */
  var delWrappedLineRight: Command = js.native
  /** Undo the last change. */
  var undo: Command = js.native
  /** Redo the last undone change. */
  var redo: Command = js.native
  /** Undo the last change to the selection, or if there are no selection-only changes at the top of the history, undo the last change. */
  var undoSelection: Command = js.native
  /** Redo the last change to the selection, or the last text change if no selection changes remain. */
  var redoSelection: Command = js.native
  /** Move the cursor to the start of the document. */
  var goDocStart: Command = js.native
  /** Move the cursor to the end of the document. */
  var goDocEnd: Command = js.native
  /** Move the cursor to the start of the line. */
  var goLineStart: Command = js.native
  /** Move to the start of the text on the line, or if we are already there, to the actual start of the line (including whitespace). */
  var goLineStartSmart: Command = js.native
  /** Move the cursor to the end of the line. */
  var goLineEnd: Command = js.native
  /** Move the cursor to the right side of the visual line it is on. */
  var goLineRight: Command = js.native
  /** Move the cursor to the left side of the visual line it is on. If this line is wrapped, that may not be the start of the line. */
  var goLineLeft: Command = js.native
  /** Move the cursor to the left side of the visual line it is on. If that takes it to the start of the line, behave like goLineStartSmart. */
  var goLineLeftSmart: Command = js.native
  /** Move the cursor up one line. */
  var goLineUp: Command = js.native
  /** Move down one line. */
  var goLineDown: Command = js.native
  /** Move the cursor up one screen, and scroll up by the same distance. */
  var goPageUp: Command = js.native
  /** Move the cursor down one screen, and scroll down by the same distance. */
  var goPageDown: Command = js.native
  /** Move the cursor one character left, going to the previous line when hitting the start of line. */
  var goCharLeft: Command = js.native
  /** Move the cursor one character right, going to the next line when hitting the end of line. */
  var goCharRight: Command = js.native
  /** Move the cursor one character left, but don't cross line boundaries. */
  var goColumnLeft: Command = js.native
  /** Move the cursor one character right, don't cross line boundaries. */
  var goColumnRight: Command = js.native
  /** Move the cursor to the start of the previous word. */
  var goWordLeft: Command = js.native
  /** Move the cursor to the end of the next word. */
  var goWordRight: Command = js.native
  /** Move to the left of the group before the cursor. A group is a stretch of word characters, a stretch of punctuation characters, a newline, or a stretch of more than one whitespace character. */
  var goGroupLeft: Command = js.native
  /** Move to the right of the group after the cursor (see above). */
  var goGroupRight: Command = js.native
  /** Delete the character before the cursor. */
  var delCharBefore: Command = js.native
  /** Delete the character after the cursor. */
  var delCharAfter: Command = js.native
  /** Delete up to the start of the word before the cursor. */
  var delWordBefore: Command = js.native
  /** Delete up to the end of the word after the cursor. */
  var delWordAfter: Command = js.native
  /** Delete to the left of the group before the cursor. */
  var delGroupBefore: Command = js.native
  /** Delete to the start of the group after the cursor. */
  var delGroupAfter: Command = js.native
  /** Auto-indent the current line or selection. */
  var indentAuto: Command = js.native
  /** Indent the current line or selection by one indent unit. */
  var indentMore: Command = js.native
  /** Dedent the current line or selection by one indent unit. */
  var indentLess: Command = js.native
  /** Insert a tab character at the cursor. */
  var insertTab: Command = js.native
  /** Insert the amount of spaces that match the width a tab at the cursor position would have. */
  var insertSoftTab: Command = js.native
  /** If something is selected, indent it by one indent unit. If nothing is selected, insert a tab character. */
  var defaultTab: Command = js.native
  /** Swap the characters before and after the cursor. */
  var transposeChars: Command = js.native
  /** Insert a newline and auto-indent the new line. */
  var newlineAndIndent: Command = js.native
  /** Flip the overwrite flag. */
  var toggleOverwrite: Command = js.native
  /** Not defined by the core library, only referred to in key maps. Intended to provide an easy way for user code to define a save command. */
  var save: Command = js.native
  /** Not defined by the core library, but defined in the search addon (or custom client addons). */
  var find: Command = js.native
  /** Not defined by the core library, but defined in the search addon (or custom client addons). */
  var findNext: Command = js.native
  /** Not defined by the core library, but defined in the search addon (or custom client addons). */
  var findPrev: Command = js.native
  /** Not defined by the core library, but defined in the search addon (or custom client addons). */
  var replace: Command = js.native
  /** Not defined by the core library, but defined in the search addon (or custom client addons). */
  var replaceAll: Command = js.native
}

object TextMarkerOptions {
  def apply() = js.Object.apply().asInstanceOf[TextMarkerOptions]
}

@js.native
trait TextMarkerOptions extends js.Object {
  var className: String = js.native
  var inclusiveLeft: Boolean = js.native
  var inclusiveRight: Boolean = js.native
  var atomic: Boolean = js.native
  var collapsed: Boolean = js.native
  var clearOnEnter: Boolean = js.native
  var replacedWith: HTMLElement = js.native
  var readOnly: Boolean = js.native
  var addToHistory: Boolean = js.native
  var startStyle: String = js.native
  var endStyle: String = js.native
  var css: String = js.native
  var title: String = js.native
  var shared: Boolean = js.native
}