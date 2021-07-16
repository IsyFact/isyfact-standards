# 2.3.0
- `IFS-1003`:
  + Refactoring des DIN-SPEC-Packages von isy-sonderzeichen
  + Entfernen des `core`-Unterpakets des DIN-SPEC-Packages von isy-sonderzeichen
  
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

