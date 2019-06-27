package net.flatmap.cobra

import better.files.File.OpenOptions
import better.files._
import better.files.Dsl.SymbolicOperations

/**
  * Created by martin on 03.02.16.
  */
object Cobra extends App {
  val version = "1.0.6"

  private def assume(cond: Boolean, msg: String) = if (!cond) {
    println("failed to initialize: " + msg)
    sys.exit()
  }

  private def printLogo() = {
    println( """            ____      _                     """)
    println( """           / ___|___ | |__  _ __ __ _ TM    """)
    println( """          | |   / _ \| '_ \| '__/ _` |      """)
    println( """          | |__| (_) | |_) | | | (_| |      """)
    println( """           \____\___/|_.__/|_|  \__,_|      """)
    println( """______________________________________________""")
    println(s"""| version $version - (c) 2016-2019 Martin Ring |""")
    println()
  }

  if ((args.length == 1 || args.length == 2) && args.head == "new") {
    printLogo()
    val name = if (args.length > 1) args(1) else {
      scala.io.StdIn.readLine("please enter a name for the new presentation: ")
    }
    println(s"creating new cobra presentation '$name'...")
    val dir = File(name)
    assume(!dir.exists() || !dir.isDirectory, s"directory '$name' exists already")
    assume(dir.createDirectories().exists, "could not create directory")

    val conf = scala.io.Source.fromURL(getClass.getResource("/template-cobra.conf")).mkString
      .replaceAll("\\{\\s*title\\s*\\}", s""""$name"""")
      .replaceAll("\\{\\s*lang\\s*\\}", '"' + System.getProperty("user.language") + '"')    
    val slides = scala.io.Source.fromURL(getClass.getResource("/template-slides.html")).mkString
      .replaceAll("\\{\\s*title\\s*\\}", name)

    (dir / "cobra.conf").createIfNotExists() < (conf)
    (dir / "slides.html").createIfNotExists() < (slides)

    println("the presentation has been successfully initialized.")
    println(s"you may start presentation with 'cobra $name'")

    sys.exit()
  }

  if (args.length > 1) {
    println(s"invalid arguments: '${args.mkString(" ")}'")
    sys.exit(-1)
  }

  val directory = if (args.isEmpty) File(".") else File(args.head)

  { // initialize
    printLogo()
    assume(directory.exists, "could not find " + directory)
    assume(directory.isDirectory, directory + " is not a directory")
    assume(directory.isReadable, "can not read " + directory)
    assume((directory / "slides.html").exists(), "no slides.html found")
    assume((directory / "cobra.conf").exists(), "no cobra.conf found")

    val server = new CobraServer(directory)
    server.start()
    while (scala.io.StdIn.readLine != "exit") ()
    server.stop()
  }
}
