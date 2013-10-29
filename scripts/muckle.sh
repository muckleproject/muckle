#!/bin/sh
if [ -z "$JAVA_HOME" ]; then
  echo "Please set JAVA_HOME"
else
  $JAVA_HOME/bin/java -Xmx512m -cp 'libs/*' org.sh.muckle.runtime.js.Bootstrap $@
fi
