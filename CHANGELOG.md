# 5.1.0
### FEATURES
- `IFS-5665`: [isyfact-standards-doc] Umstellung der Vorlagen von Anwendungen auf IT-Systeme
- `IFS-5651`: [isyfact-standards-doc] Referenzarchitektur (Vorgaben und Konventionen JPA/Hibernate) um Vorgabe für künstliche IDs ergänzt
- `IFS-5652`: [isyfact-standards-doc] Analyse+Umsetzung: Dokumentation aus isy-sgw-utilities Open Source stellen
- `IFS-4991`: [isyfact-standards-doc] Kapitel "Logging, Monitoring & Telemetrie" in der Referenzarchitektur Frontend ergänzt
- `IFS-5663`: [isyfact-standards-doc] Entfernung der Word-Vorlagen für die Erstellung von Systementwurf und Systemhandbuch
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
- Update org.springframework.boot:spring-boot-maven-plugin von Version 4.0.6 auf 4.1.0
- Update org.springframework.boot:spring-boot-dependencies von Version 4.0.6 auf 4.1.0
- Update org.codehaus.mojo:flatten-maven-plugin von Version 1.7.3 auf 1.8.0
- Update org.sonatype.central:central-publishing-maven-plugin von Version 0.10.0 auf 0.11.0
- Update org.apache.maven.plugins:maven-site-plugin von Version 3.21.0 auf 3.22.0
- Update org.apache.maven.plugins:maven-jar-plugin von Version 3.5.0 auf 3.5.1
- Update org.apache.maven.plugins:maven-dependency-plugin von Version 3.10.0 auf 3.11.0
- Update com.github.spotbugs:spotbugs-maven-plugin von Version 4.9.8.3 auf 4.10.3.0
- Update com.oracle.database.jdbc:ojdbc8 von Version 19.30.0.0 auf 19.31.0.0
- Update com.oracle.database.jdbc:ucp von Version 19.30.0.0 auf 19.31.0.0
- Update com.google.guava:guava von Version 33.5.0-jre auf 33.6.0-jre
- Update org.apache.tika:tika-core von Version 3.3.1 auf 3.3.2
- Update commons-io:commons-io von Version 2.21.0 auf 2.22.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/commit_message_checker_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/dependabot_auto_merge_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/dependency_review_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/next_version.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/maven_build_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/maven_deploy_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/maven_create_release_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/maven_dependency_scan_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/pr_agent_template.yml von Version 2.3.0 auf 3.0.0
- Update IsyFact/isy-github-actions-templates/.github/workflows/semgrep.yml von Version 2.5.0 auf 3.0.0
 
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

Für künstliche IDs, die an einer Systemgrenze exponiert werden, ist zusätzlich zum internen technischen Primärschlüssel eine separate, UUID-basierte öffentliche Kennung zu vergeben.
Sequenzbasierte künstliche Primärschlüssel gelten für neu zu exponierende Identifikatoren als deprecated; rein intern verwendete Primärschlüssel sind davon nicht betroffen.

### Dokumentation

Die Word-Vorlagen für die Erstellung von Systementwürfen und Systemhandbüchern wurden entfernt.

## MIGRATION GUIDE
