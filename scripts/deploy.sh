#!/bin/bash

BUILD_JAR=$(ls /home/ubuntu/app/build/libs/*.jar 2>/dev/null)
if [ -z "$BUILD_JAR" ]; then
    echo ">>> JAR 파일이 존재하지 않습니다." >> /home/ubuntu/deploy.log
    exit 1
fi

JAR_NAME=$(basename $BUILD_JAR)
echo ">>> build 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log

echo ">>> build 파일 복사" >> /home/ubuntu/deploy.log
DEPLOY_PATH=/home/ubuntu/app/build/libs/

if [ -f "$DEPLOY_PATH/gradlew" ]; then
    echo ">>> gradlew 파일이 이미 존재합니다. 복사를 건너뜁니다." >> /home/ubuntu/deploy.log
else
    cp $BUILD_JAR $DEPLOY_PATH
    echo ">>> build 파일이 복사되었습니다." >> /home/ubuntu/deploy.log
fi

echo ">>> 현재 실행중인 애플리케이션 pid 확인 후 일괄 종료" >> /home/ubuntu/deploy.log
sudo ps -ef | grep "$JAR_NAME" | grep -v grep | awk '{print $2}' | xargs kill -15

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포" >> /home/ubuntu/deploy.log
echo ">>> $DEPLOY_JAR의 $JAR_NAME를 실행합니다" >> /home/ubuntu/deploy.log
nohup java -jar $DEPLOY_JAR >> /home/ubuntu/deploy.log 2> /home/ubuntu/deploy_err.log &
