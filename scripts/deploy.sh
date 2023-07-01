#!/bin/bash

REPOSITORY=/home/ec2-user/app
sudo chown -R ec2-user:ec2-user $REPOSITORY
sudo chmod -R 755 $REPOSITORY
cd $REPOSITORY

APP_NAME=o-vengers
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]; then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

echo "> Stopping Docker Compose"
sudo docker-compose -f "$REPOSITORY/resources/docker-compose.yml" down
sleep 5

echo "> Starting Docker Compose"
sudo docker-compose -f "$REPOSITORY/resources/docker-compose.yml" up -d

echo "> $JAR_PATH 배포"
sudo nohup java -jar \
        $JAR_PATH > $REPOSITORY/nohup.out 2>&1 &