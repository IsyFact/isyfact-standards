# 2.1.0
- `IFS-156`: Sonderlogik, die Underscores aus dem Attribut durchfuehrenderSachbearbeiterName des Aufrufkontextes entfernt, erneut ausgebaut.
- `ISY-546`: Abräumen des SecurityContexts im NutzerAuthentifizierungInterceptor

# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert
- `IFS-318`: Cache-Implementierung über JCache-API abstrahiert

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
