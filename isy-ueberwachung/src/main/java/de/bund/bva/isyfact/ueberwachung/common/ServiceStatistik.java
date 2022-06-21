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
package de.bund.bva.isyfact.ueberwachung.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.serviceapi.annotations.FachlicherFehler;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

/**
 * This class implements a monitoring bean for services. It provides the monitoring options,
 * which each service must provide according to IsyFact.
 */
public class ServiceStatistik implements MethodInterceptor, InitializingBean {
    /**
     * Default value for number of searches used to calculate the average.
     */
    private static final int ANZAHL_AUFRUFE_FUER_DURCHSCHNITT = 10;

    /**
     * Logger.
     */
    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(ServiceStatistik.class);

    /**
     * The maximum depth for recursive checking for functional errors in extended functional
     * Error checking.
     */
    private static final int MAXTIEFE = 10;

    /**
     * Specifies whether the return object structures should be checked for business errors. May
     * have an impact on performance.
     */
    private boolean fachlicheFehlerpruefung;

    /**
     * Duration of the last search calls (in milliseconds).
     */
    private final List<Long> letzteSuchdauern = new LinkedList<>();

    /**
     * Flag for the minute in which values of the last minute were determined.
     */
    private volatile LocalDateTime letzteMinute = DateTimeUtil.localDateTimeNow();

    /**
     * Number of non-error calls made in the minute called 'last minute'.
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
     * Number of calls made in the current minute in which a techinical error
     * occurred.
     */
    private volatile int anzahlFehlerAktuelleMinute;

    /**
     * The number of technical errors in the last minute. A technical error is present if either
     * an exception of type PlusBusinessException was thrown or the returned error list contained
     * entries.
     */
    private volatile int anzahlFachlicheFehlerLetzteMinute;

    /**
     * The number of technical errors in the current minute. A technical error is present if
     * either an exception of type PlusBusinessException was thrown or the returned
     * error list contained entries.
     */
    private volatile int anzahlFachlicheFehlerAktuelleMinute;

    public ServiceStatistik(MeterRegistry meterRegistry, Tags tags) {
        Gauge.builder("anzahlAufrufe.LetzteMinute", this, ServiceStatistik::getAnzahlAufrufeLetzteMinute)
                .tags(tags)
                .description("Liefert die Anzahl der nicht fehlerhaften Aufrufe in der letzten Minute")
                .register(meterRegistry);

        Gauge.builder("anzahlFehler.LetzteMinute", this, ServiceStatistik::getAnzahlFehlerLetzteMinute)
                .tags(tags)
                .description("Liefert die Anzahl der fehlerhaften Aufrufe in der letzten Minute")
                .register(meterRegistry);

        Gauge.builder("anzahlFachlicheFehler.LetzteMinute", this, ServiceStatistik::getAnzahlFachlicheFehlerLetzteMinute)
                .tags(tags)
                .description("Liefert die Anzahl der fachlich fehlerhaften Aufrufe in der letzten Minute")
                .register(meterRegistry);

        Gauge.builder("durchschnittsDauer.LetzteAufrufe", this, ServiceStatistik::getDurchschnittsDauerLetzteAufrufe)
                .tags(tags)
                .description("Liefert die durchschnittliche Dauer der letzten 10 Aufrufe in ms")
                .register(meterRegistry);
    }

    /**
     * Calculates the current minute of the system time.
     *
     * @return The minute part of the current system time.
     */
    private static LocalDateTime getAktuelleMinute() {
        return DateTimeUtil.localDateTimeNow().truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * Specifies whether the return object structures should be checked for technical errors. May
     * have performance implications.
     *
     * @param fachlicheFehlerpruefung {@code true} if the return object structure should be checked for technical errors, otherwise {@code false}.
     */
    public void setFachlicheFehlerpruefung(boolean fachlicheFehlerpruefung) {
        this.fachlicheFehlerpruefung = fachlicheFehlerpruefung;
    }

    /**
     * This method counts a call to the component for statistics.
     * Needed for the statistics is the specification,
     * the duration and whether the call failed.
     *
     * @param dauer               The duration of the call in milliseconds.
     * @param erfolgreich         Indicator whether the call was successful ({@code true}) or a technical error
     *                            occurred ({@code false}).
     * @param fachlichErfolgreich Flag indicating whether the call was technical successful ({@code true}) or a technical * error occurred ({@code false}).
     */
    public synchronized void zaehleAufruf(long dauer, boolean erfolgreich, boolean fachlichErfolgreich) {
        aktualisiereZeitfenster();
        anzahlAufrufeAktuelleMinute++;

        if (!erfolgreich) {
            anzahlFehlerAktuelleMinute++;
        }

        if (!fachlichErfolgreich) {
            anzahlFachlicheFehlerAktuelleMinute++;
        }

        if (letzteSuchdauern.size() == ANZAHL_AUFRUFE_FUER_DURCHSCHNITT) {
            letzteSuchdauern.remove(ANZAHL_AUFRUFE_FUER_DURCHSCHNITT - 1);
        }

        letzteSuchdauern.add(0, dauer);
    }

    /**
     * This method causes the time window for the counters of errors and calls to be updated in the current
     * and last minute. If a minute has elapsed, the values of the current * minute are copied to those of the counters for the last minute.
     * The counters for the current minute are set to 0. The method ensures that this operation can be performed only once per minute.
     */
    private synchronized void aktualisiereZeitfenster() {
        LocalDateTime aktuelleMinute = getAktuelleMinute();
        if (!aktuelleMinute.isEqual(letzteMinute)) {
            if (ChronoUnit.MINUTES.between(letzteMinute, aktuelleMinute) > 1) {
                // no last minute infos
                anzahlAufrufeLetzteMinute = 0;
                anzahlFehlerLetzteMinute = 0;
                anzahlFachlicheFehlerLetzteMinute = 0;
            } else {
                anzahlAufrufeLetzteMinute = anzahlAufrufeAktuelleMinute;
                anzahlFehlerLetzteMinute = anzahlFehlerAktuelleMinute;
                anzahlFachlicheFehlerLetzteMinute = anzahlFachlicheFehlerAktuelleMinute;
            }

            anzahlAufrufeAktuelleMinute = 0;
            anzahlFehlerAktuelleMinute = 0;
            anzahlFachlicheFehlerAktuelleMinute = 0;
            letzteMinute = aktuelleMinute;
        }
    }

    /**
     * Returns the average duration of the last 10 calls. Defines a method for the
     * management interface of this MBean.
     *
     * @return The average duration of the last 10 calls in ms.
     */
    private long getDurchschnittsDauerLetzteAufrufe() {
        long result = 0;
        if (!letzteSuchdauern.isEmpty()) {
            // Kopiere Liste um konkurrierende Änderungen zu vermeiden
            // Explizit keine Synchronisierung, um die Anwendungsperformance
            // nicht zu verschlechtern.
            Long[] dauern = letzteSuchdauern.toArray(new Long[0]);
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
    private int getAnzahlAufrufeLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlAufrufeLetzteMinute;
    }

    /**
     * Returns the number of calls counted in the last minute where an error occurred.
     * Defines a method for the management interface of this MBean.
     *
     * @return The number of calls counted in the last minute where an error occurred.
     */
    private int getAnzahlFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlFehlerLetzteMinute;
    }

    /**
     * Returns the number of calls counted in the last minute in which a technical error
     * occurred.
     *
     * @return The number of calls counted in the last minute where a technical error * occurred.
     */
    private int getAnzahlFachlicheFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlFachlicheFehlerLetzteMinute;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Instant start = DateTimeUtil.getClock().instant();
        boolean erfolgreich = false;
        boolean fachlichErfolgreich = false;

        try {
            Object result = invocation.proceed();
            erfolgreich = true;
            fachlichErfolgreich = !fachlicheFehlerpruefung || !sindFachlicheFehlerVorhanden(result);
            return result;
        } catch (PlisBusinessToException t) {
            // BusinessExceptions are not counted as technical errors.
            erfolgreich = true;
            throw t;
        } finally {
            long aufrufDauer = ChronoUnit.MILLIS.between(start, DateTimeUtil.getClock().instant());
            zaehleAufruf(aufrufDauer, erfolgreich, fachlichErfolgreich);
        }
    }

    /**
     * Checks whether the return object contained business errors. The return object must contain a collection
     * annotated with @SubjectErrorList.
     *
     * @param result The return object of the call.
     * @return true if errors, false otherwise.
     */
    private static boolean sindFachlicheFehlerVorhanden(final Object result) {
        return pruefeObjektAufFehler(result, null, 1);
    }

    /**
     * Searches a class for non-null error objects or non-empty error collections.
     * Error objects are annotated with {link FachlicherFehler}.
     * <p>
     * Searches superclasses & child object structures recursively as well.
     *
     * @param result The object
     * @param clazz  The class of the object to be searched (optional). Can be left empty at
     *               start, but can be used to check for superclasses of an object.
     * @param tiefe  tiefe  Specifies the current depth of the call. Must be incremented when traversing the
     *               class structure downwards.
     * @return {@code true} if error found, otherwise {@code false}.
     */
    static boolean pruefeObjektAufFehler(final Object result, Class<?> clazz, int tiefe) {
        // If max. depth reached, do not check further
        if (tiefe > MAXTIEFE) {
            LOGISY.trace("Max. Tiefe erreicht, prüfe nicht weiter auf fachliche Fehler");
            return false;
        }

        // If no class is passed, determine yourself
        Class<?> clazzToScan = clazz;
        if (clazzToScan == null) {
            clazzToScan = result.getClass();
        }

        List<Field> objectFields = Arrays.stream(clazzToScan.getDeclaredFields())
                .filter(field -> !ClassUtils.isPrimitiveOrWrapper(field.getType()) && !field.getType().isEnum())
                .collect(Collectors.toList());

        LOGISY.trace("{} Analysiere Objekt {} (Klasse {}) {} Felder gefunden.",
                String.join("", Collections.nCopies(tiefe, "-")), result.toString(), clazzToScan.getSimpleName(),
                objectFields.size());

        boolean fehlerGefunden = false;

        for (Field field : objectFields) {
            LOGISY.trace("{} {}.{}, Type {}", String.join("", Collections.nCopies(tiefe, "-")),
                    clazzToScan.getSimpleName(), field.getName(), field.getType().getSimpleName());
            field.setAccessible(true);
            try {
                // Check individual class fields (non-collection) for annotated type and presence
                if (fieldIsNotACollection(field)) {
                    Object fieldObject = field.get(result);

                    if (fieldObject != null) {
                        if (fieldObject.getClass().isAnnotationPresent(FachlicherFehler.class)) {
                            // Subject error object found
                            return true;
                        }

                        // If no string, then recursively check object structure
                        if (fieldObject.getClass() != String.class) {
                            fehlerGefunden = pruefeObjektAufFehler(fieldObject, null, tiefe + 1) || fehlerGefunden;
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

        // Check the class hierarchy recursively upwards
        if (typeHasSuperClass(clazzToScan)) {
            LOGISY.trace("{}> Climb up class hierarchy! Source {}, Target {}",
                    String.join("", Collections.nCopies(tiefe, "-")), clazzToScan.getSimpleName(),
                    clazzToScan.getSuperclass());
            fehlerGefunden =
                    // Call with same depth, as inheritance is passed upwards
                    pruefeObjektAufFehler(result, clazzToScan.getSuperclass(), tiefe) || fehlerGefunden;
        }

        return fehlerGefunden;
    }

    private static boolean typeHasSuperClass(Class clazz) {
        return clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class);
    }

    private static boolean fieldIsNotACollection(Field field) {
        return !Collection.class.isAssignableFrom(field.getType());
    }

    @Override
    public void afterPropertiesSet() {
        if (fachlicheFehlerpruefung) {
            LOGISY.debug("ServiceStatistik mit erweiterter fachlicher Fehlerprüfung initialisiert.");
        } else {
            LOGISY.debug("ServiceStatistik initialisiert.");
        }
    }
}
