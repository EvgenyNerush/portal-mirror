## Portal

Portal — набор инструментов для установки и настройки [Xray](https://xtls.github.io/ru/), популярной программы для обхода интернет-цензуры.
В Portal будут только самые надёжные конфигурации для России. Всё управление происходит через один компактный TOML‑файл.

### Мануалы вспомогательных скриптов:

- [install-server-rocky.sh](Readme-server.md) — общая настройка сервера,
от установки xray-core до создания своего сайта с сертификатом Let's Encrypt.

- [install-mirror.sh](Readme-mirror.md) — создать «зеркало» — промежуточный узел
для перенаправления HTTPS/TLS трафика, а такжа трафика, который маскируется под таковой.

- [install-zapret2-rocky.sh](Readme-zapret2.md) — помогает установить
Zapret2 — инструмент манипулирования пакетами для обмана DPI/ТСПУ.

- [install-misc-rocky.sh](Readme-misc.md) —  настройка BBR,
установка wgcf для настройки WARP, перенаправление диапазона портов.

### Конфигурации

- XHTTP: Xray «прячется» за ваш собственный сайт на Nginx. Поддержка «зеркала» (адрес и SNI не совпадают),
раздельной загрузки и выгрузки, поддержка цепочки из двух серверов с подключением выходного узла
по схеме реверк-прокси.

- REALITY: для маскировки используется чужой сайт. Может работать через «зеркало». Использует мультиплексирование
`Mux.cool`, поэтому конфигурация совместима только с приложениями на ядре Xray;
в настройках приложения необходимо включить Туннель/Мультиплексирование (Mux) и выбрать TCP-соединения (Concurrency): 128,
XUDP-соединения: -1, Обработка QUIC (UDP443): пропустить (skip).

### Установка scala-cli

Для генерации и преобразования файлов конфигурации нужно установить `scala-cli`,
так как модули Portal написаны на языке Scala. [One-liner](https://scala-cli.virtuslab.org/install)
для установки (для обычного пользователя, не root):

```
    curl -sSLf https://scala-cli.virtuslab.org/get | sh
```

### Установка Portal

Склонируйте директорию проекта и перейдите в неё.
Для работы понадобятся файлы в директориях `apps` и `src`.

```
    git clone https://git.sr.ht/~atom4peace/portal
    cd portal
```

### Работа с конфигами

Промпт-генерация конфиг-файла `config.toml` для XHTTP или REALITY (--xhttp или --reality):

```
    scala-cli run apps/gentoml.scala -- --xhttp config.toml
```

TOML-файл удобно читать и легко редактировать. Он содержит необходимый минимум параметров Xray.
Для работы без подключения к интернету используйте `scala-cli run --power --offline`.

Конвертация TOML файла в файл конфигурации config.json для xray-core (сервер --server, клиент --client):
```
    scala-cli run apps/toml2json.scala -- --xhttp --server config.toml config.json
```

JSON-конфиг можно сразу скопировать на сервер и запустить с ним Xray:

```
    sudo cp config.json /usr/local/etc/xray/
    sudo systemctl restart xray
```

Конвертация TOML файла в клиентский файл конфигурации xray-core config.json для конкретного пользователя,
id которого начинается на `e3fbb`:
```
    scala-cli run ./apps/toml2json.scala -- --xhttp --client --user e3fbb config.toml config.json
```

Без указания пользователя (--user) генерируется конфиг для первого полязователя в списке. Если
указать id пользователя из списка `rproxyUsers`, будет сгенерирован JSON конфиг для выходнго
узла в схеме с обратным прокси (reverse proxy).

Преобразование TOML-конфига в список URI для всех пользователей (ссылки-ключи `vless://`), для использования в приложениях:

```
scala-cli run ./apps/toml2uri.scala -- --xhttp config.toml links.txt
```

URI для пользователя с id, который начинается на `e3fbb`:

```
scala-cli run ./apps/toml2uri.scala -- --xhttp --user e3fbb config.toml links.txt
```

### Поддержать

Подпишись на [телеграм-канал](https://t.me/klein_bottle_vpn) или
купи наш VPN за телеграм-звёзды в [боте](https://t.me/klein_bottle_vpn_bot). Это лучшая поддержка, потому что
помогает не только финансово, но и в тестировании технологий на территории России.

Можно просто пожертвовать телеграм-звёзды в
[том же боте](https://t.me/klein_bottle_vpn_bot) (бот расскажет, как их купить). Техподдержка, контакты которой есть в боте,
может помочь с вопросами по этому скрипту, если такие возникнут.

Для предложений и любой обратной связи анонимно — Matrix/Element `@atom4peace:matrix.org`

TON UQA9zlDMQAvgj4OoYMl0JXNBzpTdprY46pxmm3KY9hQwgKDc

ZEC u1wruwk7r4hxqkcc95wyf5z5d3nxp5hwpzcl6mxa92ddr2yw3mr3wc694w6avhg7qfkg09lg3uj2j522qu56vjrdxcrfm7x072svtk4al5

