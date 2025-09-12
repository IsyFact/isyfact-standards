# 4.1.0
## FEATURES
- `IFS-4676`: [isy-task], [isy-logging] Scope der Abhängigkeit zu AspectJ-Weaver auf Runtime gesetzt.
  * Definitionen der Aspekte zum Kompilieren werden von org.aspectj:aspectjrt bereitgestellt.
- `IFS-4731`: Korrektes Auflösen der URL in SBOMs
- `IFS-4655`: Update von Maven Checkstyle Plugin auf Version 3.6.0
- `IFS-4531`: Update von Flatten Maven Plugin auf Version 1.7.1
  * Update von Maven Version auf 3.6.3
- `IFS-4591`: [isy-security] Hinzufügen von Authentifizierungsmethoden zur Authentifizierung von Clients und Systemen ohne Issuer-URI.
- `IFS-4754`: [isy-security] Einführung von Caching im Authentifizierungsprozess
- `IFS-4785`: [isy-security] Hinzufügen einer Property für die Restlebensdauer gecachter OAuth2-Token
- `IFS-4752`: [isy-security] Wiederherstellen der initialen Authentication nach Authentifizierung mit @Authenticate-Annotation
- `IFS-4810`: [isy-security] Ausbau der Validierung des "aud"-Claims erstellter Tokens
- `IFS-4818`: [isy-ueberwachung] Load Balancer Autokonfiguration in eigene Klasse ausgelagert
- `IFS-4729`: [isyfact-products-bom] Anhebung der Version von SpringBoot auf 3.5.5
- `IFS-4804`: [isyfact-products-bom] Anhebung der Third Party Versionen:
    * Update von com.github.spotbugs:spotbugs-annotations von 4.2.3 auf 4.9.3
    * Update von org.apache.maven.plugins:maven-surefire-plugin von 3.2.5 auf 3.5.3
    * Update von org.sonatype.central:central-publishing-maven-plugin von 0.7.0 auf 0.8.0
    * Update von org.apache.maven.plugins:maven-javadoc-plugin von 3.3.2 auf 3.11.2
    * Update von org.mapstruct:mapstruct von 1.5.5.Final auf 1.6.3
    * Update von commons-io:commons-io von 2.18.0 auf 2.20.0
    * Update von org.apache.maven.plugins:maven-project-info-reports-plugin von 3.5.0 auf 3.9.0
    * Update von org.apache.maven.plugins:maven-site-plugin von 3.10.0 auf 3.21.0
    * Update von org.wiremock:wiremock-standalone von 3.9.2 auf 3.13.1
    * Update von com.oracle.database.jdbc:ucp von 19.27.0.0 auf 19.28.0.0
    * Update von commons-validator:commons-validator von 1.7 auf 1.10.0
    * Update von org.codehaus.mojo:versions-maven-plugin von 2.17.1 auf 2.18.0
    * Update von com.github.spotbugs:spotbugs-maven-plugin von 4.8.6.6 auf 4.9.3.2
    * Update von commons-beanutils:commons-beanutils von 1.9.4 auf 1.11.0
    * Update von org.springframework.boot:spring-boot-dependencies von 3.5.3 auf 3.5.4

## BUG FIXES
- `IFS-4526`: [isy-task] Logeintrag IsyTaskAspect korrigiert
- `IFS-4495`: [isy-task] Verwendung der Defaults, falls keine Task-Config definiert ist
- `IFS-4753`: [isy-batchrahmen] Änderung der Konfigurationsreihenfolge.
  * BatchSecurityConfiguration wird nach Anwendung und BatchRahmen Konfiguration geladen.
  * Beans mit der `@ConditionalOnMissingBean(...)` Annotation können wie erwartet überschrieben werden.
- `IFS-4817`: [isy-ueberwachung] Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.

## BREAKING CHANGES
- `IFS-4812`: [isy-security] Verwendung sicherer Hashfunktion mit SHA-512 für Caching
  * Rückgabe eines Byte-Arrays statt eines Integers in der Methode `generateCacheKey` der Klasse `AbstractClientRegistrationAuthenticationToken`
  * Konfigurierbare Properties für Hashfunktion und Bytegröße des Salts

## Documentation
- `IFS-1800`: [isyfact-standards-doc] Formulierung von Empfehlungen für den Einsatz von Bulk Queries in der Persistenz von Backends
- `IFS-1798`: [isyfact-standards-doc] Formulierung von Empfehlungen für den Einsatz von Second-Level-Caches in der Persistenz von Backends
- `IFS-4743`: [isyfact-standards-doc] Auszeichnung der Bausteine in Bezug auf IsyFact-Standards und IsyFact-Erweiterungen
- `IFS-4554`: [isyfact-standards-doc] Nutzung des Begriffs "Webservice" im Sinne des W3C vereinheitlicht
- `IFS-3028`: [isyfact-standards-doc] Vorgaben für Spring & Spring Boot aktualisiert
- `IFS-4208`: [isyfact-standards-doc] Verwendungen der Begriffe "Nachbarsystem" und "Externes System" korrigiert
- `IFS-1825`: [isyfact-standards-doc] Versionierung mit Liquibase erweitert
- `IFS-4546`: [isyfact-standards-doc] Verweis auf JAXP entfernt, Negativliste bereinigt und links entfernt
- `IFS-4543`: [isyfact-standards-doc] Begriff Nutzungsschicht entfernt
- `IFS-4570`: [isyfact-standards-doc] Alte Einträge aus Negativliste entfernt
- `IFS-4549`: [isyfact-standards-doc] Verwaltung von Versionen zentralisiert
- `IFS-4771`: [isyfact-standards-doc] Ergänzung der Dokumentation durch Punkt: Migration auf Spring Boot 3.5
