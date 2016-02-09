package net.flatmap.js.webjar

import com.typesafe.sbt.web.Import.WebKeys
import com.typesafe.sbt.web.pipeline.Pipeline
import com.typesafe.sbt.web.{PathMapping, SbtWeb}
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.webjars.WebJarAssetLocator
import sbt.Project.Initialize
import sbt._
import sbt.Keys._

object SourceMappings {
  /**
    * For every file, compute the hash of their canonical path.
    * The hash uniquely identifies a file and can be safely exposed to the client as the full file path is not disclosed.
    */
  def fromFiles(files: Seq[File]): Seq[(File, String)] = files.collect {
    case f if f.exists => f -> Hash.halfHashString(f.getCanonicalPath)
  }
}

object ScalaJsWebJar extends AutoPlugin {
  override def requires: Plugins = ScalaJSPlugin && SbtWeb

  val autoImport = AutoImport

  object AutoImport {
    val production = SettingKey[Boolean]("production",
      "wether to emit production version of js")
  }

  import autoImport._
  import SbtWeb.autoImport._
  import ScalaJSPlugin.autoImport._

  override def projectSettings = Seq(
    production := false,
    persistLauncher := true,
    persistLauncher in Test := false,
    skip in packageJSDependencies := false,
    relativeSourceMaps in fastOptJS := true,
    emitSourceMaps in fullOptJS := false,
    mappings in Assets <++= (
      artifactPath in (Compile,fastOptJS),
      artifactPath in (Compile,fullOptJS),
      production,
      moduleName) map {
        case (fast,full,prod,moduleName) =>
          if (prod) Seq(full -> s"js/${moduleName}.js")
          else Seq(fast -> s"js/${moduleName}.js", new File(fast.getPath + ".map") -> s"js/${moduleName}.map")
    },
    mappings in Assets <+=
      artifactPath in (Compile,packageJSDependencies) map { file =>
        file -> s"js/${file.getName}"
    },
    mappings in Assets <+=
      artifactPath in (Compile,packageScalaJSLauncher) map { file =>
        file -> s"js/${file.getName}"
    },
    fastOptJS in Compile <<= (fastOptJS in Compile).triggeredBy(compile in Compile),
    packageJSDependencies in Compile <<= (packageJSDependencies in Compile).triggeredBy(compile in Compile),
    packageScalaJSLauncher in Compile <<= (packageScalaJSLauncher in Compile).triggeredBy(compile in Compile)
  )
}

