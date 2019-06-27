package net.flatmap.cobra

import java.io.InputStreamReader
import java.util

import javax.script.{ScriptEngineManager, SimpleBindings, SimpleScriptContext}
import net.flatmap.js.codemirror.CodeMirror
import org.webjars.WebJarAssetLocator

import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.util.matching.Regex

object Math {
  private lazy val engine = new ScriptEngineManager().getEngineByMimeType("text/javascript")

  private lazy val katex = {
    val locator = new WebJarAssetLocator()
    val path = locator.getFullPath("katex","dist/katex.min.js")
    val stream = getClass.getClassLoader.getResourceAsStream(path)
    engine.eval(new InputStreamReader(stream))
    engine.getContext.getScopes.asScala.flatMap(s => {
      engine.getBindings(s).asScala
    }).find(_._1.equalsIgnoreCase("katex")).map(_._2).get
  }

  def render(math: String): String = {
    val bmap = new java.util.HashMap[String,AnyRef](1)
    bmap.put("input",math)
    bmap.put("katex",katex)
    val bindings = new SimpleBindings(bmap)
    engine.eval(s"katex.renderToString(input)",bindings).toString
  }

  val regex = new Regex("\\$\\$(((?!\\$\\$).)*)\\$\\$")

  def renderAll(html: String): String = {
    regex.replaceAllIn(html,m => {
      println(m.group(1))
      render(m.group(1))
    })
  }
}
