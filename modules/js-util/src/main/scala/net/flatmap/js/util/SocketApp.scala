package net.flatmap.js.util

import java.nio.ByteBuffer

import org.scalajs.dom._
import org.scalajs.dom.raw.WebSocket
import scala.scalajs.js.JSApp
import scala.scalajs.js.typedarray.TypedArrayBufferOps._
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

/**
 * Created by martin on 08.09.15.
 */
abstract class SocketApp[I,O](url: String, protocol: String)(deserialize: ByteBuffer => I, serialize: O => ByteBuffer) extends JSApp {
  var autoReload = true

  private var socket: Option[WebSocket] = None

  def receive: PartialFunction[I,Unit]

  def byteBuffer2ajax(data: ByteBuffer) = {
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
    s.send(byteBuffer2ajax(serialize(msg)))
  })

  def preStart() = ()

  def postStop() = ()

  def main(): Unit = whenReady {
    println("starting")
    val absoluteUrl = if (url.startsWith("ws://")) url
      else if (url.startsWith("/"))
        s"ws://${location.host}$url"
      else
        s"ws://${location.host}/${location.pathname}/url"
    val socket = new WebSocket(absoluteUrl,protocol)
    socket.binaryType = "arraybuffer"
    socket.on(Event.Socket.Open)    (e => socketOpened(socket))
    socket.on(Event.Socket.Message) (e => rawReceive(e.data.asInstanceOf[ArrayBuffer]))
    socket.on(Event.Socket.Close)   (e => socketClosed())
  }

  def socketOpened(socket: WebSocket) {
    this.socket = Some(socket)
    console.log("socket open")
    preStart()
  }

  def rawReceive(msg: ArrayBuffer) = {
    val it = deserialize(TypedArrayBuffer.wrap(msg))
    console.log("received: " + it.toString)
    receive(it)
  }

  def socketClosed(): Unit = {
    this.socket = None
    console.log("socket closed")
    $"#offline".elements.foreach(_.setAttribute("style", "opacity:1;display:block"))
    postStop()
    lazy val retry: Int = setInterval(() => {
      val xhr = new XMLHttpRequest()
      xhr.open("GET", location.href)
      xhr.on(Event.Load) { _ =>
        if (xhr.status == 200) {
          console.log("server is ready again... reloading")
          location.reload()
        }
      }
      xhr.send()
    }, 1000)
    if (autoReload) retry
  }
}
