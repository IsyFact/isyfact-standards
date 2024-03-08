# 1.13.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.12.0
- `IFS-802`: SpotBugs Fehler behoben
- `IFS-736`: Aktive Log-Level im LogHelper prüfen

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.10.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.9.1
- `IFS-466`: Fix für fehlerhafte Mdc Formatierung

# 1.9.0
- `IFS-528`: Explizite Version für isy-exception-core entfernt und stattdessen isyfact-standards-bom importiert
- `IFS-539`: Exclusion von logback-classic in logback-json-classic für Dependency Convergence
- license/DEPENDENCIES aktualisiert

# 1.8.0
- `IFS-362`: LogApplicationListener loggt nur noch für den eigenen ApplicationContext (nicht für Kind-Kontexte)
- `IFS-225`: Prüfung der Logeinträge auf maximale Länge und ggfs. Kürzung dieser
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-177`: Anwendungen und Batches loggen identisch. Der Batch-Appender `appender-batch.xml` ist deprecated und wird im Release v2.0.0 entfernt.

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-42`: Erweiterung um Performance-Logging
- `IFS-68`: Erweiterte Typisierung von Logeinträgen
- `IFS-70`: Position vom Zeitstempel verschoben

# 1.4.1
- `IFS-31`: Konfiguration der maximalen Größe von Parametern implementiert
- LogSchluessel als Deprecated markiert
