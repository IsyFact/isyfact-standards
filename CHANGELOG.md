# 5.0.0
## FEATURES
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
- `IFS-4530`: Update von UCP auf Version 19.27.0.0
- `IFS-4655`: Update von Maven Checkstyle Plugin auf Version 3.6.0
- `IFS-4531`: Update von Flatten Maven Plugin auf Version 1.7.1
  * Update von Maven Version auf 3.6.3

## BREAKING CHANGES
- `IFS-4736`: [isy-persistence] Entfernung der Bibliothek aus den IsyFact-Standards
- `IFS-4582`: [isy-persistence], [isy-polling], [isy-security], [isy-security-test], [isy-task], [isy-util] Entfernen der entkoppelten Bausteine aus dem Standards-Repository

# 4.1.0
## FEATURES
- `IFS-4570`: [isyfact-standards-doc] Alte Einträge aus Negativliste entfernt
- `IFS-4543`: [isyfact-standards-doc] Begriff Nutzungsschicht entfernt
- `IFS-4549`: [isyfact-standards-doc] Verwaltung von Versionen zentralisiert
- `IFS-1825`: [isyfact-standards-doc] Versionierung mit Liquibase erweitert
- `IFS-4584`: [isyfact-standards-doc] Entfernung von `isy-asciidoctorj-extensions`

# 4.0.0
## FEATURES
- `IFS-1802`: [isyfact-standards-doc] Mapping Vorgaben zu bidirektionalen Assoziationen aktualisiert
- `IFS-1797`: [isyfact-standards-doc] Vorgaben zu Hibernate Annotationen LazyToOne und LazyCollections entfernt
- `IFS-4508`: [isyfact-standards-doc] Verweis auf Angular-Demo (Referenzimplementierung) prominent platziert
- `IFS-4358`: [isyfact-standards-doc] Verwendungen des Begriffs IT-System in den Bausteinen (exklusive Sicherheit) korrigiert
- `IFS-4249`: [isyfact-standards-doc] Setzen der Antora-Version für die Online-Dokumentation durch Git
- `IFS-4194`: [isyfact-standards-doc] Referenzarchitektur Batch aufgrund von IFS-3736 neu verfasst
- `IFS-4209`: [isyfact-standards-doc] Anpassung einiger Glossar-Referenzen aufgrund der Entfernung von Glossareinträgen in [`isy-documentation`](https://github.com/IsyFact/isy-documentation)
- `IFS-4193`: [isyfact-standards-doc] Referenzarchitektur der Persistenzschicht aufgrund von IFS-3736 neu verfasst
- `IFS-4192`: [isyfact-standards-doc] Referenzarchitektur der Serviceschicht aufgrund von IFS-3736 neu verfasst:
  - Inhalte zu Kommunikation zwischen IT-Systemen mittels Services und Versionierung von Services auf neue Seite "Services" in der software-technischen Referenzarchitektur umgezogen,
  - Inhalte zu Verfügbarkeit auf neue Seite "Verfügbarkeit" in der technisch-infrastrukturellen Referenzarchitektur umgezogen.
- `IFS-4225`: [isyfact-standards-doc] Abschnitt Nutzungsarten einer Anwendung in der software-technischen Referenzarchitektur gelöscht
- `IFS-3029`: [isyfact-standards-doc] Möglichkeiten der SpringBoot Konfiguration um `SpringBootApplication` und `SpringConfiguration` erweitert
- `IFS-4190`: [isyfact-standards-doc] Referenzarchitektur des Anwendungskerns aufgrund von IFS-3736 neu verfasst
- `IFS-4084`: [isyfact-standards-doc] Verwendungen des Begriffs IT-System im Einstieg korrigiert
- `IFS-4064`: [isyfact-standards-doc] Referenzarchitektur für Backends aufgrund von IFS-3736 neu verfasst
- `IFS-3992`: [isyfact-standards-doc] Entfernung der Vorlage "Tailoring" Dokument
- `ISY-1025`: Versionsanhebung `maven-surefire-plugin` auf 3.2.5
    * Automatische Auswahl der benötigten JUnit Engines
- `ISY-876`: [isy-bedienkonzept] Ergänzung der Anzeigeposition von Toasts unten rechts
- `ISY-948`: Spring Cache Abstraction als verpflichtende Bibliothek für anwendungsseitige Caches eingeführt
- `ISY-748`: [isyfact-standards-doc] Versionsanhebung `isy-checkstyle-config` auf 0.2.0
    * Gruppierung der `jakarta.*` imports
- `ISY-1040`: [isyfact-standards-doc] Anpassung des logback.xml-Konfigurationspfads im IF2-Migrationsleitfaden
- `IFS-2786`: [isyfact-standards-doc] Migrationsleitfaden IsyFact 2 entfernt
- `IFS-2784`: [isyfact-standards-doc] Externe Verweise direkt verlinkt statt über `literaturextern` und URLs korrigiert
- `IFS-2816`: [isyfact-standards-doc] Begriff "Blaupause" durch "Referenzarchitektur" ersetzt
- `IFS-3665`: [isyfact-standards-doc] "Leitfaden Dokumentation" nach `isy-documentation` verschoben
- `IFS-3692`: [isyfact-standards-doc] Seite "Referenzarchitektur" nach Architektursichten aufgeteilt
- `IFS-3821`: [isyfact-standards-doc] Thematik zur internen und externen URL bei der Authentifizierung beschrieben (Multiple Issuer-URIs)
- `IFS-3834`: [isyfact-standards-doc] Kapitel Authentifizierung & Autorisierung um Multi-Tenancy erweitert.
- `IFS-3928`: [isy-bedienkonzept] Migration der neusten Bedienkonzeptversion ins Repository isy-bedienkonzept
- `IFS-4236`: [isy-datetime], [isy-sonderzeichen] Beide Submodule wurden aus `isyfact-standards` entfernt und in eigenständige Repositories verschoben
    * Sie werden ab sofort entkoppelt von der Isyfact versioniert und weiterentwickelt
    * Sie werden aber weiterhin im Dependency-Management der `isyfact-standards-bom` angeboten.
- `IFS-1796`: [isyfact-standards-doc] Detailkonzept Datenzugriff: Vorgaben zur Kommunikation von Datenzugriff und Anwendungskern klarer fassen
- `IFS-1797`: [isyfact-standards-doc] Nutzung von JPA: Vorgaben zu @LazyCollections / @LazyToOne prüfen
- `IFS-1799`: [isyfact-standards-doc] Nutzung von JPA: Vorgaben zur Verwendung von Hibernate-Filtern vervollständigen
- `IFS-2771`: [isyfact-standards-doc] Korrekte Fehlermeldungen in Dokumentation
- `IFS-2788`: [isyfact-standards-doc] Redundante Anleitung für den Enterprise Architect löschen
- `IFS-2848`: Konfiguration von Dependabot
- `IFS-2861`: Einbinden von Snyk in das Repository isy-sonderzeichen
- `IFS-2911`: [isyfact-standards-doc] Antora-Modul für die Referenzarchitektur anlegen
- `IFS-3823`: [isyfact-standards-doc] Glossar um Struktur, Begriffsänderungen erweitern und Seitentemplate anwenden
- `IFS-3852`: [isyfact-standards-doc] Bausteindokumentation aus isyfact-standards migrieren
- `IFS-4007`: [isyfact-standards-doc] Anpassung Konzept REST-Baustein
- `IFS-4272`: [isyfact-standards-doc] IFS Produktkatalog: Hinweise zur Nutzung von Lombok in der IsyFact
- `IFS-4367`: Aktualisierung der Lizenz-Files der IsyFact
- `IFS-3096`: [isyfact-standards-doc] Überarbeitung Blaupause Service - DK Service
- `IFS-3360`: [isyfact-standards-doc] Baustein Konfiguration nicht korrekt deprecated
- `IFS-3363`: [isyfact-standards-doc] Entfernen von PLIS Erwähnung in Abbildung und Löschung abgelöster Abbildung
- `IFS-4388`: Einheitlichen Formatter bereitstellen

## BUG FIXES
- `IFS-4366`: [isyfact-standards-doc] Korrektur Build Fehler der Dokumentation.
- `IFS-4194`: [isyfact-standards-doc] Umstellung aller Verweise in der Referenzarchitektur aufs Glossar auf das aktuelle Format (`xref:glossary::terms-definitions.adoc`)
- `IFS-4203`: [isyfact-standards-doc] Korrektur fehlerhafter Links in der Dokumentation
- `IFS-4212`: [isyfact-standards-doc] Dokumentation von `@OnceTask` korrigiert (`@ManualTask` existiert nicht)
- `IFS-3165`: [isyfact-standards-doc] Vorlage Systementwurf - Fehlender Hinweistext "Platzbedarf für Dateien"
## BREAKING CHANGES
- `ISY-1122`: Bausteine `isy-sicherheit`, `isy-aufrufkontext`, `isy-serviceapi-core` und `isy-konfiguration` entfernt
- `ISY-889`: Spring Boot Versionsanhebung auf 3.2.x
    * inkludiert ausserdem folgende Versionsanhebungen:
        * Spring Framework 5.x -> 6.x
        * Spring Security 5.x -> 6.x
        * Hibernate 5.x -> 6.x
- `ISY-779`: `isyfact-bom` entfernt
    * [isyfact-standards-bom] `isyfact-standards-bom` importiert dependencyManagement aus `isyfact-products-bom`
    * [isyfact-standards-doc] Bezug und Nutzung der IsyFact nach Entfernung der IsyFact-BOM beschrieben
- `ISY-737`: [isyfact-standards] Java Compile Target 17
- `IFS-1259`: [isyfact-standards-doc] Basisdaten (d.h. Möglichkeit der gemeinsamen Nutzung von Geschäftsobjekten durch mehrere Fachkomponenten) entfernt
- `IFS-3676`: [isyfact-standards-doc] IT-Systeme dürfen keine UI-Komponenten mehr enthalten.
- `IFS-3699`: [isyfact-standards-doc] Baustein Sicherheit entfernt.
- `IFS-3700`: [isyfact-standards-doc] Baustein Http Invoker entfernt.
- `IFS-3701`: [isyfact-standards-doc] Baustein JSF entfernt.
- `IFS-3736`: [isyfact-standards-doc] Abbildung von Anwendungen in IT-Systeme ("technischer Schnitt") beschrieben.
- `IFS-3763`: [isyfact-standards-doc] Dokumentation der Änderungen zu IFS-2248 (Tokenerneuerung isy-batchrahmen)
- `IFS-3750`: [isyfact-standards-doc] Baustein Konfiguration entfernt.
- `IFS-4529`: Spring Boot Versionsanhebung auf 3.4.x:
    * [isy-ueberwachung]: Signaturänderung von de.bund.bva.isyfact.ueberwachung.config.NachbarsystemRestTemplateConfigurer.CustomErrorHandler.handleError
    * [isy-ueberwachung]: Folgende Property-Keys ändern sich:
      - management.endpoints.enabled-by-default -> management.endpoints.access.default
      - management.endpoint.<id>.enabled -> management.endpoint.<id>.access

# 3.0.1
- `ISY-701`: Google Guava Versionsanhebung auf 33.1.0
- `ISY-704`: Open CSV Versionsanhebung auf 5.9
- `ISY-987`: Spring Framework Versionsanhebung auf 5.3.33
- `ISY-1073`: Spring Security Versionsanhebung auf 5.7.12
- `ISY-929`: Nimbus JOSE+JWT Versionsanhebung auf 9.37.2
- `ISY-1036`: Apache Commons Collections Versionsanhebung auf 3.2.2
- `ISY-1045`: Apache Commons FileUpload Versionsanhebung auf 1.5

# 3.0.0
- `IFS-2383`: Grafiken der TI-Architektur aller Umgebungen neu erstellt und an aktuelle Gegebenheiten angepasst.
- `ISY-653` : Erweitern des `TimeoutWiederholungHttpInvokerRequestExecutor` um die Token-Beschaffung aus dem Security Context bei Nichtverfügbarkeit des `AufrufKontextVerwalter`.
- `ISY-183`: [isyfact-standards-doc] Migrationsleitfaden zur Entfernung der Bridge-Module
- `ISY-54`: [isyfact-standards-doc] Migrationsleitfaden zur Umstellung auf isy-security
- `ISY-219`: [isyfact-standards-doc] Abschnitt zur Migration der Annotation @NutzerAuthentifizierung
- `IFS-2561`: [isy-security] Die Konfiguration via `rollenrechte.xml` erfolgt optional.
  * Initialisierung mit Standardwerten
- `IFS-1887`: [isyfact-standards-doc] Ergänzung der Nutzungsvorgaben für neuen Sicherheitsbaustein `isy-security`
- `IFS-2422`: [isyfact-serviceapi-core] Deprecation des isy-serviceapi-core Bausteins
- `IFS-2273`: [isyfact-standards-doc] Einheitliche Deprecation-Warnung für die Dokumentation
- `IFS-2488`: [isy-serviceapi-core] AufrufKontextTo wird ad hoc als HttpInvoker-Transportobjekt erzeugt, wenn `isy-security` im Classpath liegt und anstelle des Objekts `null` übergeben wird
- `IFS-2449`: [isyfact-standards-doc] Aufnahme von Testcontainers in den Produktkatalog
- `IFS-1745`: [isyfact-standards-doc] Summary Architektur- und Sicherheitsregeln
  * mit Antora Extensions generiert
- `IFS-1702`: [isy-ueberwachung] Refaktorierung ServiceStatistik
  * Entkoppelt von Micrometer API
  * Aufgeteilt in Interface und Implementierung
- `IFS-1467`: Maven Plugins für statische Informationen:
  * git-commit-id-plugin (Version 4.9.10)
  * spring-boot-maven-plugin (Version von spring-boot verwaltet)
- `IFS-1628`: [isy-ueberwachung] Standardmetriken bei Anbinden von ServiceStatistik vorhanden
- `IFS-1947`: Zeichenkodierung für Filtering von properties Dateien im Maven Resource Plugin auf ISO-8859-1 gesetzt.
- `IFS-771`: [isy-ueberwachung] Anleitung für den HealthCheck von HTTPInvoker-Endpoints hinzugefügt
- `IFS-1465`: [isyfact-products-bom] Spring Boot Versionsanhebung auf 2.7.1
- `IFS-2021`: [isyfact-products-bom] Spring Boot Versionsanhebung auf 2.7.9
  * spring-boot-maven-plugin Versionsanhebung auf 2.7.9
- `IFS-2537`: [isyfact-products-bom] Spring Boot Versionsanhebung auf 2.7.11
  * spring-boot-maven-plugin Versionsanhebung auf 2.7.11
- `IFS-1073`: [isy-serviceapi-core] Logausgabe zur Korrektur der Korrelations-Id entfernt
- `IFS-1282`: Einführung von Maven CI-friendly Versionen
- `IFS-1157`: [isyfact-standards-bom] Module und Sub-Module entfernt:
  * isy-serviceapi-sst
  * isy-exception-sst
  * isy-sst-bridge
- `IFS-1148`: [isy-batchrahmen] Fehler wegen zu langem Klassenpfad unter Windows behoben
- `IFS-1354`: [isy-persistence] Autokonfiguration mit initialisierbarer Datenbank
  * Für die Konfiguration der DataSource wurde zu den DataSourceProperties von Spring Boot gewechselt. Hierdurch haben sich die Namen der Konfigurationsschlüssel geändert und müssen angepasst werden.
- `IFS-1166`: [isy-serviceapi-core] Autokonfiguration von isy-serviceapi-core nach isy-sicherheit
- `IFS-1091`: [isy-batchrahmen] Fehlerhafte ExcludeFromBatchContext-Annotation behoben
- `IFS-1050`: [isy-batchrahmen] Stelle Beispiel-SQL-Skripte wieder her, sodass diese mit der Oracle-Datenbank kompatibel sind
- `IFS-1548`: [isyfact-standards-doc] Anleitung zum Formatieren und Einrichten des Checkstyle Plugins angepasst
- `IFS-1552`: Einheitliche Verwendung von Maven-Properties für Versionsnummern
- `IFS-1504`: [isy-sonderzeichen] Transformator für die normative Abbildung lateinischer Buchstaben auf Grundbuchstaben (Suchform) hinzugefügt
  * Integration von zugeliefertem Code aus `IFS-1270`
- `IFS-2045`: [isyfact-products-bom] Produkte Apache Tika, commons-beanutils und commons-io hinzugefügt
- `IFS-2287`: [isy-sonderzeichen] Deprecation für String-Latin-1.1-Transformatoren setzen
- `IFS-1886`: [isy-sicherheit] Modul als `@Deprecated` markiert zugunsten von`isy-security`
- `IFS-1940`: [isy-sicherheit] `IsySicherheitUtil` deprecated
- `IFS-2382`: [isy-sonderzeichen] Entfernt Mutable-Array Spotbugs Fehler
- `IFS-1169`: [isy-batchrahmen] Änderung von Tabellenname und Spaltennamen der BatchStatus-Tabelle zu `CamelCaseToUnderscoresNamingStrategy`
  * Tabelle `BATCH_STATUS` mit `CamelCaseToUnderscoresNamingStrategy` ersetzt Tabelle `BATCHSTATUS` mit `PhysicalNamingStrategyStandardImpl`
  * explizites ORM für Abwärtskompatibilität bereitgestellt (siehe Dokumentation: Die Konfiguration der Spring-Kontexte)
- `IFS-1171`: [isy-sonderzeichen] Fehlende Zeichen 0110 und 0111 zum Mapping hinzugefügt
- `IFS-2153`: [isy-task] Umstellung isy-task auf Spring Boot
  * Übersetzungen hinzugefügt
- `ISY-139`: [isyfact-products-bom] Spring Boot Versionsanhebung auf 2.7.15
- `IFS-3443`: [isy-sonderzeichen] Hinzufügen eines neuen Packages mit Transformatoren für die DIN Norm 91379
- `IFS-1853`: [isy-batchrahmen] Umstellung von `isy-sicherheit` und `isy-aufrufkontext` auf neuen Baustein `isy-security`
- `IFS-2416`: [isyfact-standards-bom] zentrale Versionsverwaltung von `isy-security`
- `IFS-2416`: [isy-task] Umstellung von isy-sicherheit auf isy-security
- `ISY-372`: [isy-styleguide] JavaScript-Referenzen entfernt
- `ISY-416`: [isyfact-products-bom] Versionsanhebung von Metro Webservices auf 2.4.9
- `ISY-658`: [isy-serviceapi-core], [isyfact-standards-doc] Setzen generierter Korrelations-IDs in das AufrufKontextTo im StelltLoggingKontextBereitInterceptor
  * [isyfact-standards-doc] Abschnitt "Anmerkung zum Parallelbetrieb von AufrufKontextVerwalter und MdcHelper" hinzugefügt
- `ISY-651`: [isyfact-standards-doc] Abschnitt zum Setzen von X-Correlation-Id in den HTTP-Header bei REST-Anfragen
- `ISY-546`: [isy-sicherheit] Abräumen des SecurityContexts im NutzerAuthentifizierungInterceptor
- `ISY-650`: [isy-aufrufkontext, isy-logging] Bean zum Setzen der Korrelations-ID angepasst:
  * `HttpHeaderNestedDiagnosticContextFilter` aus `isy-aufrufkontext` nach `isy-logging` verschoben 
  * `FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter>` überschreibbar
- `ISY-655`: [isy-serviceapi-core] Beheben einer ClassNotFoundException beim Starten von Anwendungen mit IsyServiceApiCoreAutoConfiguration
- `ISY-727`: [isy-batchrahmen] Behebung von SonarCloud Security Issues
- `ISY-1061`: [isyfact-standards-doc] Ergänzung der Dokumentation zum Zurücksetzen der Korrelations-ID aus dem MdcHelper

## BREAKING CHANGE

### isy-aufrufkontext
- `HttpHeaderNestedDiagnosticContextFilter` wurde aus `isy-aufrufkontext` nach `isy-logging` verschoben

### isy-sonderzeichen
- Die Klassen `stringlatin1_1/core/transformation/ZeichenKategorie.java` und `dinspec91379/transformation/ZeichenKategorie.java` bieten für Abruf aller Möglichkeiten einen `getter` anstelle einer `public static Variable` an
- Der Zeichensatz für String Latin 1.1 wurde korrigiert. Dies kann zu Kompatibilitätsproblemen in der Kommunikation mit Anwendungen führen, die den unkorrigierten Zeichensatz verwenden. (IsyFact-Versionen kleiner als IF 3)

### isy-task 

- Einführung von Spring Boot für Tasks
  * Taskkonfiguration und TaskkonfigurationVerwalter entfernt
- Die Konfiguration von `benutzer`, `passwort` und `bhknz` erfolgt über isy-security ClientRegistrations und für IsyTaskConfigurationProperties muss nur eine `oauth2ClientRegistrationId` zur Authentifizierung konfiguriert werden.

### isy-batchrahmen

- Die Konfiguration von `benutzer`, `passwort` und `bhknz` erfolgt über isy-security ClientRegistrations. Batches müssen nur noch eine `oauth2ClientRegistrationId` zur Authentifizierung bereitstellen. Die Methode `getAuthenticationCredentials` der `BatchAusfuehrungsBean` wurde entfernt.

### isy-persistence
- Der Konfigurationsschlüssel für die DataSource heißt nun `isy.persistence.datasource` statt `isy.persistence.oracle.datasource`

# 2.4.4
- `IFS-1997`: Fix CVE-2022-42889 durch Anhebung von 'commons-text' auf 1.10

# 2.4.3
- `IFS-1467`: Maven Plugins für statische Informationen:
    * git-commit-id-plugin (Version 4.9.10)
    * spring-boot-maven-plugin (Version von spring-boot verwaltet)
- `IFS-1628`: [isy-überwachung] Standardmetriken bei Anbinden von ServiceStatistik vorhanden
    * Hinweis: Der Parameter `MeterRegistry` wurde aus dem Konstruktor der `ServiceStatistik` entfernt.
- `IFS-1161`: [isy-aufrufkontext] `HttpHeaderNestedDiagnosticContextFilter` Priorität erhöht
    * Hinweis: `MdcFilterConfiguration` heißt jetzt `MdcFilterAutoConfiguration`
- `IFS-1356`: [isy-batchrahmen] Manuelles Herunterfahren in isy-batchrahmen ausgebaut, stattdessen hochpropagieren der BatchAusfuehrungsException, wenn ClassNotFoundException abgefangen wird.

# 2.4.2
- `IFS-1529`: Einführung der Flatten-Konfigurationen für Maven CI-friendly Versionen
- `IFS-1466`: [isy-products-bom] Versionsanhebung von Spring Boot auf 2.5.13
- `IFS-1525`: [isy-products-bom] Versionsanhebung von Spring Boot auf 2.5.14
- `IFS-1546`: Hinzufügen des Profils zum automatischen Kopieren von Release-Konfigurationen
- `IFS-1165`: [isy-überwachung] Standardmäßig wird die "/Loadbalancer" Schnittstelle durch die Autokonfiguration
  freigegeben, wenn spring-security aktiviert ist.

# 2.4.1
- `IFS-1397`: [isy-products-bom] Versionsanhebung von Spring Boot auf 2.5.12 (Fix CVE-2022-22965)

# 2.4.0
- `IFS-1162`: Interne Verschlüsselung als SOLL-Anforderung an eine Systemlandschaft beschrieben
- `IFS-1092`: Übertragungswege von Daten in der internen Servicekommunikation beschrieben
- `IFS-590`: [isy-logging] Instanziierung LogApplicationListener Parameter in korrekter Reihenfolge
- `IFS-601`: [isy-products-bom] Einbindung folgender Produkte über die Spring Boot Dependencies: JPA, JTA, Spring, Hibernate, Jackson, QueryDSL, SLF4J, Logback, JUnit, Mockito, AssertJ, H2.
- `IFS-661`: [isy-task] CompletionWatchdog loggt Stacktrace bei Exception
- `IFS-874`: [isy-aufrufkontext], [isy-serviceapi-core] Umsetzung des Transports von OAuth 2 Bearer-Token zwischen Schnittstellentechnologien.
- `IFS-686`: [isy-ueberwachung] Property-Dateien auf Unicode Escapes umgestellt
- `IFS-1066`: [isy-persistence] Protokollierung-Datenstrukturen aus den Template-DB-Skripten entfernt
- `IFS-977`: [isy-serviceapi-core] Statische Auflösung des AufrufKontextTo durch Bean-Lösung abgelöst
    * AufrufKontextToHelper ist deprecated -> stattdessen AufrufKontextToResolver verwenden
    * DefaultAufrufKontextToResolver wird auch in der Autoconfig automatisch erstellt und steht für Autowiring zur Verfügung
    * folgende Klassen benötigen jetzt den Resolver als Pflichtparameter:
        * `StelltAufrufKontextBereitInterceptor`
        * `StelltAllgemeinenAufrufKontextBereitInterceptor`
        * `StelltLoggingKontextBereitInterceptor`
        * `IsyHttpInvokerClientInterceptor`
- `IFS-978`: [isy-sst-bridge] Bridge-Erweiterung:
    * ServiceApiMapper unterstützt Mapping von AufrufKontextTo auch in Gegenrichtung (IF1 zu IF2)
    * AufrufKontextToResolver hinzugefügt, der sowohl mit IF1, als auch IF2-AufrufKontextTo umgehen kann
- `IFS-985`: [isyfact-products-bom] Entferne Spring Security (Verwaltung der Version über Spring Boot)
- `IFS-987`: [isyfact-products-bom] Entferne log4j
- `IFS-970`: [isy-products-bom] Spring-Boot Versionsanhebung auf 2.5.2
- `IFS-984`: [isyfact-products-bom] Eigene Version von Ehcache durch Spring Boot Managed Dependencies ersetzt
- `IFS-1093`: [isy-persistence], [isyfact-products-bom] Anhebung von Oracle-JDBC und UCP auf 19.11
- `IFS-988`: [isy-products-bom] Apache Commons Logging entfernt
- `IFS-688`: [isy-serviceapi-core] Zurücksetzung der KorrelationsId des AnrufKontextTo bei einem Invoke
- `IFS-1176`: [isy-sonderzeichen] Fehler gefixt, bei dem der LegacyTransformator Texte ausgibt, die nicht konform zu String.Latin 1.1 sind.
- `IFS-801`: [isy-batchrahmen] SpotBugs Fehler behoben
- `IFS-802`: [isy-logging] SpotBugs Fehler behoben
- `RF-1040`: [isy-util], [isy-sonderzeichen] Scope für Spotbugs-Annotations Abhängigkeit auf provided gesetzt
- `IFS-1158`: [isy-task] Behebung der "Missing parameter metadata"-Warnung in dem mit dem "-parameters"-Flag compiliert wird
- `IFS-744`: [isy-task] Namensänderung des Beans "taskScheduler" in "isyTaskScheduler"
- `IFS-734`: [isy-logging] Aktive Log-Level im LogHelper prüfen
- `IFE-477`:
    * PluginManagement Versionen aktualisiert
    * Build-Plugin Versionen aktualisiert
- `IFS-1175`: [isy-task] Eigenen FehlertextProvider für isy-task erstellt

# 2.3.0
- `IFS-624`: SAGA-Referenzen durch Architekturrichtlinie der IT des Bundes ersetzt
- `IFS-1004`: [isy-sonderzeichen]
    * Refactoring des DIN-SPEC-Packages von isy-sonderzeichen
    * Entfernen des `core`-Unterpakets des DIN-SPEC-Packages von isy-sonderzeichen
- `IFS-1109`: [isy-sonderzeichen] DIN SPEC 91379:
    * Erweiterung Transformator-Interface um Methoden, die Metadaten der Transformation tracken
    * neuer LegacyTransformator, der Texte nach DIN-SPEC-91379 in String.Latin-1.1-konforme Texte transformiert
- `IFS-1035`: [isy-sonderzeichen] Funktion hinzugefügt, welche die Zugehörigkeit eines Strings zu einem DIN-SPEC-Datentyp prüft.

# 2.2.0
- `IFS-453`: [isy-ueberwachung] Loglevel für isAlive-Datei-Ereignisse erhöht.
- `IFS-465`: Profile zum Bauen mit Java 11 erstellt
- `IFS-489`: [isy-logging] Entferne beim Loggen deklarierte Throwables in Methodensignaturen
- `IFS-491`: Jackson Version auf 2.10.1 angehoben
- `IFS-563`: Java-Konfiguration "HttpHeaderNestedDiagnosticContextFilter"
- `IFS-568`: [isy-util] Entfernt redundante Methode aus dem MessageSourceHolder
- `IFS-600`: [isy-products-bom] Spring-Boot Versionsanhebung auf 2.3.8 und Fasterxml Jackson auf Version 2.11.2
- `IFS-625`: Google.Guava von Version 19.0 auf 29.0 angehoben.
- `IFS-656`: [isy-ueberwachung] ServiceStatistik: Durchschnitt threadsafe berechnet
- `IFS-694`: [isy-products-bom] Spring-Boot Versionsanhebung auf 2.2.9 und Fasterxml Jackson auf Version 2.10.5
- `IFS-746`: [isy-task] Behebung von NullPointerException in TaskSchedulerImpl (wenn HostHandler false zurückgibt)
- `IFS-783`: Konfiguration von Quality Gate 1; Umstellung auf GitLabCI Templates
- `IFS-785`: [isy-products-bom] Versionsupdate von XStream von 1.4.11 auf 1.4.14 wegen CVE
- `IFS-568`: [isy-util] Entfernt redundante Methode aus dem MessageSourceHolder
- `IFS-693`: [isy-batchrahmen] Initialisierung des LoggerKontexts für Anwendungskontext
- `IFS-741`: [isy-persistence] Entferne das Anlegen von DB-User in Update-Skripten
- `IFS-676`: Versionsupdate von OpenCSV 3.8 auf 5.3
    * Hinweise zu Breaking Changes s. Changelog.md der isyfact-products-bom

# 2.1.0
- `IFS-156`: [isy-sicherheit] Sonderlogik, die Underscores aus dem Attribut `durchfuehrenderSachbearbeiterName` des Aufrufkontextes entfernt erneut ausgebaut.
- `IFS-223`: [isy-products-bom] Logging Bridges in products-pom ergänzt: jcl-over-slf4j, slf4j-jcl
- `IFS-303`: [isy-polling] Surefire-Plugin bezieht Version aus standards-pom und argLine Anpassung JaCoCo
- `IFS-323`: [isy-task] Hostnamen werden jetzt mit Regex angegeben
- `IFS-324`: Umzug des Tasks für Konfigurations-Updates von isy-task zu isy-konfiguration
- `IFS-334`: Hinzufügen eines Profils für Jacoco und Anpassung der Surefire Config
- `IFS-346`: [isy-logging, isy-serviceapi-core] Logging in REST-SGWs prinzipiell nur für Testumgebungen aktiv, aber auch im Produktivbetrieb verfügbar und per Konfiguration aktivierbar.
- `IFS-362`: [isy-logging] `LogApplicationListener` loggt nur noch für den eigenen `ApplicationContext` (nicht für Kind-Kontexte)
- `IFS-378`: [isy-serviceapi-core] Reihenfolge der Schnittstellenannotationen angepasst
- `IFS-385`: [isy-products-bom] `jackson-databind` auf 2.9.9.3 angehoben.
- `IFS-410`: Checkstyle Plugin auf Version 3.1.0 angehoben
- `IFS-418`: [isy-products-bom] Spring auf 5.1.9.RELEASE angehoben. Spring Security auf 5.1.6.RELEASE angehoben.
- `IFS-423`: [isy-products-bom] Anhebung der UCP und JDBC Version auf 12.2.0.1
- `IFS-429`: JAR signer in Phase 'deploy' verschoben
- `IFS-437`: [isy-products-bom] `com.thoughtworks.xstream:xstream` auf 1.4.11.1 angehoben
- `IFS-436`: [isy-ueberwachung] Entkopplung Health-Endpoint und hinzufügen Nachbarsystem-Check
- `IFS-444`: [isy-logging] Anpassung der Klasse `BeanToMapConverter` damit die Verwendung von TreeMaps keine Exceptions auslöst
- `IFS-454`: Maven Enforcer Rule: dependency-convergence ergänzt.
- `IFS-458`: Tidy-maven-plugin auf `validate` gesetzt und POMs aufgeräumt. Zulieferung von Github getestet und übernommen. (Versionsanhebungen rausgenommen.)
- `IFS-459`: Die Klasse `AufrufKontextVerwalterImpl` verwendet nun das Interface `AufrufKontext` anstatt der Implementierung `AufrufKontextImpl`
- `IFS-460`: [isyfact-products-bom] org.apache.poi:poi auf 4.1.1 angehoben
- `IFS-463`: [isy-sst-bridge] Abwärtskompatibilität IsyFact 2 zu IsyFact 1 - Erstellung der Bridge
- `IFS-466`: Fix für fehlerhafte Mdc Formatierung
- `IFS-488`: [isy-serviceapi-core] Setze Advisor Order auf 50 für `stelltAufrufKontextBereitAdvisor`
- `IFS-490`: [isyfact-products-bom] `com.fasterxml.jackson.core:jackson-databind` auf 2.9.10.1 angehoben
- `IFS-492`: [isyfact-products-bom] `ch.qos.logback:logback-classic` und `ch.qos.logback:logback-core` auf 1.2.3 angehoben.
- `IFS-687`: [isy-ueberwachung] Umstellung des Nachbarsystem-Checks auf RestTemplate statt WebClient
