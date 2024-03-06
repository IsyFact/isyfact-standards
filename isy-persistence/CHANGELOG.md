# 1.15.0

# 1.14.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.13.0
- `IFS-1066`: Protokollierung-Datenstrukturen aus den Template-DB-Skripten entfernt
- `IFS-1093`: Oracle-JDBC hinzugefügt

# 1.12.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.11.0
- `IFS-560`: Entferne das Anlegen von DB-User in Update-Skripten

# 1.10.0
- `IFS-461`: Anpassungen für Hibernate 5.4.8 
    * Hibernate-Entitymanager entfernt
    * Methodensignaturen angepasst  
    * AttributeConverter angepasst 
    * Alte Tests als Deprecated markiert
- `IFS-468`: license/DEPENDENCIES überarbeitet und Dozer (nach Update auf 6.5.0) entfernt
    * Dozer aus logback.xml entfernt, da es nicht mehr verwendet wird

# 1.9.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-347`: Abhängigkeiten zu `commons-lang3` aufgelöst.

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
