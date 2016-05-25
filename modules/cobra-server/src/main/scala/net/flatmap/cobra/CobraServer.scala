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
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.util.control.NonFatal

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
  def slidesTheme(config: Config) = {
    val th = config.getString("theme.slides")
    val thc = if (th.endsWith(".css")) th else th + ".css"
    if (thc.startsWith("/")) thc
    else s"lib/reveal.js/css/theme/$thc"
  }
  def codeTheme(config: Config) = {
    val th = config.getString("theme.code")
    val thc = if (th.endsWith(".css")) th else th + ".css"
    if (thc.startsWith("/")) thc
    else s"lib/codemirror/theme/$thc"
  }
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


  val configsPublisher: Publisher[Config] = new Publisher[Config] {
    val listeners: mutable.Map[Subscriber[_ >: Config],Long] = mutable.Map.empty

    private def send(listener: Subscriber[_ >: Config]) = {
      try {
        listener.onNext(config)
        listeners(listener) -= 1
      } catch {
        case NonFatal(e) => log.error("could not update config")
      }
    }

    private def publish() = {
      listeners.filter(_._2 > 0).foreach(x => send(x._1))
    }

    configPath.newWatcher(false) ! on(StandardWatchEventKinds.ENTRY_MODIFY) {
      case _ => try {
        config = readConfig()
        publish()
      } catch {
        case NonFatal(e) => log.error("could not read config: " + e.getMessage)
      }
    }

    override def subscribe(s: Subscriber[_ >: Config]): Unit = {
      listeners += s -> 0
      val sub = new Subscription {
        override def cancel(): Unit = listeners -= s
        override def request(n: Long): Unit = if (n > 0) {
          listeners(s) = n
          send(s)
        }
      }
      s.onSubscribe(sub)
    }
  }

  val configs = Source.fromPublisher(configsPublisher)

  def revealOptions = configs.map { conf =>
    conf.getConfig("reveal").entrySet().toIterable.map { kv =>
      kv.getKey -> kv.getValue.render()
    }.toMap
  }.scan((reveal,true)) {
      case ((o,_),n) => (n,o != n)
    }.collect {
      case (n,true) => RevealOptionsUpdate(n)
    }

  def titles = configs.map(_.getString("title")).scan((title,true)) {
    case ((o,_),n) => (n,o != n)
  }.collect {
    case (n,true) => TitleUpdate(n)
  }

  def languages = configs.map(_.getString("language")).scan((lang,true)) {
    case ((o,_),n) => (n,o != n)
  }.collect {
    case (n,true) => LanguageUpdate(n)
  }

  def themes = configs.map(x => (slidesTheme(x),codeTheme(x))).scan(((slidesTheme(config),codeTheme(config)),false)) {
    case ((o,_),n) => (n,o != n)
  }.collect {
    case ((slides,code),true) => ThemeUpdate(code,slides)
  }

  val locator = new WebJarAssetLocator()

  val documents = mutable.Map.empty[String,ActorRef]

  val indexRaw = io.Source.fromInputStream(
    getClass.getClassLoader.getResourceAsStream(locator.getFullPath("cobra-client","index.html"))
  ).mkString

  def index = indexRaw
    .replaceAll("""\{ *language *\}""",lang)
    .replaceAll("""\{ *title *\}""",title)
    .replaceAll("""\{ *theme\.slides *\}""",slidesTheme(config))
    .replaceAll("""\{ *theme\.code *\}""",codeTheme(config))
    .replaceAll("""\{ *theme\.code\.name *\}""",codeTheme(config).split("/").last.dropRight(4))


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

  val fileWatchers = mutable.Map.empty[File,Source[FileUpdate,NotUsed]]

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
    case WatchFile(path) =>
      val file = directory / path
      fileWatchers.getOrElseUpdate(file,{
        val (ref,pub) = Source.actorRef[FileUpdate](300,OverflowStrategy.fail).toMat(Sink.asPublisher(fanout = true))(Keep.both).run()
        val source = Source.fromPublisher(pub)
        file.newWatcher(false) ! when(
          StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY) {
          case (StandardWatchEventKinds.ENTRY_MODIFY, file) =>
            ref ! FileUpdate(directory.relativize(file).toString)
        }
        Source.fromPublisher(pub)
      })
    case msg: SnippetMessage =>
      documents.get(msg.id).foreach(doc => doc.tell(msg,client))
      Source.empty
    case other =>
      log.error(s"could not handle message $other")
      Source.empty
  }

  def configStream: Source[ServerMessage,NotUsed] =
    revealOptions
    .merge(titles)
    .merge(themes)
    .merge(languages)

  def socket: Flow[Message, Message, NotUsed] = {
    val (ref,pub) = Source.actorRef[ServerMessage](300,OverflowStrategy.fail).toMat(Sink.asPublisher(fanout = false))(Keep.both).run()
    val source = Source.fromPublisher(pub)
    Flow[Message].flatMapConcat(deserialize)
      .flatMapMerge(300, handleRequest(ref)).merge(source).merge(configStream)
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
