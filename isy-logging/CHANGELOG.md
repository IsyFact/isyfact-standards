# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-262`: Prüfung der Logeinträge auf maximale Länge und ggfs. Kürzung dieser

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
