# 4.1.0
## FEATURES
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
- `IFS-4584`: [isyfact-standards-doc] Entfernung von `isy-asciidoctorj-extensions`
- `IFS-4591`: [isy-security] Hinzufügen von Authentifizierungsmethoden zur Authentifizierung von Clients und Systemen ohne Issuer-URI.
- `IFS-4676`: [isy-task], [isy-logging] Scope der Abhängigkeit zu AspectJ-Weaver auf Runtime gesetzt.
  * Definitionen der Aspekte zum kompilieren werden von org.aspectj:aspectjrt bereitgestellt.
- `IFS-4731`: Korrektes Auflösen der URL in SBOMs
- `IFS-4655`: Update von Maven Checkstyle Plugin auf Version 3.6.0
- `IFS-4531`: Update von Flatten Maven Plugin auf Version 1.7.1
  * Update von Maven Version auf 3.6.3
- `IFS-4771`: [isyfact-standards-doc] Ergänzung der Dokumentation durch Punkt: Migration auf Spring Boot 3.5
- `IFS-4810`: [isy-security] Ausbau der Validierung des "aud"-Claims erstellter Tokens

## BUG FIXES
- `IFS-4526`: [isy-task] Logeintrag IsyTaskAspect korrigiert
- `IFS-4495`: [isy-task] Verwendung der Defaults, falls keine Task-Config definiert ist

## BREAKING CHANGES
- `IFS-4812`: [isy-security] Verwendung sicherer Hashfunktion mit SHA-512 für Caching
  * Rückgabe eines Byte-Arrays statt eines Integers in der Methode `generateCacheKey` der Klasse `AbstractClientRegistrationAuthenticationToken`
  * Konfigurierbare Properties für Hashfunktion und Bytegröße des Salts
