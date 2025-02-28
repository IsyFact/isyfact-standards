# 4.0.0
- `IFS-2395`: Umstellung von IsyFact `MessageSourceHolder` auf Spring `MessageSource`
- `IFS-4329`: IsyTaskAutoConfiguration verhindert jetzt Task-Erstellung im Batch-Profil
- `IFS-747`: Fehlermeldung bei nicht angegebenen Parametern verbessern

# 3.0.0
- `IFS-2153`: Umstellung isy-task auf Spring Boot:
    * BREAKING CHANGE: Einführung von Spring Boot
    * Taskkonfiguration und TaskkonfigurationVerwalter entfernt, Übersetzungen hinzugefügt
- `IFS-2416`: Umstellung von isy-sicherheit auf isy-security
    * BREAKING CHANGE: Die Konfiguration von `benutzer`, `passwort` und `bhknz` erfolgt über isy-security ClientRegistrations und für IsyTaskConfigurationProperties muss nur eine `oauth2ClientRegistrationId` zur Authentifizierung konfiguriert werden.
- `IFS-2124`: Umstellung der isy-task Nutzungsvorgaben nach Upgrade auf Spring Boot

# 2.4.0
- `IFS-1158`: Behebung der "Missing parameter metadata"-Warnung in dem mit dem "-parameters"-Flag compiliert wird
- `IFS-744`: Namensänderung der Bean "taskScheduler" in "isyTaskScheduler"
- `IFS-1175`: Erstelle eigenen TaskFehlertextProvider für Fehlermeldungen
- `IFS-661`: CompletionWatchdog loggt Stacktrace bei Exception

# 2.2.0
- `IFS-746`: Behebung von NullPointerException in TaskSchedulerImpl (wenn HostHandler false zurückgibt)

# 2.1.0
- `IFS-323`: Hostnamen werden jetzt mit Regex angegeben
- `IFS-324`: Umzug des Tasks für Konfigurations-Updates von isy-task zu isy-konfiguration
- `IFS-326`: Autostart-Klasse für den TaskScheduler hinzugefügt

# 2.0.0
- `IFS-228`: Einführung von Spring Boot

# 1.3.0
- `IFS-262`: `isyfact-masterpom` deprecated (Abschaffung mit IsyFact 2.0), `isyfact-masterpom-lib` aufgelöst, Bibliotheken benutzen `isyfact-standards` als Parent-POM

# 1.2.0
- `IFS-189`: Repositories der IsyFact-Standards zusammengeführt, Bibliotheken benutzen wieder gemeinsames Produkt-BOM und werden zentral über das POM isyfact-standards versioniert

# 1.1.0
- `IFS-111`: Neuer Task für Updates der Konfiguration mit isy-konfiguration
