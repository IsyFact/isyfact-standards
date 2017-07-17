# v1.4.4
- isy-sicherheit: Sonderlogik, die Underscores aus dem Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes entfernt, ausgebaut.

## Hinweise zum Upgrade
- Für Anwendungen, in die neue Version eingebunden werden soll, muss vorher geprüft, ob Underscores im Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes verarbeitet werden können.

# v1.4.2
- isy-exception-core: `IFS-44`: PlisException, PlisBusinessException und PlisTechnicalException nicht mehr Deprecated.

# v1.4.1
- isy-exception-core: `IFS-14`: PlisException, PlisBusinessException und PlisTechnicalException als Deprecated markiert.
- isy-logging: `IFS-31`: Konfiguration der maximalen Größe von Parametern implementiert
- isy-logging: LogSchluessel als Deprecated markiert

# v1.3.7
- isy-konfiguration: `RF-107`: Beim reload von properties wurden gelöschte properties nicht erfolgreich entfernt.
