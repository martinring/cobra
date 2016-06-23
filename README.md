<img align="right" src="http://www-cps.hb.dfki.de/assets/stylesheets/ric/img/logo_dfki_en.png"/>
# **Cobra** [![Build Status](https://travis-ci.org/flatmap/cobra.svg?branch=master)](https://travis-ci.org/flatmap/cobra)

Cobra is a modern code and proof presentation framework, leveraging cutting-edge presentation technology together with a state of the art interactive theorem prover to present formalized mathematics as active documents. Cobra provides both an easy way to present proofs and a novel approach to auditorium interaction. The presentation is checked live by the theorem prover, and moreover allows live changes both by the presenter as well as the audience.

Cobra currently supports [**Isabelle**](https://isabelle.in.tum.de/index.html) proofs as well as [**Scala**](http://www.scala-lang.org/) and [**Haskell**](http://haskell.org) code

## Install **Cobra** 1.0.5

> **Note, that Java 8 or higher is required for cobra to run.**
> **It will fail to start, when used with Java 7 or below!**

### Windows

Download [zip](https://github.com/flatmap/cobra/releases/download/version-1.0.5/cobra-1.0.5.zip) and extract anywhere; Add `bin/cobra.bat` to your PATH.

### macOS

Until the notability of the cobra GitHub repository meets the requirements of homebrew-core, we have an own tap:

```sh
brew tap flatmap/cobra
brew install cobra
```

### Fedora / RHEL

There is a YUM repository available which provides automatic updates

```sh
wget https://bintray.com/flatmap/rpm/rpm -O bintray-flatmap-rpm.repo
sudo mv bintray-flatmap-rpm.repo /etc/yum.repos.d/
sudo dnf install cobra
```

### Ubuntu / Debian

There is a PPA available for Debian / Ubuntu. Releases are signed with bintray's private key. To add the public key please ececute

```sh
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 379CE192D401AB61 
```

Now you can add the repo and install cobra

```sh
echo "deb https://dl.bintray.com/flatmap/deb wheezy main" | sudo tee -a /etc/apt/sources.list
sudo apt-get update
sudo apt-get install cobra-presentations
```

### Other Platforms

Download [zip](https://github.com/flatmap/cobra/releases/download/version-1.0.5/cobra-1.0.5.zip) and extract anywhere; Add `bin/cobra` to your PATH.

## Getting Started

 * **Installation**:
   * **zip**: extract to arbitrary location and add `bin/cobra` / `bin/cobra.bat` to your path
   * **other**: follow instructions above
 * **Create a new presentation**: call `cobra new` from the command line and follow the instructions
 * **Start the presentation server**: call `cobra` in the directory of the presentation.
 * **View the presentation**: Navigate to localhost:8080 with your web browser.
 * **Edit your presentation**: Configuration can be edited in `cobra.conf`, content in `slides.html`. There is no need to restart the presentation server. Changes will be immediately visible in the browser, when files are changed.

### Presentation Format

The content of a presentation is stored in a file called `slides.html`. Cobra will support a MarkDown slide format in Version 1.2.

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

To include Isabelle or Haskell simply replace `scala` class with `isabelle` or `haskell`

> Note: When including Isabelle inline you will want to set the `id` of the `code` tag to the name of your theory, because otherwise a name for your theory is generated and will most definitely clash with what you define in your header. (see #12)

####Configuring Inline Messages

It is possible to **show inline states** (for Isabelle) by adding the class `states` to a code tag

If you want to **step through states**, you can additionally add the class `state-fragments`

You can **hide info messages** by adding `no-infos` class

You can **hide warning messages** by adding `no-warnings` class

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

Note that, the sub-snippets may be nested or even overlapping as in the example and included in several editors. They will always stay in sync.

The language mode is derived from the super-snippet.

It is also possible to hide snippets. This is convenient, when super snippets shall not be included in the presentation. This can be done by adding the class `hidden` to a code snippet.

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

Within presentations it is desirable to not show everything at the beginning or exchange parts of the code. This can be achieved with special syntax:

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

Again the syntax for Haskell and Isabelle is analogous:

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

## Configuration

The file `cobra.conf` can be edited while the server is running, any change will have immediate effect, when the file is saved. Any running presentation will be updated in the browser, this way you can play around with setting until they suit your needs.

`cobra.conf` is a [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) style configuration file with the following defaults:

```
cobra {
  # display title of the presentation
  title = "Cobra"
  # display language of the presentation
  language = "en"

  theme {
    # slide theme
    # standard themes: black|white|league|sky|beige|simple|serif|blood|night|
    #                  moon|solarized
    # or reference to user theme (e.g. "/theme/mytheme.css")
    slides = "white"

    # code theme
    # standard themes: 3024-day|3024-night|abcdef|ambiance-mobile|ambiance|
    #                  base16-dark|base16-light|bespin|blackboard|cobalt|
    #                  colorforth|dracula|eclipse|elegant|erlang-dark|hopscotch|
    #                  icecoder|isotope|lesser-dark|liquibyte|material|mbo|
    #                  mdn-like|midnight|monokai|neat|neo|night|paraiso-dark|
    #                  paraiso-light|pastel-on-dark|railscasts|rubyblue|seti|
    #                  solarized|the-matrix|tomorrow-night-bright|
    #                  tomorrow-night-eighties|ttcn|twilight|vibrant-ink|
    #                  xq-dark|xq-light|yeti|zenburn
    # or reference to user theme (e.g. "/theme/my-code-theme.css")    
    code = "default"
  }

  # network interface to bind on
  binding {
    interface = "localhost"
    port = 8080
  }

  # environment variables
  env {
    # overrides ISABELLE_HOME environment variable
    # isabelle_home = "..." 
  }

  # reveal.js related settings
  reveal {
    # Display controls in the bottom right corner
    controls = true

    # Display a presentation progress bar
    progress = true

    # Display the page number of the current slide
    slideNumber = false

    # Push each slide change to the browser history
    history = true

    # Enable keyboard shortcuts for navigation
    keyboard = true

    # Enable the slide overview mode
    overview = true

    # Vertical centering of slides
    center = false

    # Enables touch navigation on devices with touch input
    touch = true

    # Loop the presentation
    loop = false

    # Change the presentation direction to be RTL
    rtl = false

    # Randomizes the order of slides each time the presentation loads
    shuffle = false

    # Turns fragments on and off globally
    fragments = true

    # Flags if the presentation is running in an embedded mode,
    # i.e. contained within a limited portion of the screen
    embedded = false

    # Flags if we should show a help overlay when the questionmark
    # key is pressed
    help = true

    # Flags if speaker notes should be visible to all viewers
    showNotes = false

    # Number of milliseconds between automatically proceeding to the
    # next slide, disabled when set to 0, this value can be overwritten
    # by using a data-autoslide attribute on your slides
    autoSlide = 0

    # Stop auto-sliding after user input
    autoSlideStoppable = true

    # Use this method for navigation when auto-sliding
    autoSlideMethod = Reveal.navigateNext

    # Enable slide navigation via mouse wheel
    mouseWheel = false

    # Hides the address bar on mobile devices
    hideAddressBar = true

    # Opens links in an iframe preview overlay
    previewLinks = false

    # Transition style
    transition = "default" # none/fade/slide/convex/concave/zoom

    # Transition speed
    transitionSpeed = "default" # default/fast/slow

    # Transition style for full page slide backgrounds
    backgroundTransition = "default" # none/fade/slide/convex/concave/zoom

    # Number of slides away from the current that are visible
    viewDistance = 3

    # Parallax background image
    parallaxBackgroundImage = "" # e.g. "'https:#s3.amazonaws.com/hakim-static/reveal-js/reveal-parallax-1.jpg'"

    # Parallax background size
    parallaxBackgroundSize = "" # CSS syntax, e.g. "2100px 900px"

    # Number of pixels to move the parallax background per slide
    # - Calculated automatically unless specified
    # - Set to 0 to disable movement along an axis
    parallaxBackgroundHorizontal = null
    parallaxBackgroundVertical = null

    # The "normal" size of the presentation, aspect ratio will be preserved
    # when the presentation is scaled to fit different resolutions. Can be
    # specified using percentage units.
    width = 960
    height = 700

    # Factor of the display size that should remain empty around the content
    margin = 0.1

    # Bounds for smallest/largest possible scale to apply to content
    # Should not be changed, if in-slide code editing should be enabled!
    minScale = 1.0
    maxScale = 1.0
  }
}
```

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
