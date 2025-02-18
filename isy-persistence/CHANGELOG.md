# 4.0.0
- `IFS-3740`: IsyFact-Standards IF4: Analyse: Stabilität der Tests wiederherstellen

# 3.0.0
- `IFS-1354`: Autokonfiguration mit initialisierbarer Datenbank
    * Für die Konfiguration der DataSource wurde zu den DataSourceProperties von Spring Boot gewechselt. Hierdurch haben sich die Namen der Konfigurationsschlüssel geändert und müssen angepasst werden.
- `IFS-1820`: Oracle-Spezifika aus isy-persistence entfernt
  - **BREAKING CHANGES**:
    - IsyPersistenceOracleAutoConfiguration zu IsyPersistenceAutoConfiguration umbenannt
    - oracleDataSourceHealthIndicator Bean entfernt
    - SQL*Plus DB-Schema Vorlage-Skripte entfernt
- `IFS-2243`: DAO-Eigenimplementierung entfernen
  - **BREAKING CHANGES**:
    - AbstractDao und Dao entfernt
# 2.4.0
- `IFS-1066`: Protokollierung-Datenstrukturen aus den Template-DB-Skripten entfernt
- `IFS-1093`: Anhebung von UCP auf 19.11

# 2.2.0
- `IFE-202`: Ergänzung von Equals und HashCode Methoden und Serializable Implementierung in @Embeddable-Typen
- `IFS-741`: Entferne das Anlegen von DB-User in Update-Skripten

# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

# 1.9.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.8.0
- `IFS-115`: Persistenz-Code von `isy-datetime` nach `isy-persistence` verschoben
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.6.0
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-63`: Vollständiges Beispiel für DB-Install/Update-Skripte eingefügt. Beispiel für schemaübergreifende Operationen eingefügt - sowohl DB-Install-Skripte, als auch DB-Update-Skripte. 

# 1.5.1
- `IFS-43`: ojdbc7 Dependency für Tests gegen h2 getauscht

# 1.5.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID

# 1.4.5
- DB-Update-Skript prüft, ob vorliegendes Schema 'gueltig' ist.

# 1.4.4
- `RF-146`: Datasource kann als non-critical markiert werden, um das Hochfahren einer Anwendung trotz fehlender Verbindung zu ermöglichen.

# 1.2.5
- `RF-146`: Datasource kann als non-critical markiert werden, um das Hochfahren einer Anwendung trotz fehlender Verbindung zu ermöglichen (letzte Version vor Hibernate 5).
