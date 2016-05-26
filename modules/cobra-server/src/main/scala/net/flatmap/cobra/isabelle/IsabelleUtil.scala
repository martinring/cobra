package net.flatmap.cobra.isabelle

import better.files._

/**
  * Created by martin on 26/05/16.
  */
object IsabelleUtil {
  def possiblePaths(version: String) = System.getProperty("os.name").toLowerCase match {
    case windows if windows.contains("windows") =>
      Set(
        "%HOMEPATH%" / s"Isabelle$version",
        "%HOMEPATH%" / "bin" / s"Isabelle$version",
        "%HOMEPATH%" / "Desktop" / s"Isabelle$version",
        "%HOMEPATH%" / "Downloads" / s"Isabelle$version",
        "%HOMEPATH%" / "Documents" / s"Isabelle$version",
        "%SYSTEMDRIVE%" / s"Isabelle$version",
        "%programfiles%" / s"Isabelle$version",
        "%programfiles(x86)%" / s"Isabelle$version"
      )
    case macos if macos.contains("mac os") =>
      Set(
        File.root / "Applications" / s"Isabelle$version.app" / "Isabelle",
        "~" / "Applications" / s"Isabelle$version.app" / "Isabelle"
      )
    case unix =>
      Set(
        File.root / "usr" / "local" / s"Isabelle$version",
        "~" / s"Isabelle$version",
        "~" / "bin" / s"Isabelle$version",
        "~" / "Downloads" / s"Isabelle$version",
        File.root / "opt" / s"Isabelle$version"
      )
  }

  def locateInstallation =
    possiblePaths("2016").find(p => (p / "bin" / "isabelle").isExecutable)

  def locateOldInstallation =
    Set("2015","2014","2013","2012")
      .map(possiblePaths)
      .map(_.find(p => (p / "bin" / "isabelle").isExecutable))
      .collectFirst {
        case Some(file) => file
      }
}
