#! /bin/bash

if [ ! "$JAVA8_HOME" ] 
then
    echo "Fehler: JAVA8_HOME ist nicht gesetzt."
    exit 1
else
    echo "Verwende JAVA8_HOME: $JAVA8_HOME"
fi

export JAVA_HOME=$JAVA8_HOME

JAVA_OPTS="-Duser.language=de -Duser.region=DE -Dfile.encoding=UTF-8" 
JAVA_COMMAND=$JAVA_HOME/bin/java
BASEDIR=`dirname $0`/..
pushd $BASEDIR > /dev/null

CP=classes/
for FILENAME in `ls -1 lib`;
do
		if [ -f "lib/${FILENAME}" ]
  		then
    		CP="${CP}:lib/${FILENAME}"
  		else
    		echo "Ignoriere Datei ${FILENAME}."
		fi
done

echo ${JAVA_COMMAND} -classpath "${CP}" ${JAVA_OPTS} de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher $*
${JAVA_COMMAND} -classpath "${CP}" ${JAVA_OPTS} de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher $*
rueckgabe=$?

popd > /dev/null
exit ${rueckgabe}
