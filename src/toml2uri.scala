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
  if ((args.contains("--xhttp") || args.contains("--vless")) && args.length == 2) then {
    val filename = args.last

    readFile(filename) match {
      case Failure(e) =>
        println(s"Could not read $filename: ${e.getMessage}")

      case Success(content) =>
        if args.contains("--vless") then {
          parseAs[VLConfig](content) match {
            case Left(e) =>
              println(s"Failed to parse TOML as VLESS config: ${e}")

            case Right(config) =>
              println("VLESS REALITY not supported yet")
          }
        } else if args.contains("--xhttp") then {
          parseAs[XHTTPConfig](content) match {
            case Left(e) =>
              println(s"Failed to parse TOML as XHTTP config: ${e}")

            case Right(config) =>
              val urls = config.users
                .map { user =>
                  s"vless://${user.id}@${config.vless.address}:${config.vless.port}?mode=auto&path=%2F${config.xhttp.path.tail}&security=tls&alpn=h2&encryption=${config.vless.vlessEncryption}&insecure=0&host=&fp=chrome&type=xhttp&allowInsecure=0&flow=xtls-rprx-vision-udp443&sni=${config.vless.address}#${config.remarks}"
                }
              println(urls.reduce(_ + "\n" + _))
          }
        } else {
          println("Unknown configuration type")
        }
    }
  } else {
    println("""Example:
      |  scala-cli run ./src/toml2uri.scala -- --xhttp portal.toml
      |Flags:
      |  --xhttp
      |  --vless""".stripMargin)
  }
}
