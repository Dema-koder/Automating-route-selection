#!/bin/bash

DEPLOY_DIR="/root"

cd $DEPLOY_DIR

sudo systemctl stop telegram-simple-bot

unzip target/myapp.zip -d $DEPLOY_DIR

sudo systemctl start telegram-simple-bot

