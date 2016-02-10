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

object ScalaJsWeb extends AutoPlugin {
  override def requires: Plugins = ScalaJSPlugin && SbtWeb

  val autoImport = AutoImport

  object AutoImport {
    val production = SettingKey[Boolean]("production",
      "wether to emit production version of js")
  }

  import autoImport._
  import SbtWeb.autoImport._
  import ScalaJSPlugin.autoImport._

  val generateScalaJS = TaskKey[Seq[java.io.File]]("dummy-js-gen")

  override def projectSettings = Seq(
    production := false,
    persistLauncher := true,
    persistLauncher in Test := false,
    skip in packageJSDependencies := false,
    relativeSourceMaps in fastOptJS := true,
    emitSourceMaps in fullOptJS := false,
    artifactPath in (Compile,fastOptJS) := WebKeys.webTarget.value / "js" / s"${moduleName.value}.js",
    artifactPath in (Compile,fullOptJS) := WebKeys.webTarget.value / "js" / s"${moduleName.value}.js",
    artifactPath in (Compile,packageJSDependencies) := WebKeys.webTarget.value / "js" / s"${moduleName.value}-deps.js",
    artifactPath in (Compile,packageScalaJSLauncher) := WebKeys.webTarget.value / "js" / s"${moduleName.value}-run.js",
    mappings in Assets <++= (
      fastOptJS in Compile,
      artifactPath in (Compile,fullOptJS),
      production,
      moduleName) map {
        case (fast,full,prod,moduleName) =>
          if (prod) Seq(full -> s"js/${full.getName}")
          else Seq(fast.data.asFile -> s"js/${fast.data.getName}", new File(fast.data.getPath + ".map") -> s"js/${fast.data.getName}.map")
    },
    mappings in Assets <+=
      artifactPath in (Compile,packageJSDependencies) map { file =>
        file -> s"js/${file.getName}"
    },
    mappings in Assets <+=
      artifactPath in (Compile,packageScalaJSLauncher) map { file =>
        file -> s"js/${file.getName}"
    },
    (generateScalaJS in Assets) <<= Def.task(Seq.empty),
    (generateScalaJS in TestAssets) <<= Def.task(Seq.empty),
    resourceGenerators in Assets += (generateScalaJS in Assets).taskValue,
    resourceGenerators in TestAssets += (generateScalaJS in TestAssets).taskValue,
    fastOptJS in Compile <<= (fastOptJS in Compile) triggeredBy (generateScalaJS in Assets)
    /*packageJSDependencies in Compile <<= (packageJSDependencies in Compile).triggeredBy(compile in Compile),
    packageScalaJSLauncher in Compile <<= (packageScalaJSLauncher in Compile).triggeredBy(compile in Compile)*/
  )
}

