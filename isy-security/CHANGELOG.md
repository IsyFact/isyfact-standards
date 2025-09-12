# 4.1.0
## Features
- `IFS-4591`: Hinzufügen von Authentifizierungsmethoden zur Authentifizierung von Clients und Systemen ohne Issuer-URI.
- `IFS-4754`: Einführung von Caching im Authentifizierungsprozess
- `IFS-4785`: Hinzufügen einer Property für die Restlebensdauer gecachter OAuth2-Token
- `IFS-4752`: Wiederherstellen der initialen Authentication nach Authentifizierung mit @Authenticate-Annotation
- `IFS-4810`: Ausbau der Validierung des "aud"-Claims erstellter Tokens

## Breaking Changes
- `IFS-4812`: Verwendung sicherer Hashfunktion mit SHA-512 für Caching
    * Rückgabe eines Byte-Arrays statt eines Integers in der Methode `generateCacheKey` der Klasse `AbstractClientRegistrationAuthenticationToken`
    * Konfigurierbare Properties für Hashfunktion und Bytegröße des Salts
