#!/bin/bash

DEPLOY_DIR="/root"

cd $DEPLOY_DIR

sudo systemctl stop simple-telegram-bot

unzip target/myapp.zip -d $DEPLOY_DIR

sudo systemctl start simple-telegram-bot

