# 2.1.0
- `IFS-378`: Reihenfolge der Schnittstellenannotationen angepasst
- `IFS-488`: Setze Advisor Order auf 50 für stelltAufrufKontextBereitAdvisor

# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

# 1.9.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-266`: Anpassung Log-Level bei Erzeugung neuer Korrelations-ID aufgrund fehlender Korrelations-ID im AufrufKontext auf debug.
- `IFS-347`: Abhängigkeiten zu commons-lang3 aufgelöst.

# 1.8.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-137`: Funktionalität von LoggingKontextAspect in StelltLoggingKontextBereitInterceptor übernommen und LoggingKontextAspect als @Deprecated markiert. Neue Testfälle angelegt. 

# 1.7.0
- `IFS-111`: StelltLoggingKontextBereit von isy-util übernommen. 

# 1.6.0
- `IFS-9`: StelltLoggingKontextBereit-Annotation nach isy-util verschoben, da auch ohne AufrufkontextTo nutzbar
- `IFS-48`: Bei ausgehenden Systemaufrufen ist nur noch das Flag loggeDauer im LogHelper standardmäßig aktiviert. Per Spring können jedoch auch alle weiteren Flags gesetzt werden. 
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken

## Hinweise zum Upgrade
- StelltLoggingKontextBereit-Annotation liegt nun in einem anderem package. Neben der Anpassung im Java-Code der Anwendung muss auch die Spring-Konfiguration des Pointcuts (expression) aktualisiert werden.

# 1.5.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID
