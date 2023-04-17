#!/bin/sh

unset http_proxy
unset https_proxy

exec java -XX:-OmitStackTraceInFastThrow \
-Djava.security.egd=file:/dev/./urandom \
-XX:MaxRAMPercentage=95.0 \
-jar \
/app.jar
