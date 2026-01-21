package portal

case class Raw(
  dialerProxy: String
)

case class XHTTP(
  path: String,
  dialerProxy: String
)

case class Vless(
    address: String,
    listen: String,
    port: Int,
    flow: String,
    vlessDecryption: String,
    vlessEncryption: String
)

case class Reality(
    target: String,
    serverName: String,
    fingerprint: String,
    privateKey: String,
    password: String
)

case class Rules(
    userProxy: List[String],
    userDirect: List[String],
    userBlock: List[String],
    serverBlockExceptions: List[String],
    serverBlock: List[String]
)

case class VLRealityUser(
    id: String,
    shortId: String,
    spiderX: String
)

case class VLUser(
    id: String
)

case class VLConfig(
    remarks: String,
    raw: Raw,
    vless: Vless,
    reality: Reality,
    rules: Rules,
    users: List[VLRealityUser]
)

case class XHTTPConfig(
    remarks: String,
    xhttp: XHTTP,
    vless: Vless,
    rules: Rules,
    users: List[VLUser]
)

val domainListMap = Map(
  "dot_ru" -> List(
    "ru",
    "xn--p1ai", // .рф
    "by",
    "xn--90ais" // .бел
  ),
  "ru"               -> List(
    "gosuslugi.ru",
    "gu-st.ru",
    "xn--d1acpjx3f.xn--p1ai", // яндекс.рф
    "ya.ru",
    "yandex.ru",
    "yandex.by",
    "yandex.net",
    "yandex.org",
    "yandex.com.ru",
    "yastatic.net",
    "yandexcloud.net",
    "yandex-bank.net",
    "yandex.md",
    "ymetrica1.com",
    "adfox.ru",
    "dzen.ru",
    "dzeninfra.ru",
    "naydex.net",
    "kinopoisk.ru",
    "ozon.ru",
    "ozonusercontent.com",
    "ozone.ru",
    "samokat.ru",
    "rambler.ru",
    "sfr7ag.ru",
    "top100.ru",
    "sberbank.ru",
    "sberbank.com",
    "sbermarket.ru",
    "sbermarketing.ru",
    "kuper.ru",
    "adriver.ru",
    "soloway.ru",
    "statad.ru",
    "upravel.com",
    "afp.ai",
    "flocktory.com",
    "uxfeedback.ru",
    "mindbox.ru",
    "retailrocket.ru",
    "wildberries.ru",
    "wbstatic.net",
    "wbbasket.ru",
    "wb.ru",
    "vk.com",
    "vk-portal.net",
    "vk.ru",
    "mvk.com",
    "userapi.com",
    "vk-cdn.me",
    "vk-cdn.net",
    "vk.cc",
    "vk.design",
    "vk.link",
    "vk.me",
    "vkcache.com",
    "vkgo.app",
    "vklive.app",
    "vkmessenger.com",
    "vkmessenger.app",
    "vkontakte.ru",
    "vkuseraudio.com",
    "vkuseraudio.net",
    "vkuserlive.net",
    "vkuservideo.com",
    "vkuservideo.net",
    "vkuser.net",
    "tns-counter.ru",
    "mail.ru",
    "imgsmail.ru",
    "yadro.ru",
    "mycdn.me",
    "ok.ru",
    "2gis.ru",
    "2gis.com",
    "lenta.com",
    "gldn.net",
    "avito.st",
    "gismeteo.st",
    "gismeteo.net",
    "mradx.net",
    "aestatic.net",
    "pushwoosh.com",
    "otm-r.com",
    "championat.com",
    "rus-tv.su",
    "kpcdn.net",
    "kp.house",
    "kp.kg",
    "kaspersky-labs.com",
    "eaglecdn.com",
    "livejournal.com",
    "livejournal.net",
    "d-cd.net",
    "mayak.help",
    "payselection.com",
    "edc.sale",
    "stellarlabs.ai",
    "myminsk.com",
    "sputniknews.com",
    "sputnikglobe.com"
  ),
  "tiktok"           -> List(
    "tiktok.com",
    "ttwstatic.com",
    "tiktokv.com",
    "tiktokv.us",
    "tiktokcdn.com",
    "tiktokcdn-us.com",
    "p16-tiktokcdn-com.akamaized.net",
    "tik-tokapi.com",
    "tiktokd.net",
    "tiktokd.org",
    "tiktokw.us"
  ),
  "media"            -> List(
    "moscowtimes.ru",
    "kasparov.ru",
    "novayagazeta.ru",
    "grani.ru",
    "newtimes.ru",
    "tvrain.ru",
    "the-village.ru",
    "arsvest.ru",
    "zasekin.ru",
    "trv-science.ru",
    "snob.ru",
    "colta.ru",
    "newizv.ru",
    "itsmycity.ru",
    "theins.ru",
    "4pda.ru",
    "abook-club.ru",
    "appstorrent.ru",
    "ej.ru",
    "onlinesim.ru",
    "republic.ru",
    "sobesednik.ru",
    "polit.ru",
    "te-st.ru",
    "habr.com",
    "habrastorage.org",
    "onlinesim.io"
  ),
  "torrent_treckers" -> List(
    "rutracker.org",
    "rutracker.nl",
    "rutracker.net",
    "thepiratebay.org",
    "thepiratebay.zone",
    "pirateproxy.live",
    "pirate-proxy.ink",
    "pirateproxy-bay.com",
    "tpbproxypirate.com",
    "proxifiedpiratebay.org",
    "ukpirate.co",
    "mirrorbay.top",
    "tpb.skynetcloud.site",
    "tpb25.ukpass.co",
    "1337x.to",
    "1337x.hashhackers.com",
    "1337x-to.pages.dev",
    "1337x.torrentbay.to",
    "1337x.unblockninja.com",
    "1337x.unblockproject.click",
    "1337x.unbl0ck.info",
    "eztv.lt",
    "eztv.tf",
    "eztv.re",
    "eztv.daytorrents.com",
    "eztv.work",
    "eztv.unblockninja.com",
    "eztv.unblockit.nz",
    "rarbg.to",
    "rarbgproxy.org",
    "torrentsbay.org",
    "therarbg.com",
    "proxyrarbg.org",
    "rarbgaccess.org",
    "rarbgget.org",
    "rarbgunblock.com",
    "rarbg.unblockninja.com",
    "extratorrent.cc",
    "extratorrents.ch",
    "extratorrent.st",
    "extratorrentsnew.xyz",
    "extratorrent.proxyninja.org",
    "extratorrent.proxyninja.net",
    "extratorrent.ninjaproxy1.com",
    "extratorrent.piratic.org",
    "yts.mx",
    "yts-mx.my",
    "yts.unblockit.ink",
    "yts.rs",
    "yts.pm",
    "yts.mx",
    "yts.io",
    "kickass.to",
    "kickass.cd",
    "proxyindex.net",
    "kickasstorrents.pw",
    "thekat.info",
    "kat.am",
    "katcr.to",
    "zamunda.net",
    "zamunda.ch",
    "zamunda.se",
    "www.bgproxy.org",
    "bg-zamunda.net",
    "tracker.opentrackr.org",
    "open.demonii.com",
    "open.stealth.si",
    "exodus.desync.com",
    "explodie.org",
    "wepzone.net",
    "ttk2.nbaonlineservice.com",
    "tracker2.dler.org",
    "tracker.tryhackx.org",
    "tracker.theoks.net",
    "tracker.srv00.com",
    "tracker.qu.ax",
    "tracker.hifimarket.in",
    "tracker.fnix.net",
    "tracker.dler.org",
    "tracker.bittor.pw",
    "tracker-udp.gbitt.info",
    "t.overflow.biz",
    "run.publictracker.xyz",
    "retracker01-msk-virt.corbina.net",
    "public.tracker.vraphim.com",
    "p4p.arenabg.com",
    "opentracker.io",
    "open.dstud.io",
    "martin-gebhardt.eu",
    "ipv4announce.sktorrent.eu",
    "evan.im",
    "discord.heihachi.pw",
    "d40969.acod.regrucolo.ru",
    "bittorrent-tracker.e-n-c-r-y-p-t.net",
    "6ahddutb1ucc3cp.ru",
    "1c.premierzal.ru",
    "tracker.yemekyedim.com",
    "tracker.pmman.tech",
    "tracker.moeblog.cn",
    "tracker.cutie.dating",
    "tracker.bt4g.com",
    "t1.hloli.org",
    "www.torrentsnipe.info",
    "www.genesis-sp.org",
    "wepzone.net",
    "tracker810.xyz",
    "tracker2.dler.org",
    "tracker1.bt.moack.co.kr",
    "tracker.xiaoduola.xyz",
    "tracker.vanitycore.co",
    "tracker.sbsub.com",
    "tracker.renfei.net",
    "tracker.qu.ax",
    "tracker.mywaifu.best",
    "tracker.moxing.party",
    "tracker.lintk.me",
    "tracker.ipv6tracker.org",
    "tracker.dmcomic.org",
    "tracker.dler.org",
    "tracker.cutie.dating",
    "tracker.corpscorp.online",
    "tracker.bz",
    "tracker.bt4g.com",
    "tracker.bt-hash.com",
    "tracker.bittor.pw",
    "tr.kxmp.cf",
    "torrent.hificode.in",
    "t.overflow.biz",
    "shubt.net",
    "share.hkg-fansub.info",
    "servandroidkino.ru",
    "seeders-paradise.org",
    "retracker.spark-rostov.ru",
    "public.tracker.vraphim.com",
    "p4p.arenabg.com",
    "open.trackerlist.xyz",
    "home.yxgz.club",
    "extracker.dahrkael.net",
    "buny.uk",
    "bt1.xxxxbt.cc",
    "bt.poletracker.org",
    "bittorrent-tracker.e-n-c-r-y-p-t.net",
    "0d.kebhana.mx",
    "0123456789nonexistent.com",
    "udp.tracker.projectk.org",
    "tracker.zupix.online",
    "tracker.valete.tf",
    "tracker.torrust-demo.com",
    "tracker.therarbg.to",
    "tracker.skillindia.site",
    "tracker.plx.im",
    "tracker.hifitechindia.com",
    "tracker.healthcareindia.store",
    "tracker.gmi.gd",
    "tracker.gigantino.net",
    "tracker.bitcoinindia.space",
    "tr4ck3r.duckdns.org",
    "retracker.lanta.me",
    "ipv4.rer.lol",
    "concen.org",
    "bt.rer.lol",
    "bt.rer.lol",
    "bandito.byterunner.io",
    "tracker.zhuqiy.top",
    "tracker.leechshield.link",
    "tracker.ghostchu-services.top",
    "tracker.gcrenwp.top",
    "tracker.aburaya.live",
    "tr.zukizuki.org",
    "tracker1.itzmx.com",
    "tracker.zhuqiy.top",
    "tracker.waaa.moe",
    "tracker.valete.tf",
    "tracker.tritan.gg",
    "tracker.privateseedbox.xyz",
    "tracker.ghostchu-services.top",
    "tracker.darkness.services",
    "tracker.23794.top",
    "ipv4.rer.lol",
    "1337.abcvg.info",
    "tracker.btorrent.xyz",
    "tracker.webtorrent.dev",
    "tracker.ghostchu-services.top",
    "tracker.files.fm",
    "tracker.ghostchu-services.top",
    "tracker.files.fm"
  )
)

def vlClientString(id: String, flow: String): String =
  s"""|          {
    |            "email": "${id.take(5)}",
    |            "id": "${id}",
    |            "flow": "${flow}"
    |          }""".stripMargin

def domainString(domainListNames: List[String]): String =
  val domains  =
    domainListNames
      .flatMap { name => domainListMap.getOrElse(name, List()) }
      .map( a => s"\"domain:${a}\"" )
  if domains.isEmpty then
    "\"foo.example\"" // правила не должны быть пустыми
  else
    domains.reduce(_ + ", " + _)

def vlessToJsonServer(
    remarks: String,
    raw: Raw,
    vless: Vless,
    reality: Reality,
    rules: Rules,
    users: List[VLRealityUser]
): String =
  val listen                = if vless.listen.isEmpty then "0.0.0.0" else vless.listen
  val clients               = users.map(client => vlClientString(client.id, vless.flow)).reduce(_ + ",\n" + _)
  val shortIds              = users.map( a => s"            \"${a.shortId}\"" ).reduce(_ + ",\n" + _)
  s"""
    |{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
    |  },
    |  "inbounds": [
    |    {
    |      "tag": "from_clients",
    |      "listen": "${listen}",
    |      "port": ${vless.port},
    |      "protocol": "vless",
    |      "settings": {
    |        "clients": [
${clients}
    |        ],
    |        "decryption": "${vless.vlessDecryption}"
    |      },
    |      "streamSettings": {
    |        "network": "raw",
    |        "security": "reality",
    |        "realitySettings": {
    |          "dest": "${reality.target}",
    |          "serverNames": [ "${reality.serverName}" ],
    |          "privateKey": "${reality.privateKey}",
    |          "shortIds": [
${shortIds}
    |          ]
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
    |    "domainStrategy": "IPIfNonMatch",
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
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.serverBlockExceptions)}
    |        ],
    |        "outboundTag": "direct"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.serverBlock)}
    |        ],
    |        "outboundTag": "block"
    |      }
    |    ]
    |  }
    |}
  """.stripMargin

def vlessToJsonClient(
    remarks: String,
    raw: Raw,
    vless: Vless,
    reality: Reality,
    rules: Rules,
    user: VLRealityUser
): String =
  val isMuxEnabled = if vless.flow == "" then "true" else "false"
  s"""
    |{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
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
    |    {
    |      "tag": "to_server",
    |      "protocol": "vless",
    |      "settings": {
    |        "vnext": [
    |          {
    |            "address": "${vless.address}",
    |            "port": ${vless.port},
    |            "users": [
    |              {
    |                "id": "${user.id}",
    |                "email": "${user.id.take(5)}",
    |                "encryption": "${vless.vlessEncryption}",
    |                "flow": "${vless.flow}"
    |              }
    |            ]
    |          }
    |        ]
    |      },
    |      "streamSettings": {
    |        "network": "raw",
    |        "security": "reality",
    |        "realitySettings": {
    |          "fingerprint": "${reality.fingerprint}",
    |          "serverName": "${reality.serverName}",
    |          "show": false,
    |          "password": "${reality.password}",
    |          "shortId": "${user.shortId}",
    |          "spiderX": "${user.spiderX}"
    |        },
    |        "sockopt": {
    |          "tcpFastOpen": true,
    |          "dialerProxy": "${raw.dialerProxy}"
    |        }
    |      },
    |      "mux": {
    |        "enabled": ${isMuxEnabled},
    |        "concurrency": 64,
    |        "xudpConcurrency": 64,
    |        "xudpProxyUDP443": "skip"
    |      }
    |    },
    |    {
    |      "tag": "direct",
    |      "protocol": "freedom"
    |    },
    |    {
    |      "tag": "block",
    |      "protocol": "blackhole"
    |    },
    |    {
    |      "tag": "direct_mod",
    |      "protocol": "freedom",
    |      "settings": {
    |        "fragment": {
    |          "packets": "tlshello",
    |          "length": "50-200",
    |          "interval": "10-50"
    |        },
    |        "noises": [
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          }
    |        ]
    |      }
    |    }
    |  ],
    |  "routing": {
    |    "domainStrategy": "AsIs",
    |    "rules": [
    |      {
    |        "type": "field",
    |        "ip": [ "geoip:private" ],
    |        "outboundTag": "block"
    |      },
    |      {
    |        "type": "field",
    |        "protocol": [ "bittorrent" ],
    |        "outboundTag": "direct"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.userProxy)}
    |        ],
    |        "outboundTag": "to_server"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.userDirect)}
    |        ],
    |        "outboundTag": "direct"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.userBlock)}
    |        ],
    |        "outboundTag": "block"
    |      }
    |    ]
    |  }
    |}
  """.stripMargin

def xhttpToJsonServer(
    remarks: String,
    xhttp: XHTTP,
    vless: Vless,
    rules: Rules,
    users: List[VLUser]
): String =
  val Array(listenAddress, listenPort) = vless.listen.split(":")
  val clients               = users.map{client => vlClientString(client.id, vless.flow)}.reduce(_ + ",\n" + _)
  s"""
    |{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
    |  },
    |  "inbounds": [
    |    {
    |      "tag": "from_clients",
    |      "listen": "${listenAddress}",
    |      "port": ${listenPort},
    |      "protocol": "vless",
    |      "settings": {
    |        "clients": [
${clients}
    |        ],
    |        "decryption": "${vless.vlessDecryption}"
    |      },
    |      "streamSettings": {
    |        "network": "xhttp",
    |        "security": "none",
    |        "xhttpSettings": {
    |          "path": "${xhttp.path}"
    |        },
    |        "sockopt": {
    |          "tcpFastOpen": false
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
    |    "domainStrategy": "IPIfNonMatch",
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
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.serverBlockExceptions)}
    |        ],
    |        "outboundTag": "direct"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.serverBlock)}
    |        ],
    |        "outboundTag": "block"
    |      }
    |    ]
    |  }
    |}
  """.stripMargin

def xhttpToJsonClient(
    remarks: String,
    xhttp: XHTTP,
    vless: Vless,
    rules: Rules,
    user: VLUser
): String =
  val isMuxEnabled = if vless.flow == "" then "true" else "false"
  s"""
    |{
    |  "remarks": "${remarks}",
    |  "log": {
    |    "access": "none",
    |    "error": "",
    |    "loglevel": "warning",
    |    "dnsLog": false
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
    |    {
    |      "tag": "to_server",
    |      "protocol": "vless",
    |      "settings": {
    |        "vnext": [
    |          {
    |            "address": "${vless.address}",
    |            "port": ${vless.port},
    |            "users": [
    |              {
    |                "id": "${user.id}",
    |                "email": "${user.id.take(5)}",
    |                "encryption": "${vless.vlessEncryption}",
    |                "flow": "${vless.flow}"
    |              }
    |            ]
    |          }
    |        ]
    |      },
    |      "streamSettings": {
    |        "network": "xhttp",
    |        "security": "tls",
    |        "xhttpSettings": {
    |          "path": "${xhttp.path}",
    |          "mode": "auto",
    |          "extra": {
    |            "noGRPCHeader": true
    |          }
    |        },
    |        "sockopt": {
    |          "tcpFastOpen": true,
    |          "dialerProxy": "${xhttp.dialerProxy}"
    |        }
    |      }
    |    },
    |    {
    |      "tag": "direct",
    |      "protocol": "freedom"
    |    },
    |    {
    |      "tag": "block",
    |      "protocol": "blackhole"
    |    },
    |    {
    |      "tag": "direct_mod",
    |      "protocol": "freedom",
    |      "settings": {
    |        "fragment": {
    |          "packets": "tlshello",
    |          "length": "50-200",
    |          "interval": "10-50"
    |        },
    |        "noises": [
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          },
    |          {
    |            "type": "rand",
    |            "packet": "50-1000",
    |            "delay": "10-20"
    |          }
    |        ]
    |      }
    |    }
    |  ],
    |  "routing": {
    |    "domainStrategy": "AsIs",
    |    "rules": [
    |      {
    |        "type": "field",
    |        "ip": [ "geoip:private" ],
    |        "outboundTag": "block"
    |      },
    |      {
    |        "type": "field",
    |        "protocol": [ "bittorrent" ],
    |        "outboundTag": "direct"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.userProxy)}
    |        ],
    |        "outboundTag": "to_server"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.userDirect)}
    |        ],
    |        "outboundTag": "direct"
    |      },
    |      {
    |        "type": "field",
    |        "domain": [
${domainString(rules.userBlock)}
    |        ],
    |        "outboundTag": "block"
    |      }
    |    ]
    |  }
    |}
  """.stripMargin
