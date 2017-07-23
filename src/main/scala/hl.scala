package highlight

import java.io.File
import scala.io.AnsiColor
import scala.io.Source
import scala.util.matching.Regex

case class Config(pattern: String, files: List[File])

object hl extends App with AnsiColor {

  val parser = new scopt.OptionParser[Config]("hl") {
    head("highlight", "0.0.1")

    help("help").text("prints this usage text")

    version("version")

    arg[String]("pattern").text("").action((x, c) => c.copy(pattern = x))

    arg[File]("file...")
      .unbounded()
      .optional()
      .action((x, c) => c.copy(files = x :: c.files))
      .text("input files, reads STDIN if empty")
      .validate(x =>
        if (x.exists) {
          success
        } else {
          failure(s"input file $x does not exist")
      })

    note("""|
            |search for pattern and highlight matches""".stripMargin)
  }

  parser.parse(args, Config(pattern = "", files = Nil)) match {
    case Some(config) =>
      val files = config.files.reverse
      val regex = config.pattern.r

      val sources: List[Source] =
        if (files.isEmpty) {
          List(Source.stdin)
        } else {
          files.map(Source.fromFile)
        }

      for {
        source <- sources
        line <- source.getLines
      } println(highlight(line, regex))

    case None =>
  }

  def highlight(line: String, regex: Regex): String = {
    def replace(m: Regex.Match) =
      s"""${BOLD}${RED}${m.matched}${RESET}"""

    regex.replaceAllIn(line, replace(_))
  }
}
