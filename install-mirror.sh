#!/usr/bin/env bash
# install-mirror-rocky.sh
# Configures Nginx stream proxy for XHTTP/REALITY/Trojan on Rocky Linux.

set -euo pipefail

LOG_FILE="install-mirror.log"
{
    cat <<EOF
****************************
Этот скрипт для Rocky/Debian поможет тебе настроить зеркало для XHTTP, REALITY или Trojan.
Подробности (TODO): https://git.sr.ht/~atom4peace/portal/tree/trunk/item/Readme.md

Если что-то пойдёт не так, все последние сообщения ищи в логе: install-mirror.log

Схема: сервер имеет два адреса (могут совпадать),
IP1 для приема данных пользователя, которые затем уходят
с адреса IP2 на целевой сервер, определяемый через SNI.
EOF
    
    if [[ "$EUID" -ne 0 ]]; then
        echo "Ошибка: Скрипт должен запускаться от имени root или с sudo."
        exit 1
    fi

    echo "Введите IP1 (входящий адрес):" 
    read -r ip1
    if [[ -z "${ip1}" ]]; then echo "Ошибка: IP1 не может быть пустым."; exit 1; fi

    echo "Введите IP2 (исходящий адрес, может совпадать с IP1):" 
    read -r ip2
    if [[ -z "${ip2}" ]]; then echo "Ошибка: IP2 не может быть пустым."; exit 1; fi

    echo "Введите доменное имя (SNI) целевого сервера:" 
    read -r sni
    if [[ -z "${sni}" ]]; then echo "Ошибка: SNI не может быть пустым."; exit 1; fi

    # Определение сестемы
    if command -v apt >/dev/null; then
        pkg_mgr="apt"
        ngnix_user="www-data"
        pkgs="nginx-extras libnginx-mod-stream"
    elif command -v microdnf >/dev/null || command -v dnf >/dev/null; then
        if command -v microdnf >/dev/null; then
            pkg_mgr="microdnf"
        else
            echo "Предупреждение: microdnf не найден. Использую dnf (требует больше RAM)."
            pkg_mgr="dnf"
        fi
        ngnix_user="nginx"
        pkgs="nginx nginx-mod-stream"
    else
        echo "Ошибка: Не найдено подходящего менеджера пакетов (apt или dnf)."
        exit 1
    fi

    # Backup
    if [[ -f /etc/nginx/nginx.conf && ! -f /etc/nginx/nginx.conf.backup ]]; then
        mv /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup
    fi

    echo "Установка зависимостей..."
    $pkg_mgr update -y
    $pkg_mgr install -y ${pkgs}

    # New configuration
    cat > /etc/nginx/nginx.conf <<EOF
user ${ngnix_user};
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '\$remote_addr - \$remote_user [\$time_local] "\$request" '
                      '\$status \$body_bytes_sent "\$http_referer" '
                      '"\$http_user_agent" "\$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 4096;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    #include /etc/nginx/conf.d/*.conf;

}

stream {
    access_log off;
    
    upstream backend_a {
        server "${sni}":443;
    }

    upstream blackhole_404 {
        # Ресет (RST) при коннекте к невалидному порту
        server 127.0.0.1:1;
    }

    map \$ssl_preread_server_name \$target_backend {
        default blackhole_404;
        "${sni}" backend_a;
    }

    server {
        listen "${ip1}":443;
        ssl_preread on;
        proxy_bind "${ip2}";
        proxy_pass \$target_backend;
        proxy_timeout 10s;
        proxy_connect_timeout 5s;
    }
}
EOF

    echo "Перезагрузка Nginx..."
    if systemctl restart nginx; then
        echo "Установка успешно завершена!"
    else
        echo "Ошибка: Nginx не удалось перезапустить."
        echo "Проверь логи: sudo nginx -t && sudo journalctl -u nginx -e"
        exit 1
    fi

} | tee "$LOG_FILE"
