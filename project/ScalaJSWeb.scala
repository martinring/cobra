package net.flatmap.js

import sbt.Keys._
import sbt._

object ScalaJSWeb {
  def webjarDependenciesOf(p: Project): Def.Initialize[Seq[ModuleID]] = Def.setting {
    val deps = (libraryDependencies in p).value ++
      libraryDependencies.all(ScopeFilter(inDependencies(p))).value.flatten
    deps.filter { module =>
      module.organization.startsWith("org.webjars")
    }.distinct
  }
}