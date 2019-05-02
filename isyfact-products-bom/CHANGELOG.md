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