## install-server-rocky.sh

Скрипт для **RHEL-подобных** систем (Rocky, Alma, CentOS), автоматизирующий рутинные действия
 — от создания swap, переноса порта `ssh` и установки `xray-core` до создания своего сайта
с сертификатом Let's Encrypt.

Скачать и запустить скрипт на сервере:

```bash
curl -L https://git.sr.ht/~atom4peace/portal/blob/trunk/install-server-rocky.sh > install-server-rocky.sh
chmod +x install-server-rocky.sh
./install-server-rocky.sh
```

