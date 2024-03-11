# 1.14.0

# 1.13.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.12.0
- `IFS-803`: SpotBugs Fehler behoben

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.10.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.9.0
- `IFS-468`: license/DEPENDENCIES überarbeitet und Dozer (nach Update auf 6.5.0) entfernt
- `IFS-496`: Update commons-codec auf 1.13
- `IFS-528`: Explizite Versionen für isy-aufrufkontext, isy-exception-core, isy-konfiguration und isy-logging entfernt und stattdessen isyfact-standards-bom importiert

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken

# 1.4.6
- Sonderlogik, die Underscores aus dem Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes entfernt, wieder eingebaut, da Ausbau erst für später relevant.

# 1.4.4
- Sonderlogik, die Underscores aus dem Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes entfernt, ausgebaut.

## Hinweise zum Upgrade
- Für Anwendungen, in die die neue Version eingebunden werden soll, muss vorher geprüft, ob Underscores im Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes verarbeitet werden können.
