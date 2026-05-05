# 3.4.0
### Features
- `IFS-5259`: Maximale Anzahl automatischer Neustarts für fehlerhafte Batches konfigurierbar.
    * Über den Konfigurationsparameter `Batchrahmen.MaxWiederholungen` kann eine Obergrenze für automatische Neustarts festgelegt werden.
    * Bei Überschreitung wird eine `BatchrahmenMaxWiederholungenException` geworfen, die nur auf Info-Niveau geloggt wird.
    * Ist der Parameter nicht gesetzt oder auf `-1` gesetzt, gibt es keine Begrenzung der Neustarts.
