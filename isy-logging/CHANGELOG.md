# 5.0.0
### FEATURES
- `IFS-4676`: Scope der Abhängigkeit zu AspectJ-Weaver auf Runtime gesetzt.
   * Definitionen der Aspekte zum kompilieren werden von org.aspectj:aspectjrt bereitgestellt.

### BUG FIXES
- `IFS-4830`: StackOverflowException in `MdcHelper.liesKorrelationsId()` behoben.
   * Debug-Logging in `liesKorrelationsId()` entfernt, da es während des Log-Layout-Prozesses
     aufgerufen wurde und eine rekursive Schleife verursachen konnte.