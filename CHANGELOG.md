# 5.0.0
### FEATURES
- `IFS-1800`: [isyfact-standards-doc] Formulierung von Empfehlungen für den Einsatz von Bulk Queries in der Persistenz von Backends
- `IFS-1798`: [isyfact-standards-doc] Formulierung von Empfehlungen für den Einsatz von Second-Level-Caches in der Persistenz von Backends
- `IFS-4743`: [isyfact-standards-doc] Auszeichnung der Bausteine in Bezug auf IsyFact-Standards und IsyFact-Erweiterungen
- `IFS-4736`: [isyfact-standards-doc] Umzug der Dokumentation des Bausteins JPA/Hibernate in die Referenzarchitektur
- `IFS-4713`: [isyfact-standards-doc] Entkopplung der Bausteine (s. `IFS-4582`) in Dokumentation nachvollzogen
- `IFS-4554`: [isyfact-standards-doc] Nutzung des Begriffs "Webservice" im Sinne des W3C vereinheitlicht
- `IFS-3028`: [isyfact-standards-doc] Vorgaben für Spring & Spring Boot aktualisiert
- `IFS-4546`: [isyfact-standards-doc] Verweis auf JAXP entfernt, Negativliste bereinigt und links entfernt
- `IFS-4208`: [isyfact-standards-doc] Verwendungen der Begriffe "Nachbarsystem" und "Externes System" korrigiert
- `IFS-4731`: Korrektes Auflösen der URL in SBOMs

### BUG FIXES
- `IFS-4753`: [isy-batchrahmen] Änderung der Konfigurationsreihenfolge.
  * BatchSecurityConfiguration wird nach Anwendung und BatchRahmen Konfiguration geladen.
  * Beans mit der `@ConditionalOnMissingBean(...)` Annotation können wie erwartet überschrieben werden.
- `IFS-4817`: [isy-ueberwachung] Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.

### BREAKING CHANGES
- `IFS-4736`: [isy-persistence] Entfernung der Bibliothek aus den IsyFact-Standards
- `IFS-4582`: [isy-persistence], [isy-polling], [isy-security], [isy-security-test], [isy-task], [isy-util] Entfernen der entkoppelten Bausteine aus dem Standards-Repository

### DEPENDENCY UPGRADES
- `IFS-4530`: Update von UCP auf Version 19.27.0.0
- `IFS-4655`: Update von Maven Checkstyle Plugin auf Version 3.6.0
- `IFS-4531`: Update von Flatten Maven Plugin auf Version 1.7.1
    * Update von Maven Version auf 3.6.3
