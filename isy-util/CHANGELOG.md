# 1.13.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.12.0
- enthält nur Versionsanhebung auf IFS 1.11.0

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.10.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.9.2
- `IFS-568`: Entfernt redundante Methode `getMessage(String schluessel)` aus dem MessageSourceHolder
- `RF-1040`: Scope für Spotbugs-Annotation Abhängigkeit auf provided gesetzt

# 1.9.0
- `IFS-528`: Explizite Versionen für isy-exception-core und isy-logging entfernt und stattdessen isyfact-standards-bom importiert
- `IFS-468`: DozerMapper im Zuge des Updates auf 6.5.0 hinzugefügt
    * Konkret wurde eine eigene Implementierung der `DozerBeanMapperFactoryBean` hinzugefügt um die Abh. auf GitHub-Version abzulösen
    * license/DEPENDENCIES überarbeitet

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.6.0
- `IFS-111`: Abhängigkeit von isy-util auf isy-serviceapi-sst aufgelöst. StelltLoggingKontextBereitInterceptor nach isy-serviceapi-core verschoben.
**Achtung**: Dieses Ticket nimmt die Änderungen von `IFS-9` (s. v1.5.1) zurück. Der `checkAndUpdate()`-Task der Konfiguration kann in Zukunft direkt über die Bibliothek `isy-task` eingebunden und konfiguriert werden.

# 1.5.4
- `IFS-120`: Der StelltLoggingKontextBereitInterceptor erzeugt keine Warn-Logausgabe mehr, wenn eine StelltLoggingKontextBereit-Annotation mit dem Parameter nutzeAufrufKontext = false definiert ist.

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-9`: StelltLoggingKontextBereit-Annotation auch ohne AufrufkontextTo nutzbar
