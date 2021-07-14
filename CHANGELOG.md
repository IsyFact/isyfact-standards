# 2.3.0
- `IFS-601`: [isy-products-bom] Einbindung folgender Produkte über die Spring Boot Dependencies: JPA, JTA, Spring, Hibernate, Jackson, QueryDSL, SLF4J, Logback, JUnit, Mockito, AssertJ, H2.
- `IFS-874`: [isy-aufrufkontext], [isy-serviceapi-core] Umsetzung des Transports von OAuth 2 Bearer-Token zwischen Schnittstellentechnologien.
- `IFS-1066`: [isy-persistence] Protokollierung-Datenstrukturen aus den Template-DB-Skripten entfernt
- `IFS-1004`: [isy-sonderzeichen]
    + Refactoring des DIN-SPEC-Packages von isy-sonderzeichen
    + Entfernen des `core`-Unterpakets des DIN-SPEC-Packages von isy-sonderzeichen
- `IFS-978`: [isy-sst-bridge] Bridge-Erweiterung:
    * ServiceApiMapper unterstützt Mapping von AufrufKontextTo auch in Gegenrichtung (IF1 zu IF2)
    * AufrufKontextToResolver hinzugefügt, der sowohl mit IF1, als auch IF2-AufrufKontextTo umgehen kann
- `IFS-987`: [isyfact-products-bom] Entferne log4j
- `IFS-970`: [isy-products-bom] Spring-Boot Versionsanhebung auf 2.5.2
- `IFS-984`: [isyfact-products-bom] Eigene Version von Ehcache durch Spring Boot Managed Dependencies ersetzt
- `IFS-1093`: [isy-persistence], [isyfact-products-bom] Anhebung von Oracle-JDBC und UCP auf 19.11

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
- `IFS-977`: [isy-serviceapi-core] Statische Auflösung des AufrufKontextTo durch Bean-Lösung abgelöst
    * AufrufKontextToHelper ist deprecated -> stattdessen AufrufKontextToResolver verwenden
    * DefaultAufrufKontextToResolver wird auch in der Autoconfig automatisch erstellt und steht für Autowiring zur Verfügung
    * folgende Klassen benötigen jetzt den Resolver als Pflichtparameter:
        * `StelltAufrufKontextBereitInterceptor`
        * `StelltAllgemeinenAufrufKontextBereitInterceptor`
        * `StelltLoggingKontextBereitInterceptor`
        * `IsyHttpInvokerClientInterceptor`
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
