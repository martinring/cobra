package net.flatmap.cobra

import java.nio.ByteBuffer
import boopickle.Default._

sealed abstract trait ClientMessage
sealed abstract trait ServerMessage
case object Ping extends ClientMessage
case object Pong extends ServerMessage

object ClientMessage {
  def read(bytes: ByteBuffer): ClientMessage = Unpickle[ClientMessage].fromBytes(bytes)
  def write(message: ClientMessage): ByteBuffer = Pickle.intoBytes(message)
}

object ServerMessage {
  def read(bytes: ByteBuffer): ServerMessage = Unpickle[ServerMessage].fromBytes(bytes)
  def write(message: ServerMessage): ByteBuffer = Pickle.intoBytes(message)
}