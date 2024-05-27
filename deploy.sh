#!/bin/bash

DEPLOY_DIR="/root"
SERVICE_FILE="/etc/systemd/system/telegram-simple-bot.service"

cd $DEPLOY_DIR

sudo systemctl stop telegram-simple-bot

unzip -o target/myapp.zip -d $DEPLOY_DIR

position=$(grep -n "\[Service\]" $SERVICE_FILE | cut -d: -f1)

head -n "$position" $SERVICE_FILE > $SERVICE_FILE.temp
tail -n +"$((position+1))" $SERVICE_FILE >> $SERVICE_FILE.temp

echo "Environment=\"BOT_TOKEN=${BOT_TOKEN}\"" >> $SERVICE_FILE.temp

sudo mv $SERVICE_FILE.temp $SERVICE_FILE

sudo systemctl daemon-reload

sudo systemctl start telegram-simple-bot
