# 1.12.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.11.0

# 1.10.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.9.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.8.0
- `IFS-303`: Surefire-Plugin bezieht Version aus Standards und argLine Anpassung JaCoCo
- `IFS-468`: license/DEPENDENCIES überarbeitet und Dozer (nach Update auf 6.5.0) entfernt
    * (test-scope) Dozer aus logback.xml entfernt, da es nicht mehr verwendet wird
    
# 1.7.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-312`: Nicht benötigte SQL-Skripte entfernt

# 1.6.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.4.0
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken

# 1.3.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID

# 1.2.2
- `RF-85`: Umstellung der IsyFact-Standards auf neue Projektstruktur (isyfact-base)
