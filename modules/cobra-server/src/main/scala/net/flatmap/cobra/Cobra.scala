package net.flatmap.cobra

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{ConfigException, ConfigFactory}

import scala.util.{Success, Failure}

/**
  * Created by martin on 03.02.16.
  */
object Cobra extends App {
  if (args.nonEmpty && args.head == "new") {
    printLogo()
    val name = if (args.length > 1) args(1) else {
      scala.io.StdIn.readLine("please enter a name for the new presentation: ")
    }
    println(s"creating new cobra presentation '$name'...")
    sys.exit()
  }

  val directory = if (args.isEmpty) new File(".").getCanonicalFile else new File(args.head)

  private def assume(cond: Boolean, msg: String) = if (!cond) {
    println("failed to start cobra: " + msg)
    sys.exit(-1)
  }

  private def printLogo() = {
    println("""     ____      _                  """)
    println("""    / ___|___ | |__  _ __ __ _    """)
    println("""   | |   / _ \| '_ \| '__/ _` |   """)
    println("""   | |__| (_) | |_) | | | (_| |   """)
    println("""    \____\___/|_.__/|_|  \__,_|   """)
    println("""__________________________________""")
    println("""| version 0.1 - 2016 Martin Ring |""")
    println("")
  }

  { // initialize
    printLogo()
    assume(directory.exists, "could not find " + directory.getAbsolutePath)
    assume(directory.isDirectory, directory.getPath + " is not a directory")
    assume(directory.canRead, "can not read " + directory.getPath)
    assume(new File(directory.getPath + File.separator + "slides.html").exists(), "no slides.html found")
    assume(new File(directory.getPath + File.separator + "cobra.conf").exists(), "no cobra.conf found")

    val server = new CobraServer(directory)
    server.start()
    while (scala.io.StdIn.readLine != "exit") ()
    server.stop()
  }
}
