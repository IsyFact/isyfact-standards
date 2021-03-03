/*
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
 */
package de.bund.bva.pliscommon.ueberwachung.common.jmx;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.serviceapi.annotations.FachlicherFehler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class implements a monitoring bean for services. It provides the monitoring options,
 * which each service must provide to IsyFact.
 *
 * @author sd&m
 * @version $Id: ServiceStatistikMBean.java 144975 2015-08-18 13:07:56Z sdm_jmeisel $
 */
@ManagedResource(description = "Diese MBean liefert Überwacht die Aufrufe eines Services.")
public class ServiceStatistikMBean implements MethodInterceptor, InitializingBean {
    /**
     * Default value for number of searches used to calculate the average.
     */
    private static final int ANZAHL_AUFRUFE_FUER_DURCHSCHNITT = 10;

    /**
     * Specifies whether the return object structures should be checked for business errors. May
     * have an impact on performance.
     */
    private boolean erweiterteFachlicheFehlerpruefung;

    /**
     * Specifies whether the return object structures should be checked for business errors. May
     * have an impact on performance.
     *
     * @param fachlicheFehlerpruefungAktiviert <code>true</code> if the return object structure should be checked for functional errors,
     *                                         otherwise <code>false</code>.
     */
    public void setErweiterteFachlicheFehlerpruefung(boolean fachlicheFehlerpruefungAktiviert) {
        this.erweiterteFachlicheFehlerpruefung = fachlicheFehlerpruefungAktiviert;
    }

    /**
     * Duration of the last search calls (in milliseconds).
     */
    private List<Long> letzteSuchdauern = new LinkedList<>();

    /**
     * Flag for the minute in which values of the last minute were determined.
     */
    private volatile int letzteMinute;

    /**
     * Number of non-error calls made in the minute indicated by lastminute.
     */
    private volatile int anzahlAufrufeLetzteMinute;

    /**
     * Number of non-error calls made in the current minute.
     */
    private volatile int anzahlAufrufeAktuelleMinute;

    /**
     * Number of calls made in the minute denoted by lastMinute, during which
     * a technical error occurred.
     */
    private volatile int anzahlFehlerLetzteMinute;

    /**
     * Number of calls made in the current minute in which a techincal error
     * occurred.
     */
    private volatile int anzahlFehlerAktuelleMinute;

    /**
     * The number of technical errors in the current minute. A business error is present if
     * either an exception of type PlusBusinessException was thrown or the returned
     * error list contained entries.
     */
    private volatile int anzahlFachlicheFehlerLetzteMinute;

    /**
     * The number of technical errors in the last minute. A business error is present if either
     * an exception of type PlusBusinessException was thrown or the returned error list contained
     * entries.
     */
    private volatile int anzahlFachlicheFehlerAktuelleMinute;

    /**
     * Logger.
     */
    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(ServiceStatistikMBean.class);

    /**
     * The maximum depth for recursive checking for functional errors in extended functional
     * Error checking.
     */
    private static final int MAXTIEFE = 10;

    /**
     * This method counts a call to the component for statistics. For the statistics the specification,
     * the duration and whether the call was erroneous is needed.
     *
     * @param dauer               The duration of the call in milliseconds.
     * @param erfolgreich         Indicator whether the call was successful (<code>true</code>) or a technical error
     *                            occurred (<code>false</code>).
     * @param fachlichErfolgreich Flag indicating whether the call was technically successful (<code>true</code>) or a technical * error occurred (<code>false</code>).
     */
    public synchronized void zaehleAufruf(long dauer, boolean erfolgreich, boolean fachlichErfolgreich) {
        aktualisiereZeitfenster();
        this.anzahlAufrufeAktuelleMinute++;
        if (!erfolgreich) {
            this.anzahlFehlerAktuelleMinute++;
        }
        if (!fachlichErfolgreich) {
            this.anzahlFachlicheFehlerAktuelleMinute++;
        }
        if (this.letzteSuchdauern.size() == ANZAHL_AUFRUFE_FUER_DURCHSCHNITT) {
            this.letzteSuchdauern.remove(ANZAHL_AUFRUFE_FUER_DURCHSCHNITT - 1);
        }
        this.letzteSuchdauern.add(0, dauer);
    }

    /**
     * This method causes the time window for the counters of errors and calls to be updated in the current
     * and last minute. If a minute has elapsed, the values of the current * minute are copied to those of the counters for the last minute.
     * The counters for the current minute are set to 0
     * The method ensures that this operation can be performed only once per minute.
     */
    private synchronized void aktualisiereZeitfenster() {
        int aktuelleMinute = getAktuelleMinute();
        if (aktuelleMinute != this.letzteMinute) {
            if ((aktuelleMinute - this.letzteMinute) > 1) {
                // no info from last minute
                this.anzahlAufrufeLetzteMinute = 0;
                this.anzahlFehlerLetzteMinute = 0;
                this.anzahlFachlicheFehlerLetzteMinute = 0;
            } else {
                this.anzahlAufrufeLetzteMinute = this.anzahlAufrufeAktuelleMinute;
                this.anzahlFehlerLetzteMinute = this.anzahlFehlerAktuelleMinute;
                this.anzahlFachlicheFehlerLetzteMinute = this.anzahlFachlicheFehlerAktuelleMinute;
            }

            this.anzahlAufrufeAktuelleMinute = 0;
            this.anzahlFehlerAktuelleMinute = 0;
            this.anzahlFachlicheFehlerAktuelleMinute = 0;
            this.letzteMinute = aktuelleMinute;
        }
    }

    /**
     * Calculates the current minute of the system time.
     *
     * @return The minute portion of the current system time.
     */
    private static int getAktuelleMinute() {
        return (int) (System.currentTimeMillis() / 60000);
    }

    /**
     * Returns the average duration of the last 10 aura calls. Defines a method for the
     * management interface of this MBean.
     *
     * @return The average duration of the last 10 calls in ms.
     */
    @ManagedAttribute(description = "Liefert die durchschnittliche Dauer der letzten 10 Aufrufe in ms.")
    public long getDurchschnittsDauerLetzteAufrufe() {
        long result = 0;
        if (this.letzteSuchdauern.size() > 0) {
            // Copy list to avoid concurrent changes
            // Explicitly no synchronization to not degrade application performance
            Long[] dauern = this.letzteSuchdauern.toArray(new Long[0]);
            for (long dauer : dauern) {
                result += dauer;
            }
            result /= dauern.length;
        }
        return result;
    }

    /**
     * Returns the number of calls counted in the last minute where no error occurred.
     * Defines a method for the management interface of this MBean.
     *
     * @return The number of calls counted in the last minute where no error occurred.
     */
    @ManagedAttribute(description = "Liefert die Anzahl der nicht fehlerhaften Aufrufe in der letzten Minute")
    public int getAnzahlAufrufeLetzteMinute() {
        aktualisiereZeitfenster();
        return this.anzahlAufrufeLetzteMinute;
    }

    /**
     * Returns the number of calls counted in the last minute where an error occurred.
     * Defines a method for the management interface of this MBean.
     *
     * @return The number of calls counted in the last minute where an error occurred.
     */
    @ManagedAttribute(description = "Liefert die Anzahl der fehlerhaften Aufrufe in der letzten Minute")
    public int getAnzahlFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return this.anzahlFehlerLetzteMinute;
    }

    /**
     * Returns the number of calls counted in the last minute in which a technical error
     * has occurred.
     *
     * @return The number of calls counted in the last minute where a technical error * occurred.
     */
    @ManagedAttribute(
            description = "Liefert die Anzahl der fachlich fehlerhaften Aufrufe in der letzten Minute")
    public int getAnzahlFachlicheFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return this.anzahlFachlicheFehlerLetzteMinute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startZeit = System.currentTimeMillis();
        boolean erfolgreich = false;
        boolean fachlichErfolgreich = false;

        try {
            Object result = invocation.proceed();
            erfolgreich = true;
            if (this.erweiterteFachlicheFehlerpruefung) {
                fachlichErfolgreich = !sindFachlicheFehlerVorhanden(result);
            } else {
                fachlichErfolgreich = true;
            }
            return result;
        } catch (PlisBusinessToException t) {
            // BusinessExceptions isn't counted as technical error
            erfolgreich = true;
            throw t;
        } finally {
            long aufrufDauer = System.currentTimeMillis() - startZeit;
            zaehleAufruf(aufrufDauer, erfolgreich, fachlichErfolgreich);
        }
    }

    /**
     * Checks whether the return object contained business errors. The return object must contain a collection
     * annotated with @SubjectErrorList.
     *
     * @param result The return object of the call.
     * @return true on errors, false otherwise.
     */
    private boolean sindFachlicheFehlerVorhanden(final Object result) {
        return pruefeObjektAufFehler(result, null, 1);
    }

    /**
     * Searches a class for non-null error objects or non-empty error collections.
     * Error objects are annotated with {link SpecializedError}.
     * <p>
     * Searches superclasses & child object structures recursively as well.
     *
     * @param result The object
     * @param clazz  The class of the object to be searched (optional). Can be left empty at
     *               start, but can be used to check for superclasses of an object.
     * @param tiefe  tiefe specifies the current depth of the call. Must be incremented when traversing the
     *               class structure down.
     * @return <code>true</code> if error found, otherwise <code>false</code>.
     */
    boolean pruefeObjektAufFehler(final Object result, Class<?> clazz, int tiefe) {
        boolean fehlerGefunden = false;
        Class<?> clazzToScan = clazz;
        // If no class is passed, determine yourself
        if (clazzToScan == null) {
            clazzToScan = result.getClass();
        }

        // If max. depth reached, do not check further
        if (tiefe > MAXTIEFE) {
            LOGISY.trace("Max. Tiefe erreicht, prüfe nicht weiter auf fachliche Fehler");
            return false;
        }

        Field[] fields = clazzToScan.getDeclaredFields();

        LOGISY.trace("{} Analysiere Objekt {} (Klasse {}) {} Felder gefunden.",
                zeigeTiefeAn(tiefe), result.toString(), clazzToScan.getSimpleName(), fields.length);

        for (Field field : fields) {
            if (!ClassUtils.isPrimitiveOrWrapper(field.getType()) && !field.getType().isEnum()) {
                LOGISY.trace("{} {}.{}, Type {}", zeigeTiefeAn(tiefe), clazzToScan.getSimpleName(),
                        field.getName(), field.getType().getSimpleName());
                field.setAccessible(true);
                try {
                    // Check individual class fields (non-collection) for annotated type and presence
                    if (!Collection.class.isAssignableFrom(field.getType())) {
                        if (field.get(result) != null) {
                            Object fieldObject = field.get(result);
                            if (fieldObject.getClass().isAnnotationPresent(FachlicherFehler.class)) {
                                // Subject error object found
                                return true;
                            }

                            // If no string, then recursively check object structure
                            if (fieldObject.getClass() != String.class) {
                                fehlerGefunden =
                                        pruefeObjektAufFehler(fieldObject, null, tiefe + 1) || fehlerGefunden;
                            }
                        }
                    } else {
                        // Collection, check if professional error list
                        ParameterizedType type = (ParameterizedType) field.getGenericType();
                        Class<?> collectionTypeArgument = (Class<?>) type.getActualTypeArguments()[0];
                        if (collectionTypeArgument.isAnnotationPresent(FachlicherFehler.class)) {
                            // Is error list, check if not empty
                            Collection<?> collection = (Collection<?>) field.get(result);
                            if (collection != null && !collection.isEmpty()) {
                                // Professional errors found in error list
                                return true;
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Do nothing, field is ignored
                    LOGISY.debug("Feldzugriffsfehler: {}", e.getMessage());
                }
            }
        }

        // Check the class hierarchy recursively upwards
        if (clazzToScan.getSuperclass() != null && !clazzToScan.getSuperclass().equals(Object.class)) {
            LOGISY.trace("{}> Climb up class hierarchy! Source {}, Target {}", zeigeTiefeAn(tiefe),
                    clazzToScan.getSimpleName(), clazzToScan.getSuperclass());
            fehlerGefunden =
                    // Call with same depth, as inheritance is passed upwards
                    pruefeObjektAufFehler(result, clazzToScan.getSuperclass(), tiefe) || fehlerGefunden;
        }

        return fehlerGefunden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        LOGISY.debug("ServiceStatistikMBean "
                + (this.erweiterteFachlicheFehlerpruefung ? " mit erweiterter fachlicher Fehlerprüfung " : "")
                + " initialisiert.");
    }

    private static String zeigeTiefeAn(final int tiefe) {
        final char anzeiger = '-';
        final char[] buffer = new char[tiefe];
        for (int i = tiefe - 1; i >= 0; i--) {
            buffer[i] = anzeiger;
        }
        return new String(buffer);
    }

}
