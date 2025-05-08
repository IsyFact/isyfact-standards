# 3.3.0
### Features
- `IFS-4464`: AutoConfiguration der Klasse MdcFilterAutoConfiguration nach isy-logging verschoben

# 3.1.0
- `IFS-3612`: Bibliothek als `@Deprecated` markiert
  - Nach Umstellung auf `isy-security` und REST Schnittstellen wird die Bibliothek nicht länger benötigt.

# 3.0.0
- `ISY-650`: `HttpHeaderNestedDiagnosticContextFilter` nach `isy-logging` verschoben

# 2.4.3
- `IFS-1161`: `HttpHeaderNestedDiagnosticContextFilter` Priorität erhöht
    - `MdcFilterConfiguration` heißt jetzt `MdcFilterAutoConfiguration`

# 2.4.0
- `IFS-874`: Umsetzung des Transports von OAuth 2 Bearer Tokens zwischen Schnittstellentechnologien
    - `AufrufKontextVerwalter` wurde um ein Feld für das OAuth2 Bearer Token erweitert.

# 2.2.0
- `IFS-563`: Java-Konfiguration "HttpHeaderNestedDiagnosticContextFilter"

# 2.0.0
- `IFS-228`: Umstellung auf Java Config
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.6.0
- `IFS-139`: Default-Name des HTTP-Headers in HttpHeaderNestedDiagnosticContextFilter geändert 
- `IFS-136`: HttpHeaderNestedDiagnosticContextFilter entfernt alle Korrelations-Ids

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
