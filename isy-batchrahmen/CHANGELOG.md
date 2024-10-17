# 4.0.0
- `IFS-2248`: Durchführen einer erneuten Authentifizierung bei Token-Ablauf in BatchrahmenImpl
- `IFS-4248`: Erstellung eines `ClaimsOnlyOAuth2AuthenticationToken` für den Fall, dass keine Authentifizierung durchgeführt wird oder diese fehlschlägt

# 3.0.0
- `IFS-1148`: Fehler wegen zu langem Klassenpfad unter Windows behoben
- `IFS-1091`: Fehlerhafte ExcludeFromBatchContext-Annotation behoben
- `IFS-1355`: WebApplication.None für ConfigurableApplicationContext im BatchLauncher hinzugefügt
- `IFS-1170`: JpaTransactionManager aus BatchrahmenImpl ausgebaut; PlatformTransactionManager wird anstelle des JpaTransactionManagers verwendet
- `IFS-1050`: Stelle Beispiel-SQL-Skripte wieder her, sodass diese mit der Oracle-Datenbank kompatibel sind
- `IFS-1169`: Änderung von Tabellenname und Spaltennamen der BatchStatus-Tabelle zu `CamelCaseToUnderscoresNamingStrategy`
    * Tabelle `BATCH_STATUS` mit `CamelCaseToUnderscoresNamingStrategy` ersetzt Tabelle `BATCHSTATUS` mit `PhysicalNamingStrategyStandardImpl`
    * explizites ORM für Abwärtskompatibilität bereitgestellt (siehe Dokumentation: Die Konfiguration der Spring-Kontexte)
- `IFS-1853`: Umstellung von `isy-sicherheit` und `isy-aufrufkontext` auf neuen Baustein `isy-security`
  #### _Breaking Change:_
  * Die Konfiguration von `benutzer`, `passwort` und `bhknz` erfolgt über isy-security ClientRegistrations. Batches müssen nur noch eine `oauth2ClientRegistrationId` zur Authentifizierung bereitstellen. Die Methode `getAuthenticationCredentials` der `BatchAusfuehrungsBean` wurde entfernt.

# 2.4.3
- `IFS-1356`: Manuelles Herunterfahren in isy-batchrahmen ausgebaut, stattdessen hochpropagieren der BatchAusfuehrungsException, wenn ClassNotFoundException abgefangen wird.

# 2.4.0
- `IFS-801`: SpotBugs Fehler behoben

# 2.2.0
- `IFS-693`: Initialisierung des LoggerKontexts für Anwendungskontext
- `IFS-542`: Fügt Korrelations-Id bei jeder Satzverarbeitung in Aufruf-Kontext hinzu

# 2.0.0
- `IFS-228`: Umstellung auf Java Config
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert
- `IFS-251`: Abhängigkeiten zu log4j entfernt

# 1.10.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-255`: Schließen des Anwendungskontexts nach Ausführung eines Batches.
- `IFS-270`: UUID als Korrelations-ID für Batches statt Batch-ID

# 1.9.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-227`: Encoding der Ergebnisdateien korrigiert

# 1.8.0
- `IFS-47`: Korrelations-ID für jede Satzverarbeitung

# 1.7.0
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-66`: Im Status "Abbruch" kann ein neuer Lauf nur mit "Restart" erfolgen, auch wenn im abgebrochenen Lauf zuvor kein Satz verarbeitet wurde.

# 1.6.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID

# 1.5.3
- `IFS-5`: Der Isy-Batchrahmen verweist auf "Register".
