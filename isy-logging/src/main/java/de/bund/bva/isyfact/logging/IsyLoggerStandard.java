package de.bund.bva.isyfact.logging;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Interface zum Erstellen von Standard-Logeinträgen. Alle Logeinträge werden als technische Daten (datentyp:
 * Technikdaten) markiert.
 */
public interface IsyLoggerStandard {

    /**
     * Erstellung eines Logeintrags im Level 'Trace'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void trace(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Debug'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void debug(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Info'.
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
    void info(LogKategorie kategorie, String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
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
    void info(LogKategorie kategorie, String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
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
    void info(LogKategorie kategorie, String nachricht, TechnicalRuntimeException exception,
        Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
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
    void info(LogKategorie kategorie, String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Warn'.
     *
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(String nachricht, TechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     *
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
    void warn(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(String nachricht, TechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
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
    void error(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(String nachricht, TechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
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
    void fatal(String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(String schluessel, String nachricht, Object... werte);

}
