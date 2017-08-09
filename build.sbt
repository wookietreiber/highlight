enablePlugins(BuildInfoPlugin)
enablePlugins(GitVersioning)
enablePlugins(ScalaNativePlugin)

name := "highlight"

git.baseVersion := "0.0.2"

scalaVersion := "2.11.11"

libraryDependencies += "com.github.scopt" %%% "scopt" % "3.7.0"

// ----------------------------------------------------------------------------
// build info
// ----------------------------------------------------------------------------

buildInfoKeys := Seq[BuildInfoKey](name, version)

buildInfoPackage := "highlight"

// ----------------------------------------------------------------------------
// scalafmt integration
// ----------------------------------------------------------------------------

scalafmtVersion := "1.1.0"

scalafmtOnCompile := true

// ----------------------------------------------------------------------------
// scalastyle integration
// ----------------------------------------------------------------------------

scalastyleConfig := file(".scalastyle-config.xml")

// ----------------------------------------------------------------------------
// install
// ----------------------------------------------------------------------------

val prefix = settingKey[String]("Installation prefix.")

val install = taskKey[Unit]("Install to prefix.")

prefix := sys.env.getOrElse("PREFIX", "/usr/local")

install := {
  import java.nio.file.Files
  import java.nio.file.StandardCopyOption._

  val bindir = file(prefix.value) / "bin"
  if (!bindir.exists) bindir.mkdirs()

  val binary = (nativeLink in Compile).value

  val source = binary.toPath
  val target = (bindir / "hl").toPath

  Files.copy(source, target, COPY_ATTRIBUTES, REPLACE_EXISTING)
}
