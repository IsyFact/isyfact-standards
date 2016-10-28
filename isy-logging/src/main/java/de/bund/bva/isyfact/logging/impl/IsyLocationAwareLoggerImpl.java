package de.bund.bva.isyfact.logging.impl;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */

import org.slf4j.spi.LocationAwareLogger;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.exceptions.FehlerhafterLogeintrag;
import de.bund.bva.isyfact.logging.util.LoggingKonstanten;
import de.bund.bva.pliscommon.exception.PlisException;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;

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
     * @see de.bund.bva.isyfact.logging.IsyLogger#trace(java.lang.String, java.lang.Object[])
     */
    public void trace(String nachricht, Object... werte) {
        log(LocationAwareLogger.TRACE_INT, null, null, nachricht, werte, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#traceFachdaten(java.lang.String, java.lang.Object[])
     */
    public void traceFachdaten(String nachricht, Object... werte) {
        log(LocationAwareLogger.TRACE_INT, null, null, nachricht, werte, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#debug(java.lang.String, java.lang.Object[])
     */
    public void debug(String nachricht, Object... werte) {
        log(LocationAwareLogger.DEBUG_INT, null, null, nachricht, werte, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#debugFachdaten(java.lang.String, java.lang.Object[])
     */
    public void debugFachdaten(String nachricht, Object... werte) {
        log(LocationAwareLogger.DEBUG_INT, null, null, nachricht, werte, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#info(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), schluessel, nachricht, werte, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#infoFachdaten(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), schluessel, nachricht, werte, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#info(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void info(LogKategorie kategorie, String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#infoFachdaten(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void infoFachdaten(LogKategorie kategorie, String nachricht, PlisException exception,
            Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), nachricht, werte, exception, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warn(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void warn(String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warnFachdaten(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void warnFachdaten(String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, nachricht, werte, exception, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#error(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void error(String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#errorFachdaten(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void errorFachdaten(String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), nachricht, werte, exception, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatal(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void fatal(String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatalFachdaten(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisException, java.lang.Object[])
     */
    public void fatalFachdaten(String nachricht, PlisException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), nachricht, werte, exception, true);
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
     * @param enthaeltFachlicheDaten
     *            Flag, zum Kennzeichnen, dass der Logeintrag fachliche Daten enthält.
     */
    private void log(int level, String kategorie, String schluessel, String nachricht, Object[] werte,
            boolean enthaeltFachlicheDaten) {
        log(level, kategorie, schluessel, nachricht, werte, null, enthaeltFachlicheDaten);
    }

    /**
     * Zentrale Methode zum Erstellen eines Logeintrags.
     * 
     * @param level
     *            der Level des Logeintrags als int gemäß LocationAwareLogger.
     * @param kategorie
     *            die Kategorie des Logeintrags.
     * @param nachricht
     *            die eigentliche Lognachricht.
     * @param werte
     *            Werte zum Ersetzen der Platzhalter in der Nachricht.
     * @param exception
     *            zu loggende PlisException.
     * @param enthaeltFachlicheDaten
     *            Flag, zum Kennzeichnen, dass der Logeintrag fachliche Daten enthält.
     */
    private void log(int level, String kategorie, String nachricht, Object[] werte, PlisException exception,
            boolean enthaeltFachlicheDaten) {
        log(level, kategorie, exception.getAusnahmeId(), nachricht, werte, exception, enthaeltFachlicheDaten);
    }

    /**
     * Zentrale Methode zum Erstellen eines Logeintrags.
     * 
     * @param level
     *            der Level des Logeintrags als int gemäß LocationAwareLogger.
     * @param kategorie
     *            die Kategorie des Logeintrags.
     * @param nachricht
     *            die eigentliche Lognachricht.
     * @param werte
     *            Werte zum Ersetzen der Platzhalter in der Nachricht.
     * @param exception
     *            zu loggende PlisTechnicalRuntimeException.
     * @param enthaeltFachlicheDaten
     *            Flag, zum Kennzeichnen, dass der Logeintrag fachliche Daten enthält.
     */
    private void log(int level, String kategorie, String nachricht, Object[] werte,
            PlisTechnicalRuntimeException exception, boolean enthaeltFachlicheDaten) {
        log(level, kategorie, exception.getAusnahmeId(), nachricht, werte, exception, enthaeltFachlicheDaten);
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
     * @param t
     *            zu loggende Exception.
     * @param enthaeltFachlicheDaten
     *            Flag, zum Kennzeichnen, dass der Logeintrag fachliche Daten enthält.
     */
    private void log(int level, String kategorie, String schluessel, String nachricht, Object[] werte,
            Throwable t, boolean enthaeltFachlicheDaten) {

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
        // nur der Fall sein, wenn eine PLIS-Exception ohne Ausnahme-ID übergeben wurde. Dies soll die
        // Erstellung des Logeintrags nicht verhindern.
        if (t != null || schluessel != null) {
            rootMarker.add(new IsyMarkerImpl(MarkerSchluessel.SCHLUESSEL, schluessel));
        } else {
            if (pruefeIstSchluesselPflicht(level)) {
                throw new FehlerhafterLogeintrag(FehlerSchluessel.FEHLERHAFTER_EINTRAG_KEIN_SCHLUESSEL,
                        ermittleLevelString(level), logger.getName());
            }
        }

        if (enthaeltFachlicheDaten) {
            rootMarker.add(new IsyMarkerImpl(MarkerSchluessel.FACHDATEN, LoggingKonstanten.TRUE));
        } else {
            rootMarker.add(new IsyMarkerImpl(MarkerSchluessel.FACHDATEN, LoggingKonstanten.FALSE));
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
     * @see de.bund.bva.isyfact.logging.IsyLogger#info(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException,
     *      java.lang.Object[])
     */
    public void info(LogKategorie kategorie, String nachricht, PlisTechnicalRuntimeException exception,
            Object... werte) {
        log(LocationAwareLogger.INFO_INT, LogKategorie.JOURNAL.name(), nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#info(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, java.lang.String, java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void info(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
            Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), schluessel, nachricht, werte, t, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#infoFachdaten(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException,
     *      java.lang.Object[])
     */
    public void infoFachdaten(LogKategorie kategorie, String nachricht,
            PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), nachricht, werte, exception, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#infoFachdaten(de.bund.bva.isyfact.logging.LogKategorie,
     *      java.lang.String, java.lang.String, java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void infoFachdaten(LogKategorie kategorie, String schluessel, String nachricht, Throwable t,
            Object... werte) {
        log(LocationAwareLogger.INFO_INT, kategorie.name(), schluessel, nachricht, werte, t, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warn(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException, java.lang.Object[])
     */
    public void warn(String nachricht, PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warnFachdaten(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException, java.lang.Object[])
     */
    public void warnFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, nachricht, werte, exception, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#error(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException, java.lang.Object[])
     */
    public void error(String nachricht, PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#errorFachdaten(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException, java.lang.Object[])
     */
    public void errorFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), nachricht, werte, exception, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatal(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException, java.lang.Object[])
     */
    public void fatal(String nachricht, PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), nachricht, werte, exception, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatalFachdaten(java.lang.String,
     *      de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException, java.lang.Object[])
     */
    public void fatalFachdaten(String nachricht, PlisTechnicalRuntimeException exception, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), nachricht, werte, exception, true);
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
     * @see de.bund.bva.isyfact.logging.IsyLogger#warn(java.lang.String, java.lang.String,
     *      java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void warn(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, schluessel, nachricht, werte, t, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warnFachdaten(java.lang.String, java.lang.String,
     *      java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void warnFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, schluessel, nachricht, werte, t, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#error(java.lang.String, java.lang.String,
     *      java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void error(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), schluessel, nachricht, werte, t,
                false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#errorFachdaten(java.lang.String, java.lang.String,
     *      java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void errorFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), schluessel, nachricht, werte, t,
                true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatal(java.lang.String, java.lang.String,
     *      java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void fatal(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), schluessel, nachricht, werte, t,
                false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatalFachdaten(java.lang.String, java.lang.String,
     *      java.lang.Throwable, java.lang.Object[])
     */
    @Override
    public void fatalFachdaten(String schluessel, String nachricht, Throwable t, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), schluessel, nachricht, werte, t,
                true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warn(java.lang.String, java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, schluessel, nachricht, werte, false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#warnFachdaten(java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @Override
    public void warnFachdaten(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.WARN_INT, null, schluessel, nachricht, werte, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#error(java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @Override
    public void error(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), schluessel, nachricht, werte,
                false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#errorFachdaten(java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @Override
    public void errorFachdaten(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.ERROR.name(), schluessel, nachricht, werte, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatal(java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @Override
    public void fatal(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), schluessel, nachricht, werte,
                false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#fatalFachdaten(java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @Override
    public void fatalFachdaten(String schluessel, String nachricht, Object... werte) {
        log(LocationAwareLogger.ERROR_INT, LogErrorKategorie.FATAL.name(), schluessel, nachricht, werte, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.IsyLogger#isFatalEnabled()
     */
    @Override
    public boolean isFatalEnabled() {
        // Fatal existiert in SLF4J nicht mehr als eigenes Log-Level und ist gleichbedeutend mit 'Error'.
        return logger.isErrorEnabled();
    }

}
