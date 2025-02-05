# 4.0.0
- `ISY-888`: Freigabe des `/Loadbalancer` Endpunkts auf `SecurityFilterChain` (`@Order(99)`) umgestellt
- `ISY-601`: Standardabsicherung und Konfigurationsparameter werden per AutoConfiguration bereitgestellt.
- `IFS-979`: Verbesserung der Nebenläufigkeit von DefaultServiceStatistik.
- `IFS-4155`: Entfernung von obsoleten `pliscommon`-Ressourcen.
  * Entfernung der Abhängigkeiten `de.bund.bva.isyfact.isy-exception-sst` und `de.bund.bva.isyfact.isy-serviceapi-sst`

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

# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-32`: Package-Name auf de.bund.bva.isyfact geändert
- `IFS-251`: Abhängigkeiten zu log4j entfernt

# 1.9.0
- `IFS-248`: Log-Level vom Start der Watchdog Prüfung auf debug gesetzt.
- `IFS-347`: Abhängigkeiten zu commons-lang3 aufgelöst.
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM


# 1.8.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.6.0
- `RF-161`: Bibliotheken binden genutzte Bibliotheken direkt ein und nicht mehr über BOM-Bibliotheken

# 1.5.0
- `IFS-17`: Umbenennung der Artifact-ID und Group-ID
