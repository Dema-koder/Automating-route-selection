#!/bin/bash

DEPLOY_DIR="/root"

cd $DEPLOY_DIR

sudo systemctl stop telegram-simple-bot

unzip -o target/myapp.zip -d $DEPLOY_DIR

sudo sed -i '/Environment="BOT_TOKEN=/d' /etc/systemd/system/telegram-simple-bot.service

sudo systemctl daemon-reload

sudo systemctl start telegram-simple-bot
