package net.flatmap.cobra

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{ConfigException, ConfigFactory}

import scala.util.{Failure, Success}

/**
  * Created by martin on 03.02.16.
  */
object Cobra extends App {
  private def assume(cond: Boolean, msg: String) = if (!cond) {
    println("failed to initialize: " + msg)
    sys.exit()
  }

  private def printLogo() = {
    println("""     ____      _                  """)
    println("""    / ___|___ | |__  _ __ __ _    """)
    println("""   | |   / _ \| '_ \| '__/ _` |   """)
    println("""   | |__| (_) | |_) | | | (_| |   """)
    println("""    \____\___/|_.__/|_|  \__,_|   """)
    println("""__________________________________""")
    println("""| version 1.0 - 2016 Martin Ring |""")
    println("")
  }

  if (args.nonEmpty && args.head == "new") {
    printLogo()
    val name = if (args.length > 1) args(1) else {
      scala.io.StdIn.readLine("please enter a name for the new presentation: ")
    }
    println(s"creating new cobra presentation '$name'...")
    val dir = new File(name)
    assume(!dir.exists() || !dir.isDirectory, s"directory '$name' exists already")
    assume(dir.mkdir(), "could not create directory")
    val isa_home = (sys.env.get("ISABELLE_HOME") orElse {
      val tryPaths = Set(
        "/usr/local/Isabelle2016",
        "~/Isabelle2016",
        "~/bin/Isabelle2016",
        "/opt/Isabelle2016")
      tryPaths.find { prefix =>
        val exists = new File(prefix + "/bin/isabelle").exists()
        if (exists) println(s"found isabelle distribution at $prefix")
        exists
      }
    }).getOrElse {
      println("could not automatically determine isabelle 2016 install location")
      scala.io.StdIn.readLine("please enter isabelle install location: ")
    }

    assume(new File(isa_home + "/bin/isabelle").exists(), s"no isabelle distribution at $isa_home")

    val conf = scala.io.Source.fromURL(getClass.getResource("/template-cobra.conf")).mkString
      .replaceAll("\\{\\s*title\\s*\\}", s""""$name"""")
      .replaceAll("\\{\\s*lang\\s*\\}", '"' + System.getProperty("user.language") + '"')
      .replaceAll("\\{\\s*isa_home\\s*\\}", s""""$isa_home"""")

    val slides = scala.io.Source.fromURL(getClass.getResource("/template-slides.html")).mkString
      .replaceAll("\\{\\s*title\\s*\\}", name)

    val bw = new BufferedWriter(new FileWriter(new File(dir.getPath + "/cobra.conf")))
    bw.write(conf)
    bw.close()

    val bw2 = new BufferedWriter(new FileWriter(new File(dir.getPath + "/slides.html")))
    bw2.write(slides)
    bw2.close()

    println("the presentation has been successfully initialized.")
    println(s"you may start presentation with 'cobra $name'")

    sys.exit()
  }

  val directory = if (args.isEmpty) new File(".").getCanonicalFile else new File(args.head)

  { // initialize
    printLogo()
    assume(directory.exists, "could not find " + directory.getAbsolutePath)
    assume(directory.isDirectory, directory.getPath + " is not a directory")
    assume(directory.canRead, "can not read " + directory.getPath)
    assume(new File(directory.getPath + File.separator + "slides.html").exists(), "no slides.html found")
    assume(new File(directory.getPath + File.separator + "cobra.conf").exists(), "no cobra.conf found")

    val server = new CobraServer(Paths.get(directory.getCanonicalFile.toURI))
    server.start()
    while (scala.io.StdIn.readLine != "exit") ()
    server.stop()
  }
}
