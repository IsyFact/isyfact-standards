# 4.2.0
### FEATURES

### BUG FIXES
- `IFS-5066`: StackOverflowException in `MdcHelper.liesKorrelationsId()` behoben.
    * Debug-Logging in `liesKorrelationsId()` entfernt, da es während des Log-Layout-Prozesses
      aufgerufen wurde und eine rekursive Schleife verursachen konnte.

### BREAKING CHANGES
