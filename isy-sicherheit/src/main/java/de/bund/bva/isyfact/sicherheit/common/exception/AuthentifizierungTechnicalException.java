package de.bund.bva.isyfact.sicherheit.common.exception;

import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Exceptions dieser Klasse werden geworfen wenn ein technischer Fehler beim Authentifizieren auftritt. 
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class AuthentifizierungTechnicalException extends SicherheitTechnicalRuntimeException {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7283769057757274739L;

    /**
     * Erzeugt die Exception.
     *      * 
     * @param detailText
     *            Detail-Text der in die Fehlermeldung übernommen werden soll.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public AuthentifizierungTechnicalException(Throwable throwable, String detailText) {
        super(SicherheitFehlerSchluessel.MSG_ZUGRIFF_ACCESSMANAGER_FEHLGESCHLAGEN, throwable, detailText);
    }

    /**
     * Erzeugt die Exception.
     * 
     * @param detailText
     *            Detail-Text, der in die Fehlermeldung übernommen werden soll.
     */
    public AuthentifizierungTechnicalException(String detailText) {
        super(SicherheitFehlerSchluessel.MSG_ZUGRIFF_ACCESSMANAGER_FEHLGESCHLAGEN, detailText);
    }
    
    /**
     * Erzeugt die Exception.
     *      
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public AuthentifizierungTechnicalException(Throwable throwable) {
        super(SicherheitFehlerSchluessel.MSG_ZUGRIFF_ACCESSMANAGER_FEHLGESCHLAGEN, throwable);
    }

}
