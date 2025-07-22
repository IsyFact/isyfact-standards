# 4.1.0

- `IFS-4591`: Hinzufügen von Authentifizierungsmethoden zur Authentifizierung von Clients und Systemen ohne Issuer-URI.
- `IFS-4754`: Einführung von Caching im Authentifizierungsprozess
- `IFS-4785`: Hinzufügen einer Property für die Restlebensdauer gecachter OAuth2-Token
- `IFS-4752`: Wiederherstellen der initialen Authentication nach Authentifizierung mit @Authenticate-Annotation
- `IFS-4810`: Ausbau der Validierung des "aud"-Claims erstellter Tokens

# 4.0.0

- `ISY-305`: Implementierung von IsySecurityTokenUtil zum Auslesen von Attributen aus dem Bearer Token
- `ISY-980`: Anpassung der Dokumentation aufgrund von Security-Umstellungen
- `IFS-2248`: Bereitstellen von Funktionalität zur Token-Gültigkeitsüberprüfung und erneuten Authentifizierung
- `IFS-2804`: Entfernen der `@EnableMethodSecurity` Annotation von `IsySecurityAutoConfiguration`
    - Die Annotation muss in der Anwendung selbst gesetzt werden um Method Security zu aktivieren
- `IFS-3833`: Implementierung von Multi-Tenanacy-Support
- `IFS-4248`: Hinzufügen einer Klasse `ClaimsOnlyOAuth2AuthenticationToken`, welche innerhalb einer Anwendung verwendet werden kann, um Metadaten (bspw. der Name eines laufenden Batches) über den SecurityContext auszutauschen

# 3.0.0

- `IFS-2561`: Die Konfiguration via `rollenrechte.xml` erfolgt optional. 
    - Initialisierung mit Standardwerten
- `IFS-1852`: Bereitstellung einer Implementierung zur Authentifizierung eines Clients mit Client Credentials
    - Property `isy.security.rolesClaimName` eingeführt
- `IFS-2302`: Umsetzung des Mappings von Rollen auf Rechte
- `IFS-2403`: Umstellung vorhandener Tests auf `isy-security-test`
- `IFS-1960`: Bereitstellung einer Implementierung zur Authentifizierung mit Resource Owner Password Credentials
- `IFS-1854`: Umsetzung des Berechtigungsmanagers
- `IFS-2400`: Schnittstellen des Bausteins `isy-security`
    - `Security` und `Authentifizierungsmanager` Interface
    - Standard Implementierungen als Beans per Autokonfiguration bereitgestellt
- `IFS-1885`: Umsetzung der Tokenweitergabe an Nachbarsysteme
- `ISY-147`: Überarbeitung der Methoden des Authentifizierungsmanagers
    - Vereinheitlichung der Methodensignaturen
    - Ergänzung des BHKNZ als optionalen Parameter in allen Methoden (wird aktuell nur für den Resource Owner Password Credentials Flow ausgewertet)
    - Überarbeitung der Dokumentation und Parameterbenennung um Unklarheiten zu beseitigen
- `ISY-83`: Annotation zur Authentifizierung innerhalb von Methoden hinzugefügt
