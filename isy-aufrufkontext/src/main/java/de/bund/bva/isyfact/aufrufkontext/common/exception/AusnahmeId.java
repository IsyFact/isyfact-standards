package de.bund.bva.isyfact.aufrufkontext.common.exception;

/**
 * Alle verfügbaren AusnahmeIds.
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public enum AusnahmeId {
    
    /** Der übergebene Parameter ist null oder leer. */
    UEBERGEBENER_PARAMETER_FALSCH("AUFRU00001"),
    
    AUFRUFKONTEXT_KEIN_DEFAULT_KOSTRUKTOR("AUFRU00002");

    /** der Code für den FehlertextProvider. */
    private String code;

    /**
     * Liefert das Feld {@link #code} zurück.
     * @return Wert von code
     */
    public String getCode() {
        return code;
    }

    private AusnahmeId(String code) {
        this.code = code;
    }
    
}

