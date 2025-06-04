# 5.0.0

## FEATURES
- `IFS-4818`: Autokonfiguration von Load Balancer separiert

## BREAKING CHANGES
- `IFS-4791`: Entfernt Metriken mit Zeiteinschränkungen
- `IFS-4817`: Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.

# 4.1.0
- `IFS-4534`: Bereitstellen von Metriken ohne Zeiteinschränkungen
- `IFS-4715`: Öffnung des Health-Endpunkts ohne Autorisierung

# 4.0.0
- `ISY-888`: Freigabe des `/Loadbalancer` Endpunkts auf `SecurityFilterChain` (`@Order(99)`) umgestellt
- `ISY-601`: Standardabsicherung und Konfigurationsparameter werden per AutoConfiguration bereitgestellt.
- `IFS-979`: Verbesserung der Nebenläufigkeit von DefaultServiceStatistik.
- `IFS-4155`: Entfernung von obsoleten `pliscommon`-Ressourcen.
  * Entfernung der Abhängigkeiten `de.bund.bva.isyfact.isy-exception-sst` und `de.bund.bva.isyfact.isy-serviceapi-sst`
- `IFS-3740`: IsyFact-Standards IF4: Analyse: Stabilität der Tests wiederherstellen

# 3.0.0
- `IFS-1702`: Refaktorierung ServiceStatistik
    * Entkoppelt von Micrometer API
    * Aufgeteilt in Interface und Implementierung
- `IFS-771`: Anleitung für den HealthCheck von HTTPInvoker-Endpoints hinzugefügt
- `IFS-1628`: Standardmetriken bei Anbinden von ServiceStatistik vorhanden

# 2.4.2
- `IFS-1165`: Standardmäßig wird die "/Loadbalancer" Schnittstelle durch die Autokonfiguration freigegeben, wenn spring-security aktiviert ist.

# 2.4.0
- `IFS-686`: Property-Dateien auf Unicode Escapes umgestellt

# 2.2.0
- `IFS-453`: Loglevel für isAlive-Datei-Ereignisse erhöht: INFO für Standardablageort wird genutzt; ERROR für Datei existiert bei Abfrage nicht.
- `IFS-656`: ServiceStatstik: Durchschnitt threadsafe berechnet

# 2.1.0
- `IFS-436`: Entkopplung Health-Endpoint und hinzufügen Nachbarsystem-Check
- `IFS-687`: Umstellung des Nachbarsystem-Checks auf RestTemplate statt WebClient
