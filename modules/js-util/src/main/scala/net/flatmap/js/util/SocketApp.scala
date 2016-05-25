package net.flatmap.js.util

import java.nio.ByteBuffer

import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket

import scala.scalajs.js.JSApp
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray._
import scala.scalajs.js

/**
 * Created by martin on 08.09.15.
 */
abstract class SocketApp[I,O](
  url: String,
  protocol: String,
  heartbeatMessage: O,
  heartbeatAcknowledge: I,
  autoReload: Boolean = true)(deserialize: ByteBuffer => I, serialize: O => ByteBuffer) extends JSApp {

  private var socket: Option[WebSocket] = None

  def receive: PartialFunction[I,Unit]

  def byteBuffer2ajax(data: ByteBuffer): Int8Array = {
    if (data.hasTypedArray()) {
      // get relevant part of the underlying typed array
      data.typedArray().subarray(data.position, data.limit)
    } else {
      // fall back to copying the data
      val tempBuffer = ByteBuffer.allocateDirect(data.remaining)
      val origPosition = data.position
      tempBuffer.put(data)
      data.position(origPosition)
      tempBuffer.typedArray()
    }
  }

  def send(msg: O) = socket.fold(
    sys.error(s"trying to send $msg while socket is closed")
  )(
  (s: WebSocket) => {
    console.log("[debug] send: ", msg.toString)
    s.send(byteBuffer2ajax(serialize(msg)).buffer)
  })

  def preStart() = ()

  def postStop() = ()

  def main(): Unit = whenReady {
    println("starting")
    val absoluteUrl = if (url.startsWith("ws://")) url
      else if (url.startsWith("/"))
        s"ws://${window.location.host}$url"
      else
        s"ws://${window.location.host}/${window.location.pathname}/url"
    val socket = new WebSocket(absoluteUrl,protocol)
    socket.binaryType = "arraybuffer"
    socket.on(Event.Socket.Open)(e => socketOpened(socket))
    socket.on(Event.Socket.Message)(e => rawReceive(e.data.asInstanceOf[ArrayBuffer]))
    socket.on(Event.Socket.Close)(e => socketClosed())
    window.on(Event.Window.BeforeUnload) { e =>
      if (socket.readyState != WebSocket.CLOSED) {
        console.log("[debug] closing socket before leaving page")
        socket.close()
      }
    }
  }

  private val heartbeatInterval = 5000

  private var heartbeatAcknowledged = true

  private def socketOpened(socket: WebSocket) {
    this.socket = Some(socket)
    console.info("socket open")
    preStart()
    lazy val interval: Int = schedule(heartbeatInterval) {
      if (!heartbeatAcknowledged) {
        console.error(s"server did not respond to heartbeat message for ${heartbeatInterval}ms")
        socket.close()
        window.clearInterval(interval)
      } else {
        heartbeatAcknowledged = false
        send(heartbeatMessage)
      }
    }
    interval
  }

  private def rawReceive(msg: ArrayBuffer) = {
    val it = deserialize(TypedArrayBuffer.wrap(msg))
    console.log("[debug] received: " + it.toString)
    it match {
      case `heartbeatAcknowledge` =>
        heartbeatAcknowledged = true
      case other => receive(it)
    }
  }

  private def socketClosed(): Unit = {
    this.socket = None
    console.log("socket closed")
    $"#connection".classes.remove("online")
    postStop()
    lazy val retry: Int = window.setInterval(() => {
      val xhr = new XMLHttpRequest()
      xhr.open("GET", window.location.href)
      xhr.on(Event.Progress.Load) { _ =>
        if (xhr.status == 200) {
          console.log("server is ready again... reloading")
          window.location.reload()
        }
      }
      xhr.send()
    }, 1000)
    if (autoReload) retry
  }
}
