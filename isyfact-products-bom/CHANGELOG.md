# 1.8.3
- `IFS-785`: Versionsupdate von XStream von 1.4.11 auf 1.4.14 wegen CVE

# 1.8.1
- `IFS-578`: Hebung Apache Commons-Validator auf 1.6
- `IFS-494`: commons-beanutils:commons-beanutils in Version 1.9.4 hinzugefügt.
- `IFS-509`: Update AssertJ auf 3.12.0

# 1.8.0
- `IFS-223`: Logging Bridges ergänzt: jcl-over-slf4j, slf4j-jcl
- `IFS-423`: Anhebung der UCP und JDBC Version auf 12.2.0.1
- `IFS-384`: Spring Security auf 4.2.13.RELEASE angehoben
- `IFS-385`: jackson-databind auf 2.9.9.3 angehoben. jackson-core auf Version 2.9.9 angehoben. jackson-annotations auf Version 2.9.0 angehoben.
- `IFS-437`: com.thoughtworks.xstream:xstream auf 1.4.11.1 angehoben
- `IFS-461`: Hibernate Version auf 5.4.8.Final angehoben.
- `IFS-460`: org.apache.poi:poi auf 3.17 angehoben (Auf Grund von CVE-2017-5644)
- `IFS-468`: Update auf dozer 6.5.0
- `IFS-490`: `com.fasterxml.jackson.core:jackson-databind` auf 2.9.10.1 angehoben
- `IFS-492`: ch.qos.logback:logback-classic und ch.qos.logback:logback-core auf 1.2.3 angehoben.

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
