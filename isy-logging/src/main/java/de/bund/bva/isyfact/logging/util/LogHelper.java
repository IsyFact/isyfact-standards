package de.bund.bva.isyfact.logging.util;



import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyMarker;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.impl.Ereignisschluessel;
import de.bund.bva.isyfact.logging.impl.IsyMarkerImpl;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;

/**
 * Helper class for creating log entries. It provides the other helper classes of this component
 * with a mechanism for creating uniform logs.
 *
 */
public class LogHelper {

    /** Flag determining whether a method invocation should be logged. */
    private final boolean loggeAufruf;

    /** Flag determining whether the result of an invocation (Success/Failure) should be logged. */
    private final boolean loggeErgebnis;

    /** Flag determining whether the duration of an invocation should be logged. */
    private final boolean loggeDauer;

    /** Flag determining whether all data passed to a method during its invocation should be logged. */
    private final boolean loggeDaten;

    /**
     * Flag determining whether the parameters of a method invocation should be
     * logged if an exception occurs.
     */
    private final boolean loggeDatenBeiException;

    /**
     * Configuration property to set the maximum size limit for an invocation parameter to be included
     * in the log.
     */
    private final long loggeMaximaleParameterGroesse;

    /** Converter to convert beans before they are serialized. */
    private BeanConverter konverter;

    /** Validator which determines the suitability of a bean to be logged as a parameter. */
    private final BeanGroessePruefer pruefer;

    /**
     * Constructor of the class. Initializes the passed in class attributes.
     *
     * @param loggeDauer
     *            Flag determining whether the duration of an invocation should be logged.
     * @param loggeAufruf
     *            Flag determining whether a method invocation should be logged.
     * @param loggeErgebnis
     *            Flag determining whether the result of an invocation (Success/Failure) should be logged.
     * @param loggeDaten
     *            Flag determining whether all data passed to a method during its invocation should be logged.
     * @param loggeDatenBeiException
     *            Flag determining whether the parameters of a method invocation should be
     *            logged if an exception occurs.
     * @param loggeMaximaleParameterGroesse
     *            Configuration property to set the maximum size limit for an invocation parameter to be included
     *            in the log.
     */
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer,
        boolean loggeDaten, boolean loggeDatenBeiException, long loggeMaximaleParameterGroesse) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException,
            loggeMaximaleParameterGroesse, null);
    }

    /**
     * Constructor of the class. Initializes the passed in class attributes.
     *
     * @param loggeDauer
     *            Flag determining whether the duration of an invocation should be logged.
     * @param loggeAufruf
     *            Flag determining whether a method invocation should be logged.
     * @param loggeErgebnis
     *            Flag determining whether the result of an invocation (Success/Failure) should be logged.
     * @param loggeDaten
     *            Flag determining whether all data passed to a method during its invocation should be logged.
     * @param loggeDatenBeiException
     *            Flag determining whether the parameters of a method invocation should be
     *            logged if an exception occurs.
     * @param loggeMaximaleParameterGroesse
     *            Configuration property to set the maximum size limit for an invocation parameter to be included
     *            in the log.
     * @param konverter
     *            Converter to convert beans before they are serialized.
     */
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer, boolean loggeDaten,
        boolean loggeDatenBeiException, long loggeMaximaleParameterGroesse, BeanConverter konverter) {

        this.loggeAufruf = loggeAufruf;
        this.loggeErgebnis = loggeErgebnis;
        this.loggeDauer = loggeDauer;
        this.loggeDaten = loggeDaten;
        this.loggeDatenBeiException = loggeDatenBeiException;
        this.loggeMaximaleParameterGroesse = loggeMaximaleParameterGroesse;
        this.pruefer = new BeanGroessePruefer();
        if (konverter == null) {
            this.konverter = erstelleStandardKonverter();
        }
        else {
            this.konverter = konverter;
        }
    }

    /**
     * Helper method for creating a BeanToMapConverter in case no converter was provided during the
     * invocation of the constructor.
     *
     * @return The converter that is to be used.
     */
    public static BeanToMapConverter erstelleStandardKonverter() {
        List<String> includes = new ArrayList<>();
        includes.add("de.bund.bva");
        return new BeanToMapConverter(includes, null);
    }

    /**
     * Creates a log entry for the invocation of the passed in method.
     *
     * @param logger
     *            The logger that is to be used.
     * @param methode
     *            The invoked method.
     */
    public void loggeAufruf(IsyLogger logger, Method methode) {
        if (logger.isInfoEnabled() && loggeAufruf) {
            logger.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO01001.name(),
                    Ereignisschluessel.EISYLO01001.getNachricht(), erstelleMethodenname(methode),
                    erstelleSignatur(methode));
        }
    }

    /**
     * Creates a log entry for the result of the passed in method's invocation.
     *
     * @param logger
     *            The logger that is to be used.
     * @param methode
     *            The invoked method.
     * @param erfolgreich
     *            Determines whether the invocation was successful (No exceptions were thrown).
     * @param parameter
     *            Parameters the method was invoked with.
     * @param ergebnis
     *            The result of the method invocation (This can also be an exception).
     */
    public void loggeErgebnis(IsyLogger logger, Method methode, boolean erfolgreich, Object[] parameter,
            Object ergebnis) {
        if (logger.isInfoEnabled() && loggeErgebnis) {
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
        // Outputs the data if
        // either "loggeDaten" is set to true
        // or the invocation was not successful and "loggeDatenBeiException" is set to true.
        boolean loggeAufrufUndErgebnisdaten = loggeDaten || !erfolgreich && loggeDatenBeiException;
        if (logger.isDebugEnabled() && loggeAufrufUndErgebnisdaten) {

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
     * Logs the duration of a method invocation and creates a corresponding log entry.
     *
     * @param logger
     *            The logger that is to be used.
     * @param methode
     *            The invoked method.
     * @param dauer
     *            The duration of the invocation.
     * @param erfolgreich
     *            Specifies whether the invocation was successful.
     */
    public void loggeDauer(IsyLogger logger, Method methode, long dauer, boolean erfolgreich) {

        if (logger.isInfoEnabled() && loggeDauer) {
            if (erfolgreich) {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01004.name(),
                        Ereignisschluessel.EISYLO01004.getNachricht(), erstelleMethodenname(methode),
                        new IsyMarkerImpl(MarkerSchluessel.DAUER, String.valueOf(dauer)), erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01005.name(),
                        Ereignisschluessel.EISYLO01005.getNachricht(), erstelleMethodenname(methode),
                        new IsyMarkerImpl(MarkerSchluessel.DAUER, String.valueOf(dauer)), erstelleSignatur(methode));
            }
        }
    }

    /**
     * Creates a log entry for the method invocation of an adjacent system.
     *
     * @param logger
     *             The logger that is to be used.
     * @param methode
     *            The invoked method.
     * @param nachbarsystemName
     *            Name of the adjacent system.
     * @param nachbarsystemUrl
     *            URL of the adjacent system.
     */
    public void loggeNachbarsystemAufruf(IsyLogger logger, Method methode, String nachbarsystemName,
            String nachbarsystemUrl) {
        if (logger.isInfoEnabled() && loggeAufruf) {
            logger.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO01011.name(),
                    Ereignisschluessel.EISYLO01011.getNachricht(), erstelleMethodenname(methode),
                    nachbarsystemName, nachbarsystemUrl, erstelleSignatur(methode));
        }
    }

    /**
     * Creates a log entry for the result of the passed in method's invocation. of an adjacent system.
     *
     * @param logger
     *            The logger that is to be used.
     * @param methode
     *            The invoked method.
     * @param nachbarsystemName
     *            Name of the adjacent system.
     * @param nachbarsystemUrl
     *            URL of the adjacent system.
     * @param erfolgreich
     *            Specifies whether the invocation was successful.
     */
    public void loggeNachbarsystemErgebnis(IsyLogger logger, Method methode, String nachbarsystemName,
            String nachbarsystemUrl, boolean erfolgreich) {
        if (logger.isInfoEnabled() && loggeErgebnis) {
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
     * Logs the duration of a method invocation of an adjacent system and creates a corresponding log entry.
     *
     * @param logger
     *            The logger that is to be used.
     * @param methode
     *            The invoked method.
     * @param dauer
     *            The duration of the invocation.
     * @param nachbarsystemName
     *            Name of the adjacent system.
     * @param nachbarsystemUrl
     *            URL of the adjacent system.
     * @param erfolgreich
     *            Specifies whether the invocation was successful.
     */
    public void loggeNachbarsystemDauer(IsyLogger logger, Method methode, long dauer,
            String nachbarsystemName, String nachbarsystemUrl, boolean erfolgreich) {
        if (logger.isInfoEnabled() && loggeDauer) {
            if (erfolgreich) {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01014.name(),
                        Ereignisschluessel.EISYLO01014.getNachricht(), erstelleMethodenname(methode),
                        nachbarsystemName, nachbarsystemUrl,
                        new IsyMarkerImpl(MarkerSchluessel.DAUER, String.valueOf(dauer)), erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01015.name(),
                        Ereignisschluessel.EISYLO01015.getNachricht(), erstelleMethodenname(methode),
                        nachbarsystemName, nachbarsystemUrl,
                        new IsyMarkerImpl(MarkerSchluessel.DAUER, String.valueOf(dauer)), erstelleSignatur(methode));
            }
        }
    }

    /**
     * Helper method to obtain the current timestamp in milliseconds. This timestamp is used to
     * determine the duration of a method invocation.
     *
     * @return The current timestamp in milliseconds.
     */
    public long ermittleAktuellenZeitpunkt() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Formats a method's signature into a String representation.
     * The method signature is stripped off declared exceptions in this process.
     *
     * @param methode
     *            The method.
     * @return The processed signature as a String.
     */
    private static IsyMarker erstelleSignatur(Method methode) {
        String signatur = methode.toString();
        int throwIndex = signatur.indexOf(" throws ");
        if (throwIndex >= 0) {
            signatur = signatur.substring(0, throwIndex);
        }
        return new IsyMarkerImpl(MarkerSchluessel.METHODE, signatur);
    }

    /**
     * Formats the method name into a shortened String representation (class.name).
     *
     * @param methode
     *            The method.
     * @return The processed method name as a String.
     */
    private static String erstelleMethodenname(Method methode) {
        return methode.getDeclaringClass().getSimpleName() + "." + methode.getName();
    }

    /**
     * Sets the value for the attribute 'konverter'.
     *
     * @param konverter
     *            The new value for the attribute.
     */
    public void setKonverter(BeanToMapConverter konverter) {
        this.konverter = konverter;
    }

    /**
     * Returns the currents value of the attribute 'konverter'.
     *
     * @return Value of the attribute.
     */
    public BeanConverter getKonverter() {
        return konverter;
    }

}
