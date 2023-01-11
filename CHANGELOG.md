# 1.12.0

# 1.11.0
- `IFS-1073`: [isy-serviceapi-core] Logausgabe zur Korrektur der Korrelations-Id entfernt
- `IFS-668`: [isy-serviceapi-core] Zurücksetzung der KorrelationsId des AnrufKontextTo bei einem Invoke
- `IFS-661`: [isy-task] CompletionWatchdog loggt Stacktrace bei Exception
- `IFS-875`: [isy-aufrufkontext], [isy-serviceapi-core] Umsetzung des Transports von OAuth 2 Bearer-Token zwischen Schnittstellentechnologien
- `IFS-1066`: [isy-persistence] Protokollierung-Datenstrukturen aus den Template-DB-Skripten entfernt
- `IFS-1093`:
    * [isyfact-products-bom] Anhebung von Oracle-JDBC und UCP auf 19.11
    * [isy-persistence] Oracle-JDBC hinzugefügt
- `IFS-1176`: [isy-sonderzeichen] Fehler gefixt, bei dem der LegacyTransformator Texte ausgibt, die nicht konform zu String.Latin 1.1 sind
- `IFS-801`: [isy-batchrahmen] SpotBugs Fehler behoben
- `IFS-802`: [isy-logging] SpotBugs Fehler behoben
- `IFS-803`: [isy-sicherheit] SpotBugs Fehler behoben
- `IFS-736`: [isy-logging] Aktive Log-Level im LogHelper prüfen
- `IFS-686`: Property-Dateien auf Unicode Escapes umgestellt
- `IFS-1175`: [isy-task] Eigenen FehlertextProvider fpr isy-task erstellt
- `IFS-702`: [isy-batchrahmen] Fügt die KorrelationsId passend zum MDC dem AufrufKontext hinzu
- `IFS-1196`: [isyfact-products-bom] Spring Framework BOM Version von 4.3.22.RELEASE auf 4.3.23.RELEASE angehoben

# 1.10.0
- `IFS-1003`: [isy-sonderzeichen]
  + Refactoring des DIN-SPEC-Packages von isy-sonderzeichen
  + Entfernen des `core`-Unterpakets des DIN-SPEC-Packages von isy-sonderzeichen
- `IFS-1109`: [isy-sonderzeichen] DIN SPEC 91379:
  * Erweiterung Transformator-Interface um Methoden, die Metadaten der Transformation tracken
  * neuer LegacyTransformator, der Texte nach DIN-SPEC-91379 in String.Latin-1.1-konforme Texte transformiert
- `IFS-1035`: [isy-sonderzeichen] Funktion hinzugefügt, welche die Zugehörigkeit eines Strings zu einem DIN-SPEC-Datentyp prüft.

# 1.9.0
- `IFS-849`: [isy-sonderzeichen] DIN SPEC: isy-sonderzeichen Transformation
    + Alte Implementierung in `de.bund.bva.pliscommon.plissonderzeichen.stringlatin1_1` verschoben
    + Neue Implementierung in `de.bund.bva.pliscommon.plissonderzeichen.dinspec91379` erstellt
    + Neue Transformation und Kategorie Tabelle hinzugefügt
- `IFS-928`: [isy-sonderzeichen] neue Resource und Regel für Transkription hinzugefügt
- `IFS-560`: [isy-persistence] Entferne das Anlegen von DB-User in Update-Skripten

# 1.8.3
- `IFS-568`: [isy-util] Entfernt redundante Methode `getMessage(String schluessel)` aus dem MessageSourceHolder
- `IFS-597`: [isy-sst-bridge], [isy-serviceapi-core] Verwendung der Klasse `IsyHttpInvokerServiceExporter` in Tests anstatt `HttpInvokerServiceExporter`
- `IFS-656`: [isy-ueberwachung] ServiceStatstik berechnet Durchschnitt nun threadsafe
- `IFS-692`: [isyfact-products-bom] Google Guava von 19.0 auf 29.0 angehoben
- `IFS-746`: [isy-task] Behebung von NullPointerException in TaskSchedulerImpl (wenn HostHandler false zurückgibt)
- `IFS-785`: [isyfact-products-bom] Versionsupdate von XStream von 1.4.11 auf 1.4.14 wegen CVE
- `IFS-783`: Konfiguration von Quality Gate 1; Umstellung auf GitLabCI Templates
- `RF-1040`: [isy-util], [isy-sonderzeichen] Scope für Spotbugs-Annotation Abhängigkeit auf provided gesetzt

# 1.8.1
- `IFS-453`: [isy-ueberwachung] Loglevel für isAlive-Datei-Ereignisse erhöht.
- `IFS-466`: [isy-logging] Fix für fehlerhafte Mdc Formatierung
- `IFS-510`: [isy-serviceapi-core] LoggingKontextAspect räumt KorrelationIds nicht richtig auf
- `IFS-564`: [isy-serviceapi-core] Deaktivieren von Proxy-Objekten in HttpInvoker Schnittstellen
- `IFS-578`: Versionsanhebung Apache Commons-Validator auf 1.6

# 1.8.0
- `IFS-223`: Logging Bridges in Products-POM ergänzt: jcl-over-slf4j, slf4j-jcl
- `IFS-334`: Hinzufügen eines Profils für Jacoco und Anpassung der Surefire Config
- `IFS-410`: Checkstyle Plugin auf Version 3.1.0 angehoben
- `IFS-454`: Maven Enforcer Rule: dependency-convergence ergänzt.
- `IFS-458`: Tidy-Plugin eingebunden, sodass immer ein Check ausgeführt wird. Alle pom.xml Dateien mit tidy:pom formatiert und getestet.
- `IFS-468`: Update Dozer auf 6.5.0
    *  Neue Option `wildcard-case-insensitive` löst Case-Sensitive-Problem
    *  GroupIDs in Submodulen wurde auf `com.github.dozermapper` umgestellt
- `IFS-463`: Abwärtskompatibilität IsyFact 2 zu IsyFact 1 - Erstellung der Bridge
