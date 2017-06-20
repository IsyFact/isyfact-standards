package de.bund.bva.isyfact.logging;

import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

public interface IsyLoggerTypisiert {

    /**
     * Erstellung eines Logeintrags im Level 'Trace'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void trace(IsyDatentypMarker typ, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Debug'.
     *
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    public void debug(IsyDatentypMarker typ, String nachricht, Object... werte);

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
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht,
        Object... werte);

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
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht, PlisException exception,
        Object... werte);

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
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht,
        PlisTechnicalRuntimeException exception, Object... werte);

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
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht,
        Throwable t, Object... werte);

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
    public void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte);

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
    public void warn(IsyDatentypMarker typ, String nachricht, PlisException exception, Object... werte);

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
    public void warn(IsyDatentypMarker typ, String nachricht, PlisTechnicalRuntimeException exception,
        Object... werte);

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
    public void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte);

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
    public void error(IsyDatentypMarker typ, String nachricht, PlisException exception, Object... werte);

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
    public void error(IsyDatentypMarker typ, String nachricht, PlisTechnicalRuntimeException exception,
        Object... werte);

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
    public void error(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte);

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
    public void error(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte);

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
    public void fatal(IsyDatentypMarker typ, String nachricht, PlisException exception, Object... werte);

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
    public void fatal(IsyDatentypMarker typ, String nachricht, PlisTechnicalRuntimeException exception,
        Object... werte);

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
    public void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte);

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
    public void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte);

}
