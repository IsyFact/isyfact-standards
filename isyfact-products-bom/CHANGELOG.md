# 4.0.0
- `IFS-4316`: Aktualisierung und Bereitstellung des Produktkatalogs der Isyfact.

# 3.0.1
- `ISY-701`: Google Guava Versionsanhebung auf 33.1.0
- `ISY-704`: Open CSV Versionsanhebung auf 5.9
- `ISY-987`: Spring Framework Versionsanhebung auf 5.3.33
- `ISY-1073`: Spring Security Versionsanhebung auf 5.7.12
- `ISY-929`: Nimbus JOSE+JWT Versionsanhebung auf 9.37.2
- `ISY-1036`: Apache Commons Collections Versionsanhebung auf 3.2.2
- `ISY-1045`: Apache Commons FileUpload Versionsanhebung auf 1.5

# 3.0.0
- `IFS-1465`: Spring Boot Versionsanhebung auf 2.7.1
- `IFS-2021`: Spring Boot Versionsanhebung auf 2.7.9
    - spring-boot-maven-plugin Versionsanhebung auf 2.7.9
- `IFS-2537`: Spring Boot Versionsanhebung auf 2.7.11
    - spring-boot-maven-plugin Versionsanhebung auf 2.7.11
- `IFS-2045`: Produkte Apache Tika, commons-beanutils und commons-io hinzugefügt
- `ISY-200`: h2 wird über Spring Boot transitiv bezogen, wie im Produktkatalog gefordert
- `ISY-139`: Spring Boot Versionsanhebung auf 2.7.15
- `ISY-416`: Versionsanhebung von Metro Webservices auf 2.4.9
- `ISY-698`: Versionsanhebung von jsoup auf 1.15.3

# 2.4.2
- `IFS-1466`: Versionsanhebung von Spring Boot auf 2.5.13
- `IFS-1525`: Versionsanhebung von Spring Boot auf 2.5.14

# 2.4.1
- `IFS-1397`: Versionsanhebung von Spring Boot auf 2.5.12 (Fix CVE-2022-22965)

# 2.4.0
- `IFS-1264`:
    - Apache Poi OOXML 4.1.2 zu Products-BOM hinzugefügt
    - Versionsanhebung von Spring Boot auf 2.5.9
    - Versionsanhebung von apache-poi auf 4.1.2
    - Versionsanhebung von commons-cli auf 1.5.0
    - Versionsanhebung von commons-net auf 3.8.0
    - Versionsanhebung von commons-validator auf 1.7
    - Versionsanhebung von dbunit auf 2.7.2
    - Versionsanhebung von greenmail auf 1.6.5
    - Versionsanhebung von greenmail-junit4 auf 1.6.5
    - Versionsanhebung von greenmail-spring auf 1.6.5
    - Versionsanhebung von guava auf 31.0.1-jre
    - Versionsanhebung von h2-database auf 2.1.210
    - Versionsanhebung von jsf-api auf 2.2.20
    - Versionsanhebung von jsf-impl auf 2.2.20
    - Versionsanhebung von jsoup auf 1.14.3
    - Versionsanhebung von opencsv auf 5.5.2
    - Versionsanhebung von pojobuilder auf 4.3.0
    - Versionsanhebung von resilience4j-* auf 1.7.1
    - Versionsanhebung von wiremock-jre8 auf 2.32.0
    - Versionsanhebung von xstream auf 1.4.18
- `IFS-798`: Pojobuilder zu Products-BOM hinzugefügt
- `IFS-601`: Einbindung folgender Produkte über die Spring Boot Dependencies: JPA, JTA, Spring, Hibernate, Jackson,
  QueryDSL, SLF4J, Logback, JUnit, Mockito, AssertJ, H2.
- `IFS-985`: Spring Security entfernt (Verwaltung der Version über Spring Boot)
- `IFS-987`: Entferne log4j
- `IFS-970`: Spring-Boot Versionsanhebung auf 2.5.2
- `IFS-984`: Eigene Version von Ehcache durch Spring Boot Managed Dependencies ersetzt
- `IFS-1093`: Anhebung von Oracle-JDBC und UCP auf 19.11
- `IFS-988`: Apache-Commons-Logging entfernt

# 2.2.0
- `IFS-643`: WireMock in products.bom ergänzt
- `IFS-465`: Profile zum Bauen mit Java 11 erstellt
- `IFS-491`: Jackson Version auf 2.10.1 angehoben
- `IFS-600`: Spring-Boot Versionsanhebung auf 2.3.8 und Fasterxml Jackson auf Version 2.11.2
- `IFS-625`: Google.Guava von Version 19.0 auf 29.0 angehoben.
- `IFS-694`: Spring-Boot Versionsanhebung auf 2.2.9 und Fasterxml Jackson auf Version 2.10.5
- `IFS-785`: Versionsupdate von XStream von 1.4.11 auf 1.4.14 wegen CVE
    - es gibt voraussichtlich keine Kompatibilitätsprobleme
- `IFS-676`: Versionsupdate von OpenCSV 3.8 auf 5.3
    - Breaking Changes in rf-asv und isy-outputmanagement:
        - Die Methode readNext() der Klasse CSVReader wirft zusätzlich zu der IOException eine CsvValidationException
        - Viele Konstruktoren wurden entfernt. Wichtig ist, dass der Konstruktor des CSVReader nur noch 1 Argument
          erwartet, da empfohlen wird, den CsvReaderBuilder zu nutzen

# 2.1.0
- `IFS-223`: Logging Bridges ergänzt: jcl-over-slf4j, slf4j-jcl
- `IFS-385`: jackson-databind auf 2.9.9.3 angehoben.
- `IFS-418`: Spring auf 5.1.9.RELEASE angehoben. Spring Security auf 5.1.6.RELEASE angehoben.
- `IFS-423`: Anhebung der UCP und JDBC Version auf 12.2.0.1
- `IFS-437`: com.thoughtworks.xstream:xstream auf 1.4.11.1 angehoben
- `IFS-460`: org.apache.poi:poi auf 4.1.1 angehoben (Auf Grund von CVE-2017-5644)
- `IFS-490`: `com.fasterxml.jackson.core:jackson-databind` auf 2.9.10.1 angehoben
- `IFS-492`: ch.qos.logback:logback-classic und ch.qos.logback:logback-core auf 1.2.3 angehoben.
- `IFS-336`: Apache Commons Validator auf Version 1.6 angehoben
- `IFS-509`: Update AssertJ auf 3.12.0

# 2.0.0
- `IFS-228`: Einführung von Spring Boot
- `IFS-353`: Ablösung von Dozer durch Orika

# 1.7.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst,
  Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-274`: Spring-Framework auf 4.3.22 angehoben
- `IFS-307`: Referenz zu javax.el:el-api auf Tomcat 8.5.x angehoben

# 1.6.0
- `IFS-272`: Maven-Koordinaten der Oracle-Bibliotheken angepasst
- `IFS-117`: Spring Web Flow auf Version 2.4.8 angehoben
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM
  und werden zentral über das POM isyfact-standards versioniert
## Migrationshinweise
- Wegen `IFS-272` müssen die Maven-Koordinaten der Oracle-Bibliotheken angepasst werden.

# 1.5.0
- `IFS-138`: Jackson auf Version 2.8.11 angehoben
