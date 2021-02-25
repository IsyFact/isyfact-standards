# 1.4.1
- `IFS-746`: Behebung von NullPointerException in TaskSchedulerImpl (wenn HostHandler false zurückgibt)

# 1.4.0
- `IFS-325`: Ungenutze Abhaengigkeit auf `isy-ueberwachung` entfernt
- `IFS-468`: Update auf dozer 6.5.0
    * license/DEPENDENCIES hinzugefügt
    * (test-scope) Dozer aus logback.xml entfernt, da es nicht mehr verwendet wird
    
# 1.3.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.2.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.1.0
- `IFS-111`: Neuer Task für Updates der Konfiguration mit isy-konfiguration