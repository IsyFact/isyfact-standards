# 4.1.0
## FEATURES
- `IFS-4534`: Bereitstellen von Metriken ohne Zeiteinschränkungen
- `IFS-4715`: Öffnung des Health-Endpunkts ohne Autorisierung
- `IFS-4791`: ServiceStatistik: Deprecation von Metriken mit Zeiteinschränkungen
- `IFS-4818`: Load Balancer Autokonfiguration in eigene Klasse ausgelagert

## BUG FIXES
- `IFS-4817`: Verwendung von `securityMatcher` in actuatorSecurityFilterChain und loadbalancerSecurityFilterChain für korrektes Filtern von Anfragen.

## BREAKING CHANGES
- `IFS-4911`: Absicherung Actuator mit OAuth2