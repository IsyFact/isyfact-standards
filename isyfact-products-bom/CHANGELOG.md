# 2.3.1
- `IFS-1133`: Hibernate-Version wird nun automatisch durch Spring Boot angezogen

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
      - Viele Konstruktoren wurden entfernt. Wichtig ist, dass der Konstruktor des CSVReader nur noch 1 Argument erwartet, da empfohlen wird, den CsvReaderBuilder zu nutzen

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
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM
- `IFS-274`: Spring-Framework auf 4.3.22 angehoben
- `IFS-307`: Referenz zu javax.el:el-api auf Tomcat 8.5.x angehoben

# 1.6.0
- `IFS-272`: Maven-Koordinaten der Oracle-Bibliotheken angepasst
- `IFS-117`: Spring Web Flow auf Version 2.4.8 angehoben
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert
## Migrationshinweise
- Wegen `IFS-272` müssen die Maven-Koordinaten der Oracle-Bibliotheken angepasst werden. 

# 1.5.0
- `IFS-138`: Jackson auf Version 2.8.11 angehoben
