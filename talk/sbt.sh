#! /usr/bin/env sh

if [ -z "$SBT_VERSION" ]
then
  SBT_VERSION=0.13.1
fi

SBT_LAUNCH_JAR=$HOME/.sbt/sbt-launch-$SBT_VERSION.jar

mkdir -p $HOME/.sbt

if [ ! -r $SBT_LAUNCH_JAR ]
then
  SBT_URL="http://javarepo.bis.epost-dev.de/nexus/content/repositories/typesafe/org.scala-sbt/sbt-launch/$SBT_VERSION/sbt-launch.jar"
  echo "Downloading sbt $SBT_VERSION from $SBT_URL"
  wget -q --no-proxy -O "$SBT_LAUNCH_JAR" $SBT_URL

  if [ ! -r $SBT_LAUNCH_JAR ]
  then
    echo "Unable to download file."
  fi
fi

if test "$1" = "debug"; then
  JPDA_PORT="9999"
  shift
fi

if [ -z "${JPDA_PORT}" ]; then
  DEBUG_PARAM=""
else
  DEBUG_PARAM="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${JPDA_PORT}"
fi

SSL_CONTEXT=""
if test "$1" = "ssl"; then
  # first make a http request: http://localhost:9001/safe/internal/health?pretty=true
  # then you can use safe-ui with SSL: https://localhost:9443/safe/list
  SSL_CONTEXT="-Dhttps.port=9444 -Dhttps.keyStore=puppet/modules/sau/files/authservice-keystore.jks -Dhttps.keyStorePassword=s3cr3t -Dhttps.keyStoreAlgorithm=epostKey"
  shift
fi

java ${DEBUG_PARAM} ${SSL_CONTEXT} -Dhttp.port=9001 -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -Dhttp.proxyHost=cache.epost.de -Dhttp.proxyPort=8080 -jar $SBT_LAUNCH_JAR "$@"
