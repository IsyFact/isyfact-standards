#!/bin/bash
#===========================================================
# Beispiel Shellbatch
#
# Vorbedingungen
# - Leeres Datenbank Schema angelegt
#
# Parameter
# - Optional: Verzeichnis, in dem die Log-Dateien abgelegt werden
#
# Rückgabewerte:
# -  0 = Erfolg
# - 10 = Keine OracleConnection angegeben (jpa.properties existiert nicht oder enthält den Schlüssel database.connection nicht)
# - 20 = Fehler beim Ausführen von Schritt 1 (01_tabelle-erstellen.sql)
# - 30 = Fehler beim Ausführen von Schritt 2 (02_tabelle-loeschen.sql)
#
# Version: $Id: shellbatch.sh <revision> <datum> <user> $
#
#===========================================================

#Zeit zu Beginn ermitteln
start=`date +%s`

# Homeverzeichnis ermitteln
HomeVerzeichnis=$(cd -P -- "$(dirname -- "$0")" && pwd -P)

# Parameter prüfen
if [ -z "$1" ] ; then
  LogVerzeichnis=${HomeVerzeichnis}/log/
else
  LogVerzeichnis=$1
fi

# Auslesen der Oracle-Datenbankverbindung aus einer Konfigurationsdatei (jpa.properties).
PROPERTY_FILE="${HomeVerzeichnis}/etc/jpa.properties"
PROP_KEY="database.connection"

# Alles nach dem ersten "=" wird gematched (inklusive Leerzeichen)
OracleConnection=`cat $PROPERTY_FILE | grep "$PROP_KEY" | sed 's/[^=]*=\(.*\)/\1/'`
if [ -z "$OracleConnection" ] ; then
  echo "+++ OracleConnection konnte nicht ermittelt werden. Existiert die jpa.properties Datei und ist der richtige Schlüssel enthalten? Abbruch!"
  exit 10
fi


# Startmeldung
echo "# "$(basename $0)" um "$(date +%Y-%m-%d-%T)" gestartet"
RC=0

# Batchschritte
# 1. Ausführen eines SQL-Skripts mit SQL-Plus
echo "#  Fuehre 01_tabelle-erstellen.sql aus ..."
LogDatei=${LogVerzeichnis}/shellbatch_$(date +%Y-%m-%d_%H-%M-%S).log
cd ${HomeVerzeichnis}/sql
sqlplus "${OracleConnection}" @01_tabelle-erstellen.sql | tee ${LogDatei}
res=$?
AnzahlFehler=$(egrep -c "SP2\-[0-9]{4}\:|ORA\-[0-9]{5}\:" ${LogDatei})
if [ ${AnzahlFehler} -ne 0 ]; then
  echo "+++ "${AnzahlFehler}" Fehler in Schritt 01, RC="${res}"!"
  RC=20
else
	# 2. Ausführen weiteren SQL-Skripts mit SQL-Plus
	echo "#  Fuehre 02_tabelle-loeschen.sql aus ..."
	LogDatei=${LogVerzeichnis}/shellbatch_$(date +%Y-%m-%d_%H-%M-%S).log
	cd ${HomeVerzeichnis}/sql
	sqlplus "${OracleConnection}" @02_tabelle-loeschen.sql | tee ${LogDatei}
	res=$?
	AnzahlFehler=$(egrep -c "SP2\-[0-9]{4}\:|ORA\-[0-9]{5}\:" ${LogDatei})
	if [ ${AnzahlFehler} -ne 0 ]; then
		echo "+++ "${AnzahlFehler}" Fehler in Schritt 02, RC="${res}"!"
	RC=30
	fi
fi

end=`date +%s`
# Ende-Meldung
echo "Shell-Batch wurde erfolgreich beendet. - Laufzeit" $(($end - $start)) "Sekunden"
echo "# "$(basename $0)" um "$(date +%Y-%m-%d-%T)" mit RC="${RC}" beendet"
exit ${RC}
