#!/usr/bin/env bash

echo "Выберите действие:

(0) Апдейт системы и установка минимальных зависимостей для других шагов.
(1) Создать нового пользователя и открыть новый порт для ssh.
(2) Установить Xray.
(3) Создать конфигурацию portal-xhttp.toml.
(4) Установить и настроить Nginx и сайт.
(5) Установить Scala-cli."

read variant

if [[ $variant -eq 0 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    dnf install -y epel-release
    dnf update -y
    dnf install -y openssl iproute # -> команда ss
    dnf install -y nano vim

elif [[ $variant -eq 1 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    echo "Введите имя пользователя. Оставьте пустым, если
пользователя создавать не нужно."
    read username

    echo "Введите новый порт для ssh, например 20022.
Оставьте пустым, если не планируете переносить порт ssh."
    read ssh_port

    if [[ -v username && ! -z $username ]]; then
        if getent passwd $username >/dev/null; then
            echo "Пользователь '$username' уже существует."
        else
            useradd -m "${username}"
            user_password=$(openssl rand -base64 9)
            echo "${user_password}\n${user_password}" | passwd $username --stdin
            if getent group wheel > /dev/null; then
                usermod -aG wheel $username
            fi
        fi
    fi

    if [[ -v user_password ]]; then
        echo "Создан новый пользователь:
    username: $username
    password: $password
Не забудьте поменять пароль с помощью \$ passwd $username"
    fi

    echo "Для продолжения нажмите Enter."
    read useless

    if [[ -v ssh_port && ! -z $ssh_port ]]; then
        if ss -tnlp | grep :$ssh_port > /dev/null; then
            if ss -tnlp | grep :$ssh_port | grep \"sshd\" > /dev/null; then
                echo "sshd уже использует порт $ssh_port, ok."
            else
                echo "Порт $ssh_port уже используется другим сервисом, не могу изменить!"
                exit 1
            fi
        else
            echo "Port 22" | tee -a /etc/ssh/sshd_config
            echo "Port $ssh_port" | tee -a /etc/ssh/sshd_config
            dnf install -y policycoreutils-python-utils
            command -v semanage > /dev/null && semanage port -a -t ssh_port_t -p tcp $ssh_port
            if command -v firewall-cmd >/dev/null 2>&1 && [ "$(firewall-cmd --state)" = "running" ]; then
                firewall-cmd --permanent --add-port=$ssh_port/tcp
                firewall-cmd --reload
            fi
            if $(sshd -t && systemctl restart sshd); then
                echo "sshd перезапущен."
            else
                echo "Сервис sshd не смог перезапусьтиться!"
                echo "Доступ к серверу может быть потерян!
Пожалуйста, прочитайти внимательно все сообщения об ошибках,
внесите необходимые исправления и попробуйте перезапустить sshd:
    # systemctl restart sshd
затем проверьте его статус:
    \$ systemctl status sshd"
            fi
            if ss -tnlp "sport = :$ssh_port" | grep -q sshd; then
                echo "sshd теперь использует порт $ssh_port, ok."
                echo "Проверьте из другого терминала, что можете подключиться
новым пользователем по новому порту. После этого можете закомментировать строки
    Port 22
в файле /etc/ssh/sshd_config и перезапустить sshd:
    # systemctl restart sshd"
            else
                echo "Ошибка: sshd перезапущен, но не использует новый порт."
            fi
        fi
    fi

elif [[ $variant -eq 2 ]]; then

    if [[ $EUID -ne 0 || -z $SUDO_USER ]]; then
        echo "Эту команду нужно выполнять с sudo, и не от пользователя root!"
        exit 1
    fi

    # https://github.com/XTLS/Xray-install
    bash -c "$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)" @ install -u $SUDO_USER --logrotate 23:59:59

elif [[ $variant -eq 3 ]]; then

    if [[ "$EUID" -eq 0 ]]; then
        echo "Нужно быть обычным пользователем (не рутом) для выполнения этой команды."
        exit 1
    fi

    if [[ -f portal-xhttp.toml ]]; then
        echo "Файл portal-xhttp.toml уже существует!"
        exit 1
    fi

    echo "Введите доменное имя (адрес) вашего сайта, за которым будет прятаться Xray."
    read address

    echo "Введите желаемое число ключей (по умолчанию — 50)."
    read n_users
    if [[ ! -v n_users || -z $n_users ]]; then
        n_users=50
    fi

    if [[ -v address && ! -z $address ]]; then
        path=$(openssl rand -hex 4)
        dec_enc=$(xray vlessenc | head -n 5)
        decryption=$(printf '%s\n' "$dec_enc" | grep decryption | cut -d " " -f 2)
        encryption=$(printf '%s\n' "$dec_enc" | grep encryption | cut -d " " -f 2)
        cat > portal-xhttp.toml <<EOF
remarks = "XHTTP"

[xhttp]
path = "/$path"
dialerProxy = "direct"

[vless]
address = "$address"
listen = "127.0.0.1:9443"
port = 443
flow = "xtls-rprx-vision"
vlessDecryption = $decryption
vlessEncryption = $encryption

[rules]
userProxy = ["media", "tiktok"]
userDirect = ["dot_ru", "ru", "torrent_trackers"]
userBlock = [""]
serverBlockExceptions = ["media"]
serverBlock = ["ru", "torrent_trackers"]
EOF
        for ((i = 1; i <= n_users; i++)); do
            id=$(xray uuid)
            cat >> portal-xhttp.toml <<EOF

[[users]]
id = "$id"
EOF
        done

        echo "Конфигурационный файл potral.toml создан. При желании вы можете исправить его —
он будет использоваться при настройке Nginx и Xray."

    else
        echo "Адрес не может быть пустым!"
        exit 1
    fi
    
elif [[ $variant -eq 4 ]]; then

    if [[ "$EUID" -ne 0 ]]; then
        echo "Нужно быть суперпользователем или рутом для выполнения этой команды."
        exit 1
    fi

    if [[ ! -f portal-xhttp.toml ]]; then
        echo "Файл portal-xhttp.toml не найден, сгенерируйте его на предыдущем шаге!"
        exit 1
    fi

    dnf install -y epel-release
    dnf update -y
    dnf install -y nginx gawk sed certbot python3-certbot-nginx bind-utils # -> команда dig

    path=$(grep -E "^\s*path\s*=" "portal-xhttp.toml" | awk -F'=' '{gsub(/[[:space:]]*/, "", $2); print $2}' | awk '{ sub(/^"/, ""); sub(/"$/, ""); print }')
    address=$(grep -E "^\s*address\s*=" "portal-xhttp.toml" | awk -F'=' '{gsub(/[[:space:]]*/, "", $2); print $2}' | awk '{ sub(/^"/, ""); sub(/"$/, ""); print }')
    listen=$(grep -E "^\s*listen\s*=" "portal-xhttp.toml" | awk -F'=' '{gsub(/[[:space:]]*/, "", $2); print $2}' | awk '{ sub(/^"/, ""); sub(/"$/, ""); print }')
    port=$(grep -E "^\s*port\s*=" "portal-xhttp.toml" | awk -F'=' '{gsub(/[[:space:]]*/, "", $2); print $2}')

    echo "Введите IP-адрес, который Nginx должен слушать (IP сервера)."
    read ip

    if command -v firewall-cmd >/dev/null 2>&1 && [ "$(firewall-cmd --state)" = "running" ]; then
        firewall-cmd --permanent --add-port=${port}/tcp
        firewall-cmd --reload
    fi

    mkdir -p /var/www/fake_site
    curl https://raw.githubusercontent.com/XTLS/Xray-install/refs/heads/main/install-release.sh > /var/www/fake_site/testfile

    cat > /var/www/fake_site/index.html <<EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$(echo -n $address | cut -d "." -f 1)</title>
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

    mkdir -p /etc/ssl/${address}
    cat > /etc/nginx/conf.d/fake_site.conf <<EOF
server {
    listen $ip:$port http2;

    # Если вы планируете использовать свои сертификаты,
    # закомментируйте строку выше (listen) и раскомментируйте строки ниже;
    # Поместите сертификаты в указанную директорию, выполните при необходимости:
    #  semanage fcontext -a -t httpd_config_t "/etc/ssl/${address}(/.*)?"
    #  restorecon -Rv /etc/ssl/$address

    #listen $ip:$port ssl http2;
    #ssl_certificate /etc/ssl/${address}/fullchain_cert.pem;
    #ssl_certificate_key /etc/ssl/${address}/private_key.pem;
    #ssl_protocols       TLSv1.2 TLSv1.3;
    #ssl_ciphers         HIGH:!aNULL:!MD5;
    #ssl_prefer_server_ciphers on;

    server_name $address;

    root /var/www/fake_site;
    index index.html;

    location ${path}/ {
        proxy_pass http://${listen};

        proxy_set_header Host \$host;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-For \$remote_addr;
        proxy_set_header Connection "";

        proxy_http_version 1.1;
    }

    location / {
        try_files \$uri \$uri/ /index.html;
    }

    #access_log off;
    access_log  /var/log/nginx/access.log  main;
    error_log   /var/log/nginx/error.log   warn;
}
EOF

    dnf install -y policycoreutils-python-utils
    if [[ $(getenforce 2>/dev/null) == "Enforcing" ]]; then
        chcon -R -t httpd_sys_content_t /var/www/fake_site
        setsebool -P httpd_can_network_connect 1
        setsebool -P httpd_can_network_relay 1
        listen_port=$(echo -n $listen | cut -d ":" -f 2)
        semanage port -a -t http_port_t -p tcp $port
        semanage port -a -t http_port_t -p tcp $listen_port
    fi

    echo "Получить сертификаты Letsencrypt? Y/n"
    read letsencrypt

    if [[ $letsencrypt == "n" ]]; then
        echo "Планируете использовать свои сертификаты?
Прочитайте комментарии в файле /etc/nginx/conf.d/fake_site.conf!
Не забудьте перезапустить nginx после настройки сертификатов.
Для продолжения нажмите Enter."
        read useless
    else
        echo "Для получения сертификатов IP-адрес сервера должен совпадать с DNS записью для $address,
$(dig +short $address)"
        echo "Для продолжения нажмите Enter."
        read useless

        if certbot --register-unsafely-without-email --nginx -d "$address" && systemctl enable nginx && nginx -t && systemctl restart nginx; then
            echo "Сертификаты получены, nginx перезапущен!"
        else
            echo "Что-то пошло не так!"
            exit 1
        fi
    fi

    echo "
Важно!
- Проверьте работу сайта $address.
- Проверьте, что файл ${address}/testfile скачивается целиком. Если скачивание
  останавливается на середине, ваш сервер вероятно под блокировкой РКН. Если файл
  скачивается полностью, удалите /var/www/fake_site/testfile!
- Поменяйте код вашего сайта /var/www/fake_site/index.html.
  Например, добавьте иконку или сгенерируйте полностью новую страницу
  на duck.ai.
- В файле /etc/nginx/nginx.conf рекомендуем закомментировать блок
    server {
        listen 80;
        ...
    }
- Если нужно временно выключить firewall и selinux, выполните:
  # setenforce 0
  # systemctl stop firewalld"

elif [[ $variant -eq 5 ]]; then

    if [[ "$EUID" -eq 0 ]]; then
        echo "Нужно быть обычным пользователем (не рутом) для выполнения этой команды."
        exit 1
    fi

    # scala-cli https://scala-cli.virtuslab.org/install
    curl -sSLf https://scala-cli.virtuslab.org/get | sh

else

    echo "Нужно выбрать число от 1 до 5."
    exit 1

fi

