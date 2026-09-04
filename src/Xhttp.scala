package portal

import Routing.routingListMap

object Xhttp:

  case class Xhttp(
      address: String,
      port: Int,
      downloadAddress: String,
      downloadPort: Int,
      sni: String,
      path: String
  )

  case class Vless(
      decryption: String,
      encryption: String
  )

  case class ClientRule(
      clientProxy: List[String],
      clientDirect: List[String],
      clientBlock: List[String]
  )

  case class ServerRule(
      serverDirect: List[String],
      serverBlock: List[String]
  )

  case class VlessUser(
      id: String
  )

  object Misc:
      val flow: String = "xtls-rprx-vision-udp443"
      val alpn = List("h3", "h2")
      val fingerprint = "chrome"
      val xPaddingBytes = "200-800"
      // maxConcurrency конфликтует с maxConnections; нужно выбрать только одно, другое выставив в "0"
      val maxConcurrency = "0"
      val maxConnections = "1-2"


  case class Config(
      remarks: String,
      xhttp: Xhttp,
      vless: Vless,
      clientRules: List[ClientRule],
      serverRules: List[ServerRule],
      rproxyUsers: List[VlessUser] = Nil,
      users: List[VlessUser]
  ) {

      def outboundString(id: String): String =
          (s"""|    {
    |      "tag": "to_server",
    |      "protocol": "vless",
    |      "settings": {
    |        "vnext": [
    |          {
    |            "address": "${xhttp.address}",
    |            "port": ${xhttp.port},
    |            "users": [
    |              {
    |                "id": "${id}",
    |                "email": "${id.take(5)}",
    |                "encryption": "${vless.encryption}",
    |                "flow": "${Misc.flow}"
    |              }
    |            ]
    |          }
    |        ]
    |      },
    |      "streamSettings": {
    |        "network": "xhttp",
    |        "security": "tls",
    |        "tlsSettings": {
    |          "serverName": "${xhttp.sni}",
    |          "alpn": [ ${Misc.alpn.map(x => s"\"${x}\"").mkString(", ")} ],
    |          "fingerprint": "${Misc.fingerprint}"
    |        },
    |        "xhttpSettings": {
    |          "path": "${xhttp.path}",
    |          "mode": "auto",
    |          "extra": {
    |            "xPaddingBytes": "${Misc.xPaddingBytes}",
    |            "noGRPCHeader": true,
    |            "xmux": {
    |              ${if (Misc.maxConnections == "0") then s"\"maxConcurrency\": \"${Misc.maxConcurrency}\"" else s"\"maxConnections\": \"${Misc.maxConnections}\""}
    |            }""" ++
    (if xhttp.downloadAddress.isEmpty then "" else
    s""",
    |            "downloadSettings": {
    |              "address": "${xhttp.downloadAddress}",
    |              "port": ${xhttp.downloadPort},
    |              "network": "xhttp",
    |              "security": "tls",
    |              "tlsSettings": {
    |                "serverName": "${xhttp.sni}",
    |                "alpn": [ ${Misc.alpn.map(x => s"\"${x}\"").mkString(", ")} ],
    |                "fingerprint": "${Misc.fingerprint}"
    |              },
    |              "xhttpSettings": {
    |                "path": "${xhttp.path}",
    |                "mode": "auto",
    |                "extra": {
    |                  "xPaddingBytes": "${Misc.xPaddingBytes}",
    |                  "noGRPCHeader": true,
    |                  "xmux": {
    |                    ${if (Misc.maxConnections == "0") then s"\"maxConcurrency\": \"${Misc.maxConcurrency}\"" else s"\"maxConnections\": \"${Misc.maxConnections}\""}
    |                  }
    |                }
    |              }
    |            }""") ++
    s"""
    |          }
    |        }
    |      }
    |    }""").stripMargin

      def rproxyOutboundString(id: String): String =
          (s"""|    {
    |      "tag": "to_server",
    |      "protocol": "vless",
    |      "settings": {
    |        "address": "${xhttp.address}",
    |        "port": ${xhttp.port},
    |        "id": "${id}",
    |        "email": "${id.take(5)}",
    |        "encryption": "${vless.encryption}",
    |        "flow": "${Misc.flow}",
    |        "reverse": {
    |          "tag": "r-inbound",
    |          "sniffing" : {
    |            "enabled": true,
    |            "destOverride": [
    |              "http",
    |              "tls"
    |            ],
    |            "routeOnly": true
    |          }
    |        }
    |      },
    |      "streamSettings": {
    |        "network": "xhttp",
    |        "security": "tls",
    |        "tlsSettings": {
    |          "serverName": "${xhttp.sni}",
    |          "alpn": [ ${Misc.alpn.map(x => s"\"${x}\"").mkString(", ")} ],
    |          "fingerprint": "${Misc.fingerprint}"
    |        },
    |        "xhttpSettings": {
    |          "path": "${xhttp.path}",
    |          "mode": "auto",
    |          "extra": {
    |            "xPaddingBytes": "${Misc.xPaddingBytes}",
    |            "noGRPCHeader": true,
    |            "xmux": {
    |              ${if (Misc.maxConnections == "0") then s"\"maxConcurrency\": \"${Misc.maxConcurrency}\"" else s"\"maxConnections\": \"${Misc.maxConnections}\""}
    |            }""" ++
    (if xhttp.downloadAddress.isEmpty then "" else
    s""",
    |            "downloadSettings": {
    |              "address": "${xhttp.downloadAddress}",
    |              "port": ${xhttp.downloadPort},
    |              "network": "xhttp",
    |              "security": "tls",
    |              "tlsSettings": {
    |                "serverName": "${xhttp.sni}",
    |                "alpn": [ ${Misc.alpn.map(x => s"\"${x}\"").mkString(", ")} ],
    |                "fingerprint": "${Misc.fingerprint}"
    |              },
    |              "xhttpSettings": {
    |                "path": "${xhttp.path}",
    |                "mode": "auto",
    |                "extra": {
    |                  "xPaddingBytes": "${Misc.xPaddingBytes}",
    |                  "noGRPCHeader": true,
    |                  "xmux": {
    |                    ${if (Misc.maxConnections == "0") then s"\"maxConcurrency\": \"${Misc.maxConcurrency}\"" else s"\"maxConnections\": \"${Misc.maxConnections}\""}
    |                  }
    |                }
    |              }
    |            }""") ++
    s"""
    |          }
    |        }
    |      }
    |    }""").stripMargin

    def domainString(domainListNames: List[String]): String = {
      val domains  =
        domainListNames
          .flatMap { name => routingListMap.getOrElse(name, List()) }
          .map( a => s"\"domain:${a}\"" )
      if domains.isEmpty then
        "\"foo.example\"" // правила не должны быть пустыми
      else
        domains.mkString(", ")
    }

    def domainRuleString(domainNames: List[String], outbound_tag: String): String =
      if domainNames.contains("all") then
    s"""|      {
      |        "type": "field",
      |        "network": "tcp,udp",
      |        "outboundTag": "${outbound_tag}"
      |      }""".stripMargin
  else
    s"""|      {
      |        "type": "field",
      |        "domain": [
      |          ${domainString(domainNames)}
      |        ],
      |        "network": "tcp,udp",
      |        "outboundTag": "${outbound_tag}"
      |      }""".stripMargin

      val clientRulesString: String =
        clientRules
          .map { rule =>
          s"""
${domainRuleString(rule.clientProxy, "to_server")},
${domainRuleString(rule.clientDirect, "direct")},
${domainRuleString(rule.clientBlock, "block")}
          |""".stripMargin
          }
          .mkString(",\n")

      val serverRulesString: String =
        serverRules
          .map { rule =>
          s"""
${domainRuleString(rule.serverDirect, if rproxyUsers.isEmpty then "direct" else "r-outbound")},
${domainRuleString(rule.serverBlock, "block")}
          |""".stripMargin
          }
          .mkString(",\n")

      def toJsonClient(id: String): String =
        s"""|{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
    |  },
    |  "dns": {
    |    "servers": [
    |      "https://cloudflare-dns.com/dns-query",
    |      "https://dns.quad9.net/dns-query",
    |      "https+local://dns.google/dns-query",
    |      "https+local://doh.opendns.com/dns-query"
    |    ],
    |    "queryStrategy": "UseIPv4",
    |    "disableCache": false
    |  },
    |  "inbounds": [
    |    {
    |      "tag": "local_socks",
    |      "port": 10808,
    |      "listen": "127.0.0.1",
    |      "protocol": "socks",
    |      "sniffing": {
    |        "enabled": true,
    |        "destOverride": [
    |          "http",
    |          "tls"
    |        ],
    |        "routeOnly": true
    |      },
    |      "settings": {
    |        "auth": "noauth",
    |        "udp": true
    |      }
    |    }
    |  ],
    |  "outbounds": [
${outboundString(id)},
    |    {
    |      "tag": "direct",
    |      "protocol": "freedom"
    |    },
    |    {
    |      "tag": "block",
    |      "protocol": "blackhole"
    |    }
    |  ],
    |  "routing": {
    |    "domainStrategy": "AsIs",
    |    "rules": [
    |      {
    |        "type": "field",
    |        "ip": [ "geoip:private" ],
    |        "network": "tcp,udp",
    |        "outboundTag": "block"
    |      },
${clientRulesString}
    |    ]
    |  }
    |}
  """.stripMargin


      def toJsonServer(): String =
        val rproxyClients = rproxyUsers.map { user =>
            s"""|          {
              |            "email": "${user.id.take(5)}",
              |            "id": "${user.id}",
              |            "flow": "${if (Misc.flow == "xtls-rprx-vision-udp443") then "xtls-rprx-vision" else Misc.flow}",
              |            "reverse": {
              |              "tag": "r-outbound"
              |            }
              |          }""".stripMargin
        }.mkString(",\n")
        val clients = users.map { user =>
            s"""|          {
              |            "email": "${user.id.take(5)}",
              |            "id": "${user.id}",
              |            "flow": "${if (Misc.flow == "xtls-rprx-vision-udp443") then "xtls-rprx-vision" else Misc.flow}"
              |          }""".stripMargin
        }.mkString(",\n")
        s"""
    |{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
    |  },
    |  "dns": {
    |    "servers": [
    |      "https://cloudflare-dns.com/dns-query",
    |      "https://dns.quad9.net/dns-query",
    |      "https+local://dns.google/dns-query",
    |      "https+local://doh.opendns.com/dns-query"
    |    ],
    |    "queryStrategy": "UseIPv4",
    |    "disableCache": false
    |  },
    |  "inbounds": [
    |    {
    |      "tag": "from_clients",
    |      "listen": "127.0.0.1",
    |      "port": 9443,
    |      "protocol": "vless",
    |      "settings": {
    |        "clients": [
${if rproxyUsers.isEmpty then clients else rproxyClients ++ ",\n" ++ clients}
    |        ],
    |        "decryption": "${vless.decryption}"
    |      },
    |      "streamSettings": {
    |        "network": "xhttp",
    |        "security": "none",
    |        "xhttpSettings": {
    |          "path": "${xhttp.path}",
    |          "mode": "auto",
    |          "extra": {
    |            "xPaddingBytes": "${Misc.xPaddingBytes}"
    |          }
    |        },
    |        "sockopt": {
    |          "tcpFastOpen": true
    |        }
    |      },
    |      "sniffing": {
    |        "enabled": true,
    |        "destOverride": [
    |          "http",
    |          "tls"
    |        ],
    |        "routeOnly": true
    |      }
    |    }
    |  ],
    |  "outbounds": [
    |    {
    |      "tag": "direct",
    |      "protocol": "freedom"
    |    },
    |    {
    |      "tag": "block",
    |      "protocol": "blackhole"
    |    }
    |  ],
    |  "routing": {
    |    "domainStrategy": "AsIs",
    |    "rules": [
    |      {
    |        "type": "field",
    |        "ip": [
    |          "geoip:private"
    |        ],
    |        "outboundTag": "block"
    |      },
    |      {
    |        "type": "field",
    |        "protocol": [
    |          "bittorrent"
    |        ],
    |        "outboundTag": "block"
    |      },
${serverRulesString}
    |    ]
    |  }
    |}
  """.stripMargin

      def toJsonRproxy(id: String): String =
        s"""|{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
    |  },
    |  "dns": {
    |    "servers": [
    |      "https+local://cloudflare-dns.com/dns-query",
    |      "https+local://dns.quad9.net/dns-query",
    |      "https+local://dns.google/dns-query",
    |      "https+local://doh.opendns.com/dns-query"
    |    ],
    |    "queryStrategy": "UseIPv4",
    |    "disableCache": false
    |  },
    |  "inbounds": [ ],
    |  "outbounds": [
    |    {
    |      "tag": "direct",
    |      "protocol": "freedom"
    |    },
    |    {
    |      "tag": "block",
    |      "protocol": "blackhole"
    |    },
${rproxyOutboundString(id)}
    |  ],
    |  "routing": {
    |    "domainStrategy": "AsIs",
    |    "rules": [
    |      {
    |        "type": "field",
    |        "network": "tcp,udp",
    |        "outboundTag": "direct"
    |      }
    |    ]
    |  }
    |}
  """.stripMargin

  }
