# 1.14.0
- enthält nur Versionsanhebung auf IFS 1.12.0

# 1.13.0
- `IFS-1073`: Logausgabe zur Korrektur der Korrelations-Id entfernt
- `IFS-668`: Zurücksetzung der KorrelationsId des AnrufKontextTo bei einem Invoke
- `IFS-875`: Umsetzung des Transports von OAuth 2 Bearer-Token zwischen Schnittstellentechnologien
    - `IsyHttpInvokerServiceExporter` und `TimeoutWiederholungHttpInvokerRequestExecutor` um den `AufrufKontextVerwalter` erweitert.
    - Das OAuth 2 Bearer Token wird über den HTTP-Authentication-Header transportiert bzw. aus diesem ausgelesen.

# 1.12.0
- enthält nur Versionsanhebung auf IFS 1.10.0

# 1.11.0
- enthält nur Versionsanhebung auf IFS 1.9.0

# 1.10.2
- `IFS-597`: Verwendung der Klasse `IsyHttpInvokerServiceExporter` in Tests anstatt `HttpInvokerServiceExporter`

# 1.10.1
- `IFS-510`: LoggingKontextAspect räumt KorrelationIds nicht richtig auf: ersetzen der Methode `entferneKorrelationsId()` durch `entferneKorrelationsIds()`
- `IFS-564`: Deaktivieren von Proxy-Objekten in HttpInvoker Schnittstellen

# 1.10.0
- `IFS-223`: Logging Bridges in Products-POM ergänzt: jcl-over-slf4j, slf4j-jcl
- `IFS-468`: Update Dozer auf 6.5.0
    *  Paketpfade und Maven-Dependency auf `com.github.dozermapper` umgestellt
    * (test-scope) `dozerMapper`-Bean-Pfad auf eigene `DozerBeanMapperFactoryBean` (neu in isy-util 1.9.0) umgestellt
    * (test-scope) logback.xml: Warnings über fehlende ELExpressionFactory deaktiviert da diese nicht verwendet wird

# 1.9.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-266`: Anpassung Log-Level bei Erzeugung neuer Korrelations-ID aufgrund fehlender Korrelations-ID im AufrufKontext auf debug.
- `IFS-347`: Abhängigkeiten zu commons-lang3 aufgelöst.

# 1.8.0
- `IFS-378`: StelltAufrufKontextBereit-Interceptoren eine Standard-Reihenfolge hinzugefügt.
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
- `IFS-137`: Funktionalität von LoggingKontextAspect in StelltLoggingKontextBereitInterceptor übernommen und LoggingKontextAspect als @Deprecated markiert. Neue Testfälle angelegt. 

# 1.7.0
- `IFS-111`: StelltLoggingKontextBereit von isy-util übernommen. 

# 1.6.0
- `IFS-9`: StelltLoggingKontextBereit-Annotation nach isy-util verschoben, da auch ohne AufrufkontextTo nutzbar
- `IFS-48`: Bei ausgehenden Systemaufrufen ist nur noch das Flag loggeDauer im LogHelper standardmäßig aktiviert. Per Spring können jedoch auch alle weiteren Flags gesetzt werden. 
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken

## Hinweise zum Upgrade
- StelltLoggingKontextBereit-Annotation liegt nun in einem anderem package. Neben der Anpassung im Java-Code der Anwendung muss auch die Spring-Konfiguration des Pointcuts (expression) aktualisiert werden.

# 1.5.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID
