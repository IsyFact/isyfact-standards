package de.bund.bva.isyfact.logging.impl;



import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.logging.IsyDatentypMarker;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.util.LoggingKonstanten;
import org.slf4j.spi.LocationAwareLogger;

import java.util.Arrays;

/**
 * Standardimplementierung des Loggers der IsyFact.
 *
 */
public class IsyLocationAwareLoggerImpl implements IsyLogger {

    /**
     * "Full-Qualified-Class-Name" dieses Loggers. Wird von SLF4J verwendet um Aufruferinformationen
     * bereitzustellen.
     */
    private final String fqcn = IsyLocationAwareLoggerImpl.class.getName();

    private static final Throwable DEFAULT_THROWABLE = null;

    private static final IsyMarker[] FACHDATEN_MARKER = { new FachdatenMarker() };

    private static final IsyMarker[] TECHNIKDATEN_MARKER = { new TechnikdatenMarker() };

    /**
     * Gekapselter LocationAwareLogger. Es wird der LocationAwareLogger verwendet, da dieser die Hilfsmethode
     * "log" anbietet, der das Log-Level übergeben werden kann. Dadurch können alle Logeinträge einfacher
     * gleich behandelt werden.
     */
    private final LocationAwareLogger logger;

    /**
     * Konstruktor der Klasse. Es werden die übergebenen Klassenattribute gesetzt.
     *
     * @param logger
     *            zu verwendender sl4j-Logger.
     */
    public IsyLocationAwareLoggerImpl(LocationAwareLogger logger) {
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#trace(String, Object[])
     */
    public void trace(String nachricht, Object... werte) {
        log(LocationAwareLogger.TRACE_INT, null, TECHNIKDATEN_MARKER, null, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#traceFachdaten(String, Object[])
     */
    public void traceFachdaten(String nachricht, Object... werte) {
        log(LocationAwareLogger.TRACE_INT, null, FACHDATEN_MARKER, null, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#debug(String, Object[])
     */
    public void debug(String nachricht, Object... werte) {
        log(LocationAwareLogger.DEBUG_INT, null, TECHNIKDATEN_MARKER, null, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#debugFachdaten(String, Object[])
     */
    public void debugFachdaten(String nachricht, Object... werte) {
        log(LocationAwareLogger.DEBUG_INT, null, FACHDATEN_MARKER, null, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#info(LogKategorie,
     *      String, String, Object[])
     */
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), TECHNIKDATEN_MARKER, schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#infoFachdaten(LogKategorie,
     *      String, String, Object[])
     */
    public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), FACHDATEN_MARKER, schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#info(LogKategorie,
     *      String, BaseException, Object[])
     */
    public void info(LogKategorie kategorie, String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.INFO_INT, kategorie.name(), TECHNIKDATEN_MARKER, nachricht,
            werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#infoFachdaten(LogKategorie,
     *      String, BaseException, Object[])
     */
    public void infoFachdaten(LogKategorie kategorie, String nachricht, BaseException exception,
            Object... werte) {
        logException(LocationAwareLogger.INFO_INT, kategorie.name(), FACHDATEN_MARKER, nachricht, werte,
            exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warn(String,
     *      BaseException, Object[])
     */
    public void warn(String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.WARN_INT, null, TECHNIKDATEN_MARKER, nachricht, werte,
            exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warnFachdaten(String,
     *      BaseException, Object[])
     */
    public void warnFachdaten(String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.WARN_INT, null, FACHDATEN_MARKER, nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#error(String,
     *      BaseException, Object[])
     */
    public void error(String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), TECHNIKDATEN_MARKER,
            nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#errorFachdaten(String,
     *      BaseException, Object[])
     */
    public void errorFachdaten(String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), FACHDATEN_MARKER,
            nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatal(String,
     *      BaseException, Object[])
     */
    public void fatal(String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), TECHNIKDATEN_MARKER,
            nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatalFachdaten(String,
     *      BaseException, Object[])
     */
    public void fatalFachdaten(String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), FACHDATEN_MARKER,
            nachricht, werte, exception);
    }

    /**
     * Zentrale Methode zum Erstellen eines Logeintrags bei einer Ausnahme.
     *
     * @param level
     *            der Level des Logeintrags als int gemäß LocationAwareLogger
     * @param kategorie
     *            die Kategorie des Logeintrags
     * @param expliziteMarker
     *            Explizite Marker der Nachricht
     * @param nachricht
     *            die eigentliche Lognachricht
     * @param werte
     *            Werte zum Ersetzen der Platzhalter in der Nachricht
     * @param exception
     *            zu loggende Ausnahme
     */
    private void logException(int level, String kategorie, IsyMarker[] expliziteMarker, String nachricht,
        Object[] werte, BaseException exception) {
        log(level, kategorie, expliziteMarker, exception.getAusnahmeId(), nachricht, werte, exception);
    }

    /**
     * Zentrale Methode zum Erstellen eines Logeintrags bei technischen Ausnahmen.
     *
     * @param level
     *            der Level des Logeintrags als int gemäß LocationAwareLogger
     * @param kategorie
     *            die Kategorie des Logeintrags
     * @param expliziteMarker
     *            Explizite Marker der Nachricht
     * @param nachricht
     *            die eigentliche Lognachricht
     * @param werte
     *            Werte zum Ersetzen der Platzhalter in der Nachricht
     * @param exception
     *            zu loggende Ausnahme
     */
    private void logTechnicalRuntimeException(int level, String kategorie, IsyMarker[] expliziteMarker,
        String nachricht, Object[] werte, TechnicalRuntimeException exception) {
        log(level, kategorie, expliziteMarker, exception.getAusnahmeId(), nachricht, werte, exception);
    }

    /**
     * Zentrale Methode zum Erstellen eines Logeintrags.
     *
     * @param level
     *            der Level des Logeintrags als int gemäß LocationAwareLogger.
     * @param kategorie
     *            die Kategorie des Logeintrags.
     * @param schluessel
     *            der Ereigenisschlüssel.
     * @param nachricht
     *            die eigentliche Lognachricht.
     * @param werte
     *            Werte zum Ersetzen der Platzhalter in der Nachricht.
     * @param expliziteMarker
     *            Explizite Marker der Nachricht.
     * @param t
     *            zu loggende Exception.
     */
    private void log(int level, String kategorie, IsyMarker[] expliziteMarker, String schluessel,
        String nachricht, Object[] werte, Throwable t) {

        IsyMarker rootMarker = IsyMarkerImpl.createRootMarker();

        if (kategorie == null) {
            if (pruefeIstKategoriePflicht(level)) {
                throw new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEINE_KATEGORIE,
                    ermittleLevelString(level), logger.getName());
            }
        } else {
            rootMarker.add(new IsyMarkerImpl(MarkerSchluessel.KATEGORIE, kategorie));
        }

        // Bei einer Exception wird der Schlüssel immer ungeprüft übernommen, auch wenn er NULL ist. Dies kann
        // nur der Fall sein, wenn eine Exception ohne Ausnahme-ID übergeben wurde. Dies soll die
        // Erstellung des Logeintrags nicht verhindern.
        if (t != null || schluessel != null) {
            rootMarker.add(new IsyMarkerImpl(MarkerSchluessel.SCHLUESSEL, schluessel));
        } else {
            if (pruefeIstSchluesselPflicht(level)) {
                throw new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEIN_SCHLUESSEL,
                    ermittleLevelString(level), logger.getName());
            }
        }

        if (expliziteMarker != null) {
            for (IsyMarker isyMarker : expliziteMarker) {
                rootMarker.add(isyMarker);
            }
        }

        // SLF4J erlaubt es grundsätzlich, dass beliebige Objekte zur Ersetzung der Platzhalter in der
        // Lognachricht übergeben werden. Wird ein IsyMarker übergeben, übernehmen wir diesen Marker als
        // "richtigen" Marker des Events und setzen nur den Wert des Markers in die Nachricht.
        for (int i = 0; i < werte.length; i++) {
            if (werte[i] instanceof IsyMarker) {
                IsyMarker marker = (IsyMarker) werte[i];
                rootMarker.add(marker);
                werte[i] = marker.getValue();
            }
        }

        logger.log(rootMarker, fqcn, level, nachricht, werte, t);
    }

    /**
     * Ermittelt als dem übergebenen Level eine String-Repräsentstion zur Ausgabe in Texten (DEBUG, IFNO
     * etc.).
     *
     * @param level
     *            der Level des Logeintrags als int gemäß LocationAwareLogger.
     * @return den ermittelten LevelString.
     */
    private String ermittleLevelString(int level) {
        switch (level) {
        case LocationAwareLogger.DEBUG_INT:
            return "DEBUG";
        case LocationAwareLogger.ERROR_INT:
            return "ERROR";
        case LocationAwareLogger.INFO_INT:
            return "INFO";
        case LocationAwareLogger.TRACE_INT:
            return "TRACE";
        case LocationAwareLogger.WARN_INT:
            return "WARN";
        default:
            return null;
        }
    }

    /**
     * Prüft ob für das Loglevel eine Log-Kategorie angegeben werden muss.
     *
     * @param level
     *            das zu prüfende Loglevel.
     * @return <code>true</code> falls eine Kategorie angegeben werden muss, <code>false</code> sonst.
     */
    private boolean pruefeIstKategoriePflicht(int level) {
        // Log-Kategorien sind nur für INFO und ERROR Pflicht.
        return level == LocationAwareLogger.INFO_INT || level == LocationAwareLogger.ERROR_INT;
    }

    /**
     * Prüft ob für das Loglevel ein Ereignisschlüssel angegeben werden muss.
     *
     * @param level
     *            das zu prüfende Loglevel.
     * @return <code>true</code> falls ein Schlüssel angegeben werden muss, <code>false</code> sonst.
     */
    private boolean pruefeIstSchluesselPflicht(int level) {
        // Schlüssel sind ab INFO (und schwerwiegender) Pflicht.
        return level >= LocationAwareLogger.INFO_INT;
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#info(LogKategorie,
     *      String, TechnicalRuntimeException,
     *      Object[])
     */
    public void info(LogKategorie kategorie, String nachricht, TechnicalRuntimeException exception,
            Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.INFO_INT, LogKategorie.JOURNAL.name(),
            TECHNIKDATEN_MARKER, nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#info(LogKategorie,
     *      String, String, Throwable, Object[])
     */
    @Override
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
            Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), TECHNIKDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#infoFachdaten(LogKategorie,
     *      String, TechnicalRuntimeException,
     *      Object[])
     */
    public void infoFachdaten(LogKategorie kategorie, String nachricht,
            TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.INFO_INT, kategorie.name(), FACHDATEN_MARKER,
            nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#infoFachdaten(LogKategorie,
     *      String, String, Throwable, Object[])
     */
    @Override
    public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
            Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), FACHDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warn(String,
     *      TechnicalRuntimeException, Object[])
     */
    public void warn(String nachricht, TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.WARN_INT, null, TECHNIKDATEN_MARKER, nachricht,
            werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warnFachdaten(String,
     *      TechnicalRuntimeException, Object[])
     */
    public void warnFachdaten(String nachricht, TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.WARN_INT, null, FACHDATEN_MARKER, nachricht,
            werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#error(String,
     *      TechnicalRuntimeException, Object[])
     */
    public void error(String nachricht, TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(),
            TECHNIKDATEN_MARKER, nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#errorFachdaten(String,
     *      TechnicalRuntimeException, Object[])
     */
    public void errorFachdaten(String nachricht, TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(),
            FACHDATEN_MARKER, nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatal(String,
     *      TechnicalRuntimeException, Object[])
     */
    public void fatal(String nachricht, TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(),
            TECHNIKDATEN_MARKER, nachricht, werte, exception);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatalFachdaten(String,
     *      TechnicalRuntimeException, Object[])
     */
    public void fatalFachdaten(String nachricht, TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(),
            FACHDATEN_MARKER, nachricht, werte, exception);
    }

    /**
     * Liefert den Wert des Attributs 'logger'.
     *
     * @return Wert des Attributs.
     */
    public LocationAwareLogger getLogger() {
        return logger;
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warn(String, String,
     *      Throwable, Object[])
     */
    @Override
    public void warn(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, TECHNIKDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warnFachdaten(String, String,
     *      Throwable, Object[])
     */
    @Override
    public void warnFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, FACHDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#error(String, String,
     *      Throwable, Object[])
     */
    @Override
    public void error(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), TECHNIKDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#errorFachdaten(String, String,
     *      Throwable, Object[])
     */
    @Override
    public void errorFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), FACHDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatal(String, String,
     *      Throwable, Object[])
     */
    @Override
    public void fatal(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), TECHNIKDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatalFachdaten(String, String,
     *      Throwable, Object[])
     */
    @Override
    public void fatalFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), FACHDATEN_MARKER, schluessel, nachricht, werte, t);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warn(String, String, Object[])
     */
    @Override
    public void warn(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, TECHNIKDATEN_MARKER, schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#warnFachdaten(String, String,
     *      Object[])
     */
    @Override
    public void warnFachdaten(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, FACHDATEN_MARKER, schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#error(String, String,
     *      Object[])
     */
    @Override
    public void error(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), TECHNIKDATEN_MARKER, schluessel,
            nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#errorFachdaten(String, String,
     *      Object[])
     */
    @Override
    public void errorFachdaten(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), FACHDATEN_MARKER, schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatal(String, String,
     *      Object[])
     */
    @Override
    public void fatal(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), TECHNIKDATEN_MARKER, schluessel,
            nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#fatalFachdaten(String, String,
     *      Object[])
     */
    @Override
    public void fatalFachdaten(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), FACHDATEN_MARKER, schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     *
     * @see IsyLogger#isFatalEnabled()
     */
    @Override
    public boolean isFatalEnabled() {
        // Fatal existiert in SLF4J nicht mehr als eigenes Log-Level und ist gleichbedeutend mit 'Error'.
        return logger.isErrorEnabled();
    }

    @Override
    public void trace(IsyDatentypMarker typ, String nachricht, Object... werte) {
        log(LocationAwareLogger.TRACE_INT, null, new IsyMarker[] { typ }, null, nachricht, werte,
            DEFAULT_THROWABLE);
    }

    @Override
    public void debug(IsyDatentypMarker typ, String nachricht, Object... werte) {
        log(LocationAwareLogger.DEBUG_INT, null, new IsyMarker[] { typ }, null, nachricht, werte,
            DEFAULT_THROWABLE);
    }

    @Override
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht,
        Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), new IsyMarker[] { typ }, schluessel, nachricht, werte,
            DEFAULT_THROWABLE);
    }

    @Override
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht, BaseException exception,
        Object... werte) {
        logException(LocationAwareLogger.INFO_INT, kategorie.name(), new IsyMarker[] { typ }, nachricht, werte,
            exception);
    }

    @Override
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String nachricht,
        TechnicalRuntimeException exception, Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.INFO_INT, kategorie.name(), new IsyMarker[] { typ },
            nachricht, werte, exception);
    }

    @Override
    public void info(LogKategorie kategorie, IsyDatentypMarker typ, String schluessel, String nachricht,
        Throwable t, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), new IsyMarker[] { typ }, schluessel, nachricht, werte, t);
    }

    @Override
    public void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, new IsyMarker[] { typ }, schluessel, nachricht, werte,
            DEFAULT_THROWABLE);
    }

    @Override
    public void warn(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.WARN_INT, null, new IsyMarker[] { typ }, nachricht, werte,
            exception);
    }

    @Override
    public void warn(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception,
        Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.WARN_INT, null, new IsyMarker[] { typ },
            nachricht, werte, exception);
    }

    @Override
    public void warn(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t,
        Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, new IsyMarker[] { typ }, schluessel, nachricht, werte, t);
    }

    @Override
    public void error(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(),
            new IsyMarker[] { typ }, nachricht, werte, exception);
    }

    @Override
    public void error(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception,
        Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(),
            new IsyMarker[] { typ }, nachricht, werte, exception);
    }

    @Override
    public void error(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t,
        Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), new IsyMarker[] { typ },
            schluessel, nachricht, werte, t);
    }

    @Override
    public void error(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), new IsyMarker[] { typ },
            schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }

    @Override
    public void fatal(IsyDatentypMarker typ, String nachricht, BaseException exception, Object... werte) {
        logException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(),
            new IsyMarker[] { typ }, nachricht, werte, exception);
    }

    @Override
    public void fatal(IsyDatentypMarker typ, String nachricht, TechnicalRuntimeException exception,
        Object... werte) {
        logTechnicalRuntimeException(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(),
            new IsyMarker[] { typ }, nachricht, werte, exception);
    }

    @Override
    public void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Throwable t,
        Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), new IsyMarker[] { typ },
            schluessel, nachricht, werte, t);
    }

    @Override
    public void fatal(IsyDatentypMarker typ, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), new IsyMarker[] { typ },
            schluessel, nachricht, werte, DEFAULT_THROWABLE);
    }
}
