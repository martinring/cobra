package net.flatmap.cobra

import akka.actor.Props

/**
  * Created by martin on 19.05.16.
  */
trait LanguageService {
  def props(env: Map[String,String]): Props
}
