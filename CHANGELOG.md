# 3.4.0

### Hinweise & bekannte Probleme
- `IFS-5419`: [isyfact-standards-doc] Schlüssel von Korrelations-ID zu korrelationsId geändert
- Siehe [isy-security CHANGELOG](./isy-security/CHANGELOG.md)

### Umgesetzte Tickets
#### Features
- `IFS-5259`: [isy-batchrahmen] Maximale Anzahl automatischer Neustarts für fehlerhafte Batches konfigurierbar.
    * Über den Konfigurationsparameter `Batchrahmen.MaxWiederholungen` kann eine Obergrenze für automatische Neustarts festgelegt werden.
    * Bei Überschreitung wird eine `BatchrahmenMaxWiederholungenException` geworfen, die nur auf Info-Niveau geloggt wird.
    * Ist der Parameter nicht oder auf eine negative Nummer gesetzt, gibt es keine Begrenzung der Neustarts.
- `IFS-4591`: [isy-security] Hinzufügen von Authentifizierungsmethoden zur Authentifizierung von Clients und Systemen ohne Issuer-URI.
- `IFS-5872`: [isy-security] Konfigurierbarkeit der OU pro Authentifizierung

#### Bug Fixes
- keine

#### Interne Anpassungen
- keine

### Durchzuführende Aktionen vor dem ersten Einsatz
- keine
