package net.flatmap.cobra

import java.io.File

import scala.io.Source
import akka.actor.ActorSystem
import akka.event.{LogSource, Logging}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.webjars.WebJarAssetLocator

import scala.util.{Failure, Success}

object CobraServer {
  implicit val logSource: LogSource[CobraServer] = new LogSource[CobraServer] {
    def genString(o: CobraServer): String = o.directory.getName
    override def getClazz(o: CobraServer): Class[_] = o.getClass
  }
}

/**
  * Created by martin on 03.02.16.
  */
class CobraServer(val directory: File) {
  lazy val log = Logging.apply(system, this)

  val config = ConfigFactory.parseFile(
    new File(directory.getAbsolutePath + File.separator + "cobra.conf")
  ).withFallback(ConfigFactory.load().getConfig("cobra"))

  val title = config.getString("title")
  val theme = config.getString("theme")
  val lang = config.getString("language")

  val interface = config.getString("binding.interface")
  val port = config.getInt("binding.port")

  import akka.http.scaladsl.Http
  import akka.http.scaladsl.server._
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.model.ContentType
  import akka.http.scaladsl.model.{HttpEntity, HttpCharsets, MediaTypes}

  implicit val system = ActorSystem("cobra")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val locator = new WebJarAssetLocator()

  val index = Source.fromURL(getClass.getResource("/index.html")).mkString
    .replaceAll("""\{ *language *\}""",lang)
    .replaceAll("""\{ *title *\}""",title)
    .replaceAll("""\{ *theme *\}""",theme)

  val routes = get {
    pathSingleSlash(complete(HttpEntity(ContentType(MediaTypes.`text/html`, HttpCharsets.`UTF-8`),  index))) ~
    path("lib" / PathMatchers.Rest)(path => getFromResource(locator.getFullPath(path))) ~
    path("js" / PathMatchers.Rest)(path => getFromResource(locator.getFullPath(path))) ~
    path("cobra" / PathMatchers.Rest)(path => getFromResource(path))
  } ~ getFromDirectory(directory.getPath)

  var binding = Option.empty[Http.ServerBinding]

  def start() = {
    val binding = Http(system).bindAndHandle(routes, interface, port)

    binding.onComplete {
      case Success(binding) =>
        this.binding = Some(binding)
        val localAddress = binding.localAddress
        log.info("serving presentation from " + directory.getAbsolutePath)
        log.info(s"server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
      case Failure(e) =>
        log.error(s"binding failed with ${e.getMessage}")
    }
  }

  def stop() = {
    log.info("stopping server...")
    binding.foreach(_.unbind())
    binding = None
    system.shutdown()
  }
}
