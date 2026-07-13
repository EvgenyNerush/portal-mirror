## install-zapret2-rocky.sh

Скрипт для RHEL-подобных систем (Rocky Linux, AlmaLinux, CentOS), помогающий установить
[Zapret2](https://github.com/bol-van/zapret2/tree/master). Zapret2 — это инструмент манипулирования пакетами для обмана оборудования,
осуществляющего цензуру (ТСПУ/DPI). Внимательно читайте вывод скрипта; при необходимости
поставьте недостающие пакеты вручную и перезапустите скрипт.

Скачать и запустить install-zapret2-rocky.sh можно так:

```bash
curl -L https://git.sr.ht/~atom4peace/portal/blob/trunk/install-zapret2-rocky.sh > install-zapret2-rocky.sh
chmod +x install-zapret2-rocky.sh
sudo ./install-zapret2-rocky.sh
````

После установки зависимостей запустится установщик Zapret2. На все его вопросы можно выбрать
ответ по умолчанию, нажав Enter, кроме следующих:

````
easy install is supported only from default location : /opt/zapret2
currently its run from /home/username/zapret2
do you want the installer to copy it for you?
````
— выберите Y,

```
enable nfqws2 ?
```
— Y

```
LAN interface :
1 : NONE
2 : ens0
```
— следует выбрать ваш интерфейс.

### Что дальше?

Скрипт устанавливает простое правило модификации пакетов, которое тебе наверняка захочется поменять.
Правило Zapret2 описано в переменной `NFQWS2_OPT` в файле `/opt/zapret2/config`. При написании своих правил
полезно пользоваться [справкой](https://github.com/bol-van/zapret2/blob/master/docs/manual.md) (например,
[lua-desync](https://github.com/bol-van/zapret2/blob/master/docs/manual.md#%D0%BF%D0%BE%D0%BB%D0%BD%D1%8B%D0%B9-%D1%81%D0%BF%D0%B8%D1%81%D0%BE%D0%BA-%D0%BE%D0%BF%D1%86%D0%B8%D0%B9),
[multidisorder](https://github.com/bol-van/zapret2/blob/master/docs/manual.md#multidisorder))
и включить флаг `--debug=@/var/log/nfqws2.log`. После перезапуска Запрета `sudo systemctl restart zapret2` и проверки статуса
`systemctl status zapret2` логи можно посмотреть в `/var/log/nfqws2.log`.

Возможно, тебе пригодится пример сложного правила — как в [config.default](https://github.com/bol-van/zapret2/blob/master/config.default):

```bash
--filter-tcp=80 --filter-l7=http <HOSTLIST> --payload=http_req --lua-desync=fake:blob=fake_default_http:tcp_md5 --lua-desync=multisplit:pos=method+2 --new
--filter-tcp=443 --filter-l7=tls <HOSTLIST> --payload=tls_client_hello --lua-desync=fake:blob=fake_default_tls:tcp_md5:tcp_seq=-10000 --lua-desync=multidisorder:pos=1,midsld --new
--filter-udp=443 --filter-l7=quic <HOSTLIST_NOAUTO> --payload=quic_initial --lua-desync=fake:blob=fake_default_quic:repeats=6
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
