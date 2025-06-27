#!/usr/bin/env bash


# feel free to change
username="prtlusr"
sshport="12345"


# for colored stdout
red='\033[0;31m'
green='\033[0;32m'
yellow='\033[0;33m'
normal='\033[0m'


# user
if getent passwd $username >/dev/null; then
    echo -e "${green}User '$username' already exists, ok.$normal"
else
    useradd -m "${username}"
    password=$(openssl rand -base64 9)
    echo -e "${password}\n${password}" | passwd $username --stdin
fi

if getent group wheel > /dev/null; then
    usermod -aG wheel $username
fi


# deps
dnf update --assumeyes
dnf install --assumeyes openssl iproute


# ssh
if ss -tunlp | grep :$sshport > /dev/null; then
    if ss -tunlp | grep :$sshport | grep \"sshd\" > /dev/null; then
        echo -e "${green}ssh already uses port $sshport, ok.$normal"
    else
        echo -e "${red}Port $sshport is already in use, aborting!$normal"
        exit 1
    fi
else
    echo "Port 22" | tee -a /etc/ssh/sshd_config # just in case
    echo "Port $sshport" | tee -a /etc/ssh/sshd_config
    command -v semanage > /dev/null && semanage port -a -t ssh_port_t -p tcp $sshport
    sshd -t && systemctl restart sshd
fi


# firewall
if [ $(command -v firewall-cmd > /dev/null) ] && [ $(firewall-cmd --state) = "running" ]
then
    firewall-cmd --list-all
    firewall-cmd --permanent --add-port=443/tcp
    firewall-cmd --permanent --add-port=$sshport/tcp
    firewall-cmd --reload
fi


# xray https://github.com/XTLS/Xray-install
bash -c "$(curl -L https://github.com/XTLS/Xray-install/raw/main/install-release.sh)" @ install -u $username --logrotate 23:59:59


# scala-cli https://scala-cli.virtuslab.org/install
su - "$username" <<'EOF'
curl -sSLf https://scala-cli.virtuslab.org/get | sh
EOF


echo -e "
${green}==== Summary ====$normal
"

echo "Check that ssh is available at port $sshport
then fill free to close port 22 commenting line(s)
    Port 22
in /etc/ssh/sshd_config and running
    systemctl restart sshd
"

if [ ! -v $password ]
then
    echo -e "New user has been created,
    username: $username
    password: $password
${yellow}!! Please set new password with command: !!$normal
    passwd $username
"
fi
