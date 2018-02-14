# v1.6.0
- isy-util: `IFS-111`: Abhängigkeit von isy-util auf isy-serviceapi-sst aufgelöst. StelltLoggingKontextBereitInterceptor nach isy-serviceapi-core verschoben.


# v1.5.4
## Bugfix
- isy-util: `IFS-120`: Der StelltLoggingKontextBereitInterceptor erzeugt keine Warn-Logausgabe mehr, wenn eine StelltLoggingKontextBereit-Annotation mit dem Parameter nutzeAufrufKontext = false definiert ist.

# v1.5.2
## Bugfix
- isy-konfiguration: `IFS-98`: Sortierung von Dateien darf nur bei Konfiguration als Ordnerstruktur verwendet werden

# v1.5.1
- alle: `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- isy-konfiguration: `IFS-26`: Konfigurationsaktualisierung via Timertask nun mit Korrelations-ID
- isy-konfiguration: `IFS-59`: Konfiguration kann beliebige Konfigurationsdateien lesen
- isy-util: `IFS-9`: StelltLoggingKontextBereit-Annotation auch ohne AufrufkontextTo nutzbar
- isy-logging: `IFS-42`: Erweiterung um Performance-Logging
- isy-logging: `IFS-68`: Erweiterte Typisierung von Logeinträgen
- isy-logging: `IFS-70`: Position vom Zeitstempel verschoben

# v1.4.6
- isy-sicherheit: Sonderlogik, die Underscores aus dem Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes entfernt, wieder eingebaut, da Ausbau erst für v1.5.x relevant.

# v1.4.4
- isy-sicherheit: Sonderlogik, die Underscores aus dem Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes entfernt, ausgebaut.

## Hinweise zum Upgrade
- Für Anwendungen, in die die neue Version eingebunden werden soll, muss vorher geprüft, ob Underscores im Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes verarbeitet werden können.

# v1.4.2
- isy-exception-core: `IFS-44`: PlisException, PlisBusinessException und PlisTechnicalException nicht mehr Deprecated.

# v1.4.1
- isy-exception-core: `IFS-14`: PlisException, PlisBusinessException und PlisTechnicalException als Deprecated markiert.
- isy-logging: `IFS-31`: Konfiguration der maximalen Größe von Parametern implementiert
- isy-logging: LogSchluessel als Deprecated markiert

# v1.3.7
- isy-konfiguration: `RF-107`: Beim reload von properties wurden gelöschte properties nicht erfolgreich entfernt.
