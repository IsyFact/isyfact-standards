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
package de.bund.bva.isyfact.ueberwachung.metrics.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.metrics.ServiceStatistik;


/**
 * Default implementation of {@link ServiceStatistik} which intercepts service method calls to gather the statistics.
 */
public class DefaultServiceStatistik implements ServiceStatistik, MethodInterceptor, InitializingBean {
    /**
     * Default value for number of searches used to calculate the average.
     */
    private static final int ANZAHL_AUFRUFE_FUER_DURCHSCHNITT = 10;

    /**
     * Logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(DefaultServiceStatistik.class);

    /**
     * Specifies whether the return object structures should be checked for business errors. May
     * have an impact on performance.
     */
    @Deprecated
    private boolean businessFehlerpruefung;

    /**
     * Duration of the last search calls (in milliseconds).
     */
    private final ConcurrentLinkedDeque<Duration> letzteSuchdauern = new ConcurrentLinkedDeque<>();

    /**
     * Number of calls made.
     */
    private final AtomicLong anzahlAufrufe = new AtomicLong();

    /**
     * The number of technical errors.
     * <p>
     * A technical error occurs if a {@link TechnicalException} was thrown.
     */
    private final AtomicLong anzahlTechnicalExceptions = new AtomicLong();

    /**
     * The number of business errors.
     * <p>
     * A business error occurs if a {@link BusinessException} was thrown.
     */
    private final AtomicLong anzahlBusinessExceptions = new AtomicLong();


    /**
     * Flag for the minute in which values of the last minute were determined.
     */
    private final AtomicReference<LocalDateTime> letzteMinute = new AtomicReference<>(DateTimeUtil.localDateTimeNow());

    /**
     * Number of non-error calls made in the minute called 'last minute'.
     */
    private final AtomicInteger anzahlAufrufeLetzteMinute = new AtomicInteger();

    /**
     * Number of calls made in the current minute.
     */
    private final AtomicInteger anzahlAufrufeAktuelleMinute = new AtomicInteger();

    /**
     * The number of technical errors in the last minute.
     * <p>
     * A technical error occurs if a {@link TechnicalException} was thrown.
     */
    private final AtomicInteger anzahlTechnicalExceptionsLetzteMinute = new AtomicInteger();

    /**
     * The number of technical errors in the current minute.
     * <p>
     * A technical error occurs if a {@link TechnicalException} was thrown.
     */
    private final AtomicInteger anzahlTechnicalExceptionsAktuelleMinute = new AtomicInteger();

    /**
     * The number of business errors in the last minute.
     * <p>
     * A business error occurs if a {@link BusinessException} was thrown.
     */
    private final AtomicInteger anzahlBusinessExceptionsLetzteMinute = new AtomicInteger();

    /**
     * The number of business errors in the current minute.
     * <p>
     * A business error occurs if a {@link BusinessException} was thrown.
     */
    private final AtomicInteger anzahlBusinessExceptionsAktuelleMinute = new AtomicInteger();

    /**
     * Micrometer tags.
     */
    private final String[] tags;

    /**
     * Constructor.
     *
     * @param tags an even number of arguments representing key/value pairs of tags; e.g.:
     *             {@code ["anwendung", "de.bund.bva.isyfact.myapp", "servicestatistik", "myservice"]}.
     */
    public DefaultServiceStatistik(String... tags) {
        this.tags = tags.clone();
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
     * @param businessFehlerpruefung {@code true} if the return object structure should be checked for technical errors, otherwise {@code false}.
     */
    @Deprecated
    public void setBusinessFehlerpruefung(boolean businessFehlerpruefung) {
        this.businessFehlerpruefung = businessFehlerpruefung;
    }

    @Override
    public String[] getTags() {
        return tags.clone();
    }

    /**
     * This method counts a call to the component for statistics.
     *
     * @param dauer                  The duration of the call
     * @param technicallySuccessful  flag, if the call was successful ({@code true}) or a technical error occurred ({@code false}).
     * @param functionallySuccessful Flag, if the call was successful ({@code true}) or a business error occurred ({@code false}).
     */
    public void zaehleAufruf(Duration dauer, boolean technicallySuccessful, boolean functionallySuccessful) {
        synchronized (anzahlAufrufeAktuelleMinute) {
            aktualisiereZeitfenster();
            anzahlAufrufe.incrementAndGet();
            anzahlAufrufeAktuelleMinute.incrementAndGet();

            if (!technicallySuccessful) {
                anzahlTechnicalExceptions.incrementAndGet();
                anzahlTechnicalExceptionsAktuelleMinute.incrementAndGet();
            }

            if (!functionallySuccessful) {
                anzahlBusinessExceptions.incrementAndGet();
                anzahlBusinessExceptionsAktuelleMinute.incrementAndGet();
            }
        }
        synchronized (letzteSuchdauern) {
            if (letzteSuchdauern.size() == ANZAHL_AUFRUFE_FUER_DURCHSCHNITT) {
                letzteSuchdauern.removeLast();
            }
            letzteSuchdauern.addFirst(dauer);
        }
    }

    /**
     * This method causes the time window for the counters of errors and calls to be updated in the current
     * and last minute. If a minute has elapsed, the values of the current * minute are copied to those of the counters for the last minute.
     * The counters for the current minute are set to 0. The method ensures that this operation can be performed only once per minute.
     */
    private void aktualisiereZeitfenster() {
        synchronized (letzteMinute) {
            LocalDateTime aktuelleMinute = getAktuelleMinute();
            LocalDateTime letzteMinute = this.letzteMinute.get();
            if (!aktuelleMinute.isEqual(letzteMinute)) {
                if (ChronoUnit.MINUTES.between(letzteMinute, aktuelleMinute) > 1) {
                    // no last minute infos
                    anzahlAufrufeLetzteMinute.set(0);
                    anzahlTechnicalExceptionsLetzteMinute.set(0);
                    anzahlBusinessExceptionsLetzteMinute.set(0);
                } else {
                    anzahlAufrufeLetzteMinute.set(anzahlAufrufeAktuelleMinute.get());
                    anzahlTechnicalExceptionsLetzteMinute.set(anzahlTechnicalExceptionsAktuelleMinute.get());
                    anzahlBusinessExceptionsLetzteMinute.set(anzahlBusinessExceptionsAktuelleMinute.get());
                }

                anzahlAufrufeAktuelleMinute.set(0);
                anzahlTechnicalExceptionsAktuelleMinute.set(0);
                anzahlBusinessExceptionsAktuelleMinute.set(0);
                this.letzteMinute.set(aktuelleMinute);
            }
        }
    }

    @Override
    public Duration getDurchschnittsDauerLetzteAufrufe() {
        Duration result = Duration.ZERO;
        if (!letzteSuchdauern.isEmpty()) {
            // Copy List to avoid concurrent changes
            // Explicitly no synchronization so as not to affect performance
            Duration[] dauern = letzteSuchdauern.toArray(new Duration[0]);
            for (Duration dauer : dauern) {
                result = result.plus(dauer);
            }
            result = result.dividedBy(dauern.length);
        }
        return result;
    }

    @Override
    public long getAnzahlAufrufe() {
        return anzahlAufrufe.get();
    }

    @Override
    public long getAnzahlTechnicalExceptions() {
        return anzahlTechnicalExceptions.get();
    }

    @Override
    public long getAnzahlBusinessExceptions() {
        return anzahlBusinessExceptions.get();
    }

    @Override
    public int getAnzahlAufrufeLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlAufrufeLetzteMinute.get();
    }

    @Override
    public int getAnzahlFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlTechnicalExceptionsLetzteMinute.get();
    }

    @Override
    public int getAnzahlFachlicheFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlBusinessExceptionsLetzteMinute.get();
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Instant start = DateTimeUtil.getClock().instant();
        boolean technicallySuccessful = false;
        boolean functionallySuccessful = false;

        try {
            Object result = invocation.proceed();
            technicallySuccessful = true;
            functionallySuccessful = true;
            return result;
        } catch (BusinessException be) {
            // BusinessExceptions are considered only as business errors.
            // Redundant assignment of values to clarify exception-handling.
            technicallySuccessful = true;
            functionallySuccessful = false;
            throw be;
        } catch (TechnicalException te) {
            // TechnicalExceptions are considered only as technical errors.
            technicallySuccessful = false;
            functionallySuccessful = true;
            throw te;
        } catch (Throwable e) {
            // All other exceptions are considered as technical and business errors.
            technicallySuccessful = false;
            functionallySuccessful = false;
            throw e;
        } finally {
            Duration aufrufDauer = Duration.between(start, DateTimeUtil.getClock().instant());
            zaehleAufruf(aufrufDauer, technicallySuccessful, functionallySuccessful);
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (businessFehlerpruefung) {
            LOG.debug("ServiceStatistik mit erweiterter fachlicher Fehlerprüfung initialisiert.");
        } else {
            LOG.debug("ServiceStatistik initialisiert.");
        }
    }
}
