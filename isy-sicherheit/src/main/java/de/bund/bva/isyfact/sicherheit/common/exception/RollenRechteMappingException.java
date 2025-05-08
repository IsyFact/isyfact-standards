package de.bund.bva.isyfact.sicherheit.common.exception;

/**
 * Diese Klasse sammelt alle Exceptions die beim Erstellen (aus XML) oder dem Zugriff auf das
 * RollenRechteMapping auftreten.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class RollenRechteMappingException extends SicherheitTechnicalRuntimeException {

    /**
     * SerialId.
     */
    private static final long serialVersionUID = -1532697675642517514L;

    /**
     * Erstellt eine Exception mit AusnahmeId.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     */
    public RollenRechteMappingException(String ausnahmeId) {
        super(ausnahmeId);
    }

    /**
     * Erstellt eine Exception mit AusnahmeId und Parametern mit denen der Fehlertext parametrisiert wird.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param parameter
     *            Den oder die Parameter für den Fehlertext
     */
    public RollenRechteMappingException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

    /**
     * Erstellt eine Exception mit AusnahmeId, auslösendem Fehler und Parameter für den Fehlertext.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param t
     *            Der auslösende Fehler
     * @param parameter
     *            Parameter für den Fehlertext
     */
    public RollenRechteMappingException(String ausnahmeId, Throwable t, String... parameter) {
        super(ausnahmeId, t, parameter);
    }
}
