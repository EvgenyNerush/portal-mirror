//> using scala 3.7.4
//> using dep "com.indoorvivants::toml:0.3.0"
//> using file "portal.scala"

import scala.util.{Try, Success, Failure}
import scala.io.Source
import toml.Toml.parseAs
import toml.derivation.auto._
import portal._

def readFile(filename: String): Try[String] =
  Try(Source.fromFile(filename).getLines.toList.reduce(_ + _))

@main def run(args: String*): Unit = {
  if ((args.contains("--xhttp") || args.contains("--vless")) && args.length >= 2) then {
    val isClientConfig = if args.contains("--client") then true else false
    val filename       = args.last

    readFile(filename) match {
      case Failure(e) =>
        println(s"Could not read $filename: ${e.getMessage}")

      case Success(content) =>
        if args.contains("--vless") then {
          parseAs[VLConfig](content) match {
            case Left(e) =>
              println(s"Failed to parse TOML as VLESS config: ${e}")

            case Right(config) =>
              if isClientConfig then
                println(
                  vlessToJsonClient(
                    config.remarks,
                    config.raw,
                    config.vless,
                    config.reality,
                    config.rules,
                    config.users.head
                  )
                )
              else
                println(
                  vlessToJsonServer(
                    config.remarks,
                    config.raw,
                    config.vless,
                    config.reality,
                    config.rules,
                    config.users
                  )
                )
          }
        } else if args.contains("--xhttp") then {
          parseAs[XHTTPConfig](content) match {
            case Left(e) =>
              println(s"Failed to parse TOML as XHTTP config: ${e}")

            case Right(config) =>
              if isClientConfig then
                println(
                  xhttpToJsonClient(
                    config.remarks,
                    config.xhttp,
                    config.vless,
                    config.rules,
                    config.users.head
                  )
                )
              else
                println(
                  xhttpToJsonServer(
                    config.remarks,
                    config.xhttp,
                    config.vless,
                    config.rules,
                    config.users
                  )
                )
          }
        } else {
          println("Unknown configuration type")
        }
    }
  } else {
    println("""Example:
      |  scala-cli run ./src/toml2json.scala -- --xhttp --client portal.toml
      |Flags:
      |  --xhttp
      |  --vless
      |  --client""".stripMargin)
  }
}
