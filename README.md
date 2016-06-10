# **Cobra** <img src="https://travis-ci.org/flatmap/cobra.svg?branch=master"/>

Cobra is a modern code and proof presentation framework, leveraging cutting-edge presentation technology together with a state of the art interactive theorem prover to present formalized mathematics as active documents. Cobra provides both an easy way to present proofs and a novel approach to auditorium interaction. The presentation is checked live by the theorem prover, and moreover allows live changes both by the presenter as well as the audience.

Cobra currently supports [**Isabelle**](https://isabelle.in.tum.de/index.html) proofs as well as [**Scala**](http://www.scala-lang.org/) and [**Haskell**](http://haskell.org) code

## Download **Cobra** 1.0

There is a pre built binary relase for Cobra.

> **Note, that Java 8 or higher is required for cobra to run.**
> **It will fail to start, when used with Java 7 or below!**

All platforms: [zip](https://github.com/flatmap/cobra/raw/master/modules/cobra-server/target/universal/cobra-1.0.zip) | Fedora: [rpm](https://github.com/flatmap/cobra/raw/master/modules/cobra-server/target/rpm/RPMS/noarch/cobra-1.0-1.noarch.rpm)

## Getting Started

 * **Installation**:
   * **rpm**: just install using your package manager
   * **zip**: extract to arbitrary location and add `bin/cobra` / `bin/cobra.bat` to your path
 * **Create a new presentation**: call `cobra new` from the command line and follow the instructions
 * **Start the presentation server**: call `cobra` in the directory of the presentation.
 * **View the presentation**: Navigate to localhost:8080 with your web browser.
 * **Edit your presentation**: Configuration can be edited in `cobra.conf`, content in `slides.html`.

### Presentation Format

The content of a presentation is stored in a file called `slides.html`. Cobra will support a MarkDown slideformat in Version 1.1.

To add a slide, simply add `<section>` tags to the `slides.html` file. For the general slide format please refer to the **reveal.js** [documentation](https://github.com/hakimel/reveal.js).

#### Including Code Snippets

The simplest option is to include inline code snippets:

```html
<code class="scala">
  case class Person(name: String, age: String)
  object Test {
    val p = Person("Albert Einstein", ???)
  }
</code>
```

This will produce a code snippet, which will be semantically treated by the scala compiler. It is possible to edit the code in the presentation, just as in an IDE. It is also possible to select parts of the code (e.g. identifiers) to display semantic information about the code.

To include Isabelle or haskell simply replace `scala` class with `isabelle` or `haskell`


####External Sources

It is possible to include external source files. Simply place a code file within the folder of the presenation. (e.g. `<presentation root>/src/Test.scala`)

You can then include the snippet with

```html
<code src="src/Test.scala"></code>
```

Note, that you don't have to specify the language in this case, since it is recognised from the file extesion.

####Advanced Inclusion Options

Often it is desired to include only parts of larger examples, for example omitting all imports for the presentation:

This can be done with special comments:

```scala
import system.io._

Object Example {
  /// begin #example
  val x = 7

  /// begin #def-f
  def f(y: Int) = ???
  /// end #example
  /// end #def-f
}
```

The comments won't be shown in the presentation and the sub-snippets can be included as such:

```
<code src="#example></code>
```

Note that, the sub-snippets may be nested or even overlapping as in the example and included in several editors. They will allways stay in sync.


The language mode is derived from the super-snippet.

The comment syntax for Haskell and Isabelle is as follows:

```haskell
--- begin #snippet-name
haskell code
--- end #snippet-name
```

```ml
(** begin #snippet-name *)
isabelle code
(** end #snippet-name *)
```

Snippet names are global and thus have to be unique.

####Code Fragments

Within presentations it is desireable to not show everything at the beginning or exchange parts of the code. This can be achieved with special syntax:

```scala
val x = /*(*/???/*|3 * 7)*/
// or
val x = /*(???|*/3 * 7/*)*/
```

will result both in the following sequence


```scala
val x = ???
```

*hit next*

```scala
val x = 3 * 7
```

The difference in the two lines is just their meaning in the source file.

Again the syntax for haskell and isabelle is analogous:

```haskell
fibs = {-(-}undefined{-|0 : 1 : zipWith (+) fibs (tail fibs))-}
```

```isabelle
lemma x: "A ==> A" (*(*)oops(*|by auto)*)
```

####Selection Fragments

It is also possible to select parts of the code automatically as such:

```scala
val x = /*(*/7/*)*/
```

```haskell
x = {-(-}7{-)-}
```

```isabelle
lemma x: "A ==> (*(*)A(*)*)"
```

They will act the same as manual selections, displaying semantic information about the selected portion.

In Version 1.1, it will be possible to annotate custom text to selected portions.

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
