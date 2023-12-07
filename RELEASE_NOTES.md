# Isyfact-Standards

## 2.5.0
### Hinweise & bekannte Probleme
- keine

### Umgesetzte Tickets
#### Features
- `ISY-218`: Einführung der DIN Norm 91379 (erweitert DIN SPEC 91379)
  * Package _dinnorm91379_ und die dazu benötigten Transformatoren wurden angelegt
  * Die neu hinzugekommenen Zeichen sind im Anhang des Nutzungskonzeptes Sonderzeichen dokumentiert
  * Die alten Packages _stringlatin1_1_ und _dinspec91379_ werden durch _dinnorm91379_ ersetzt und in einer zukünftigen Version entfernt

#### Bug Fixes
- keine

#### Interne Anpassungen
- `ISY-151`, `ISY-342`: Umstellung auf Github-Workflow
  * Isyfact-Standards wird zukünftig auf Github gehostet
- Dependabot-Patches: Ab dieser Version werden die Versionen von 3rd-Party-Produkten automatisiert mit Hilfe des Dependabots gepatcht.
- `IFS-2044`: OpenCSV von 5.5.2 auf 5.7.1 angehoben
- `ISY-381`: Release Isyfact-Standards 2.5.0
  * Konfiguration des Plugins 'cyclonedx' hinzugefügt, damit beim Aufruf von 'mvn package' eine SBOM generiert wird
  * AsciidoctorJ-Extensions von 2.3.0 auf 2.6.0 angehoben

### Durchzuführende Aktionen vor dem ersten Einsatz
- keine