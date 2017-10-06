package de.bund.bva.isyfact.logging;

import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

/**
 * Interface zum Erstellen von Logeinträgen, die Fachdaten enthalten. Alle Logeinträge werden als fachliche
 * Daten (datentyp: Fachdaten) markiert.
 */
public interface IsyLoggerFachdaten {

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, im Level 'Trace'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void traceFachdaten(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, im Level 'Debug'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void debugFachdaten(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, im Level 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void infoFachdaten(LogKategorie kategorie, String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void infoFachdaten(LogKategorie kategorie, String nachricht, PlisTechnicalRuntimeException exception,
        Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Info'.
     * 
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
        Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warnFachdaten(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warnFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warnFachdaten(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Warn'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warnFachdaten(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void errorFachdaten(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void errorFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void errorFachdaten(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Error'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void errorFachdaten(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatalFachdaten(String nachricht, PlisException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatalFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatalFachdaten(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags, der fachliche Daten enthält, zu einer aufgetretenen Exception im Level
     * 'Fatal'.
     * 
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatalFachdaten(String schluessel, String nachricht, Object... werte);
	
}
