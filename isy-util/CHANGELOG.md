# 3.2.0
- `IFS-2395`: `MessageSourceHolder` sowie `MessageSourceFehlertextProvider` als deprecated markiert

# 2.4.0
- `RF-1040`: Scope für Spotbugs-Annotations Abhängigkeit auf provided gesetzt

# 2.2.0
- `IFS-568`: Entfernt redundante Methode `getMessage(String schluessel)` aus dem MessageSourceHolder

# 2.0.0
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

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
