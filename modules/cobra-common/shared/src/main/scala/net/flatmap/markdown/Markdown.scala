package net.flatmap.markdown

sealed trait Markdown extends Any {
  def html: String
}

/*object Markdown {
  sealed trait TextSpan extends Markdown
  sealed trait MultiLineTextSpan extends Markdown

  sealed trait TextStyle

  object TextStyle {
    case object Italic extends TextStyle
    case object Bold extends TextStyle
    case object StrikeThrough extends TextStyle
  }

  case class Span(val text: String) extends AnyVal with Markdown {
    def html = text
    override def toString = text
  }

  case class Paragraph(val elems: Span*) extends AnyVal with Markdown {
    def html = s"<p>$content</p>"
    override def toString = content.toString + "\n\n"
  }

  case object LineBreak extends Markdown {
    def html = "<br/>"
    override def toString = "/n"
  }

  case class Italic(val content: Markdown) extends AnyVal with Markdown {
    def html = s"<i>$content</i>"
    override def toString = s"*$content*"
  }

  case class Bold(val content: Markdown) extends AnyVal with Markdown {
    def html = s"<b>$content</b>"
    override def toString = s"**$content**"
  }

  case class Code(val content: String) extends AnyVal with Markdown {
    def html = s"<code>$content</code>"
    override def toString = s"`$content`"
  }

  case class CodeBlock(val content: String, language: Option[String]) extends Markdown {
    def html = s"<pre><code${language.map(x => s" class='$x'").getOrElse("")}>$content</code></pre>"
    override def toString = s"```${language.getOrElse("")}\n$content\n```"
  }

  case class Link(val caption: String, val target: String) extends Markdown {
    def html = s"<a href='$target'>$content</a>"
  }

  case class List(elems: )
}*/