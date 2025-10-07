# 4.1.0
## Features

- `IFS-4748`: Alle deprecated Spring-Boot-Funktionen auf aktuelle Alternativen umgestellt

## Bug Fixes
- `IFS-4753`: Änderung der Konfigurationsreihenfolge.
  * BatchSecurityConfiguration wird nach Anwendung und BatchRahmen Konfiguration geladen.
  * Beans mit der `@ConditionalOnMissingBean(...)` Annotation können wie erwartet überschrieben werden.
