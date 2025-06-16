package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationFactory;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.LogHelper;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * HTTP-InvokerClientInterceptor to generate IsyFact compliant logging entries.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class IsyHttpInvokerClientInterceptor extends HttpInvokerClientInterceptor {

    /**
     * Logger.
     */
    private static final IsyLogger LOGGER = IsyLoggerFactory.getLogger(IsyHttpInvokerClientInterceptor.class);

    /**
     * Helper for creating Logentries.
     */
    private LogHelper logHelper = new LogHelper(false, false, true, false, false, 0);

    /**
     * Name of the remote system that is being called.
     */
    private String remoteSystemName;

    /**
     * Resolver for AufrufKontextTo from Parameter-Lists.
     */
    private AufrufKontextToResolver aufrufKontextToResolver;

    /**
     * {@inheritDoc}
     * <p>
     * When called, a new correlation ID is always created and added to the existing correlation ID of the
     * calling context.
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String korrelationsId = UUID.randomUUID().toString();

        Method methode = methodInvocation.getMethod();

        LOGGER.debug("Erzeuge neue Korrelations-ID {}", korrelationsId);
        MdcHelper.pushKorrelationsId(korrelationsId);

        Optional<AufrufKontextTo> aufrufKontextToOptional =
                aufrufKontextToResolver.leseAufrufKontextTo(methodInvocation.getArguments());
        aufrufKontextToOptional.ifPresent(
                aufrufKontextTo -> aufrufKontextTo.setKorrelationsId(MdcHelper.liesKorrelationsId())
        );

        // Logging call of remote system
        logHelper.loggeNachbarsystemAufruf(LOGGER, methode, remoteSystemName, getServiceUrl());
        long startzeit = 0;
        boolean aufrufErfolgreich = false;
        try {
            startzeit = logHelper.ermittleAktuellenZeitpunkt();
            Object ergebnis = super.invoke(methodInvocation);

            // call was executed without exceptions
            aufrufErfolgreich = true;
            return ergebnis;

        } finally {
            long endezeit = logHelper.ermittleAktuellenZeitpunkt();
            long dauer = endezeit - startzeit;
            logHelper.loggeNachbarsystemErgebnis(LOGGER, methode, remoteSystemName, getServiceUrl(),
                    aufrufErfolgreich);
            logHelper.loggeNachbarsystemDauer(LOGGER, methode, dauer, remoteSystemName, getServiceUrl(),
                    aufrufErfolgreich);

            MdcHelper.entferneKorrelationsId();

            aufrufKontextToOptional.ifPresent(
                    aufrufKontextTo -> aufrufKontextTo.setKorrelationsId(MdcHelper.liesKorrelationsId()));
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (remoteSystemName == null) {
            throw new IllegalArgumentException("Property 'remoteSystemName' is required");
        }
        if (aufrufKontextToResolver == null) {
            throw new IllegalArgumentException("Property 'aufrufKontextToResolver' is required");
        }
    }

    /**
     * Sets value of attribute 'remoteSystemName'.
     *
     * @param remoteSystemName New value of the attribute.
     */
    public void setRemoteSystemName(String remoteSystemName) {
        this.remoteSystemName = remoteSystemName;
    }

    /**
     * Sets value of attribute 'logHelper'.
     *
     * @param logHelper New value of the attribute.
     */
    public void setLogHelper(LogHelper logHelper) {
        this.logHelper = logHelper;
    }

    /**
     * Sets aufrufKontextToResolver. Resolver for reading AufrufKontextTo from Parameterlists.
     * Standard implementation is {@link AufrufKontextToResolver}
     *
     * @param aufrufKontextToResolver New value of aufrufKontextToResolver
     */
    // Autowiring was used to provide an easier transition from static resolving of AufrufKontextTo.
    // Try to autowire as it requires users of the class to make less changes to their config
    // and this class is typically used as a Spring-Bean
    @Autowired
    public void setAufrufKontextToResolver(AufrufKontextToResolver aufrufKontextToResolver) {
        this.aufrufKontextToResolver = aufrufKontextToResolver;
    }

    /**
     * Used to fill the request with further information.
     */
    @Override
    @Autowired(required = false)
    public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        super.setRemoteInvocationFactory(remoteInvocationFactory);
    }

    public AufrufKontextToResolver getAufrufKontextToResolver() {
        return aufrufKontextToResolver;
    }

}
