# 3.0.0
- `IFS-1504`: Transformator für die normative Abbildung lateinischer Buchstaben auf Grundbuchstaben (Suchform) hinzugefügt
  * Integration von zugeliefertem Code aus `IFS-1270`
- `IFS-1171`: Fehlende Zeichen 0110 und 0111 zum Mapping hinzugefügt
    #### _Breaking Change:_
  * Der Zeichensatz für String Latin 1.1 wurde korrigiert. Dies kann zu Kompatibilitätsproblemen in der Kommunikation mit Anwendungen führen, die den unkorrigierten Zeichensatz verwenden. (IsyFact-Versionen kleiner als IF 3)
- `IFS-1912`: Tabelle angepasst, sodass Glyphen, die von dem Buchstaben e abstammen, im Legacy Mapping auf sich selbst gemappt werden
- `IFS-2382`: Entfernt Mutable-Array Spotbugs Fehler
    #### _Breaking Change:_
    - Die Klassen `stringlatin1_1/core/transformation/ZeichenKategorie.java` und `dinspec91379/transformation/ZeichenKategorie.java` bieten für Abruf aller Möglichkeiten einen `getter` anstelle einer `public static Variable` an
- `IFS-3443`: Hinzufügen eines neuen Packages mit Transformatoren für die DIN Norm 91379

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

