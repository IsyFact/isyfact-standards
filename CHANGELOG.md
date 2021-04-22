# 1.9.0
- `IFS-849`: [isy-sonderzeichen] DIN SPEC: isy-sonderzeichen Transformation
    + Alte Implementierung in `de.bund.bva.pliscommon.plissonderzeichen.stringlatin1_1` verschoben
    + Neue Implementierung in `de.bund.bva.pliscommon.plissonderzeichen.dinspec91379` erstellt
    + Neue Transformation und Kategorie Tabelle hinzugefügt
- `IFS-928`: [isy-sonderzeichen] neue Resource und Regel für Transkription hinzugefügt

# 1.8.3
- `IFS-568`: [isy-util] Entfernt redundante Methode `getMessage(String schluessel)` aus dem MessageSourceHolder
- `IFS-597`: [isy-sst-bridge], [isy-serviceapi-core] Verwendung der Klasse `IsyHttpInvokerServiceExporter` in Tests anstatt `HttpInvokerServiceExporter`
- `IFS-656`: [isy-ueberwachung] ServiceStatstik berechnet Durchschnitt nun threadsafe
- `IFS-692`: [isyfact-products-bom] Google Guava von 19.0 auf 29.0 angehoben
- `IFS-746`: [isy-task] Behebung von NullPointerException in TaskSchedulerImpl (wenn HostHandler false zurückgibt)
- `IFS-785`: [isyfact-products-bom] Versionsupdate von XStream von 1.4.11 auf 1.4.14 wegen CVE
- `IFS-783`: Konfiguration von Quality Gate 1; Umstellung auf GitLabCI Templates

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

