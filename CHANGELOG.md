# 3.0.2
- `IFS-4383`: Spring Security Versionsanhebung auf 5.7.13

# 3.0.1
- `ISY-701`: Google Guava Versionsanhebung auf 33.1.0
- `ISY-704`: Open CSV Versionsanhebung auf 5.9
- `ISY-987`: Spring Framework Versionsanhebung auf 5.3.33
- `ISY-1073`: Spring Security Versionsanhebung auf 5.7.12
- `ISY-929`: Nimbus JOSE+JWT Versionsanhebung auf 9.37.2
- `ISY-1036`: Apache Commons Collections Versionsanhebung auf 3.2.2
- `ISY-1045`: Apache Commons FileUpload Versionsanhebung auf 1.5
- `IFS-3665`: [isyfact-standards-doc] "Leitfaden Dokumentation" nach `isy-documentation` verschoben

# 3.0.0
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
- `IFS-2287`: [isy-sonderzeichen]  Deprecation für String-Latin-1.1-Transformatoren setzen
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
- [isy-sonderzeichen] Hinzufügen eines neuen Packages mit Transformatoren für die DIN Norm 91379
- `IFS-1853`: [isy-batchrahmen] Umstellung von `isy-sicherheit` und `isy-aufrufkontext` auf neuen Baustein `isy-security`
- `IFS-2416`: [isyfact-standards-bom] zentrale Versionsverwaltung von `isy-security`
- `IFS-2416`: [isy-task] Umstellung von isy-sicherheit auf isy-security
- `ISY-372`: [isy-styleguide] JavaScript-Referenzen entfernt
- `ISY-416`: [isyfact-products-bom] Versionsanhebung von Metro Webservices auf 2.4.9
- `ISY-658`: [isy-serviceapi-core], [isyfact-standards-doc] Setzen generierter Korrelations-IDs in das AufrufKontextTo im StelltLoggingKontextBereitInterceptor
  * [isyfact-standards-doc] Abschnitt "Anmerkung zum Parallelbetrieb von AufrufKontextVerwalter und MdcHelper" hinzugefügt
- `ISY-651`: [isyfact-standards-doc] Abschnitt zum Setzen von X-Correlation-Id in den HTTP-Header bei REST-Anfragen
- `ISY-650`: [isy-aufrufkontext, isy-logging] Bean zum Setzen der Korrelations-ID angepasst:
  * `HttpHeaderNestedDiagnosticContextFilter` aus `isy-aufrufkontext` nach `isy-logging` verschoben 
  * `FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter>` überschreibbar
- `ISY-655`: [isy-serviceapi-core] Beheben einer ClassNotFoundException beim Starten von Anwendungen mit IsyServiceApiCoreAutoConfiguration

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
- `IFS-656`: [isy-ueberwachung] ServiceStatstik: Durchschnitt threadsafe berechnet
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
- `IFS-323`: [isy-taks] Hostnamen werden jetzt mit Regex angegeben
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
