#!/bin/bash

DEPLOY_DIR="/root"

cd $DEPLOY_DIR

sudo systemctl stop telegram-simple-bot

unzip -o target/myapp.zip -d $DEPLOY_DIR

sudo sed -i '/Environment="BOT_TOKEN=/d' /etc/systemd/system/telegram-simple-bot.service
sudo sed -i '/Environment="GPT_TOKEN=/d' /etc/systemd/system/telegram-simple-bot.service
echo "Environment=\"BOT_TOKEN=${BOT_TOKEN}\"" | sudo tee -a /etc/systemd/system/telegram-simple-bot.service
echo "Environment=\"GPT_TOKEN=${GPT_TOKEN}\"" | sudo tee -a /etc/systemd/system/telegram-simple-bot.service

sudo systemctl daemon-reload

sudo systemctl start telegram-simple-bot
