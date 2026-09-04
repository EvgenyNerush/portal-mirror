//> using scala 3.7.4
//> using dep "com.indoorvivants::toml:0.3.0"
//> using dep "org.typelevel::cats-core:2.13.0"

import scala.util.{Try, Success, Failure}
import scala.io.StdIn
import java.io.FileWriter
import scala.sys.process.{stringToProcess, ProcessLogger}
import toml.Toml.generate
import toml.Root
import toml.Node.*
import toml.Value.*
import cats.implicits.*

def writeFile(filename: String, content: String): Try[Unit] =
  Try {
      val writer = new FileWriter(filename)
      try {
        writer.write(content)
      } finally {
        writer.close()
      }
  }

@main def run(args: String*): Unit = {
  if ((args.contains("--xhttp") || args.contains("--reality")) && args.length == 2) then {
    val filename       = args.last

    if args.contains("--xhttp") then {
      println("Введите название конфига для GUI клиентов (remarks):")
      val remarks: Try[String] = Try {
        StdIn.readLine()
      }
      println("Введите адрес сервера для подключения (домен или IP):")
      val address: Try[String] = Try {
        StdIn.readLine()
      }
      println("Введите порт для подключения (по умолчанию 443):")
      val port: Int = Try {
        StdIn.readLine().toInt
      }.getOrElse(443)
      println("Введите отдельный адрес сервера для загрузки (download),")
      println("оставьте пустым, если разделение загрузки и выгрузки не нужно:")
      val downloadAddress: String = Try {
        StdIn.readLine()
      }.getOrElse("")
      val downloadPort: Int =
        if (downloadAddress.isEmpty) then
          port
        else
          println(s"Введите порт для загрузки (по умолчанию ${port}):")
          Try {
            StdIn.readLine().toInt
          }.getOrElse(port)
      println("Доменное имя \"сайта\" на сервере, используемое в сертификатах (SNI):")
      val sni: Try[String] = Try {
        StdIn.readLine()
      }
      println("Схема из двух серверов с реверс-прокси (y/N):")
      val rproxy: Try[String] = Try {
        StdIn.readLine()
      }
      println("Желаемое число пользователей (по умолчанию 10):")
      val nUsers: Int = Try {
        StdIn.readLine().toInt
      }.getOrElse(10)

      val clientRules: List[(List[String], List[String], List[String])] = List(
        ( List("media", "dns"),
          List("what_is_my_ip","ru","ru_blacklist"),
          List()
        ),
        ( List("all"),
          List(),
          List()
        )
      )

      val serverRules: List[(List[String], List[String])] = List((
        List("dns", "ip:dns"),
        List("ru", "vk", "max", "ru_blacklist", "what_is_my_ip")
      ))

      def vlessenc(): Try[(String, String)] =
        Try {
          val output = new StringBuilder
          val exitCode = """xray vlessenc""" ! ProcessLogger(line => output.append(line + '\n'))
          def getField(fieldName: String): String =
            val fieldString = output.toString.split('\n').filter(_.startsWith(s"\"${fieldName}\"")).head
            fieldString.split(' ').drop(1).head.drop(1).dropRight(1)
          (getField("decryption"), getField("encryption"))
        }

      def path(): Try[String] =
        Try {
          val output = new StringBuilder
          val exitCode = """openssl rand -base64 16""" ! ProcessLogger(line => output.append(line))
          val alpha = ('a' to 'z').mkString("")
          val alphanum = alpha ++ alpha.map(_.toUpper) ++ (0 to 9).mkString("")
          val path = output.toString.filter(alphanum.contains(_))
          s"/${path}"
        }

      def rproxyUids(): Try[List[String]] =
        (1 to (if (rproxy.isFailure || rproxy.get.toUpperCase.startsWith("N")) then 0 else 1)).toList
          .map( _ =>
            Try {
              val output = new StringBuilder
              val exitCode = """xray uuid""" ! ProcessLogger(line => output.append(line))
              output.toString
            }
          )
          .sequence

      def uids(n: Int): Try[List[String]] =
        (1 to n).toList
          .map( _ =>
            Try {
              val output = new StringBuilder
              val exitCode = """xray uuid""" ! ProcessLogger(line => output.append(line))
              output.toString
            }
          )
          .sequence

      def toml(remarks: String, address: String, port: Int,
        downloadAddress: String, downloadPort: Int,
        vlessenc: (String, String),
        sni: String, path: String, rproxyUids: List[String], uids: List[String]): Root =
          Root(List(
            Pair("remarks", Str(remarks)),
            NamedTable(List("xhttp"),
              List(("address", Str(address)),
                ("port", Num(port)),
                ("downloadAddress", Str(downloadAddress)),
                ("downloadPort", Num(downloadPort)),
                ("sni", Str(sni)),
                ("path", Str(path)))),
            NamedTable(List("vless"),
              List(("decryption", Str(vlessenc._1)),
                ("encryption", Str(vlessenc._2))))
          )
            ++ clientRules.map { rules =>
              NamedArray(List("clientRules"),
                  List(("clientProxy", Arr(rules._1.map(Str(_)))),
                    ("clientDirect", Arr(rules._2.map(Str(_)))),
                    ("clientBlock", Arr(rules._3.map(Str(_))))
                    ))
            }
            ++ serverRules.map { rules =>
                NamedArray(List("serverRules"),
                  List(("serverDirect", Arr(rules._1.map(Str(_)))),
                    ("serverBlock", Arr(rules._2.map(Str(_))))
                    ))
            }
            ++ rproxyUids.map { id =>
              NamedArray(List("rproxyUsers"),
                List( ("id", Str(id)) ))
            }
            ++ uids.map { id =>
              NamedArray(List("users"),
                List( ("id", Str(id)) ))
            }
          )

      val maybeToml: Try[Root] = for {
        rem <- remarks
        addr <- address
        enc <- vlessenc()
        sn <- sni
        pth <- path()
        rids <- rproxyUids()
        ids <- uids(nUsers)
      } yield toml(rem, addr, port, downloadAddress, downloadPort, enc, sn, pth, rids, ids)

      maybeToml.flatMap(t => writeFile(filename, generate(t))) match {
        case Success(toml) => println(s"Ok, TOML конфигурация записана.")
        case Failure(e) => println(s"ERROR: ${e}")
      }

    } else {
      println("Don't know how to generate TOML for this type:" + args.mkString(" "))
    }
  } else {
    println("""Example:
      |  scala-cli run ./apps/gentoml.scala -- --xhttp config.toml
      |Flags:
      |  --xhttp
      |  --reality""".stripMargin)
  }
}
