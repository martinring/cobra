import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

cancelable in Global := true

val commonSettings = Seq(
  scalaVersion := "2.13.0",
  version := "1.0.6",
  maintainer := "Martin Ring",
  organization := "net.flatmap",
  scalacOptions ++= Seq("-deprecation","-feature"),
  // Disable ScalaDoc generation
  mappings in (Compile, packageDoc) := Seq()
)

scalaVersion := "2.13.0"

lazy val iconGlob = sys.props("os.name").toLowerCase match {
  case os if os.contains("mac") ⇒ "*.icns"
  case os if os.contains("win") ⇒ "*.ico"
  case _ ⇒ "*.png"
}


lazy val server = (project in file("modules/cobra-server"))
  .enablePlugins(JavaAppPackaging,UniversalPlugin,LinuxPlugin,RpmPlugin,DebianPlugin,WindowsPlugin)
  .settings(commonSettings :_*)
  .settings(
    fork := true,
    run / baseDirectory := file("."),
    name in Universal := "cobra",
    packageName in Universal := "cobra-" + version.value,
    name := "cobra",
    (packageName in Debian) := "cobra-presentations",
    rpmVendor := "Martin Ring",
    rpmLicense := Some("LGPL"),
    jdkAppIcon :=  (sourceDirectory.value ** iconGlob).getPaths.headOption.map(file),
    packageSummary := "Cobra proof and code presentation framework",
    packageDescription := """Cobra is a modern code and proof presentation
                            |framework, leveraging cutting-edge presentation technology together with
                            |a state of the art interactive theorem prover to present formalized
                            |mathematics as active documents. Cobra provides both an easy way
                            |to present proofs and a novel approach to auditorium interaction. The
                            |presentation is checked live by the theorem prover, and moreover
                            |allows live changes both by the presenter as well as the audience.""".stripMargin.split('\n').mkString(" "),
    libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.8" ,
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7",
    libraryDependencies += "org.webjars" % "webjars-locator" % "0.32",
    libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.8",
    libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.8.0",
    libraryDependencies += "com.github.pathikrit" %% "better-files-akka" % "3.8.0",
    libraryDependencies += "org.webjars.npm" % "katex" % "0.9.0"
  ).dependsOn(commonJVM, clientAssets)

lazy val clientAssets  = (project in file("modules/cobra-client"))
  .enablePlugins(SbtWeb)
  .settings(commonSettings :_*)
  .settings(
    autoScalaLibrary := false,
    scalaJSProjects := Seq(client),
    (sourceDirectories in Compile) := Seq.empty,
    (unmanagedSourceDirectories in Compile) := Seq.empty,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    target := target.value / "assets",
    name := "cobra.client.assets",
    moduleName := "cobra-client",
    includeFilter in (Assets, LessKeys.less) := "cobra.less" | "print.less",
    libraryDependencies ++= Seq(
      "org.webjars.npm" % "octicons" % "8.5.0")
  ).dependsOn(client)

lazy val client = (project in file("modules/cobra-client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings :_*)
  .settings(
    target := target.value / "js",
    name := "cobra.client.js",
    moduleName := "cobra-client",
    artifactPath in (Compile,fastOptJS) :=
      ((crossTarget in fastOptJS).value /
        ((moduleName in fastOptJS).value + ".js")),
    artifactPath in (Compile,fullOptJS) := (artifactPath in (Compile,fastOptJS)).value,
    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true
  ).dependsOn(reveal,codemirror,utilJS,commonJS)

lazy val utilJS = (project in file("modules/js-util"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings :_*)
  .settings(
    name := "util.js",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    skip in packageJSDependencies := false
  )

lazy val common = crossProject(JSPlatform, JVMPlatform).in(file("modules/cobra-common"))
  .settings(commonSettings :_*)
  .settings(
    name := "cobra.common",
    libraryDependencies += "io.suzaku" %%% "boopickle" % "1.3.1"
  ).jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.0.0" % "provided"
  )

lazy val commonJS = common.js
lazy val commonJVM = common.jvm

// ##############################################################
// js Bindings

lazy val reveal = (project in file("modules/js-bindings/reveal-js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings :_*)
  .settings(
    name := "reveal.js",
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    jsDependencies := Seq(
      "org.webjars.bowergithub.hakimel" % "reveal.js" % "3.8.0" / "reveal.js"),
    skip in packageJSDependencies := false
  ).dependsOn(utilJS)

lazy val codemirror = (project in file("modules/js-bindings/codemirror"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings :_*)
  .settings(
    name := "codemirror",
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    jsDependencies := Seq(
      "org.webjars.npm" % "codemirror" % "5.48.0" / "lib/codemirror.js",
      "org.webjars.npm" % "codemirror" % "5.48.0" / "addon/runmode/runmode.js" dependsOn "lib/codemirror.js",
      "org.webjars.npm" % "codemirror" % "5.48.0" / "mode/haskell/haskell.js" dependsOn "lib/codemirror.js",
      "org.webjars.npm" % "codemirror" % "5.48.0" / "mode/clike/clike.js" dependsOn "lib/codemirror.js",
      "org.webjars.npm" % "codemirror" % "5.48.0" / "mode/htmlmixed/htmlmixed.js" dependsOn "lib/codemirror.js",
      "org.webjars.npm" % "codemirror" % "5.48.0" / "mode/javascript/javascript.js" dependsOn "lib/codemirror.js",
      "org.webjars.npm" % "codemirror" % "5.48.0" / "mode/css/css.js" dependsOn "lib/codemirror.js"),
    skip in packageJSDependencies := false
  ).dependsOn(utilJS)

// ##############################################################
// load server project as default

onLoad in Global :=
  (Command.process("project server", _: State)) compose (onLoad in Global).value
