package de.bund.bva.isyfact.ueberwachung.metrics.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

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
        synchronized (anzahlAufrufe) {
            anzahlAufrufe.incrementAndGet();

            if (!technicallySuccessful) {
                anzahlTechnicalExceptions.incrementAndGet();
            }

            if (!functionallySuccessful) {
                anzahlBusinessExceptions.incrementAndGet();
            }
        }
        synchronized (letzteSuchdauern) {
            if (letzteSuchdauern.size() == ANZAHL_AUFRUFE_FUER_DURCHSCHNITT) {
                letzteSuchdauern.removeLast();
            }
            letzteSuchdauern.addFirst(dauer);
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
