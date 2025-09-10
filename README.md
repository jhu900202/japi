# japi


# Linux Server Setup Guide

## Overview
리눅스 서버 환경 구축 과정입니다.

### Hardware Configuration
- 노트북 덮개 동작 비활성화(서버용 설정)
```bash
vi /etc/systemd/logind.conf
```
```ini
# 기존: #HandleLidSwitch=suspend
# 변경 후:
HandleLidSwitch=ignore
```
```bash
systemctl restart systemd-logind
```

### Network Configuration
Wake-on-LAN 기능이 필요한 경우 다음 과정을 진행합니다.
```bash
yum install -y ethtool net-tools
```
- 네트워크 인터페이스 확인
```bash
ifconfig
```
- WOL 서비스 설정 파일 생성/편집
```bash
vi /etc/systemd/system/wol.service
```
```ini
[Unit]
Description=Configure Wake-up on LAN
After=network.target

[Service]
Type=oneshot
ExecStart=/sbin/ethtool -s [ETHERNET_INTERFACE] wol g

[Install]
WantedBy=basic.target
```
- WOL 서비스 등록 및 시작
```bash
systemctl daemon-reload
systemctl enable wol.service
systemctl start wol.service
```
- 네트워크 인터페이스 WOL 설정
```bash
nmcli con show
nmcli c show "[ETHERNET_INTERFACE]" | grep ethernet.wake-on-lan
nmcli c modify "[ETHERNET_INTERFACE]" ethernet.wake-on-lan magic
nmcli c show "[ETHERNET_INTERFACE]" | grep ethernet.wake-on-lan
```
- 시스템 재부팅
```bash
reboot
```

### Remote Access Configuration
- SSH 구성
```bash
firewall-cmd --permanent --add-port=22/tcp
firewall-cmd --reload
```
- FTP 구성
```bash
# vsftpd 설치 및 서비스 설정
dnf install -y vsftpd
systemctl start vsftpd
systemctl enable vsftpd
systemctl status vsftpd

# FTP 서비스 방화벽 설정
firewall-cmd --permanent --add-service=ftp
firewall-cmd --reload
firewall-cmd --list-all

# SELinux 상태 확인 및 FTP 권한 설정
sestatus
setsebool -P ftpd_full_access on

# 방화벽 설정 확인
firewall-cmd --list-all
```
- 원격 데스크탑(XRDP) 구성
```bash
# XRDP 패키지를 얻기 위한 EPEL 저장소 설치
dnf install epel-release -y

# XRDP 설치
dnf install xrdp -y
systemctl enable --now xrdp

# RDP 포트 방화벽 개방
firewall-cmd --permanent --add-port=3389/tcp
firewall-cmd --reload

# GUI 환경 설치
dnf groupinstall "Server with GUI" -y

# 기본 부팅 타겟을 GUI 모드로 설정
systemctl set-default graphical.target
```
- 시스템 재부팅
```bash
reboot
```

### Database Server Setup
- MariaDB 서버 설치 및 구성
``` bash
# MariaDB 설치
dnf install mariadb-server

# 서비스 시작 및 활성화
systemctl start mariadb
systemctl enable mariadb

# 설치 확인
mysql --version
systemctl status mariadb

# MariaDB 보안 설정
mysql_secure_installation

# 데이터베이스 포트 방화벽 개방
firewall-cmd --permanent --add-port=3306/tcp
firewall-cmd --reload

# MariaDB 접속 테스트
mariadb -u root -p
```
- 외부 접속 허용하도록 설정 변경
```bash
vi /etc/my.cnf.d/mariadb-server.cnf
```
```ini
# 기존: #bind-address=0.0.0.0
# 변경 후:
bind-address=0.0.0.0
```
- 서비스 재시작
```bash
systemctl restart mariadb
```
- 권한 설정
sql -- MariaDB 접속 후 실행 CREATE DATABASE japi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; CREATE USER 'japi'@'%' IDENTIFIED BY '[PASSWORD]'; GRANT ALL PRIVILEGES ON japi.* TO 'japi'@'%'; FLUSH PRIVILEGES;
-- 설정 확인 SHOW DATABASES; SELECT User, Host FROM mysql.user WHERE User = 'japi';

### Development Environment
- Git 설치 및 설정
```bash
# Git 설치
dnf install git
git --version

# Git 사용자 설정
git config --global user.name [USERNAME]
git config --global user.email [EMAIL]
```
- Java 웹 애플리케이션용 포트 방화벽 개방
```bash
firewall-cmd --list-ports
firewall-cmd --permanent --add-port=8080-8089/tcp
firewall-cmd --reload
```