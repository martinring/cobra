# **Cobra** <img src="https://travis-ci.org/flatmap/cobra.svg?branch=master"/>

Cobra is a modern code and proof presentation framework, leveraging cutting-edge presentation technology together with a state of the art interactive theorem prover to present formalized mathematics as active documents. Cobra provides both an easy way to present proofs and a novel approach to auditorium interaction. The presentation is checked live by the theorem prover, and moreover allows live changes both by the presenter as well as the audience.

Cobra currently supports **Isabelle** proofs as well as **Scala** and **Haskell** code

## Download **Cobra** 0.9

There is a pre built binary relase for Cobra. 

> **Note, that Java 8 or higher is required for cobra to run.**
> **It will fail to start, when used with Java 7 or below!**

All platforms: [zip](https://github.com/flatmap/cobra/raw/gh-pages/modules/cobra-server/target/universal/cobra-0.9.zip) | Fedora: [rpm](https://github.com/flatmap/cobra/raw/gh-pages/modules/cobra-server/target/rpm/RPMS/noarch/cobra-0.9-1.noarch.rpm)

## Getting Started

 * **Installation**:
   * **rpm**: just install using your package manager
   * **zip**: extract to arbitrary location and add `bin/cobra` / `bin/cobra.bat` to your path
 * **Create a new presentation**: call `cobra new` from the command line and follow the instructions
 * **Start the presentation server**: call `cobra` in the directory of the presentation.
 * **View the presentation**: Navigate to localhost:8080 with your web browser.
 * **Edit your presentation**: Configuration can be edited in `cobra.conf`, content in `slides.html`.

## License

Cobra is Licensed under LGPL

## Libraries used by **Cobra**

| Name | Author | License | Usage |
| ---- | ------ | ------- | ---- |
| [reveal.js](https://github.com/hakimel/reveal.js) | [Hakim El Hattab](https://github.com/hakimel) | [MIT](https://raw.githubusercontent.com/hakimel/reveal.js/master/LICENSE) | Bundeled
| [CodeMirror](https://codemirror.net/) | [Marijn Haverbeke](http://marijnhaverbeke.nl/) | [MIT](https://raw.githubusercontent.com/codemirror/CodeMirror/master/LICENSE) | Bundeled
| [MathJax](https://www.mathjax.org/) | [MathJax Consortium](https://www.mathjax.org/#about) | [Apache 2.0](https://github.com/mathjax/MathJax/blob/master/LICENSE) | Bundeled
| [akka / akka-http](http://akka.io/) | [Akka Team](http://akka.io/team/), [Lightbend](https://www.lightbend.com/) | [Apache 2.0](https://github.com/akka/akka/blob/master/LICENSE) | Library
| [Isabelle](https://isabelle.in.tum.de/index.html) | [University of Cambridge](http://www.cl.cam.ac.uk/research/hvg/Isabelle/Cambridge/), [TU Munich](http://www21.in.tum.de/) | [BSD](https://isabelle.in.tum.de/dist/Isabelle2016/COPYRIGHT) | Library / Optional Dependency
| [ghc-mod](http://www.mew.org/~kazu/proj/ghc-mod/en/) | [IIJ Innovation Institute Inc](http://www.iij-ii.co.jp/en/) | [BSD3/AGPL3](https://github.com/DanielG/ghc-mod/blob/master/LICENSE) | Optional Dependency
| [scalac](http://www.scala-lang.org/) | [EPFL](https://www.epfl.ch/), [Lightbend](https://www.lightbend.com/) | [Scala License](http://www.scala-lang.org/license.html) | Library
| [Scala Refactoring Library](https://github.com/scala-ide/scala-refactoring#the-scala-refactoring-library) | [Mirko Stocker](http://misto.ch/) |  [Scala License](http://www.scala-lang.org/license.html) | Library
| [Scala.js](https://www.scala-js.org/) | [Sébastien Doeraene](http://lampwww.epfl.ch/~doeraene/) | [Scala License](http://www.scala-lang.org/license.html) | Compile Time

### Other Libraries and Compile Time Dependencies

[octicons](https://octicons.github.com/) (SIL OFL 1.1), [LOGBack](http://logback.qos.ch/) (EPL 1.0 / LGPL 2.1), [Typesafe Config](https://github.com/typesafehub/config) (Apache 2.0), [webjars-locator](https://github.com/webjars/webjars-locator) (MIT), [sbt-revolver](https://github.com/spray/sbt-revolver) (Apache 2.0), [SBT Native Packager](http://www.scala-sbt.org/sbt-native-packager/) (MIT), [sbt-web](https://github.com/sbt/sbt-web) (Apache 2.0), [sbt-play-scalajs](https://github.com/vmunier/sbt-play-scalajs) (All Rights Reserved), [scala-js-dom](https://github.com/scala-js/scala-js-dom) (MIT), [BooPickle](https://github.com/ochrons/boopickle) (MIT), [better-files](https://github.com/pathikrit/better-files) (MIT)
