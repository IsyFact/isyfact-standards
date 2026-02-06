# 4.2.0
### FEATURES
- `IFS-5162`: Konfiguration für die Bean Injection angepasst, sodass der PerformanceLogAdvisor korrekt geladen wird

### BUG FIXES
- `IFS-5066`: StackOverflowException in `MdcHelper.liesKorrelationsId()` behoben.
    * Debug-Logging in `liesKorrelationsId()` entfernt, da es während des Log-Layout-Prozesses
      aufgerufen wurde und eine rekursive Schleife verursachen konnte.

### BREAKING CHANGES
