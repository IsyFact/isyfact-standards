# 5.0.0
### FEATURES
- `IFS-4737`: [isyfact-standards-doc] Umzug der Dokumentation des Bausteins REST in die Referenzarchitektur und in die Werkzeuge
- `IFS-1800`: [isyfact-standards-doc] Formulierung von Empfehlungen für den Einsatz von Bulk Queries in der Persistenz von Backends
- `IFS-1798`: [isyfact-standards-doc] Formulierung von Empfehlungen für den Einsatz von Second-Level-Caches in der Persistenz von Backends
- `IFS-4743`: [isyfact-standards-doc] Auszeichnung der Bausteine in Bezug auf IsyFact-Standards und IsyFact-Erweiterungen
- `IFS-4736`: [isyfact-standards-doc] Umzug der Dokumentation des Bausteins JPA/Hibernate in die Referenzarchitektur
- `IFS-4713`: [isyfact-standards-doc] Entkopplung der Bausteine (s. `IFS-4582`) in Dokumentation nachvollzogen
- `IFS-4554`: [isyfact-standards-doc] Nutzung des Begriffs "Webservice" im Sinne des W3C vereinheitlicht
- `IFS-3028`: [isyfact-standards-doc] Vorgaben für Spring & Spring Boot aktualisiert
- `IFS-4546`: [isyfact-standards-doc] Verweis auf JAXP entfernt, Negativliste bereinigt und links entfernt
- `IFS-4208`: [isyfact-standards-doc] Verwendungen der Begriffe "Nachbarsystem" und "Externes System" korrigiert
- `IFS-4731`: Korrektes Auflösen der URL in SBOMs
- `IFS-4530`: Update von UCP auf Version 19.27.0.0
- `IFS-4655`: Update von Maven Checkstyle Plugin auf Version 3.6.0
- `IFS-4531`: Update von Flatten Maven Plugin auf Version 1.7.1
  * Update von Maven Version auf 3.6.3
- `IFS-4763`: [isyfact-standards-doc] Erweiterung und Konkretisierung der Liquibase-Dokumentation

### BUG FIXES
- `IFS-4753`: [isy-batchrahmen] Änderung der Konfigurationsreihenfolge.
  * BatchSecurityConfiguration wird nach Anwendung und BatchRahmen Konfiguration geladen.
  * Beans mit der `@ConditionalOnMissingBean(...)` Annotation können wie erwartet überschrieben werden.
- `IFS-4817`: [isy-ueberwachung] Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.

### BREAKING CHANGES
- `IFS-4736`: [isy-persistence] Entfernung der Bibliothek aus den IsyFact-Standards
- `IFS-4582`: [isy-persistence], [isy-polling], [isy-security], [isy-security-test], [isy-task], [isy-util] Entfernen der entkoppelten Bausteine aus dem Standards-Repository

### DEPENDENCY UPGRADES
- `IFS-4530`: Update von UCP auf Version 19.27.0.0
- `IFS-4655`: Update von Maven Checkstyle Plugin auf Version 3.6.0
- `IFS-4531`: Update von Flatten Maven Plugin auf Version 1.7.1
    * Update von Maven Version auf 3.6.3

## RELEASE NOTES

[//]: # (### Allgemeine Änderungen)
[//]: # (_keine_)

### Fachliche Referenzarchitektur
Der Begriff "Nachbarsystem" wurde definiert und seine Verwendung, wo nötig, korrigiert.

### Software-technische Referenzarchitektur
Der Begriff "externes System" wurde definiert und seine Verwendung, wo nötig, korrigiert.
Die Begrifflichkeiten "internes System" bzw. "interne Anwendung" wurden entfernt.

#### Backend
Die Seiten zur Persistenzschicht enthalten nun die Inhalte des aufgelösten Bausteins JPA/Hibernate.
Die Inhalte wurden gestrafft und Abschnitte, die Dokumentation der verwendeten Produkte wiedergaben, wurden durch Verweise auf die entsprechenden Dokumentationen ersetzt.
Grundlegende Erklärungen zu den verwendeten Produkten wurden gänzlich entfernt.

Bisherige Verbote im Rahmen der Persistenz wurden durch eine Empfehlung, basierend auf den Qualitätskriterien der ISO 25010, ersetzt.
Dies gilt für den Einsatz von **Second-Level-Caches** sowie von **Bulk Queries**.

#### Schema-Versionierung
Die bisher angebotene Schema-Versionierung in Form einer Eigenentwicklung wird nicht mehr unterstützt.
Die Schema-Versionierung mit Liquibase wurde konzeptionell erweitert und um Nutzungsvorgaben ergänzt.

#### Services
Die Nutzung des Begriffs "Webservice" wurde vereinheitlicht und entspricht jetzt der Definition des W3C.

### Bausteine

#### REST
Die Konzeption wurde um die strukturierten Verwendung von HTTP-Statuscodes (inkl. `Retry-After`, Status-URLs, Fehlermeldungen) im Kapitel "Verwendung von HTTP-Statuscodes" ergänzt.

#### Überwachung
Die folgenden zeitbeschränkten Metriken wurden entfernt:

* `AnzahlAufrufeLetzteMinute`: Anzahl der Anrufe in der letzten Minute
* `AnzahlTechnicalExceptionsLetzteMinute`: Anzahl der technischen Fehler in der letzten Minute
* `AnzahlBusinessExceptionsLetzteMinute`: Anzahl der geschäftlichen Fehler in der letzten Minute

Diese Metriken waren zuvor über die `ServiceStatistik`-Schnittstelle verfügbar und wurden automatisch bei Micrometer in der `IsyMetricsAutoConfiguration` registriert.

#### Entkopplung
Folgende Bausteine wurden in eigenständige Repositories umgezogen:

* [isyfact-standards (Package `de.bund.bva.isyfact.persistence.datetime`)](https://github.com/IsyFact/isyfact-standards/tree/release/4.x/isy-persistence/src/main/java/de/bund/bva/isyfact/persistence/datetime) →
  [isy-datetime-persistence](https://github.com/IsyFact/isy-datetime-persistence)
* [isyfact-standards (Modul isy-polling)](https://github.com/IsyFact/isyfact-standards/tree/release/4.x/isy-polling) → [isy-polling](https://github.com/IsyFact/isy-polling)
* [isyfact-standards (Modul isy-security)](https://github.com/IsyFact/isyfact-standards/tree/release/4.x/isy-security) → [isy-security](https://github.com/IsyFact/isy-security)
* [isyfact-standards (Modul isy-task)](https://github.com/IsyFact/isyfact-standards/tree/release/4.x/isy-task) → [isy-task](https://github.com/IsyFact/isy-task)
* [isyfact-standards (Modul isy-util)](https://github.com/IsyFact/isyfact-standards/tree/release/4.x/isy-util) → [isy-util](https://github.com/IsyFact/isy-util)

Für diese Bausteine gilt:

* Alle Bausteine lassen sich weiterhin über die `isyfact-standards-bom` einbinden und auch die Group- und Artifact-IDs bleiben dieselben.
* Die Version wird allerdings in zukünftigen Releases nicht mehr mit der Version der `isyfact-standards` synchron gehalten und entwickelt sich entkoppelt davon fort.

[//]: # (* Die Bausteine besitzen keinerlei Abhängigkeiten auf die restliche IsyFact mehr.)
[//]: # (Sie setzen allein Java 17 voraus.)
[//]: # (So können sie auch in Anwendungen integriert werden, die noch auf einem älteren Stand der IsyFact beruhen.)

#### Aufgelöste Bausteine

**JPA/Hibernate**: Die konzeptionellen Inhalte des Bausteins JPA/Hibernate wurden in die Referenzarchitektur überführt, da die zugehörige Bibliothek `isy-persistence` zugunsten der direkten Nutzung von Spring Data und Liquibase aufgelöst wurde.

[//]: # (### Deprecations)
[//]: # (_keine_)

[//]: # (### Dokumentation)
[//]: # (_keine_)

## MIGRATION GUIDE

### Aufgelöster Baustein "JPA/Hibernate"
Anwendungen migrieren konzeptionell auf die Beschreibungen zur Persistenzschicht von Backends in der Referenzarchitektur.

Die Bibliothek `isy-persistence` entfällt.
Hilfsfunktionen zur Definition von Enums und zur Prüfung der Schema-Version wurden in den Baustein Util in die Bibliothek `isy-util` verschoben.
Alle weitere Funktionalität entfällt und wird durch die direkte Verwendung der Produkte Spring Data, Spring Boot und Spring ersetzt.

### Baustein Überwachung
Anwendungen, die auf die zeitbeschränkten Metriken (`...LetzteMinute`) zur Überwachung oder Alarmierung angewiesen sind, müssen ihre Überwachungskonfigurationen anpassen. 
Die übrigen Metriken ohne Zeitbeschränkung funktionieren weiterhin wie gehabt.

> **Hinweis:** Mithilfe der z.B. in Prometheus verfügbaren Funktionen lässt sich die gewohnte Funktionalität nahezu vollständig nachbilden.
