# 1.14.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.13.0
- enthält nur Versionsanhebung auf IFS 1.11.0

# 1.12.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.10.2
- `IFS-656`: ServiceStatstik berechnet Durchschnitt nun threadsafe

# 1.10.1
- `IFS-453`: Loglevel für isAlive-Datei-Ereignisse erhöht: INFO für Standardablageort wird genutzt; ERROR für Datei existiert bei Abfrage nicht.

# 1.10.0
- `IFS-325`: Implementierung vom `AdministrationWatchdogTask` und dazugehoerigen Tests.
- `IFS-468`: license/DEPENDENCIES überarbeitet und Dozer (nach Update auf 6.5.0) entfernt

# 1.9.0
- `IFS-248`: Log-Level vom Start der Watchdog Prüfung auf debug gesetzt.
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-347`: Abhängigkeiten zu `commons-lang3` aufgelöst.

# 1.8.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.6.0
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken

# 1.5.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID
