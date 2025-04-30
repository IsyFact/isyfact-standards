# 4.1.0
- `IFS-4676`: Scope der Abhängigkeit zu AspectJ-Weaver auf Runtime gesetzt.
    * Definitionen der Aspekte zum kompilieren werden von org.aspectj:aspectjrt bereitgestellt.

# 4.0.0
- `IFS-4452`: Logging Autoconfiguration aus dem isy-aufrufkontext (in 4.0.0 nicht mehr vorhanden) hier eingeführt:
    * `de.bund.bva.isyfact.logging.autoconfigure.MdcFilterAutoConfiguration` in `org.springframework.boot.autoconfigure.AutoConfiguration.imports` eingefügt
    * In Spring Boot 3 wird META-INF/spring.factories nicht mehr verwendet und durch META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports ersetzt
- `IFS-3740`: IsyFact-Standards IF4: Analyse: Stabilität der Tests wiederherstellen

# 3.0.0
- `ISY-650`: Bean zum Setzen der Korrelations-ID angepasst:
    * `HttpHeaderNestedDiagnosticContextFilter` aus `isy-aufrufkontext` eingefügt
    * `FilterRegistrationBean<HttpHeaderNestedDiagnosticContextFilter>` überschreibbar

# 2.4.0
- `IFS-590`: Instanziierung LogApplicationListener Parameter in korrekter Reihenfolge
- `IFS-802`: SpotBugs Fehler behoben
- `IFS-734`: Aktive Log-Level im LogHelper prüfen

# 2.2.0
- `IFS-489`: Entferne beim Loggen deklarierte Throwables in Methodensignaturen

# 2.1.0
- `IFS-362`: LogApplicationListener loggt nur noch für den eigenen ApplicationContext (nicht für Kind-Kontexte)
- `IFS-444`: Anpassung der Klasse BeanToMapConverter
- `IFS-466`: Fix für fehlerhafte Mdc Formatierung

# 2.0.0
- `IFS-71`: Alter Fachdaten-Marker entfernt
- `IFS-228`: Einführung von Spring Boot
- `IFS-177`: Batch-Appender entfernt
- `IFS-251`: Abhängigkeiten zu log4j entfernt

# 1.8.0
- `IFS-225`: Prüfung der Logeinträge auf maximale Länge und ggfs. Kürzung dieser
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.7.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-177`: Anwendungen und Batches loggen identisch. Der Batch-Appender `appender-batch.xml` ist deprecated und wird im Release v2.0.0 entfernt.

# 1.5.1
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken
- `IFS-42`: Erweiterung um Performance-Logging
- `IFS-68`: Erweiterte Typisierung von Logeinträgen
- `IFS-70`: Position vom Zeitstempel verschoben

# 1.4.1
- `IFS-31`: Konfiguration der maximalen Größe von Parametern implementiert
- LogSchluessel als Deprecated markiert
