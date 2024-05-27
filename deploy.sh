#!/bin/bash

DEPLOY_DIR="/root"
SERVICE_FILE="/etc/systemd/system/telegram-simple-bot.service"

cd $DEPLOY_DIR

sudo systemctl stop telegram-simple-bot

unzip -o target/myapp.zip -d $DEPLOY_DIR

sudo sed -i '/Environment="BOT_TOKEN=/d' /etc/systemd/system/telegram-simple-bot.service

position=$(awk '/\[Service\]/{print NR}' $SERVICE_FILE)

awk -v pos=$((position + 1)) 'NR == pos {print "Environment=\"BOT_TOKEN=${BOT_TOKEN}\""} 1' $SERVICE_FILE | sudo tee $SERVICE_FILE.temp > /dev/null

sudo mv $SERVICE_FILE.temp $SERVICE_FILE

sudo systemctl daemon-reload

sudo systemctl start telegram-simple-bot
