#! /bin/bash
###
# See the NOTICE file distributed with this work for additional
# information regarding copyright ownership.
# The Federal Office of Administration (Bundesverwaltungsamt, BVA)
# licenses this file to you under the Apache License, Version 2.0 (the
# License). You may not use this file except in compliance with the
# License. You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied. See the License for the specific language governing
# permissions and limitations under the License.
###

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

JAVA_COMMAND=$JAVA_HOME/bin/java
JAVA_OPTS="${JAVA_OPTS} -Duser.language=de -Duser.region=DE -Dfile.encoding=UTF-8" 

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

echo ${JAVA_COMMAND} -classpath "${CP}" ${JAVA_OPTS} de.bund.bva.pliscommon.batchrahmen.core.launcher.BatchLauncher $*
${JAVA_COMMAND} -classpath "${CP}" ${JAVA_OPTS} de.bund.bva.pliscommon.batchrahmen.core.launcher.BatchLauncher $*
rueckgabe=$?

popd > /dev/null
exit ${rueckgabe}
