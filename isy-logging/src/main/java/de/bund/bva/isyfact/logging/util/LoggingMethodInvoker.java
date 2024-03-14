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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.exceptions.InterceptionFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * Hilfsklasse zum Aufruf von Methoden per Reflection und dem gleichzeitigen erstellen standardisierter
 * Logeinträge.
 * 
 * 'T' ist die Ergebnisklasse der aufgerufenen Methode.
 * 
 */
public class LoggingMethodInvoker<T> {

    /** Der zu verwendende Logger. */
    private final IsyLogger logger;

    /** Der LogHelper. */
    private final LogHelper helper;

    /** Die aufzurufende Methode. */
    private final Method methode;

    /** Name des aufgerufenen Nachbarsystems. */
    private final String nachbarsystemName;

    /** URL des aufgerufenen Nachbarsystems. */
    private final String nachbarsystemUrl;

    /** Flag zum Kennzeichnen, ob es sich um einen Nachbarsystemaufruf handelt. */
    private final boolean nachbarsystemAufruf;

    /**
     * Konstruktor der Klasse für den Aufruf einer Methode. Es werden die übergebenen Klassenattribute
     * initialisiert.
     *
     * @param methode
     *            auszuführende Methode.
     * @param logger
     *            zu verwendender Logger.
     * @param loggeAufruf
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeErgebnis
     *            Flag zum Kennzeichnen, ob das Ergebnis (Erfolg/Misserfolg) des Aufrufs gelogged werden soll.
     * @param loggeDauer
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeDaten
     *            Flag zum Kennzeichnen, ob immer die kompletten Anfragedaten gelogged werden sollen.
     * @param loggeDatenBeiException
     *            Flag zum Kennzeichnen, ob die kompletten Anfragedaten gelogged werden sollen, wenn das
     *            Ergebnis nicht erfolgreich war.
     * @param loggeMaximaleParameterGroesse
     *            Konfigurationsparameter zum Festlegen der maximalen Größe von übergebenen Parameter des
     *            Aufrufs, mit der sie noch ins Log geschrieben werden.
     */
    public LoggingMethodInvoker(Method methode, IsyLogger logger, boolean loggeAufruf, boolean loggeErgebnis,
        boolean loggeDauer, boolean loggeDaten, boolean loggeDatenBeiException,
        long loggeMaximaleParameterGroesse) {
        this.methode = methode;
        this.logger = logger;

        this.helper = new LogHelper(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten,
            loggeDatenBeiException, loggeMaximaleParameterGroesse);
        this.nachbarsystemName = null;
        this.nachbarsystemUrl = null;
        nachbarsystemAufruf = false;
    }

    /**
     * Konstruktor der Klasse für den Aufruf einer Methode eines Nachbarsystems. Es werden die übergebenen
     * Klassenattribute initialisiert.
     *
     * Beim Aufruf von Nachbarsystemmethoden werd der Name und die URL des Nachbarsystems in die Logeinträge
     * aufgenommen.
     *
     * @param methode
     *            auszuführende Methode.
     * @param logger
     *            zu verwendender Logger.
     * @param loggeDauer
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeAufruf
     *            Flag zum Kennzeichnen, ob die Dauer des Aufrufs gelogged werden soll.
     * @param loggeErgebnis
     *            Flag zum Kennzeichnen, ob das Ergebnis (Erfolg/Misserfolg) des Aufrufs gelogged werden soll.
     * @param loggeDaten
     *            Flag zum Kennzeichnen, ob immer die kompletten Anfragedaten gelogged werden sollen.
     * @param loggeDatenBeiException
     *            Flag zum Kennzeichnen, ob die kompletten Anfragedaten gelogged werden sollen, wenn das
     *            Ergebnis nicht erfolgreich war.
     * @param loggeMaximaleParameterGroesse
     *            Konfigurationsparameter zum Festlegen der maximalen Größe von übergebenen Parameter des
     *            Aufrufs, mit der sie noch ins Log geschrieben werden.
     * @param nachbarsystemName
     *            Name des aufgerufenen Nachbarsystems.
     * @param nachbarsystemUrl
     *            URL des aufgerufenen Nachbarsystems.
     */
    public LoggingMethodInvoker(Method methode, IsyLogger logger, boolean loggeAufruf, boolean loggeErgebnis,
        boolean loggeDauer, boolean loggeDaten, boolean loggeDatenBeiException,
        long loggeMaximaleParameterGroesse, String nachbarsystemName, String nachbarsystemUrl) {
        this.methode = methode;
        this.logger = logger;

        this.helper = new LogHelper(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten,
            loggeDatenBeiException, loggeMaximaleParameterGroesse);

        this.nachbarsystemName = nachbarsystemName;
        this.nachbarsystemUrl = nachbarsystemUrl;
        nachbarsystemAufruf = true;
    }

    /**
     * Führt die Methode mit den übergebenen Parametern auf dem übergebenen Zielobjekt aus und erstellt die
     * entsprechenden Logeinträge.
     * 
     * @param ziel
     *            Object, auf dem die Methode ausgführt werden soll.
     * @param parameter
     *            Parameter, mit denen die Methode ausgeführt werden soll.
     * @return das Ergebnis des Methodenaufrufs.
     * 
     * @throws IllegalAccessException
     *             wenn die Methode nicht zugreifbar ist.
     * @throws IllegalArgumentException
     *             wenn das Zielobjekt die aufgerufene Methode nicht bereitstellt.
     * @throws InvocationTargetException
     *             wenn die ausgerufene Methode eine Exception wirft.
     */
    @SuppressWarnings("unchecked")
    public T fuehreMethodeAus(Object ziel, Object... parameter) throws IllegalAccessException,
            InvocationTargetException {
        boolean aufrufErfolgreich = false;

        erstelleLogVorAuruf();
        long startzeit = 0;

        Object ergebnis = null;

        try {
            startzeit = helper.ermittleAktuellenZeitpunkt();
            ergebnis = methode.invoke(ziel, parameter);

            // Aufruf ist ohne Exception verarbeitet worden.
            aufrufErfolgreich = true;
            return (T) ergebnis;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            ergebnis = e;
            throw e;
        } catch (ClassCastException e) {
            ergebnis = new InterceptionFehler(
                    FehlerSchluessel.LOG_INTERCEPTOR_FEHLER_BEI_AUFRUF, e, methode.getName());
            throw e;
        } finally {
            long endezeit = helper.ermittleAktuellenZeitpunkt();
            long dauer = endezeit - startzeit;
            erstelleLogNachAufruf(aufrufErfolgreich, dauer, parameter, ergebnis);
        }
    }

    /**
     * Erstellt die Logeinträge nach dem Aufruf der Methode.
     */
    protected void erstelleLogVorAuruf() {

        if (nachbarsystemAufruf) {
            helper.loggeNachbarsystemAufruf(logger, methode, nachbarsystemName, nachbarsystemUrl);
        } else {
            helper.loggeAufruf(logger, methode);
        }

    }

    /**
     * Erstellt die Logeinträge nach dem Aufruf der Methode.
     * 
     * @param aufrufErfolgreich
     *            gibt an, ob der Aufruf erfolgreich war.
     * @param dauer
     *            die Dauer des Aufrufs.
     * @param parameter
     *            Parameter, mit denen die Methode ausgeführt wurde.
     * @param ergebnis
     *            Ergebnis des Methodenaufrufs (dies kann auch eine Exception sein).
     */
    protected void erstelleLogNachAufruf(boolean aufrufErfolgreich, long dauer, Object[] parameter,
            Object ergebnis) {
        if (nachbarsystemAufruf) {
            helper.loggeNachbarsystemErgebnis(logger, methode, nachbarsystemName, nachbarsystemUrl,
                    aufrufErfolgreich);
            helper.loggeNachbarsystemDauer(logger, methode, dauer, nachbarsystemName, nachbarsystemUrl,
                    aufrufErfolgreich);
        } else {
            helper.loggeDauer(logger, methode, dauer, aufrufErfolgreich);
            helper.loggeErgebnis(logger, methode, aufrufErfolgreich, parameter, ergebnis);
        }
    }
}
