package de.bund.bva.isyfact.logging.util;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

/**
 * AspectJ aspect for logging AWF and AFU classes that are not public and therefore
 * cannot be logged through Spring AOP.
 */
@Aspect
public class PerformanceLoggingAspect {

    /** Helper for writing duration log entries. */
    private LogHelper logHelper = new LogHelper(false, false, true, false,
        false, 0, LogHelper.erstelleStandardKonverter());

    @Around("awfUndAfuKlassen()")
    public Object loggeDauer(ProceedingJoinPoint pjp) throws Throwable {
        Class<?> klasse = pjp.getTarget().getClass();
        Method methode = ((MethodSignature) pjp.getSignature()).getMethod();

        IsyLogger logger = IsyLoggerFactory.getLogger(klasse);

        long startzeit = logHelper.ermittleAktuellenZeitpunkt();

        try {
            Object ergebnis = pjp.proceed();
            long dauer = ermittleDauer(startzeit);
            logHelper.loggeDauer(logger, methode, dauer, true);
            return ergebnis;
        } catch (Throwable t) {
            long dauer = ermittleDauer(startzeit);
            logHelper.loggeDauer(logger, methode, dauer, false);
            throw t;
        }
    }

    @Pointcut("execution(* * ..core..Awf*.*(..)) || execution(* * ..core..Afu*.*(..))")
    public void awfUndAfuKlassen() { }

    /**
     * Internal helper method to calculate the duration of an invocation based on its start time.
     *
     * @param startzeit
     *            start time of the invocation.
     * @return invocation duration (current time - start time).
     */
    private long ermittleDauer(long startzeit) {
        long endezeit = logHelper.ermittleAktuellenZeitpunkt();
        return endezeit - startzeit;
    }
}
