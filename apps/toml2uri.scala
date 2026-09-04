//> using scala 3.7.4
//> using dep "com.indoorvivants::toml:0.3.0"
//> using file "../src/Xhttp.scala"
//> using file "../src/Routing.scala"

import scala.util.{Try, Success, Failure}
import scala.io.Source
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.net.URLEncoder
import toml.Toml.parseAs
import toml.derivation.auto._
import portal.{Xhttp}

def readFile(filename: String): Try[String] =
  Try(Source.fromFile(filename).mkString)

def writeFile(filename: String, content: String): Try[Unit] =
  Try {
      val writer = new FileWriter(filename)
      try {
        writer.write(content)
      } finally {
        writer.close()
      }
  }

def toUriEncoded(input: String): String =
  URLEncoder.encode(input, StandardCharsets.UTF_8.name).replace("+", "%20")

@main def run(args: String*): Unit = {
  if ((args.contains("--xhttp") || args.contains("--reality"))
  && args.length == 3 + (if (args.contains("--user")) then 2 else 0)) then {
    val filenameFrom       = args(args.length - 2)
    val filenameTo       = args(args.length - 1)

    readFile(filenameFrom) match {
      case Failure(e) =>
        println(s"Не могу прочитать $filenameFrom: ${e.getMessage}")

      case Success(content) =>
        if args.contains("--vless") then {
          println("TODO. Пока не поддерживается.")
        } else if args.contains("--xhttp") then {
          parseAs[Xhttp.Config](content) match {
            case Left(e) =>
              println(s"Не удалось прочитать TOML XHTTP конфигурацию: ${e}")

            case Right(config) =>
              val remarks = toUriEncoded(config.remarks)
              val alpn = toUriEncoded(Xhttp.Misc.alpn.mkString(","))
              val extra = toUriEncoded(
                (s"""{ "xPaddingBytes": "${Xhttp.Misc.xPaddingBytes}",
                   |   "noGRPCHeader": true,
                   |   "xmux": {
                   |     ${if (Xhttp.Misc.maxConnections == "0") then s"\"maxConcurrency\": \"${Xhttp.Misc.maxConcurrency}\"" else s"\"maxConnections\": \"${Xhttp.Misc.maxConnections}\""}
                   |   }""" ++
                (if config.xhttp.downloadAddress.isEmpty then "" else
                s""",
                   |   "downloadSettings": {
                   |     "address": "${config.xhttp.downloadAddress}",
                   |     "port": ${config.xhttp.downloadPort},
                   |     "network": "xhttp",
                   |     "security": "tls",
                   |     "tlsSettings": {
                   |       "serverName": "${config.xhttp.sni}",
                   |       "alpn": [ ${Xhttp.Misc.alpn.map(x => s"\"${x}\"").mkString(", ")} ],
                   |       "fingerprint": "${Xhttp.Misc.fingerprint}"
                   |     },
                   |     "xhttpSettings": {
                   |       "path": "${config.xhttp.path}",
                   |       "mode": "auto",
                   |       "extra": {
                   |         "xPaddingBytes": "${Xhttp.Misc.xPaddingBytes}",
                   |         "noGRPCHeader": true,
                   |         "xmux": {
                   |           ${if (Xhttp.Misc.maxConnections == "0") then s"\"maxConcurrency\": \"${Xhttp.Misc.maxConcurrency}\"" else s"\"maxConnections\": \"${Xhttp.Misc.maxConnections}\""}
                   |         }
                   |       }
                   |     }
                   |   }""") ++
                s"""
                   | }""").stripMargin.replace("\n", " ").replaceAll("\\s+", " ")
              )
              val users =
                if (args.contains("--user")) then
                    val id_starting_symbols = args(args.indexWhere(_ == "--user") + 1)
                    val usersFiltered = config.users.filter(_.id.startsWith(id_starting_symbols))
                    if usersFiltered.isEmpty then
                      println(s"Внимание, не найден пользаватель с идентификатором ${id_starting_symbols}..!")
                    usersFiltered
                else
                    config.users
              val uris = users
                .map { user =>
                  s"vless://${user.id}@${config.xhttp.address}:${config.xhttp.port}?mode=auto&path=%2F${config.xhttp.path.tail}&security=tls&alpn=${alpn}&encryption=${config.vless.encryption}&extra=${extra}&insecure=0&fp=${Xhttp.Misc.fingerprint}&type=xhttp&allowInsecure=0&flow=xtls-rprx-vision-udp443&sni=${config.xhttp.sni}#${remarks}"
                }
              writeFile(filenameTo, uris.mkString("\n")) match
                case Success(_) => println("Ключи-ссылки записаны.")
                case Failure(e) => println(s"Ошибка записи файла: ${e}")
          }
        } else {
          println("Unknown configuration type")
        }
    }
  } else {
    println("""Использование:
      |  scala-cli run ./apps/toml2uri.scala -- --xhttp config.toml links.txt
      |  scala-cli run ./apps/toml2uri.scala -- --xhttp --user e3fbb config.toml links.txt
      |Флаги:
      |  --xhttp
      |  --reality
      |  --user id_starting_symbols""".stripMargin)
  }
}
