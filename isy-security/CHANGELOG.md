# 3.0.3

### Hinweise & bekannte Probleme
Die folgenden Methoden wurden entfernt, da die Übergabe des BHKNZ ohne die passende Zertifikat-OU ein Sicherheitsrisiko darstellt:

- `Authentifizierungsmanager#authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password, String bhknz)`
- `AdditionalCredentials#createWithUsernamePasswordBhknz(String username, String password, String bhknz)`
- `PasswordClientRegistrationAuthenticationToken(ClientRegistration clientRegistration, String username, String password, String bhknz)`

Als Ersatz wurden die folgenden Methoden hinzugefügt:

- `AdditionalCredentials#createWithUsernamePasswordBhknzOu(String username, String password, String bhknz, String certificateOu)`
- `PasswordClientRegistrationAuthenticationToken(ClientRegistration clientRegistration, String username, String password, String bhknz, String certificateOu)`

Im Falle von `authentifiziereSystem()` muss auf `authenticate(ClientRegistration, AdditionalCredentials)` umgestellt werden.
Die ClientRegistration kann dabei wie folgt erstellt werden:

```java
ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .authorizationGrantType(AuthorizationGrantType.PASSWORD)
        .build();
```

### Umgesetzte Tickets
#### Features
- `IFS-4591`: Hinzufügen von Authentifizierungsmethoden zur Authentifizierung von Clients und Systemen ohne Issuer-URI.
- `IFS-5872`: Konfigurierbarkeit der OU pro Authentifizierung

#### Bug Fixes
- keine

#### Interne Anpassungen
- keine

### Durchzuführende Aktionen vor dem ersten Einsatz
- keine
