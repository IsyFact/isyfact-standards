package de.bund.bva.isyfact.logging;

import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

public interface IsyLoggerStandard {
	
    /**
     * Erstellung eines Logeintrags im Level 'Trace'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void trace(String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Debug'.
     * 
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void debug(String nachricht, Object... werte);

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
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Object... werte);

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
    public void info(LogKategorie kategorie, String nachricht, PlisException exception, Object... werte);

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
    public void info(LogKategorie kategorie, String nachricht, PlisTechnicalRuntimeException exception,
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
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
        Object... werte);

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
    public void warn(String schluessel, String nachricht, Object... werte);

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
    public void warn(String nachricht, PlisException exception, Object... werte);

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
    public void warn(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

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
    public void warn(String schluessel, String nachricht, Throwable t, Object... werte);

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
    public void error(String nachricht, PlisException exception, Object... werte);

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
    public void error(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

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
    public void error(String schluessel, String nachricht, Throwable t, Object... werte);

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
    public void error(String schluessel, String nachricht, Object... werte);

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
    public void fatal(String nachricht, PlisException exception, Object... werte);

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
    public void fatal(String nachricht, PlisTechnicalRuntimeException exception, Object... werte);

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
    public void fatal(String schluessel, String nachricht, Throwable t, Object... werte);

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
    public void fatal(String schluessel, String nachricht, Object... werte);

}
