## Portal

Portal — набор инструментов для установки и настройки [Xray](https://xtls.github.io/ru/), популярной программы для обхода интернет-цензуры.
В Portal будут только самые надёжные конфигурации для России. Всё управление происходит через один компактный TOML‑файл.

- `install-server-rocky.sh` – переносит SSH‑порт (по желанию), создаёт обычного пользователя, разворачивает сайт (нужен домен) и настраивает Xray на **Rocky Linux**
(Fedora, CentOS и AlmaLinux).

- `src/toml2json.scala` превращает TOML-конфиг в JSON-конфиг сервера Xray или в клиентский JSON (TODO выбор пользователя).

  ```scala-cli run ./src/toml2json.scala -- --xhttp --client portal.toml > xray_config.json```

- `src/toml2uri.scala` преобразует TOML-конфиг в набор URI (ссылки/ключи) для использования в приложениях:

  ```scala-cli run ./src/toml2uri.scala -- --xhttp portal.toml```

### Конфигурации

- XHTTP.
В настоящий момент поддерживается только XHTTP конфигурация, в которой Xray «прячется» за ваш собственный сайт на Nginx.

### Контакты/донаты

Интересно? Узнай больше в [Телеграм-канале](https://t.me/klein_bottle_vpn)

[Контакты](https://sr.ht/~atom4peace/)

TON UQA9zlDMQAvgj4OoYMl0JXNBzpTdprY46pxmm3KY9hQwgKDc

ZEC u1wruwk7r4hxqkcc95wyf5z5d3nxp5hwpzcl6mxa92ddr2yw3mr3wc694w6avhg7qfkg09lg3uj2j522qu56vjrdxcrfm7x072svtk4al5

