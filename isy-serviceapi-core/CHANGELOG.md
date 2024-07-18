# 3.1.0
## Bug Fixes
- `IFS-3871`: Vermeidet ClassCastException in TimeoutWiederholungHttpInvokerRequestExecutor bei Nutzung von AufrufkontextVerwalter

# 3.0.0
- `IFS-1166`: Autokonfiguration von isy-serviceapi-core nach isy-sicherheit
- `IFS-1073`: Logausgabe zur Korrektur der Korrelations-Id entfernt
- `IFS-2488`: AufrufKontextTo wird ad hoc als HttpInvoker-Transportobjekt erzeugt, wenn `isy-security` im Classpath liegt und anstelle des Objekts `null` übergeben wird
- `IFS-2422`: Deprecation des isy-serviceapi-core Bausteins
- `ISY-653` : Erweitern des `TimeoutWiederholungHttpInvokerRequestExecutor` um die Token-Beschaffung aus dem Security Context bei Nichtverfügbarkeit des `AufrufKontextVerwalter`.
- `ISY-655`: Beheben einer ClassNotFoundException beim Starten von Anwendungen mit IsyServiceApiCoreAutoConfiguration
- `ISY-658`: Setzen generierter Korrelations-IDs in das AufrufKontextTo im StelltLoggingKontextBereitInterceptor

# 2.4.0
- `IFS-668`: Zurücksetzung der KorrelationsId des AnrufKontextTo bei einem Invoke
- `IFS-874`: Umsetzung des Transports von OAuth 2 Bearer Tokens zwischen Schnittstellentechnologien
    - `IsyHttpInvokerServiceExporter` und `TimeoutWiederholungHttpInvokerRequestExecutor` um den `AufrufKontextVerwalter` erweitert.
    - Der OAuth 2 Bearer Token wird über den HTTP Authentication Header transportiert bzw. aus diesem ausgelesen.
- `IFS-977`: Statische Auflösung des AufrufKontextTo durch Bean-Lösung abgelöst
    * AufrufKontextToHelper ist deprecated -> stattdessen AufrufKontextToResolver verwenden
    * DefaultAufrufKontextToResolver wird auch in der Autoconfig automatisch erstellt und steht für Autowiring zur Verfügung
    * folgende Klassen benötigen jetzt den Resolver als Pflichtparameter:
        * `StelltAufrufKontextBereitInterceptor`
        * `StelltAllgemeinenAufrufKontextBereitInterceptor`
        * `StelltLoggingKontextBereitInterceptor`
        * `IsyHttpInvokerClientInterceptor`

# 2.2.0
- `IFS-564`: Deaktivieren von Proxy-Objekten in HttpInvoker Schnittstellen
- `IFS-597`: Verwendung der Klasse `IsyHttpInvokerServiceExporter` anstatt `HttpInvokerServiceExporter`
  aus Tests entfernt

# 2.1.0
- `IFS-378`: Reihenfolge der Schnittstellenannotationen angepasst
- `IFS-488`: Setze Advisor Order auf 50 für stelltAufrufKontextBereitAdvisor

# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert

# 1.9.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-266`: Anpassung Log-Level bei Erzeugung neuer Korrelations-ID aufgrund fehlender Korrelations-ID im AufrufKontext auf debug.
- `IFS-347`: Abhängigkeiten zu commons-lang3 aufgelöst.

# 1.8.0
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
