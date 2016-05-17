import net.flatmap.js.ScalaJSWeb.webjarDependenciesOf

cancelable in Global := true

val commonSettings = Seq(
  scalaVersion := "2.11.8",
  version := "0.1",
  organization := "net.flatmap",
  scalacOptions ++= Seq("-deprecation","-feature")
)

lazy val server = (project in file("modules/cobra-server"))
  .enablePlugins(UniversalPlugin,LinuxPlugin)
  .settings(commonSettings :_*)
  .settings(
    name := "cobra.server",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "2.4.4",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.4",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7",
    libraryDependencies += "org.webjars" % "webjars-locator" % "0.31"
  ).dependsOn(commonJVM, clientAssets)

lazy val clientAssets  = (project in file("modules/cobra-client"))
  .enablePlugins(SbtWeb,PlayScalaJS)
  .settings(commonSettings :_*)
  .settings(
    autoScalaLibrary := false,
    scalaJSProjects := Seq(client),
    mappings in Assets <++= scalaJSDev.map { mappings =>
      mappings.map { case (file,path) =>
        file -> s"js/$path"
      }
    },
    (sourceDirectories in Compile) := Seq.empty,
    (unmanagedSourceDirectories in Compile) := Seq.empty,
    (compile in Compile) <<= (compile in Compile) dependsOn (LessKeys.less in Assets),
    pipelineStages := Seq(scalaJSProd),
    target := target.value / "assets",
    name := "cobra.client.assets",
    moduleName := "cobra-client",
    watchSources <++= (sourceDirectory in Assets) map { assets =>
      (assets ** "*").get
    },
    includeFilter in (Assets, LessKeys.less) := "cobra.less" | "print.less",
    libraryDependencies <++= webjarDependenciesOf(client)
  )

lazy val client     = (project in file("modules/cobra-client"))
  .enablePlugins(ScalaJSPlay)
  .settings(commonSettings :_*)
  .settings(
    target := target.value / "js",
    name := "cobra.client.js",
    moduleName := "cobra-client",
    artifactPath in (Compile,fastOptJS) :=
      ((crossTarget in fastOptJS).value /
        ((moduleName in fastOptJS).value + ".js")),
    artifactPath in (Compile,fullOptJS) <<= artifactPath in (Compile,fastOptJS),
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    libraryDependencies += "org.webjars" % "MathJax" % "2.6.1",
    libraryDependencies += "org.webjars" % "octicons" % "3.5.0"
  ).dependsOn(reveal,codemirror,utilJS,commonJS)

lazy val utilJS = (project in file("modules/js-util"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings :_*)
  .settings(
    name := "util.js",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    skip in packageJSDependencies := false
  )

lazy val common = (crossProject in file("modules/cobra-common"))
  .settings(commonSettings :_*)
  .settings(
    name := "cobra.common",
    libraryDependencies += "me.chrons" %%% "boopickle" % "1.1.3"
  ).jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
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
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    jsDependencies += "org.webjars.bower" % "reveal.js" % "3.3.0" / "reveal.js",
    jsDependencies += "org.webjars.bower" % "reveal.js" % "3.3.0" / "lib/js/head.min.js" dependsOn "reveal.js",
    skip in packageJSDependencies := false
  ).dependsOn(utilJS)

lazy val codemirror = (project in file("modules/js-bindings/codemirror"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings :_*)
  .settings(
    name := "codemirror",
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.14.2" / "codemirror.js",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.14.2" / "mode/haskell/haskell.js" dependsOn "codemirror.js",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.14.2" / "mode/clike/clike.js" dependsOn "codemirror.js",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.14.2" / "mode/htmlmixed/htmlmixed.js" dependsOn "codemirror.js",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.14.2" / "mode/javascript/javascript.js" dependsOn "codemirror.js",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.14.2" / "mode/css/css.js" dependsOn "codemirror.js",
    skip in packageJSDependencies := false
  ).dependsOn(utilJS)

// ##############################################################
// load server project as default

onLoad in Global :=
  (Command.process("project server", _: State)) compose (onLoad in Global).value
