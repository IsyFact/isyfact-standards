#!/bin/bash
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

########################################################################
# Richtet die Datenbank für das System ein.
#######################################################################

LogFile=logs/ausgabe.log

sqlplus -S /nolog @00_install-main.sql 01_environment.sql ${LogFile}
echo ''

AnzahlFehler=$(egrep -c "SP2\-[0-9]{4}\:|ORA\-[0-9]{5}\:" ${LogFile})
echo "${AnzahlFehler}"
if [ ${AnzahlFehler} -ne 0 ] ; then
    echo "+++ ${AnzahlFehler} Fehler beim Ausführen des Skripts."
    egrep "SP2\-[0-9]{4}\:|ORA\-[0-9]{5}\:" ${LogFile}
    echo "+++ Bitte die Log-Datei ${LogFile} überprüfen."
    exit 1
fi

exit