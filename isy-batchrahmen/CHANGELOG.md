# 3.3.0

### Features
- `IFS-4248`: In Batches wird im Falle einer fehlgeschlagenen oder nicht vorhandenen Authentifizierung ein `ClaimsOnlyOAuth2AuthenticationToken` erstellt und der SecurityContext mit dem Batch-Namen befüllt, damit dieser außerhalb der Batch-Klassen (über `IsySecurityTokenUtil`) verarbeitet werden kann (bspw. zum Speichern des Batch-Namens in einer Datenbank).
  Hiermit wird ein ähnliches Verhalten zu einem unauthentifizierten AufrufKontext wiederhergestellt.