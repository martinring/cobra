package net.flatmap.cobra

import java.nio.file.{Path, StandardWatchEventKinds, WatchEvent}

import better.files._
import FileWatcher._
import akka.{Done, NotUsed}
import akka.http.scaladsl.model.ws.{BinaryMessage, Message}
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.io
import akka.actor._
import akka.event.{LogSource, Logging}
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpProtocols}
import akka.stream.{ActorMaterializer, OverflowStrategy, scaladsl}
import com.typesafe.config._
import org.webjars.WebJarAssetLocator

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.CacheDirectives
import akka.http.scaladsl.server.Directives._

object CobraServer {
  implicit val logSource: LogSource[CobraServer] = new LogSource[CobraServer] {
    def genString(o: CobraServer): String = o.directory.name
    override def getClazz(o: CobraServer): Class[_] = o.getClass
  }
}

/**
  * Created by martin on 03.02.16.
  */
class CobraServer(val directory: File) {
  lazy val log = Logging(system, this)

  val configPath = directory / "cobra.conf"

  def readConfig() = ConfigFactory.parseFile(
    configPath.toJava
  ).withFallback(ConfigFactory.load().getConfig("cobra"))

  var config = readConfig()

  def title = config.getString("title")
  def slidesTheme = config.getString("theme.slides")
  def codeTheme = config.getString("theme.code")
  def lang = config.getString("language")

  def interface = config.getString("binding.interface")
  def port = config.getInt("binding.port")

  def reveal = config.getConfig("reveal").entrySet().toIterable.map { kv =>
    kv.getKey -> kv.getValue.render(ConfigRenderOptions.concise())
  }.toMap

  def env = config.getConfig("env").entrySet().toIterable.map { kv =>
    kv.getKey.toUpperCase() -> kv.getValue.unwrapped().toString
  }.toMap

  import akka.http.scaladsl.Http
  import akka.http.scaladsl.server._
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.model.ContentType
  import akka.http.scaladsl.model.{HttpEntity, HttpCharsets, MediaTypes}

  implicit val system = ActorSystem(directory.name)
  implicit val materializer = ActorMaterializer()
  import system.dispatcher


  def getConfigs: Source[Config,NotUsed] = {
    val (ref,pub) =
      Source.actorRef[Config](300,OverflowStrategy.dropTail).toMat(Sink.asPublisher(fanout = true))(Keep.both).run()

    val watcher = new ThreadBackedFileMonitor(directory) {
      override def onModify(file: File) = if (file == configPath) {
        Try(config = readConfig()).foreach(_ => ref ! config)
      }
    }

    Source.fromPublisher(pub)
  }

  val configs = getConfigs

  val revealOptions = configs.map { conf =>
    conf.getConfig("reveal").entrySet().toIterable.map { kv =>
      kv.getKey -> kv.getValue.render()
    }.toMap
  }.scan((reveal,true)) {
      case ((o,_),n) => (n,o != n)
    }.collect {
      case (n,true) => RevealOptionsUpdate(n)
    }

  val titles = configs.map(_.getString("title")).scan((title,false)) {
    case ((o,_),n) => (n,o != n)
  }.collect {
    case (n,true) => TitleUpdate(n)
  }

  val languages = configs.map(_.getString("language")).scan((lang,false)) {
    case ((o,_),n) => (n,o != n)
  }.collect {
    case (n,true) => LanguageUpdate(n)
  }

  val themes = configs.map(x => (x.getString("theme.slides"),x.getString("theme.code"))).scan(((slidesTheme,codeTheme),false)) {
    case ((o,_),n) => (n,o != n)
  }.collect {
    case ((slides,code),true) => ThemeUpdate(code,slides)
  }

  val locator = new WebJarAssetLocator()

  val documents = mutable.Map.empty[String,ActorRef]

  def index = io.Source.fromInputStream(
    getClass.getClassLoader.getResourceAsStream(locator.getFullPath("cobra-client","index.html"))
  ).mkString
    .replaceAll("""\{ *language *\}""",lang)
    .replaceAll("""\{ *title *\}""",title)
    .replaceAll("""\{ *theme\.slides *\}""",slidesTheme)
    .replaceAll("""\{ *theme\.code *\}""",codeTheme)

  def routes = respondWithHeader(headers.`Cache-Control`(CacheDirectives.`no-cache`)) { get {
    pathSingleSlash {
      complete(HttpEntity(ContentType(MediaTypes.`text/html`, HttpCharsets.`UTF-8`),  index))
    } ~
    path("socket")(handleWebSocketMessagesForProtocol(socket,"cobra")) ~
    path("lib" / PathMatchers.Segment / PathMatchers.Remaining) {
      (segment,path) =>
        val res = Try(locator.getFullPath(segment,path))
        res.toOption.fold[Route](reject)(getFromResource)
    } ~
    path(_segmentStringToPathMatcher("lib") / "codemirror" / "theme" / "default.css") {
      complete(HttpEntity(ContentType(MediaTypes.`text/css`, HttpCharsets.`UTF-8`), "/* default cm theme */"))
    }
  } ~ getFromDirectory(directory.toString) }

  def deserialize: PartialFunction[Message,Source[ClientMessage,NotUsed]] = {
    case BinaryMessage.Strict(bytes) =>
      Source.single(ClientMessage.read(bytes.asByteBuffer))
    case BinaryMessage.Streamed(bytes) =>
      bytes.reduce(_ ++ _).map(bytes => ClientMessage.read(bytes.asByteBuffer)).mapMaterializedValue(_ => NotUsed)
  }

  def handleRequest(client: ActorRef): ClientMessage => Source[ServerMessage,NotUsed] = {
    case HeartBeat => Source.single(HeartBeat)
    case msg@InitDoc(id,content,mode) =>
      documents.get(id).fold[Unit] {
        log.info(s"initializing new ${mode.name} document '$id'")
        val doc = system.actorOf(SnippetServer.props(env), id)
        doc.tell(msg, client)
        documents += id -> doc
      } { case doc =>
        doc.tell(msg,client)
      }
      Source.empty
    case msg@Edit(id,op,rev) =>
      documents.get(id).foreach(doc => doc.tell(msg,client))
      Source.empty
    case msg@RequestInfo(id,from,to) =>
      documents.get(id).foreach(doc => doc.tell(msg,client))
      Source.empty
    case other =>
      log.error(s"could not handle message $other")
      Source.empty
  }

  def socket: Flow[Message, Message, NotUsed] = {
    val (ref,pub) = Source.actorRef[ServerMessage](300,OverflowStrategy.fail).toMat(Sink.asPublisher(fanout = false))(Keep.both).run()
    val source = Source.fromPublisher(pub)
    Flow[Message].flatMapConcat(deserialize)
      .flatMapMerge(300, handleRequest(ref)).merge(source).merge(revealOptions).merge(titles).merge(languages).merge(themes)
      .map(msg => BinaryMessage.Strict(ByteString(ServerMessage.write(msg))))
      .watchTermination(){ (n: NotUsed, f: Future[Done]) => f.foreach(_ => ref ! PoisonPill); NotUsed }
  }

  var binding = Option.empty[Http.ServerBinding]

  def start() = {
    val binding = Http(system).bindAndHandle(routes, interface, port)

    binding.onComplete {
      case Success(binding) =>
        this.binding = Some(binding)
        val localAddress = binding.localAddress
        log.info("serving presentation from " + directory.toString)
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
