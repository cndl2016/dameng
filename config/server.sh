#!/bin/sh
# ./ncdb.sh start 启动 stop 停止 restart 重启 status 状态
AppName=ncdb-admin-3.8.6.jar

# JVM参数
#JVM_OPTS="-Dname=$AppName  -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDateStamps  -XX:+PrintGCDetails -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC -XX:+UseParallelOldGC"
JVM_OPTS="-Dfile.encoding=utf-8 -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m"
APP_HOME=$(dirname $(readlink -f $0))
LOG_PATH=$APP_HOME/logs/$AppName.log
# H2数据库
H2_NAME=h2-2.2.224.jar

if [ "$1" = "" ];
then
    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$AppName" = "" ];
then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

function start_db()
{
PID=$(ps -ef |grep java|grep $H2_NAME|grep -v grep|awk '{print $2}')

if [ x"$PID" != x"" ]; then
    echo "$H2_NAME is running..."
else
    cd $APP_HOME
    nohup $APP_HOME/h2/bin/h2.sh > /dev/null 2>&1 &
    H2_PID=$(ps -ef |grep java|grep $H2_NAME|grep -v grep|awk '{print $2}')
    until [ -n "$H2_PID" ]
    do
    H2_PID=$(ps -ef |grep java|grep $H2_NAME|grep -v grep|awk '{print $2}')
    done
    
    echo "H2 pid is $H2_PID"
    echo "Start $H2_NAME success..."
fi
}

function start()
{
#start_db

PID=$(ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}')

if [ x"$PID" != x"" ]; then
    echo "$AppName is running..."
else
    cd $APP_HOME
    nohup java $JVM_OPTS -jar $APP_HOME/$AppName > /dev/null 2>&1 &
    MANAGER_PID=$(ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}')
    until [ -n "$MANAGER_PID" ]
    do
    MANAGER_PID=$(ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}')
    done
    
    echo "MANAGER pid is $MANAGER_PID"
    echo "Start $AppName success..."
fi
}

function stop_db()
{
echo "Stop $H2_NAME"

PID=""
query(){
PID=$(ps -ef |grep java|grep $H2_NAME|grep -v grep|awk '{print $2}')
}

query
if [ x"$PID" != x"" ]; then
    kill -TERM $PID
    echo "$H2_NAME (pid:$PID) exiting..."
    while [ x"$PID" != x"" ]
    do
        sleep 1
        query
    done
    echo "$H2_NAME exited."
else
    echo "$H2_NAME already stopped."
fi
}

function stop()
{
echo "Stop $AppName"

PID=""
query(){
    PID=$(ps -ef |grep java|grep $AppName|grep -v grep|awk '{print $2}')
}

query
if [ x"$PID" != x"" ]; then
    kill -TERM $PID
    echo "$AppName (pid:$PID) exiting..."
    while [ x"$PID" != x"" ]
    do
        sleep 1
        query
    done
    echo "$AppName exited."
else
    echo "$AppName already stopped."
fi

stop_db
}

function restart()
{
    stop
    sleep 2
    start
}

function status()
{
PID=$(ps -ef |grep java|grep $H2_NAME|grep -v grep|wc -l)
if [ $PID != 0 ];then
    echo "$H2_NAME is running..."
else
    echo "$H2_NAME is not running..."
fi

PID=$(ps -ef |grep java|grep $AppName|grep -v grep|wc -l)
if [ $PID != 0 ];then
    echo "$AppName is running..."
else
    echo "$AppName is not running..."
fi
}

case $1 in
    start)
    start;;
    stop)
    stop;;
    restart)
    restart;;
    status)
    status;;
    *)

esac
