# 2.5.0
- Hinzufügen eines neuen Packages mit Transformatoren für die DIN Norm 91379

# 2.4.4
- `IFS-1912`: Tabelle angepasst, sodass Glyphen, die von dem Buchstaben e abstammen, im Legacy Mapping auf sich selbst gemappt werden

# 2.4.0
- `IFS-1176`: Fehler gefixt, bei dem der LegacyTransformator Texte ausgibt, die nicht konform zu String.Latin 1.1 sind
- `RF-1040`: Scope für Spotbugs-Annotations Abhängigkeit auf provided gesetzt

# 2.3.0
- `IFS-1003`:
  + Refactoring des DIN-SPEC-Packages von isy-sonderzeichen
  + Entfernen des `core`-Unterpakets des DIN-SPEC-Packages von isy-sonderzeichen
- `IFS-1109`: DIN SPEC 91379:
  * Erweiterung Transformator-Interface um Methoden, die Metadaten der Transformation tracken
  * neuer LegacyTransformator, der Texte nach DIN-SPEC-91379 in String.Latin-1.1-konforme Texte transformiert
- `IFS-1035`: Funktion hinzugefügt, welche die Zugehörigkeit eines Strings zu einem DIN-SPEC-Datentyp prüft.

# 2.2.0
- `IFS-929`: 
    + Alte Implementierung in `de.bund.bva.isyfact.sonderzeichen.stringlatin1_1` verschoben
    + Neue Implementierung in `de.bund.bva.isyfact.sonderzeichen.dinspec91379` erstellt
    + Neue Transformation und Kategorie Tabelle hinzugefügt
    + Neue Resource und Regel für Transkription hinzugefügt

# 2.0.0
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

