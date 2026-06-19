# 4.3.0
### FEATURES
- `IFS-5491`: Maven wird jetzt in mindestens Version 3.9.0 vorausgesetzt (enforced via maven-enforcer-plugin)
  * Maven < 3.9.0 hat den Status End of Life erreicht

### DEPENDENCIES
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

### BUG FIXES
- `IFS-5438`: [isy-security] Maven-Dependency ehcache verwendet classifier `jakarta`

### BREAKING CHANGES

### DOCUMENTATION
- `IFS-5419`: [isyfact-standards-doc] Schlüssel von Korrelations-ID zu korrelationsId geändert