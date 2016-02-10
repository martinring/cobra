import net.flatmap.js.webjar.ScalaJsWeb

val commonSettings = Seq(
  scalaVersion := "2.11.7",
  version := "0.1",
  organization := "net.flatmap",
  scalacOptions ++= Seq("-deprecation","-feature")
)

lazy val server = (project in file("modules/cobra-server"))
  .settings(commonSettings :_*)
  .settings(
    name := "cobra.server",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.0.3",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9",
    libraryDependencies += "org.webjars" % "webjars-locator" % "0.28"
  ).dependsOn(commonJVM, client)

lazy val client     = (project in file("modules/cobra-client"))
  .settings(commonSettings :_*)
  .enablePlugins(ScalaJsWeb)
  .settings(
    name := "cobra.client",
    includeFilter in (Assets, LessKeys.less) := "cobra.less"
  ).dependsOn(reveal,codemirror,utilJS,commonJS)

lazy val utilJS = (project in file("modules/js-util"))
  .settings(commonSettings :_*)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "util.js",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    skip in packageJSDependencies := false
  )


lazy val common = (crossProject in file("modules/cobra-common"))
  .settings(commonSettings :_*)
  .settings(
    name := "cobra.common"
  ).jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
  )

lazy val commonJS = common.js
lazy val commonJVM = common.jvm

// ##############################################################
// js Bindings

lazy val reveal = (project in file("modules/js-bindings/reveal-js"))
  .settings(commonSettings :_*)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "reveal.js",
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    jsDependencies += "org.webjars.bower" % "reveal.js" % "3.2.0" / "reveal.js",
    skip in packageJSDependencies := false
  ).dependsOn(utilJS)

lazy val codemirror = (project in file("modules/js-bindings/codemirror"))
  .settings(commonSettings :_*)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "codemirror",
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.11.0" / "codemirror.js",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.11.0" / "mode/clike/clike.js" dependsOn "codemirror.js",
    skip in packageJSDependencies := false
  ).dependsOn(utilJS)

// ##############################################################
// load server project as default

onLoad in Global :=
  (Command.process("project server", _: State)) compose (onLoad in Global).value
