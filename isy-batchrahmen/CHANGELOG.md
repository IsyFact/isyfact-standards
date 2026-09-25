# 4.4.0

#### Features
- Keine

#### Bug Fixes
- `IFS-5834` Ein Batch brach mit `ORA-00942` ab, wenn die Tabelle `BATCHSTATUS_KONFIGURATIONSPARAMETER` nicht vorhanden war.
  - Der Zugriff auf die Tabelle erfolgt jetzt nur, wenn das Feature `Batchrahmen.MaxWiederholungen` tatsächlich konfiguriert ist.
  - Die Tabelle ist nun in den SQL-Skripten dokumentiert.

#### Interne Anpassungen
- Keine
