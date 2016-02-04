val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "net.flatmap"
)

lazy val server = (project in file("modules/cobra-server"))
  .settings(commonSettings :_*)    
  .enablePlugins(SbtWeb)
  .settings(
    name := "cobra.server",
    libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.0.3",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.9",
    libraryDependencies += "org.webjars" % "webjars-locator" % "0.28",
    libraryDependencies += "org.webjars.bower" % "reveal.js" % "3.2.0",   
    libraryDependencies += "org.webjars.bower" % "codemirror" % "5.11.0",
    (resourceGenerators in Compile) <+=
      (fastOptJS in Compile in js, packageScalaJSLauncher in Compile in js, packageJSDependencies in Compile in js)
        .map((f1,f2,f3) => Seq(f1.data,f2.data,f3))
  )

lazy val js     = (project in file("modules/cobra-js"))
  .settings(commonSettings :_*)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "cobra.js",
    unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    libraryDependencies += "me.chrons" %%% "boopickle" % "1.1.0",
    jsDependencies += "org.webjars.bower" % "codemirror" % "5.11.0" / "codemirror.js",
    jsDependencies += "org.webjars.bower" % "reveal.js" % "3.2.0" / "reveal.js",
    skip in packageJSDependencies := false
  )

lazy val common = (crossProject in file("modules/cobra-common"))
  .settings(commonSettings :_*)
  .settings(
    name := "cobra.common"
  )

lazy val commonJS = common.js
lazy val commonJVM = common.jvm

// ##############################################################
// load server project as default

onLoad in Global :=
  (Command.process("project server", _: State)) compose (onLoad in Global).value
