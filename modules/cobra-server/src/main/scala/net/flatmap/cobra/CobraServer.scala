package net.flatmap.cobra

import java.io.File

import akka.NotUsed
import akka.http.scaladsl.model.ws.{BinaryMessage, Message}
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.io
import akka.actor.ActorSystem
import akka.event.{LogSource, Logging}
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.webjars.WebJarAssetLocator

import scala.util.{Failure, Success, Try}

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
  val slidesTheme = config.getString("theme.slides")
  val codeTheme = config.getString("theme.code")
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

  val index = io.Source.fromInputStream(
    getClass.getClassLoader.getResourceAsStream(locator.getFullPath("cobra-client","index.html"))
  ).mkString
    .replaceAll("""\{ *language *\}""",lang)
    .replaceAll("""\{ *title *\}""",title)
    .replaceAll("""\{ *theme\.slides *\}""",slidesTheme)
    .replaceAll("""\{ *theme\.code *\}""",codeTheme)

  val routes = get {
    pathSingleSlash {
      complete(HttpEntity(ContentType(MediaTypes.`text/html`, HttpCharsets.`UTF-8`),  index))
    } ~
    path("socket")(handleWebSocketMessagesForProtocol(socket,"cobra")) ~
    path("lib" / PathMatchers.Segment / PathMatchers.Rest) {
      (segment,path) =>
        val res = Try(locator.getFullPath(segment,path))
        res.toOption.fold[Route](reject)(getFromResource)
    } ~
    path("lib" / "codemirror" / "theme" / "default.css") {
      complete(HttpEntity(ContentType(MediaTypes.`text/css`, HttpCharsets.`UTF-8`), "/* default cm theme */"))
    }
  } ~ getFromDirectory(directory.getPath)

  val deserialize: PartialFunction[Message,ClientMessage] = {
    case BinaryMessage.Strict(bytes) => ClientMessage.read(bytes.asByteBuffer)
    case other => sys.error(s"incompatible message from client: '$other'")
  }

  val handleRequest: ClientMessage => Source[ServerMessage,NotUsed] = {
    case HeartBeat => Source.single(HeartBeat)
  }

  val socket: Flow[Message, Message, NotUsed] =
    Flow[Message].map(deserialize)
     .flatMapMerge(1000, handleRequest)
     .map(msg => BinaryMessage.Strict(ByteString(ServerMessage.write(msg))))

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
    system.terminate()
  }
}
