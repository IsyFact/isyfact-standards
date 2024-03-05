# 1.13.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.12.0
- `IFS-875`: Umsetzung des Transports von OAuth 2 Bearer-Token zwischen Schnittstellentechnologien
    - `AufrufKontextVerwalter` wurde um ein Feld für das OAuth 2 Bearer-Token erweitert.

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.10.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.9.0
- `IFS-468`: license/DEPENDENCIES überarbeitet und Dozer (nach Update auf 6.5.0) entfernt

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.6.0
- `IFS-139`: Default-Name des HTTP-Headers in HttpHeaderNestedDiagnosticContextFilter geändert 
- `IFS-136`: HttpHeaderNestedDiagnosticContextFilter entfernt alle Korrelations-Ids

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
