package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Die Basisklasse für alle technischen RuntimeExceptions der Komponente Sicherheit.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public abstract class SicherheitTechnicalRuntimeException extends TechnicalRuntimeException {

    /**
     * UID.
     */
    private static final long serialVersionUID = 5561951645613590935L;

    /**
     * Konstruktor mit AusnahmeId und Parameter für einen Fehlertext, welche an die Vaterklasse weitergegeben
     * werden.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param parameter
     *            Die Parameter
     */
    protected SicherheitTechnicalRuntimeException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, new SicherheitFehlertextProvider(), parameter);
    }

    /**
     * Konstruktor mit AusnahmeId, auslösendem Fehler und Parametern für die Fehlernachricht.
     * 
     * @param ausnahmeId
     *            Die Id der Ausnahme
     * @param throwable
     *            Der auslösende Fehler
     * @param parameter
     *            Parameter für den Fehlertext
     */
    protected SicherheitTechnicalRuntimeException(String ausnahmeId, Throwable throwable, String... parameter) {
        super(ausnahmeId, throwable, new SicherheitFehlertextProvider(), parameter);
    }

}
