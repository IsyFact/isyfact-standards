# 4.3.0

### Hinweise & bekannte Probleme

- `IFS-5252`: [isy-logging] Anwendungen, die die `IsyRestLogger`-Funktionalität für das Logging von ausgehenden REST-Calls via `WebClient` nutzen möchten, müssen `spring-webflux` explizit als Dependency hinzufügen.

### Umgesetzte Tickets 

#### FEATURES
- `IFS-5491`: Maven wird jetzt in mindestens Version 3.9.0 vorausgesetzt (enforced via maven-enforcer-plugin)
  * Maven < 3.9.0 hat den Status End of Life erreicht
- `IFS-5512`: [isy-batchrahmen] Umstellung zur Nutzung innerhalb einer Executable Jar
- `IFS-5259`: [isy-batchrahmen] Maximale Anzahl automatischer Neustarts für fehlerhafte Batches konfigurierbar.
  * Über den Konfigurationsparameter `Batchrahmen.MaxWiederholungen` kann eine Obergrenze für automatische Neustarts festgelegt werden.
  * Bei Überschreitung wird eine `BatchrahmenMaxWiederholungenException` geworfen, die nur auf Info-Niveau geloggt wird.
  * Ist der Parameter nicht oder auf eine negative Nummer gesetzt, gibt es keine Begrenzung der Neustarts.

#### DEPENDENCIES
- `IFS-5562`: [isyfact-products-bom] Aktualisierung der 3rd-Party-Dependencies auf aktuelle Versionen (via Dependabot PRs):
  - Spring Boot auf 3.5.15
  - Metro WebServices auf 4.0.7
  - stax2-api auf 4.3.0
  - woodstox-core auf 7.2.1
  - Oracle JDBC (ojdbc8) auf 19.30.0.0
  - Oracle UCP auf 19.30.0.0
  - Guava auf 33.6.0-jre
  - Commons NET auf 3.13.0
  - jsoup auf 1.22.2
  - Apache Tika auf 3.3.1
  - Commons IO auf 2.22.0
  - Resilience4j BOM auf 2.4.0
  - ANTLR4 Runtime auf 4.13.2

#### BUG FIXES
- `IFS-5438`: [isy-security] Maven-Dependency ehcache verwendet classifier `jakarta`

#### DOCUMENTATION
- `IFS-5419`: [isyfact-standards-doc] Schlüssel von Korrelations-ID zu korrelationsId geändert