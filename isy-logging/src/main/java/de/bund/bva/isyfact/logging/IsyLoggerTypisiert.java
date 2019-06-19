package de.bund.bva.isyfact.logging;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Interface zum Erstellen von typisierten Logeinträgen. Die Logeinträge können mit einem benutzerdefinierten
 * Marker versehen werden.
 */
public interface IsyLoggerTypisiert {

    /**
     * Erstellung eines Logeintrags im Level 'Trace'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void trace(IsyDatentypMarker typ, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Debug'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void debug(IsyDatentypMarker typ, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Info'.
     *
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht,
        Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
     *
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht, BaseException exception,
        Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
     *
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht,
        TechnicalRuntimeException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Info'.
     *
     * @param kategorie
     *            die Log-Kategorie des Eintrags.
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht,
        Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags im Level 'Warn'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception,
        Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Warn'.
     *
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception,
        Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Error'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void error(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param nachricht
     *            die Lognachricht.
     * @param exception
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception,
        Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param t
     *            die aufgetrene Exception.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t, Object... werte);

    /**
     * Erstellung eines Logeintrags zu einer aufgetretenen Exception im Level 'Fatal'.
     *
     * @param typ
     *            Marker für den Datentyp des Logeintrags
     * @param schluessel
     *            der Ereignisschlüssel des Eintrags.
     * @param nachricht
     *            die Lognachricht.
     * @param werte
     *            Werte zum Ersetzen von Platzhaltern in der Lognachricht.
     */
    void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte);

}
