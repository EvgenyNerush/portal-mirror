#!/usr/bin/env bash

exec > >(stdbuf -oL tee install-zapret2-rocky.log)
exec 2>&1

if [[ "$EUID" -ne 0 ]]; then
    echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
    exit 1
fi

cat <<EOF
*********************
Этот скрипт для Rocky Linux (Alma, CentOS) поможет тебе
установить Zapret2 (https://github.com/bol-van/zapret2/tree/master).
(TODO) Подробности: https://git.sr.ht/~atom4peace/portal/tree/trunk/item/Readme.md
Правила модификации пакетов - см. NFQWS2_OPT в /opt/zapret2/config.

Если что-то пойдёт не так, все последние сообщения ищи в логе:
install-zapret2-rocky.log

Нажми Enter, чтобы продолжить.
EOF

read -r useless

if command -v microdnf > /dev/null; then
  mdnf=microdnf
else
  cat <<EOF
microdnf не найден!

Настроить swap и установить microdnf
можно с помощью ./install-server-rocky.sh

Продолжить с dnf (потребуется больше RAM)? (Y/n)
EOF
  read -r yn
  [[ ${yn:-} = "n" ]] && exit 1
  mdnf=dnf
fi

$mdnf install -y epel-release

if $mdnf install -y git luajit-devel libcap-devel zlib-devel systemd-devel libnetfilter_queue-devel libmnl-devel; then
  echo "Зависимости установлены."
else
  cat <<EOF
Не все зависимости найдены в репозитории!
Попробовать дополнительный репозиторий CRB для dnf? (Y/n)
EOF
  read -r yn
  [[ ${yn:-} = "n" ]] && exit 1
  dnf install -y dnf-plugins-core
  dnf config-manager --set-enabled crb
  dnf makecache
  if ! $mdnf install -y git luajit-devel libcap-devel zlib-devel systemd-devel libnetfilter_queue-devel libmnl-devel; then
    echo "Что-то пошло не так!"
    exit 1
  fi
fi

if [ ! -d "zapret2" ]; then
  if ! git clone --progress https://github.com/bol-van/zapret2.git; then
    echo "Что-то пошло не так!"
    exit 1
  fi
else
  echo "ОШИБКА: директория zapret2 уже существует!"
  exit 1
fi

cd zapret2
if ! bash install_prereq.sh; then
    echo "ОШИБКА! install_prereq.sh завершился с кодом 1."
    cd ..
    exit 1
fi

if ! bash install_easy.sh; then
    echo "ОШИБКА! install_easy.sh завершился с кодом 1."
    cd ..
    exit 1
fi

cd ..
if sed -i '/^NFQWS2_OPT=/,/^"/c\
NFQWS2_OPT="\
#--debug=@/var/log/nfqws2.log\
--filter-tcp=443 --filter-l7=tls --payload=tls_client_hello --lua-desync=multidisorder:pos=1,midsld\
"' /opt/zapret2/config; then
  echo "Правила NFQWS2_OPT записаны в /opt/zapret2/config"
else
  echo "ОШИБКА! Не смог записать правила для Zapret2."
  exit 1
fi

if systemctl restart zapret2 > /dev/null; then
  systemctl status zapret2
  echo "Готово!"
else
  echo "ОШИБКА: Zapret2 не смог перезапуститься!"
fi

