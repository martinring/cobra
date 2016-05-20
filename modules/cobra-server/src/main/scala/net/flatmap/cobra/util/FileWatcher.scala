package net.flatmap.cobra.util


/**
  * TAKEN FROM https://github.com/mcaserta/swatch
  * Apache License applies
  */

import akka.actor.ActorRef

import concurrent.{ExecutionContext, Future}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.WatchEvent.Kind

import language.implicitConversions
import org.slf4j.LoggerFactory

import util.{Failure, Success, Try}

/**
  * A wrapper for a Java 7 [[java.nio.file.WatchService]].
  */
object FileWatcher {

  val log = LoggerFactory.getLogger(getClass)

  type Listener = (SwatchEvent) ⇒ Unit

  sealed trait EventType

  case object Create extends EventType

  case object Modify extends EventType

  case object Delete extends EventType

  case object Overflow extends EventType

  sealed trait SwatchEvent {
    def path: Path
  }

  case class Create(path: Path) extends SwatchEvent

  case class Modify(path: Path) extends SwatchEvent

  case class Delete(path: Path) extends SwatchEvent

  private[this] implicit def eventType2Kind(et: EventType) = {
    import java.nio.file.StandardWatchEventKinds._

    et match {
      case Create ⇒ ENTRY_CREATE
      case Modify ⇒ ENTRY_MODIFY
      case Delete ⇒ ENTRY_DELETE
      case Overflow ⇒ OVERFLOW
    }
  }

  private[this] implicit def kind2EventType(kind: Kind[Path]) = {
    import java.nio.file.StandardWatchEventKinds._

    kind match {
      case ENTRY_CREATE ⇒ Create
      case ENTRY_MODIFY ⇒ Modify
      case ENTRY_DELETE ⇒ Delete
      case _ ⇒ Overflow
    }
  }

  implicit def string2path(path: String): Path = Paths.get(path)

  /**
    * Message class for the SwatchActor.
    *
    * @param path the path to watch
    * @param eventTypes event types to watch for
    * @param recurse should subdirs be watched too?
    * @param listener an optional [[akka.actor.ActorRef]]
    *                 where notifications will get sent to;
    *                 if unspecified, the [[akka.actor.Actor#sender]]
    *                 ref will be used
    */
  case class Watch(path: Path, eventTypes: Seq[EventType], recurse: Boolean = false, listener: Option[ActorRef] = None)

  /**
    * Watch the given path by using a Java 7
    * [[java.nio.file.WatchService]].
    *
    * @param path the path to watch
    * @param eventTypes event types to watch for
    * @param listener events will be sent here
    * @param recurse should subdirs be watched too?
    */
  def watch(path: Path,
            eventTypes: Seq[EventType],
            listener: Listener,
            recurse: Boolean = false)(implicit executionContext: ExecutionContext) {
    log.debug(s"watch(): entering; path='$path', eventTypes='$eventTypes', listener='$listener', recurse=$recurse")
    if (recurse) {
      Files.walkFileTree(path, new SimpleFileVisitor[Path] {
        override def preVisitDirectory(path: Path, attrs: BasicFileAttributes) = {
          watch(path, eventTypes, listener)
          FileVisitResult.CONTINUE
        }
      })
    } else {
      val watchService = FileSystems.getDefault.newWatchService
      path.register(watchService, eventTypes map eventType2Kind: _*)

      Future {
        import collection.JavaConversions._
        var loop = true

        while (loop) {
          Try(watchService.take) match {
            case Success(key) =>
              key.pollEvents map {
                event =>
                  import java.nio.file.StandardWatchEventKinds.OVERFLOW

                  event.kind match {
                    case OVERFLOW => // weeee
                    case _ =>
                      val ev = event.asInstanceOf[WatchEvent[Path]]
                      val tpe = kind2EventType(ev.kind)
                      val notification = tpe match {
                        case Create ⇒ Create(path.resolve(ev.context))
                        case Modify ⇒ Modify(path.resolve(ev.context))
                        case Delete ⇒ Delete(path.resolve(ev.context))
                      }
                      log.info(s"watch(): notifying listener; notification=$notification")
                      listener(notification)
                      if (!key.reset) {
                        log.debug("watch(): reset unsuccessful, exiting the loop")
                        loop = false
                      }
                  }
              }
            case Failure(e) ⇒ // ignore failure, just as IRL
          }
        }
      }
    }

    log.debug(s"watch(): exiting; path='$path', eventTypes='$eventTypes', listener='$listener', recurse=$recurse")
  }

}



import akka.actor.{Actor, ActorLogging}

class FileWatcherActor extends Actor with ActorLogging {

  import FileWatcher._

  implicit val dispatcher = context.system.dispatcher

  def receive = {
    case Watch(path, eventTypes, recurse, listener) ⇒
      log.debug(s"receive(): got a watch request; path='$path', eventTypes=$eventTypes, recurse=$recurse, listener='$listener'")

      val senderRef = listener getOrElse sender

      val lstnr = {
        event: SwatchEvent ⇒
          log.debug(s"receive(): notifying; event='$event', senderRef='$senderRef'")
          senderRef ! event
      }

      watch(path, eventTypes, lstnr, recurse)
  }

}