package net.flatmap.collaboration

import scala.util.Success

case class Client[T](val rev: Long, pending: Option[Operation[T]] = None, buffer: Option[Operation[T]] = None) {
  def localEdit(operation: Operation[T]): (Boolean, Client[T]) = {
    val newPending = pending orElse Some(operation)
    val newBuffer = pending.map(_ => (buffer ++ Some(operation)).reduce(Operation.compose(_,_).get))
    (pending.isEmpty,Client(rev,newPending,newBuffer))
  }

  def remoteEdit(operation: Operation[T]): (Operation[T], Client[T]) = (pending,buffer) match {
    case (None,None) => (operation,Client(rev + 1, None, None))
    case (Some(p),None) =>
      val Success((a,b)) = Operation.transform(p,operation)
      (b,Client(rev + 1, Some(a), None))
    case (Some(p),Some(b)) =>
      val Success((a1,b1)) = Operation.transform(p,operation)
      val Success((a2,b2)) = Operation.transform(b,b1)
      (b2,Client(rev + 1, Some(a1), Some(a2)))
  }

  def ack: (Option[Operation[T]], Client[T]) =
    (buffer, Client(rev + 1, buffer, None))

  def isSynchronized = pending.isEmpty
}

trait EditorInterface[T] {
  def applyOperation(operation: Operation[T])
  def sendOperation(operation: Operation[T], revision: Long)
  def applyAnnotations(aid: String, annotations: Annotations)
  def sendAnnotations(aid: String, annotations: Annotations, revision: Long)
}

trait ClientInterface[T] {
  def serverAck()
  def remoteEdit(operation: Operation[T])
  def remoteAnnotations(aid: String, annotations: Annotations)
  def localEdit(operation: Operation[T])
  def localAnnotations(aid: String, annotations: Annotations)
  def reset(revision: Long)
}

object ClientInterface {
  def apply[T](editorInterface: EditorInterface[T], rev: Long = 0) = new ClientInterface[T] {
    private var client = new Client[T](rev)

    def serverAck() = {
      val (send,nc) = client.ack
      client = nc
      send.foreach(editorInterface.sendOperation(_,client.rev))
    }

    def remoteEdit(operation: Operation[T]) = {
      val (apply,nc) = client.remoteEdit(operation)
      client = nc
      editorInterface.applyOperation(apply)
    }

    def remoteAnnotations(aid: String, annotations: Annotations) = {
      val pending =
        client.pending.map(pending => client.buffer.fold(pending)(buffer => Operation.compose(pending,buffer).get))
      val transformedAnnotations = pending.fold(annotations) { pending =>
        Annotations.transform(annotations,pending).get
      }
      editorInterface.applyAnnotations(aid, transformedAnnotations)
    }

    def combinedRemoteEdit(operation: Operation[T], revisions: Long) = {
      val (apply,nc) = client.remoteEdit(operation)
      client = nc.copy(rev = client.rev + revisions)
      editorInterface.applyOperation(apply)
    }

    def localEdit(operation: Operation[T]) = {
      val (send,nc) = client.localEdit(operation)
      client = nc
      if (send) editorInterface.sendOperation(operation, client.rev)
    }

    def localAnnotations(aid: String, annotations: Annotations) = {
      if (client.isSynchronized) editorInterface.sendAnnotations(aid, annotations, client.rev)
    }

    def reset(revision: Long) = {
      client = new Client[T](revision)
    }
  }
}