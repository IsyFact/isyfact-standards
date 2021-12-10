package de.bund.bva.isyfact.logging.util;

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

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;
import de.bund.bva.isyfact.logging.impl.IsyMarkerImpl;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper class for creating log entries.
 * It provides the other auxiliary classes of this module with uniform mechanisms for log generation.
 */
public class LogHelper {

    /**
     * Flag to indicate whether the start of a method call should be logged.
     */
    private final boolean loggeAufruf;

    /**
     * Flag to indicate whether the result of a call (success/failure) should be logged.
     */
    private final boolean loggeErgebnis;

    /**
     * Flag to indicate whether the duration of a call should be logged.
     */
    private final boolean loggeDauer;

    /**
     * Flag to indicate whether all data passed when the method is called should be logged.
     */
    private final boolean loggeDaten;

    /**
     * Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     */
    private final boolean loggeDatenBeiException;

    /**
     * Configuration parameter to set the maximum size of passed parameters of the call, with which they are still written to the log.
     */
    private final long loggeMaximaleParameterGroesse;

    /**
     * Converter to convert beans before serializing them.
     */
    private BeanConverter konverter;

    /**
     * Checker that checks beans for their suitability as parameters before logging.
     */
    private BeanGroessePruefer pruefer;

    /**
     * Constructor of the class. The passed class attributes are initialized.
     *
     * @param loggeDauer                    Flag to indicate whether the duration of the call should be logged.
     * @param loggeAufruf                   Flag to indicate whether the start of a method call should be logged.
     * @param loggeErgebnis                 Flag to indicate whether the result (success/failure) of the call should be logged.
     * @param loggeDaten                    Flag to indicate whether all data passed when the method is called should be logged.
     * @param loggeDatenBeiException        Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     * @param loggeMaximaleParameterGroesse Configuration parameter to set the maximum size of passed parameters of the call, with which they are still written to the log.
     */
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer,
                     boolean loggeDaten, boolean loggeDatenBeiException, long loggeMaximaleParameterGroesse) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException,
                loggeMaximaleParameterGroesse, null);
    }

    /**
     * Constructor of the class. The passed class attributes are initialized.
     *
     * @param loggeDauer                    Flag to indicate whether the duration of the call should be logged.
     * @param loggeAufruf                   Flag to indicate whether the start of a method call should be logged.
     * @param loggeErgebnis                 Flag to indicate whether the result (success/failure) of the call should be logged.
     * @param loggeDaten                    Flag to indicate whether all data passed when the method is called should be logged.
     * @param loggeDatenBeiException        Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     * @param loggeMaximaleParameterGroesse Configuration parameter to set the maximum size of passed parameters of the call, with which they are still written to the log.
     * @param konverter                     converter to convert beans before serializing them.
     */
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer, boolean loggeDaten,
                     boolean loggeDatenBeiException, long loggeMaximaleParameterGroesse, BeanConverter konverter) {

        if (konverter == null) {
            konverter = erstelleStandardKonverter();
        }

        this.loggeAufruf = loggeAufruf;
        this.loggeErgebnis = loggeErgebnis;
        this.loggeDauer = loggeDauer;
        this.loggeDaten = loggeDaten;
        this.loggeDatenBeiException = loggeDatenBeiException;
        this.loggeMaximaleParameterGroesse = loggeMaximaleParameterGroesse;
        this.konverter = konverter;
        this.pruefer = new BeanGroessePruefer();
    }

    /**
     * Constructor of the class. The passed class attributes are initialized.
     *
     * @param loggeDauer             Flag to indicate whether the duration of the call should be logged.
     * @param loggeAufruf            Flag to indicate whether the start of a method call should be logged.
     * @param loggeErgebnis          Flag to indicate whether the result (success/failure) of the call should be logged.
     * @param loggeDaten             Flag to indicate whether all data passed when the method is called should be logged.
     * @param loggeDatenBeiException Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     * @deprecated In the future the constructor with parameter {@link #loggeMaximaleParameterGroesse} must be used.
     */
    @Deprecated
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer, boolean loggeDaten,
                     boolean loggeDatenBeiException) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException, 0, null);
    }

    /**
     * Constructor of the class. The passed class attributes are initialized.
     *
     * @param loggeDauer             Flag to indicate whether the duration of the call should be logged.
     * @param loggeAufruf            Flag to indicate whether the start of a method call should be logged.
     * @param loggeErgebnis          Flag to indicate whether the result (success/failure) of the call should be logged.
     * @param loggeDaten             Flag to indicate whether all data passed when the method is called should be logged.
     * @param loggeDatenBeiException Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     * @param konverter              converter to convert beans before serializing them.
     * @deprecated In the future the constructor with parameter {@link #loggeMaximaleParameterGroesse} must be used.
     */
    @Deprecated
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer, boolean loggeDaten,
                     boolean loggeDatenBeiException, BeanConverter konverter) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException, 0, konverter);
    }

    /**
     * Constructor of the class. The passed class attributes are initialized.
     * The constructor sets the flag {@link #loggeDaten} to {@code false}.
     *
     * @param loggeDauer             Flag to indicate whether the duration of the call should be logged.
     * @param loggeAufruf            Flag to indicate whether the start of a method call should be logged.
     * @param loggeErgebnis          Flag to indicate whether the result (success/failure) of the call should be logged.
     * @param loggeDatenBeiException Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     * @see #LogHelper(boolean, boolean, boolean, boolean, boolean)
     * @deprecated In the future the constructor with flag {@link #loggeDaten} as well as {@link #loggeMaximaleParameterGroesse} must be used
     */
    @Deprecated
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer,
                     boolean loggeDatenBeiException) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, false, loggeDatenBeiException);
    }

    /**
     * Constructor of the class. The passed class attributes are initialized.
     * The constructor sets the flag {@link #loggeDaten} to {@code false}.
     *
     * @param loggeDauer             Flag to indicate whether the duration of the call should be logged.
     * @param loggeAufruf            Flag to indicate whether the start of a method call should be logged.
     * @param loggeErgebnis          Flag to indicate whether the result (success/failure) of the call should be logged.
     * @param loggeDatenBeiException Flag to indicate whether the passed parameters of the call should be logged if an exception occurs.
     * @param konverter              converter to convert beans before serializing them.
     * @see #LogHelper(boolean, boolean, boolean, boolean, BeanConverter)
     * @deprecated In the future the constructor with flag {@link #loggeDaten} as well as {@link #loggeMaximaleParameterGroesse} must be used
     */
    @Deprecated
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer,
                     boolean loggeDatenBeiException, BeanConverter konverter) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, false, loggeDatenBeiException, konverter);
    }

    /**
     * Auxiliary method to create a BeanToMapConverter if no converter was passed when calling the constructor of this class.
     *
     * @return the converter to use.
     */
    public static BeanToMapConverter erstelleStandardKonverter() {
        List<String> includes = new ArrayList<>();
        includes.add("de.bund.bva");
        return new BeanToMapConverter(includes, null);
    }

    /**
     * Creates a log entry for the call of the passed method.
     *
     * @param logger  the logger to be used.
     * @param methode the called method.
     */
    public void loggeAufruf(IsyLogger logger, Method methode) {
        if (loggeAufruf) {
            logger.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO01001.name(),
                    Ereignisschluessel.EISYLO01001.getNachricht(), erstelleMethodenname(methode),
                    erstelleSignatur(methode));
        }
    }

    /**
     * Creates a log entry for the call result of the passed method.
     *
     * @param logger      the logger to be used.
     * @param methode     the called method.
     * @param erfolgreich indicates whether the call was successful.
     * @param parameter   Method parameters.
     * @param ergebnis    Result of the method call, this can also be an exception.
     */
    public void loggeErgebnis(IsyLogger logger, Method methode, boolean erfolgreich, Object[] parameter,
                              Object ergebnis) {
        if (loggeErgebnis) {
            if (erfolgreich) {
                logger.info(LogKategorie.METRIK, Ereignisschluessel.EISYLO01002.name(),
                        Ereignisschluessel.EISYLO01002.getNachricht(), erstelleMethodenname(methode),
                        erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.METRIK, Ereignisschluessel.EISYLO01003.name(),
                        Ereignisschluessel.EISYLO01003.getNachricht(), erstelleMethodenname(methode),
                        erstelleSignatur(methode));
            }
        }

        // Output the data if either "loggeDaten" is set, or the call was not successful and
        // "loggeDatenBeiException" is set.
        boolean loggeAufrufUndErgebnisdaten = loggeDaten || (!erfolgreich && loggeDatenBeiException);
        if (loggeAufrufUndErgebnisdaten) {

            List<Object> parameterWerte = null;
            if (parameter != null) {
                parameterWerte = new ArrayList<>();
                for (int index = 0; index < parameter.length; index++) {
                    if (pruefer.pruefeGroesse(parameter[index], loggeMaximaleParameterGroesse)) {
                        parameterWerte.add(konverter.convert(parameter[index]));
                    } else {
                        parameterWerte
                                .add(Ereignisschluessel.DEBUG_LOGGE_DATEN_PARAMETER_ZU_GROSS.getNachricht());
                        logger.debugFachdaten(Ereignisschluessel.DEBUG_LOGGE_DATEN_GROESSE.getNachricht(),
                                erstelleMethodenname(methode), index + 1, parameterWerte.getClass().getName());
                    }
                }
            }

            logger.debugFachdaten(Ereignisschluessel.DEBUG_LOGGE_DATEN.getNachricht(),
                    erstelleMethodenname(methode), parameterWerte, ergebnis, erstelleSignatur(methode));
        }
    }

    /**
     * Logged the duration of a method call and creates a corresponding log entry.
     *
     * @param logger      the logger to be used.
     * @param methode     the called method.
     * @param dauer       Duration of the call.
     * @param erfolgreich indicates whether the call was successful.
     */
    public void loggeDauer(IsyLogger logger, Method methode, long dauer, boolean erfolgreich) {

        if (loggeDauer) {
            if (erfolgreich) {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01004.name(),
                        Ereignisschluessel.EISYLO01004.getNachricht(), erstelleMethodenname(methode),
                        new IsyMarkerImpl(MarkerSchluessel.DAUER, "" + dauer), erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01005.name(),
                        Ereignisschluessel.EISYLO01005.getNachricht(), erstelleMethodenname(methode),
                        new IsyMarkerImpl(MarkerSchluessel.DAUER, "" + dauer), erstelleSignatur(methode));
            }
        }
    }

    /**
     * Creates a log entry for calling the passed method of a neighbor system.
     *
     * @param logger            the logger to be used.
     * @param methode           the called method.
     * @param nachbarsystemName Name of the neighboring system.
     * @param nachbarsystemUrl  URL of the neighboring system.
     */
    public void loggeNachbarsystemAufruf(IsyLogger logger, Method methode, String nachbarsystemName,
                                         String nachbarsystemUrl) {
        if (loggeAufruf) {
            logger.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO01011.name(),
                    Ereignisschluessel.EISYLO01011.getNachricht(), erstelleMethodenname(methode),
                    nachbarsystemName, nachbarsystemUrl, erstelleSignatur(methode));
        }
    }

    /**
     * Creates a log entry for the call result of the passed method of a neighbor system.
     *
     * @param logger            the logger to be used.
     * @param methode           the called method.
     * @param nachbarsystemName Name of the neighboring system.
     * @param nachbarsystemUrl  URL of the neighboring system.
     * @param erfolgreich       indicates whether the call was successful.
     */
    public void loggeNachbarsystemErgebnis(IsyLogger logger, Method methode, String nachbarsystemName,
                                           String nachbarsystemUrl, boolean erfolgreich) {
        if (loggeErgebnis) {
            if (erfolgreich) {
                logger.info(LogKategorie.METRIK, Ereignisschluessel.EISYLO01012.name(),
                        Ereignisschluessel.EISYLO01012.getNachricht(), erstelleMethodenname(methode),
                        nachbarsystemName, nachbarsystemUrl, erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.METRIK, Ereignisschluessel.EISYLO01013.name(),
                        Ereignisschluessel.EISYLO01013.getNachricht(), erstelleMethodenname(methode),
                        nachbarsystemName, nachbarsystemUrl, erstelleSignatur(methode));
            }
        }
    }

    /**
     * Notes the duration of a method call of a neighboring system and creates a corresponding log entry.
     *
     * @param logger            the logger to be used.
     * @param methode           the called method.
     * @param dauer             Duration of the call.
     * @param nachbarsystemName Name of the neighboring system.
     * @param nachbarsystemUrl  URL of the neighboring system.
     * @param erfolgreich       indicates whether the call was successful.
     */
    public void loggeNachbarsystemDauer(IsyLogger logger, Method methode, long dauer,
                                        String nachbarsystemName, String nachbarsystemUrl, boolean erfolgreich) {
        if (loggeDauer) {
            if (erfolgreich) {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01014.name(),
                        Ereignisschluessel.EISYLO01014.getNachricht(), erstelleMethodenname(methode),
                        nachbarsystemName, nachbarsystemUrl, new IsyMarkerImpl(MarkerSchluessel.DAUER, ""
                                + dauer), erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01015.name(),
                        Ereignisschluessel.EISYLO01015.getNachricht(), erstelleMethodenname(methode),
                        nachbarsystemName, nachbarsystemUrl, new IsyMarkerImpl(MarkerSchluessel.DAUER, ""
                                + dauer), erstelleSignatur(methode));
            }
        }
    }

    /**
     * Auxiliary method to determine the current timestamp in milliseconds.
     * This is used to calculate the duration of a call.
     *
     * @return the current timestamp in milliseconds.
     */
    public long ermittleAktuellenZeitpunkt() {
        return new Date().getTime();
    }

    /**
     * Prepares the complete signature of a method as a {@link String}.
     *
     * @param methode the method.
     * @return the processed signature as a {@link String}.
     */
    public IsyMarker erstelleSignatur(Method methode) {
        return new IsyMarkerImpl(MarkerSchluessel.METHODE, methode.toString());
    }

    /**
     * Prepares the name method in a short form (class.name) as a {@link String}.
     *
     * @param methode the method.
     * @return the processed signature as a {@link String}.
     */
    public String erstelleMethodenname(Method methode) {
        return methode.getDeclaringClass().getSimpleName() + "." + methode.getName();
    }

    /**
     * Sets the value of the attribute {@link #konverter}.
     *
     * @param konverter New value of the attribute.
     */
    public void setKonverter(BeanToMapConverter konverter) {
        this.konverter = konverter;
    }

    /**
     * Returns the value of the attribute {@link #konverter}.
     *
     * @return Value of the attribute.
     */
    public BeanConverter getKonverter() {
        return konverter;
    }

}
