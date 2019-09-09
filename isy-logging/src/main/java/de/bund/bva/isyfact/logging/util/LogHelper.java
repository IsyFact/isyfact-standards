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
 * Helperklasse zum Erstellen von Logeinträgen. Sie stellt den anderen Hilfsklassen dieses Bausteins
 * einheitliche Mechanismen zur Logerzeugung bereit.
 *
 */
public class LogHelper {

    /** Flag zum Kennzeichnen, ob der Start eines Methodenaufrufs gelogged werden soll. */
    private final boolean loggeAufruf;

    /** Flag zum Kennzeichnen, ob das Ergebnis eines Aufrufs (Erfolg/Misserfolg) gelogged werden soll. */
    private final boolean loggeErgebnis;

    /** Flag zum Kennzeichnen, ob die Dauer eines Aufrufs gelogged werden soll. */
    private final boolean loggeDauer;

    /**
     * Flag zum Kennzeichnen, ob sämtliche Daten gelogged werden sollen, die beim Aufruf der Methode übergeben
     * wurden.
     */
    private final boolean loggeDaten;

    /**
     * Flag zum Kennzeichnen, ob die übergebenen Parameter des Aufrufs gelogged werden sollen, wenn eine
     * Exception auftritt.
     */
    private final boolean loggeDatenBeiException;

    /**
     * Konfigurationsparameter zum Festlegen der maximalen Größe von übergebenen Parameter des Aufrufs, mit
     * der sie noch ins Log geschrieben werden.
     */
    private final long loggeMaximaleParameterGroesse;

    /** Konverter, um Beans vor deren Serialisierung zu konvertieren. */
    private BeanConverter konverter;

    /** Prüfer, der Beans vor dem Loggen auf ihre Eignung als Parameter hin prüft. */
    private BeanGroessePruefer pruefer;

    /**
     * Konstruktor der Klasse. Es werden die übergebenen Klassenattribute initialisiert.
     *
     * @param loggeDauer
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeAufruf
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeErgebnis
     *            Flag zum Kennzeichnen, ob das Ergebnis (Erfolg/Misserfolg) des Aufrufs gelogged werden soll.
     * @param loggeDaten
     *            Flag zum Kennzeichnen, ob die kompletten Anfragedaten gelogged werden sollen, wenn das
     *            Ergebnis nicht erfolgreich war.
     * @param loggeDatenBeiException
     *            Flag zum Kennzeichnen, ob die kompletten Anfragedaten gelogged werden sollen, wenn das
     *            Ergebnis nicht erfolgreich war.
     * @param loggeMaximaleParameterGroesse
     *            Konfigurationsparameter zum Festlegen der maximalen Größe von übergebenen Parameter des
     *            Aufrufs, mit der sie noch ins Log geschrieben werden.
     */
    public LogHelper(boolean loggeAufruf, boolean loggeErgebnis, boolean loggeDauer,
        boolean loggeDaten, boolean loggeDatenBeiException, long loggeMaximaleParameterGroesse) {
        this(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten, loggeDatenBeiException,
            loggeMaximaleParameterGroesse, null);
    }

    /**
     * Konstruktor der Klasse. Es werden die übergebenen Klassenattribute initialisiert.
     *
     * @param loggeDauer
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeAufruf
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeErgebnis
     *            Flag zum Kennzeichnen, ob das Ergebnis (Erfolg/Misserfolg) des Aufrufs gelogged werden soll.
     * @param loggeDaten
     *            Flag zum Kennzeichnen, ob die kompletten Anfragedaten gelogged werden sollen, wenn das
     *            Ergebnis nicht erfolgreich war.
     * @param loggeDatenBeiException
     *            Flag zum Kennzeichnen, ob die kompletten Anfragedaten gelogged werden sollen, wenn das
     *            Ergebnis nicht erfolgreich war.
     * @param loggeMaximaleParameterGroesse
     *            Konfigurationsparameter zum Festlegen der maximalen Größe von übergebenen Parameter des
     *            Aufrufs, mit der sie noch ins Log geschrieben werden.
     * @param konverter
     *            Konverter, um Beans vor deren Serialisierung zu konvertieren.
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
     * Hilfmethode zum Erstellen eines BeanToMapConverters, falls beim Aufruf des Konstruktors dieser Klasse
     * kein Konverter übergeben wurde.
     *
     * @return der zu verwendende Konverter.
     */
    public static BeanToMapConverter erstelleStandardKonverter() {
        List<String> includes = new ArrayList<>();
        includes.add("de.bund.bva");
        return new BeanToMapConverter(includes, null);
    }

    /**
     * Erstellt einen Logeintrag für den Aufruf der übergebenen Methode.
     *
     * @param logger
     *            der zu verwendende Logger.
     * @param methode
     *            die aufgerufene Methode.
     */
    public void loggeAufruf(IsyLogger logger, Method methode) {
        if (loggeAufruf) {
            logger.info(LogKategorie.JOURNAL, Ereignisschluessel.EISYLO01001.name(),
                Ereignisschluessel.EISYLO01001.getNachricht(), erstelleMethodenname(methode),
                erstelleSignatur(methode));
        }
    }

    /**
     * Erstellt einen Logeintrag für das Aufrufergebnis der übergebenen Methode.
     *
     * @param logger
     *            der zu verwendende Logger.
     * @param methode
     *            die aufgerufene Methode.
     * @param erfolgreich
     *            gibt an, ob der Auruf erfolgreich war (keine Exception aufgetreten ist).
     * @param parameter
     *            Methodenarameter.
     * @param ergebnis
     *            Ergebnis des Methodenaufrufs, dies kann auch eine Exception sein.
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
        // Ausgabe der Daten, wenn
        // entweder "loggeDaten" gesetzt ist,
        // oder der Aufruf nicht erfolgreich war und "loggeDatenBeiException" gesetzt ist.
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
     * Logged die Dauer eines Methodenaufrufs und erstellt einen entsprechenden Logeintrag.
     *
     * @param logger
     *            der zu verwendende Logger.
     * @param methode
     *            die aufgerufene Methode.
     * @param dauer
     *            Dauer des Aufrufs.
     * @param erfolgreich
     *            gibt an, ob der Auruf erfolgreich war.
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
     * Erstellt einen Logeintrag für den Aufruf der übergebenen Methode eines Nachbarsystems.
     *
     * @param logger
     *            der zu verwendende Logger.
     * @param methode
     *            die aufgerufene Methode.
     * @param nachbarsystemName
     *            Name des Nachbarsystems.
     * @param nachbarsystemUrl
     *            URL des Nachbarsystems.
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
     * Erstellt einen Logeintrag für das Aufrufergebnis der übergebenen Methode eines Nachbarsystems.
     *
     * @param logger
     *            der zu verwendende Logger.
     * @param methode
     *            die aufgerufene Methode.
     * @param nachbarsystemName
     *            Name des Nachbarsystems.
     * @param nachbarsystemUrl
     *            URL des Nachbarsystems.
     * @param erfolgreich
     *            gibt an, ob der Auruf erfolgreich war.
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
     * Logged die Dauer eines Methodenaufrufs eines Nachbarsystems und erstellt einen entsprechenden
     * Logeintrag.
     *
     * @param logger
     *            der zu verwendende Logger.
     * @param methode
     *            die aufgerufene Methode.
     * @param dauer
     *            Dauer des Aufrufs.
     * @param nachbarsystemName
     *            Name des Nachbarsystems.
     * @param nachbarsystemUrl
     *            URL des Nachbarsystems.
     * @param erfolgreich
     *            gibt an, ob der Auruf erfolgreich war.
     */
    public void loggeNachbarsystemDauer(IsyLogger logger, Method methode, long dauer,
        String nachbarsystemName, String nachbarsystemUrl, boolean erfolgreich) {
        if (loggeDauer) {
            if (erfolgreich) {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01014.name(),
                    Ereignisschluessel.EISYLO01014.getNachricht(), erstelleMethodenname(methode),
                    nachbarsystemName, nachbarsystemUrl, new IsyMarkerImpl(MarkerSchluessel.DAUER, ""
                        + dauer),
                    erstelleSignatur(methode));
            } else {
                logger.info(LogKategorie.PROFILING, Ereignisschluessel.EISYLO01015.name(),
                    Ereignisschluessel.EISYLO01015.getNachricht(), erstelleMethodenname(methode),
                    nachbarsystemName, nachbarsystemUrl, new IsyMarkerImpl(MarkerSchluessel.DAUER, ""
                        + dauer),
                    erstelleSignatur(methode));
            }
        }
    }

    /**
     * Hilfsmethode zum ermitteln des aktuellen Zeitstempels in Millisekunden. Dieser wird zur Berechnung der
     * Dauer eines Aufrufs verwendet.
     *
     * @return den aktuellen Zeitstempel in Millisekunden.
     */
    public long ermittleAktuellenZeitpunkt() {
        return new Date().getTime();
    }

    /**
     * Bereitet die vollständige Signatur einer Methode als String auf.
     *
     * @param methode
     *            die Methode.
     * @return die aufbereitete Signatur als String.
     */
    private IsyMarker erstelleSignatur(Method methode) {
        return new IsyMarkerImpl(MarkerSchluessel.METHODE, methode.toString());
    }

    /**
     * Bereitet den Namen Methode in einer Kurzform (Klasse.name) als String auf.
     *
     * @param methode
     *            die Methode.
     * @return die aufbereitete Signatur als String.
     */
    private String erstelleMethodenname(Method methode) {
        return methode.getDeclaringClass().getSimpleName() + "." + methode.getName();
    }

    /**
     * Setzt den Wert des Attributs 'konverter'.
     *
     * @param konverter
     *            Neuer Wert des Attributs.
     */
    public void setKonverter(BeanToMapConverter konverter) {
        this.konverter = konverter;
    }

    /**
     * Liefert den Wert des Attributs 'konverter'.
     *
     * @return Wert des Attributs.
     */
    public BeanConverter getKonverter() {
        return konverter;
    }

}
