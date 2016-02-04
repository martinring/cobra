package net.flatmap.js.codemirror

import scala.scalajs.js
import scala.scalajs.js.RegExp
import org.scalajs.dom.raw.HTMLElement

@js.native
trait CodeMirrorConfiguration extends js.Object {
  /**
   * The starting value of the editor. Can be a string, or a document object.
   */
  var value: js.Any = js.native

  /**
   * The mode to use. When not given, this will default to the first mode that was loaded. It may be a string, which either simply names the mode or is a MIME type associated with the mode. Alternatively, it may be an object containing configuration options for the mode, with a name property that names the mode (for example {name: "javascript", json: true}). The demo pages for each mode contain information about what configuration parameters the mode supports. You can ask CodeMirror which modes and MIME types have been defined by inspecting the CodeMirror.modes and CodeMirror.mimeModes objects. The first maps mode names to their constructors, and the second maps MIME types to mode specs.
   */
  var mode: js.Any = js.native

  /**
   * The theme to style the editor with. You must make sure the CSS file defining the corresponding .cm-s-[name] styles is loaded (see the theme directory in the distribution). The default is "default", for which colors are included in codemirror.css. It is possible to use multiple theming classes at once—for example "foo bar" will assign both the cm-s-foo and the cm-s-bar classes to the editor.
   */
  var theme: String = js.native

  /**
   * How many spaces a block (whatever that means in the edited language) should be indented. The default is 2.
   */
  var indentUnit: Int = js.native

  /**
   * Whether to use the context-sensitive indentation that the mode provides (or just indent the same as the line before). Defaults to true.
   */
  var smartIndent: Boolean = js.native

  /**
   * The width of a tab character. Defaults to 4.
   */
  var tabSize: Int = js.native

  /**
   * Whether, when indenting, the first N*tabSize spaces should be replaced by N tabs. Default is false.
   */
  var indentWithTabs: Boolean = js.native

  /**
   * Configures whether the editor should re-indent the current line when a character is typed that might change its proper indentation (only works if the mode supports indentation). Default is true.
   */
  var electricChars: Boolean = js.native

  /**
   * A regular expression used to determine which characters should be replaced by a special placeholder. Mostly useful for non-printing special characters. The default is /[\u0000-\u0019\u00ad\u200b\u2028\u2029\ufeff]/.
   */
  var specialChars: RegExp = js.native

  /**
   * A function that, given a special character identified by the specialChars option, produces a DOM node that is used to represent the character. By default, a red dot (•) is shown, with a title tooltip to indicate the character code.
   */
  var specialCharPlaceholder: js.Function1[Char, HTMLElement] = js.native

  /**
   * Determines whether horizontal cursor movement through right-to-left (Arabic, Hebrew) text is visual (pressing the left arrow moves the cursor left) or logical (pressing the left arrow moves to the next lower index in the string, which is visually right in right-to-left text). The default is false on Windows, and true on other platforms.
   */
  var rtlMoveVisually: Boolean = js.native

  /**
   * Configures the key map to use. The default is "default", which is the only key map defined in codemirror.js itself. Extra key maps are found in the key map directory. See the section on key maps for more information.
   */
  var keyMap: String = js.native

  /**
   * Can be used to specify extra key bindings for the editor, alongside the ones defined by keyMap. Should be either null, or a valid key map value.
   */
  var extraKeys: js.Any = js.native

  /**
   * Whether CodeMirror should scroll or wrap for long lines. Defaults to false (scroll).
   */
  var lineWrapping: Boolean = js.native

  /**
   * Whether to show line numbers to the left of the editor.
   */
  var lineNumbers: Boolean = js.native

  /**
   * At which number to start counting lines. Default is 1.
   */
  var firstLineNumber: Int = js.native

  /**
   * A function used to format line numbers. The function is passed the line number, and should return a string that will be shown in the gutter.
   */
  var lineNumberFormatter: js.Function1[Int, String] = js.native

  /**
   * Can be used to add extra gutters (beyond or instead of the line number gutter). Should be an array of CSS class names, each of which defines a width (and optionally a background), and which will be used to draw the background of the gutters. May include the CodeMirror-linenumbers class, in order to explicitly set the position of the line number gutter (it will default to be to the right of all other gutters). These class names are the keys passed to setGutterMarker.
   */
  var gutters: js.Array[String] = js.native

  /**
   * Determines whether the gutter scrolls along with the content horizontally (false) or whether it stays fixed during horizontal scrolling (true, the default).
   */
  var fixedGutter: Boolean = js.native

  /**
   * When fixedGutter is on, and there is a horizontal scrollbar, by default the gutter will be visible to the left of this scrollbar. If this option is set to true, it will be covered by an element with class CodeMirror-gutter-filler.
   */
  var coverGutterNextToScrollbar: Boolean = js.native

  /**
   * This disables editing of the editor content by the user. If the special value "nocursor" is given (instead of simply true), focusing of the editor is also disallowed.
   */
  var readOnly: js.Any = js.native

  /**
   * Whether the cursor should be drawn when a selection is active. Defaults to false.
   */
  var showCursorWhenSelecting: Boolean = js.native

  /**
   * The maximum number of undo levels that the editor stores. Note that this includes selection change events. Defaults to 200.
   */
  var undoDepth: Int = js.native

  /**
   * The period of inactivity (in milliseconds) that will cause a new history event to be started when typing or deleting. Defaults to 1250.
   */
  var historyEventDelay: Int = js.native

  /**
   * The tab index to assign to the editor. If not given, no tab index will be assigned.
   */
  var tabindex: Int = js.native

  /**
   * Can be used to make CodeMirror focus itself on initialization. Defaults to off. When fromTextArea is used, and no explicit value is given for this option, it will be set to true when either the source textarea is focused, or it has an autofocus attribute and no other element is focused.
   */
  var autofocus: Boolean = js.native

  /**
   * Controls whether drag-and-drop is enabled. On by default.
   */
  var dragDrop: Boolean = js.native

  /**
   * Half-period in milliseconds used for cursor blinking. The default blink rate is 530ms. By setting this to zero, blinking can be disabled. A negative value hides the cursor entirely.
   */
  var cursorBlinkRate: Int = js.native

  /**
   * How much extra space to always keep above and below the cursor when approaching the top or bottom of the visible view in a scrollable document. Default is 0.
   */
  var cursorScrollMargin: Int = js.native

  /**
   * Determines the height of the cursor. Default is 1, meaning it spans the whole height of the line. For some fonts (and by some tastes) a smaller height (for example 0.85), which causes the cursor to not reach all the way to the bottom of the line, looks better
   */
  var cursorHeight: Int = js.native

  /**
   * Controls whether, when the context menu is opened with a click outside of the current selection, the cursor is moved to the point of the click. Defaults to true.
   */
  var resetSelectionOnContextMenu: Boolean = js.native

  /**
   * Highlighting is done by a pseudo background-thread that will work for workTime milliseconds, and then use timeout to sleep for workDelay milliseconds. The defaults are 200 and 300, you can change these options to make the highlighting more or less aggressive.
   */
  var workTime: Int = js.native

  /**
   * Highlighting is done by a pseudo background-thread that will work for workTime milliseconds, and then use timeout to sleep for workDelay milliseconds. The defaults are 200 and 300, you can change these options to make the highlighting more or less aggressive.
   */
  var workDelay: Int = js.native

  /**
   * Indicates how quickly CodeMirror should poll its input textarea for changes (when focused). Most input is captured by events, but some things, like IME input on some browsers, don't generate events that allow CodeMirror to properly detect it. Thus, it polls. Default is 100 milliseconds.
   */
  var pollInterval: Int = js.native

  /**
   * By default, CodeMirror will combine adjacent tokens into a single span if they have the same class. This will result in a simpler DOM tree, and thus perform better. With some kinds of styling (such as rounded corners), this will change the way the document looks. You can set this option to false to disable this behavior.
   */
  var flattenSpans: Boolean = js.native

  /**
   * When enabled (off by default), an extra CSS class will be added to each token, indicating the (inner) mode that produced it, prefixed with "cm-m-". For example, tokens from the XML mode will get the cm-m-xml class.
   */
  var addModeClass: Boolean = js.native

  /**
   * When highlighting long lines, in order to stay responsive, the editor will give up and simply style the rest of the line as plain text when it reaches a certain position. The default is 10 000. You can set this to Infinity to turn off this behavior.
   */
  var maxHighlightLength: Int = js.native

  /**
   * When measuring the character positions in long lines, any line longer than this number (default is 10 000), when line wrapping is off, will simply be assumed to consist of same-sized characters. This means that, on the one hand, measuring will be inaccurate when characters of varying size, right-to-left text, markers, or other irregular elements are present. On the other hand, it means that having such a line won't freeze the user interface because of the expensiveness of the measurements.
   */
  var crudeMeasutingFrom: Int = js.native

  /**
   * Specifies the amount of lines that are rendered above and below the part of the document that's currently scrolled into view. This affects the amount of updates needed when scrolling, and the amount of work that such an update does. You should usually leave it at its default, 10. Can be set to Infinity to make sure the whole document is always rendered, and thus the browser's text search works on it. This will have bad effects on performance of big documents.
   */
  var viewportMargin: Int = js.native
}