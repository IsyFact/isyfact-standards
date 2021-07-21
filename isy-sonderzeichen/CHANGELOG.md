# 1.11.0:
- `IFS-1003`:
  + Refactoring des DIN-SPEC-Packages von isy-sonderzeichen
  + Entfernen des `core`-Unterpakets des DIN-SPEC-Packages von isy-sonderzeichen
  
# 1.10.0:
- `IFS-849`: 
    + Alte Implementierung in `de.bund.bva.pliscommon.plissonderzeichen.stringlatin1_1` verschoben
    + Neue Implementierung in `de.bund.bva.pliscommon.plissonderzeichen.dinspec91379` erstellt
    + Neue Transformation und Kategorie Tabelle hinzugefügt
- `IFS-928`: neue Resource und Regel für Transkription hinzugefügt

# 1.9.0
- `IFS-468`: license/DEPENDENCIES überarbeitet und Dozer (nach Update auf 6.5.0) entfernt
- `IFS-528`: Explizite Version für isy-logging entfernt und stattdessen isyfact-standards-bom importiert

# 1.8.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

