#!/usr/bin/env bash

exec > >(tee install-server-rocky.log)
exec 2>&1

# stdout styles
red='\033[0;31m'
normal='\033[0m'

cat <<EOF
Этот скрипт поможет тебе настроить систему для обхода блокировок интернета.
Подробности: https://git.sr.ht/~atom4peace/portal/tree/trunk/item/Readme.md

Если что-то пойдёт не так, все последние сообщения ищи в логе: install-server-rocky.log

Выбери действие:

(0) Создать swap.
(1) Апдейт системы и установка необходимых/полезных утилит.
(2) Создать обычного пользователя в системе.
(3) Перенести порт для ssh.
(4) Установить Xray (xray-core).
(5) Установить и настроить Nginx (сайт-заглушка для XHTTP, сертификат Let's Еncrypt).
EOF

read variant

if [[ $variant -eq 0 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    if swapon --show | grep -q .; then
        echo "Swap уже есть."
    else
        sudo dd if=/dev/zero of=/swapfile bs=1M count=2048 status=progress
        sudo chmod 600 /swapfile
        sudo mkswap /swapfile
        echo "/swapfile none swap sw 0 0" >> /etc/fstab
        echo "Готово!"
    fi

elif [[ $variant -eq 1 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    if ! command -v microdnf &>/dev/null; then
        dnf install -y microdnf;
    fi
    microdnf install -y epel-release
    microdnf update -y
    microdnf install -y openssl iproute bind-utils nano vim git coreutils

elif [[ $variant -eq 2 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    echo "Введите имя пользователя."
    read -r username

    if [[ -n $username ]]; then
        if getent passwd $username >/dev/null; then
            echo "Пользователь '$username' уже существует."
            exit 1
        else
            useradd -m "${username}"
            user_password=$(openssl rand -base64 9)
            echo -e "${user_password}\n${user_password}" | passwd $username --stdin
            if getent group wheel > /dev/null; then
                usermod -aG wheel $username
            fi
            echo "Создан новый пользователь, $username,"
            echo "Не забудь поменять пароль с помощью passwd $username"
        fi
    fi

elif [[ $variant -eq 3 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    echo "Введите новый порт для ssh, например 20022."
    read -r ssh_port

    if [[ ! -z $ssh_port ]]; then
        [[ $ssh_port -ge 1 && $ssh_port -le 65535 ]] || { echo "ОШИБКА! Порт должен быть в диапазоне 1-65535."; exit 1; }
        if ss -tnlp | grep -w ":$ssh_port" > /dev/null; then
            if ss -tnlp | grep :$ssh_port | grep \"sshd\" > /dev/null; then
                echo "sshd уже использует порт $ssh_port."
                exit 1
            else
                echo "Порт $ssh_port уже используется другим сервисом, не могу изменить!"
                exit 1
            fi
        else
            echo "Port 22" | tee -a /etc/ssh/sshd_config
            echo "Port $ssh_port" | tee -a /etc/ssh/sshd_config
            microdnf install -y policycoreutils-python-utils
            command -v semanage > /dev/null && semanage port -a -t ssh_port_t -p tcp $ssh_port
            if command -v firewall-cmd >/dev/null 2>&1 && [ "$(firewall-cmd --state)" = "running" ]; then
                firewall-cmd --permanent --add-port=80/tcp # должен быть доступен для certbot challenge
                firewall-cmd --permanent --add-port=${ssh_port}/tcp
                firewall-cmd --reload
            fi
            if sshd -t && systemctl restart sshd; then
                echo "sshd перезапущен."
            else
                echo ""
                cat <<EOF
**** Сервис sshd не смог перезапусьтиться! ****
!! Доступ к серверу может быть потерян !!
Пожалуйста, прочитайти внимательно все сообщения об ошибках,
внесите необходимые исправления и попробуйте перезапустить sshd:
    # systemctl restart sshd
затем проверьте его статус:
    $ systemctl status sshd
EOF
            fi
            if ss -tnlp "sport = :$ssh_port" | grep -q sshd; then
                echo "sshd теперь использует порт $ssh_port, ok."
                cat <<EOF
Проверьте из другого терминала, что можете подключиться
новым пользователем по новому порту. После этого можете закомментировать строки
    Port 22
в файле /etc/ssh/sshd_config и перезапустить sshd:
    # systemctl restart sshd
EOF
            else
                echo "ОШИБКА: sshd перезапущен, но не использует новый порт."
            fi
        fi
    fi

elif [[ $variant -eq 4 ]]; then

    if [[ $EUID -ne 0 || -z $SUDO_USER ]]; then
        echo "Эту команду нужно выполнять с sudo, и не от пользователя root!"
        exit 1
    fi

    # https://github.com/XTLS/Xray-install
    bash -c "$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)" @ install -u $SUDO_USER --logrotate 23:59:59

elif [[ $variant -eq 5 ]]; then

    if [[ $EUID -ne 0 || -z $SUDO_USER ]]; then
        echo "Эту команду нужно выполнять с sudo, и не от пользователя root!"
        exit 1
    fi

    echo "Введите доменное имя для сайта (SNI):"
    read -r domain
    if [[ -z $domain ]]; then
        echo "Имя не может быть пустым!"
        exit 1
    fi

    echo "Введите порт (по умолчанию 443):"
    read -r port
    if [[ -z $port ]]; then
        port=443
    fi

    echo "Введите IP, на котором будет слушать Nginx (по умолчанию 0.0.0.0):"
    read -r listen
    if [[ -z $listen ]]; then
        listen="0.0.0.0"
    fi

    echo "Введите секретный путь для XHTTP (должен начинаться с '/'):"
    read -r xhttp_secret_path 
    if [[ -z $xhttp_secret_path ]]; then
        xhttp_secret_path=$(openssl rand -hex 7)
    fi

    microdnf install -y nginx policycoreutils-python-utils
    sudo semanage port -a -t http_port_t -p tcp 9443
    if command -v firewall-cmd >/dev/null; then
        firewall-cmd --permanent --add-port=${port}/tcp
        firewall-cmd --permanent --add-port=${port}/udp
        firewall-cmd --reload
    fi

    xray_listen="127.0.0.1:9443"

    mkdir -p /var/www/fake_site

    cat > /var/www/fake_site/index.html <<EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$(echo -n ${domain} | cut -d "." -f 1)</title>
    <!-- link rel="icon" type="image/png" href="favicon.png" -->
    <style>
        body { display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial, sans-serif; }
        .container { text-align: center; }
        input[type="text"] { padding: 10px; font-size: 16px; margin-right: 10px; }
        button { padding: 10px 20px; font-size: 16px; }
    </style>
</head>
<body>
    <div class="container">
        <input type="text" id="idInput" placeholder="Enter Id">
        <button onclick="redirectTo()">Go!</button>
    </div>
    <script>
        function redirectTo() {
            var url = document.getElementById('idInput').value;
            if (url) {
                window.location.href = url;
            } else {
                alert('Please enter your Id here');
            }
        }
    </script>
</body>
</html>
EOF

    nginx_version=$(nginx -v | cut -d "." -f 2)
    if [[ $nginx_version -ge 25 ]]; then
        listen_line="listen ${listen}:${port} ssl;"
        http2_line="http2 on;"
    else
        listen_line="listen ${listen}:${port} ssl http2;"
        http2_line=""
    fi
    cat > /etc/nginx/conf.d/fake_site.conf <<EOF
server {
    ${listen_line}
    ${http2_line}

    ssl_certificate /etc/letsencrypt/live/${domain}/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/${domain}/privkey.pem;
    ssl_protocols       TLSv1.2 TLSv1.3;
    ssl_ciphers         HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    server_name ${domain};

    root /var/www/fake_site;
    index index.html;

    location ${xhttp_secret_path}/ {
        proxy_pass http://127.0.0.1:9443;

        proxy_set_header Host \$host;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-For \$remote_addr;
        proxy_set_header Connection "";

        proxy_http_version 1.1;
    }

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    access_log off;
    #access_log  /var/log/nginx/access.log  main;
    error_log   /var/log/nginx/error.log   warn;
}
EOF

    if [[ $(getenforce 2>/dev/null) == "Enforcing" ]]; then
        chcon -R -t httpd_sys_content_t /var/www/fake_site
        setsebool -P httpd_can_network_connect 1
        setsebool -P httpd_can_network_relay 1
    fi

    microdnf install -y certbot python3-certbot-nginx
    if certbot certonly --standalone --register-unsafely-without-email -d "$domain" && systemctl enable nginx && nginx -t && (killall nginx || :) && systemctl restart nginx; then
         echo "Сертификаты получены, nginx перезапущен!"
         cat <<EOF
Сертификат получен!
Рекомендуем поменять код вашего сайта /var/www/fake_site/index.html,
например, добавиты иконку или сгенерироваты полностью новую страницу на duck.ai.
EOF
        else
            echo "ОШИБКА! Что-то пошло не так!"
            exit 1
        fi

else

    echo "Нужно выбрать число от 0 до 5."
    exit 1

fi

