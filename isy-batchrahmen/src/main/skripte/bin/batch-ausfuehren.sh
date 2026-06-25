#! /bin/bash

BASEDIR=`dirname $0`/..
pushd $BASEDIR > /dev/null

if [ ! "$JAVA8_HOME" ] 
then
    echo "Fehler: JAVA8_HOME ist nicht gesetzt."
    exit 1
else
    echo "Verwende JAVA8_HOME: $JAVA8_HOME"
fi

export JAVA_HOME=$JAVA8_HOME

JAVA=$JAVA_HOME/bin/java
JAVA_OPTS="${JAVA_OPTS} -Duser.language=de -Duser.region=DE -Dfile.encoding=UTF-8" 

CMD=(
  "$JAVA"
  $JAVA_OPTS
  -jar "$JAR"
  "$@"
  -batch
)
echo "${CMD[@]}"

"${CMD[@]}"
rueckgabe=$?

popd > /dev/null
exit ${rueckgabe}
