//> using scala 3.7.4
//> using dep "com.indoorvivants::toml:0.3.0"
//> using file "../src/Xhttp.scala"
//> using file "../src/Routing.scala"

import scala.util.{Try, Success, Failure}
import scala.io.Source
import java.io.FileWriter
import toml.Toml.parseAs
import toml.derivation.auto._
import portal.{Xhttp}

def readFile(filename: String): Try[String] =
  Try(Source.fromFile(filename).getLines.toList.reduce(_ + _))

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
  if ((args.contains("--client") || args.contains("--server"))
    && args.length == 4 + (if (args.contains("--user")) then 2 else 0)) then {
    val isClient = args.contains("--client")
    val filenameFrom       = args(args.length - 2)
    val filenameTo       = args(args.length - 1)

    readFile(filenameFrom) match {
      case Failure(e) =>
        println(s"Не могу прочитать $filenameFrom: ${e.getMessage}")

      case Success(content) =>
        if (args.contains("--reality")) then
          println("TODO, пока не поддерживается.")
        else if (args.contains("--xhttp")) then
          parseAs[Xhttp.Config](content) match {
            case Left(e) =>
              println(s"Ошибка, прочитать TOML XHTTP конфиг не получилось: ${e}")

            case Right(config) =>
              val rproxyIds = config.rproxyUsers.map(_.id)
              val ids = config.users.map(_.id)
              if (ids.isEmpty) then
                println("Ошибка: пользователи в конфиге не заданы.")
              else
                if (isClient) then
                  if (args.contains("--user")) then
                    val id_starting_symbols = args(args.indexWhere(_ == "--user") + 1)
                    val rproxyIdsFiltered = rproxyIds.filter(_.startsWith(id_starting_symbols))
                    val idsFiltered = ids.filter(_.startsWith(id_starting_symbols))
                    if (rproxyIdsFiltered ++ idsFiltered).isEmpty then
                      println(s"Ошибка: не найден пользаватель с идентификатором, начинающимся на ${id_starting_symbols}")
                    else if (rproxyIdsFiltered.isEmpty)
                      val id = idsFiltered.head
                      writeFile(filenameTo, config.toJsonClient(id)) match
                        case Success(_) => println("Ok, JSON конфигурация записана.")
                        case Failure(e) => println(s"Ошибка записи файла: ${e}")
                    else
                      val id = rproxyIdsFiltered.head
                      writeFile(filenameTo, config.toJsonRproxy(id)) match
                        case Success(_) => println("Ok, JSON конфигурация записана.")
                        case Failure(e) => println(s"Ошибка записи файла: ${e}")
                  else
                    val id = ids.head
                    writeFile(filenameTo, config.toJsonClient(id)) match
                      case Success(_) => println("Ok, JSON конфигурация записана.")
                      case Failure(e) => println(s"Ошибка записи файла: ${e}")
                else
                  writeFile(filenameTo, config.toJsonServer()) match
                    case Success(_) => println("Ok, JSON конфигурация записана.")
                    case Failure(e) => println(s"Ошибка записи файла: ${e}")
                      
          }
        else
          println(s"Неизвестный тип конфигурации в ${args}")
    }
  } else {
    println("""Пример:
      |  scala-cli run ./apps/toml2json.scala -- --xhttp --client config.toml config.json
      |  scala-cli run ./apps/toml2json.scala -- --xhttp --client --user e3fbb config.toml config.json
      |Флаги:
      |  --xhttp
      |  --reality
      |  --client
      |  --server
      |  --user id_starting_symbols""".stripMargin)
  }
}
