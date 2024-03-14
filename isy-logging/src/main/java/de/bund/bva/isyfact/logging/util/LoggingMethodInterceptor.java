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

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * Dieser Interceptor ermöglicht es, standardisierte Logeinträge bei Aufrufen von Methoden an System und
 * Komponentengrenzen zu erstellen.
 * 
 */
public class LoggingMethodInterceptor implements MethodInterceptor, InitializingBean {

    /**
     * Hilfsklasse zum Erstellen der Logeinträge.
     */
    private LogHelper logHelper;

    /** Flag zum Kennzeichnen, ob der Start eines Methodenaufrufs gelogged werden soll. */
    private boolean loggeAufruf = true;

    /** Flag zum Kennzeichnen, ob das Ergebnis eines Aufrufs (Erfolg/Misserfolg) gelogged werden soll. */
    private boolean loggeErgebnis = true;

    /** Flag zum Kennzeichnen, ob die Dauer eines Aufrufs gelogged werden soll. */
    private boolean loggeDauer = true;

    /** Flag zum Kennzeichnen, ob immer die kompletten Anfragedaten gelogged werden sollen. */
    private boolean loggeDaten = false;

    /**
     * Flag zum Kennzeichnen, ob die übergebenen Parameter des Aufrufs gelogged werden sollen, wenn eine
     * Exception auftritt.
     */
    private boolean loggeDatenBeiException = true;

    /**
     * Konfigurationsparameter zum Festlegen der maximalen Größe von übergebenen Parameter des Aufrufs, mit
     * der sie noch ins Log geschrieben werden.
     */
    private long loggeMaximaleParameterGroesse = 0;


    /** Gibt an, ob der Standard BeanToMapConverter verwendet werden soll. */
    private final boolean verwendeStandardKonverter;

    /**
     * Includes, die für den BeanToMapConverter bei der Seriailiserung von Objektstrukturen zu verwenden sind.
     */
    private List<String> converterIncludes;

    /**
     * Excludes, die für den BeanToMapConverter bei der Serialisierung von Objektstrukturen zu verwenden sind.
     */
    private List<String> converterExcludes;

    /**
     * Standardkonstruktor.
     */
    public LoggingMethodInterceptor() {
        verwendeStandardKonverter = true;
    }

    /**
     * Konstruktor der Klasse. Initialisiert die übergebenen Properties. Er kann verwendet werden, um das
     * Verhalten des BeanToMapConverters zur Serialisierung von Objektstrukturen (beim Loggen von Parametern
     * von Methodenaufrufen) anzupassen.
     * 
     * @param converterIncludes
     *            Includes der BeanToMapConverters.
     * @param converterExcludes
     *            Excludes der BeanToMapConverters.
     */
    public LoggingMethodInterceptor(List<String> converterIncludes, List<String> converterExcludes) {
        verwendeStandardKonverter = false;
        this.converterExcludes = converterExcludes;
        this.converterIncludes = converterIncludes;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> klasse = invocation.getMethod().getDeclaringClass();
        Method methode = invocation.getMethod();

        IsyLogger logger = IsyLoggerFactory.getLogger(klasse);

        logHelper.loggeAufruf(logger, methode);
        long startzeit = logHelper.ermittleAktuellenZeitpunkt();
        try {
            Object ergebnis = invocation.proceed();
            long dauer = ermittleDauer(startzeit);
            logHelper.loggeDauer(logger, methode, dauer, true);
            logHelper.loggeErgebnis(logger, methode, true, invocation.getArguments(), ergebnis);
            return ergebnis;
        } catch (Throwable t) {
            long dauer = ermittleDauer(startzeit);
            logHelper.loggeDauer(logger, methode, dauer, false);
            logHelper.loggeErgebnis(logger, methode, false, invocation.getArguments(), t);
            throw t;
        }
    }

    /**
     * Interne Hilfsmethode zum ermitteln der Dauer eines Aufrufs an hand der Startzeit und der aktuellen
     * Zeit.
     * 
     * @param startzeit
     *            die Startzeit des Aufrufs.
     * @return Aufrufdauer (Aktuelle Zeit - Startzeit).
     */
    private long ermittleDauer(long startzeit) {
        long endezeit = logHelper.ermittleAktuellenZeitpunkt();
        long dauer = endezeit - startzeit;
        return dauer;
    }

    /**
     * Setzt den Wert des Attributs 'loggeAufruf'.
     * 
     * @param loggeAufruf
     *            Neuer Wert des Attributs.
     */
    public void setLoggeAufruf(boolean loggeAufruf) {
        this.loggeAufruf = loggeAufruf;
    }

    /**
     * Setzt den Wert des Attributs 'loggeErgebnis'.
     * 
     * @param loggeErgebnis
     *            Neuer Wert des Attributs.
     */
    public void setLoggeErgebnis(boolean loggeErgebnis) {
        this.loggeErgebnis = loggeErgebnis;
    }

    /**
     * Setzt den Wert des Attributs 'loggeDauer'.
     * 
     * @param loggeDauer
     *            Neuer Wert des Attributs.
     */
    public void setLoggeDauer(boolean loggeDauer) {
        this.loggeDauer = loggeDauer;
    }

    /**
     * Setzt den Wert des Attributs 'loggeDatenBeiException'.
     * 
     * @param loggeDatenBeiException
     *            Neuer Wert des Attributs.
     */
    public void setLoggeDatenBeiException(boolean loggeDatenBeiException) {
        this.loggeDatenBeiException = loggeDatenBeiException;
    }

    /**
     * Setzt den Wert des Attributs 'loggeMaximaleParameterGroesse'.
     *
     * @param loggeMaximaleParameterGroesse
     *            Neuer Wert des Attributs.
     */
    public void setLoggeMaximaleParameterGroesse(long loggeMaximaleParameterGroesse) {
        this.loggeMaximaleParameterGroesse = loggeMaximaleParameterGroesse;
    }

    /**
     * Initialisierung des LogHelpers nachdem alle Properties gesetzt wurden.
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * @throws Exception
     *             wenn bei der Initialisierung eine Exception aufgetreten ist.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        BeanConverter beanConverter = erstelleBeanConverter();
        this.logHelper = new LogHelper(loggeAufruf, loggeErgebnis, loggeDauer, loggeDaten,
                loggeDatenBeiException, loggeMaximaleParameterGroesse, beanConverter);
    }

    /**
     * Erstellt den BeanConverter, der für die Konvertierung von Beans für die Logausgabe verwendet wird.
     * Diese Methode kann von Subklassen überschrieben werden, um die Konvertierung spezifischen Anforderungen
     * anzupassen.
     * 
     * @return der zu verwendende BeanConverter der durch den LogHelper verwendet werden soll.
     */
    protected BeanConverter erstelleBeanConverter() {
        if (verwendeStandardKonverter) {
            return LogHelper.erstelleStandardKonverter();
        } else {
            return new BeanToMapConverter(converterIncludes, converterExcludes);
        }
    }

    /**
     * Liefert den Wert des Attributs 'logHelper'.
     * 
     * @return Wert des Attributs.
     */
    public LogHelper getLogHelper() {
        return logHelper;
    }

    /**
     * Setzt den Wert des Attributs 'loggeDaten'.
     * 
     * @param loggeDaten
     *            Neuer Wert des Attributs.
     */
    public void setLoggeDaten(boolean loggeDaten) {
        this.loggeDaten = loggeDaten;
    }

}
