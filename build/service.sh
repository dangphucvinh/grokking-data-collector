#!/bin/sh
#
# Startup script for Main
export APPNAME=kafka-collector
export JMXHOST=localhost
export JMXPORT=13333
export XMS=2G
export CONF=conf
export JARFILE=sbin/grokking.data.server.EmbeddedJettyServer.jar
pid_file=tmp/server.pid
log_file=tmp/server.log

# Arguments to pass to the JVM
JVM_OPTS=" \
        -Dappname=$APPNAME \
        -Dforeground=true \
        -Xmx$XMS \
        -XX:SurvivorRatio=8 \
        -XX:+UseParNewGC \
        -XX:+UseConcMarkSweepGC \
        -XX:+CMSParallelRemarkEnabled \
        -XX:MaxTenuringThreshold=1 \
        -XX:CMSInitiatingOccupancyFraction=75 \
        -XX:+UseCMSInitiatingOccupancyOnly \
        -XX:+HeapDumpOnOutOfMemoryError \
        -XX:-UseGCOverheadLimit \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dpidfile=$pid_file \
        -Dlog4j.configurationFile=conf/log4j2.xml \
        -Dmode=production"


if [ -x $JAVA_HOME/bin/java ]; then
    JAVA=$JAVA_HOME/bin/java
else
    JAVA=`which java`
fi

case "$1" in
    start)
        # Main startup
        echo -n "Starting $APPNAME.... "
#        exec $JAVA $JVM_OPTS -jar  $JARFILE 
        exec $JAVA $JVM_OPTS -jar  $JARFILE > $log_file 2>&1 &
	[ ! -z $pid_file ] && printf "%d" $! > $pid_file
        echo "OK"
        ;;
    stop)
        # Main shutdown
        echo -n "Shutdown $APPNAME ... "
        kill `cat $pid_file`
        echo "OK"
        ;;
    reload|restart)
        $0 stop
        $0 start
        ;;
    status)
        ;;
    *)
        echo "Usage: `basename $0` start|stop|restart|reload"
        exit 1
esac

exit 0