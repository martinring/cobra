package net.flatmap.cobra

case class Client[T](val rev: Long, pending: Option[Operation[T]] = None, buffer: Option[Operation[T]] = None) {
  def localEdit(operation: Operation[T]): (Boolean, Client[T]) = 
    (pending.isEmpty, Client(rev, pending orElse Some(operation), buffer.map(Operation.compose(_,operation).get)))
    
  def remoteEdit(operation: Operation[T]): (Operation[T], Client[T]) = { 
    val t1 = pending.map(Operation.transform(_, operation).get)
    val t2 = buffer.flatMap(buf => t1.map { case (a1,b1) => Operation.transform(buf, b1).get } )
    (t2.map(_._2) orElse t1.map(_._2) getOrElse operation, Client(rev + 1, t1.map(_._1), t2.map(_._1)))
  }
  
  def ack: (Option[Operation[T]], Client[T]) = 
    (buffer, Client(rev + 1, buffer, None))
}