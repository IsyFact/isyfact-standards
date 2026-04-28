# 4.2.2
### FEATURES
- IFS-5434: Absicherung der Aktuatoren durch OAuth2.

### BUG FIXES
- `IFS-4793`: Anpassung von Spring Security Context, um die ThreadLocals richtig abzuräumen

### BREAKING CHANGES
- Die Aktuatoren /actuator/** werden mit OAuth2 abgesichert. 
Die jwk-set-uri kann dafür in den application.properties als `isy.ueberwachung.security.jwk-set-uri` laut Dokumentation angegeben werden.
Wird dieses Property nicht gesetzt, ist ein Zugriff auf die Aktuatoren mit der vorhanden Security-Konfiguration der API-Endpunkte möglich.
Ist die Security-Konfiguration nicht vorhanden, ist ein Zugriff auf die Endpunkte nicht möglich.

### DOCUMENTATION
