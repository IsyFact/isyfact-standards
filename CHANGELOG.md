# 5.1.0
### FEATURES
- `IFS-5673`: [isyfact-standards-doc] Aufnahme von `apache.santuario:xmlsec` im Produktkatalog
- `IFS-4990`: [isyfact-standards-doc] Kapitel "Daten & Zustandsmanagement" in der Referenzarchitektur Frontend ergänzt
- `IFS-5416`: [isyfact-standards-doc] Umstellung der technischen Referenzarchitektur von Anwendungen auf IT-Systeme
- `IFS-5538`: [isyfact-standards-doc] Aktualisierung des Produktkatalogs auf Mindestversionen, die von Spring Boot 4 und dem Spring Framework 7 verlangt werden
- `IFS-5259`: [isyfact-standards-doc] Beschreibung der konfigurierbaren maximalen Anzahl automatischer Neustarts für fehlerhafte Batches hinzugefügt
- `IFS-5450`: [isyfact-standards-doc] Erweiterung der Beschreibung des Anwendungskerns (Service Consumer, Konfiguration) 
- `IFS-5370`: [isyfact-standards-doc] Konsolidierung der Vorgaben zur Versionierung von REST-Services, Nachrichten und Events
- `IFS-5340`: [isyfact-standards-doc] Erweiterung der OpenAPI-Dokumentation um Beschreibungen zum Deployment der Spezifikation als Maven-Submodul (SST-Artifact) sowie zur Client-Generierung über eine Maven-Dependency
- `IFS-5491`: Maven wird jetzt in mindestens Version 3.9.0 vorausgesetzt (enforced via maven-enforcer-plugin)
    - Maven < 3.9.0 hat den Status End of Life erreicht

### BUG FIXES

### BREAKING CHANGES

### DEPENDENCY UPGRADES
 
## RELEASE NOTES

[//]: # (### Allgemeine Änderungen)
[//]: # (_keine_)

### Software-technische Referenzarchitektur
Die IsyFact erweitert die Vorgaben und Empfehlungen zur Versionierung von REST-Services, Nachrichten und Events.
Sie stellt ein Entscheidungsmodell, basierend auf der Abwärtskompatibilität von Änderungen, zur Auswahl der Versionierungsstrategie bereit und beschreibt die wichtigsten Maßnahmen.

Die Beschreibung der technischen Referenzarchitektur erfolgte bisher auf Basis von Anwendungen. 
Dies war bis einschließlich IsyFact 3 korrekt, da die Referenzarchitektur bis dahin monolithische IT-Systeme zur Umsetzung von Anwendungen vorsah. 
Ab der IsyFact 4 zerfallen die Anwendungen in mehrere IT-Systeme der Typen Backend, Frontend, Batch und Gateway.
Diese Änderung wurde in der Dokumentation nachgezogen.

## MIGRATION GUIDE