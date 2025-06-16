package de.bund.bva.isyfact.serviceapi.core.aufrufkontext;

import java.util.Optional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

import ma.glasnost.orika.MapperFacade;

/**
 * An interceptor that provides the calling context based on a mapping.
 * @param <T> Implementation of AufrufKontext that should be mapped to.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class StelltAllgemeinenAufrufKontextBereitInterceptor<T extends AufrufKontext> implements
    MethodInterceptor, Ordered {

    /** Logger. */
    private static final IsyLogger LOGISY = IsyLoggerFactory
        .getLogger(StelltAllgemeinenAufrufKontextBereitInterceptor.class);

    /**
     * Default order for interceptor execution if no other is set.
     * Must be executed before GesichertInterceptor.
     */
    private static final int DEFAULT_ORDER = 9_000;

    /** Mapper. */
    private final MapperFacade mapper;

    /**
     * Access the AufrufKontextFactory to create the application specific AufrufKontext.
     */
    private final AufrufKontextFactory<T> aufrufKontextFactory;

    /** Access to AufrufKontextVerwalter, to set the AufrufKontext. */
    private final AufrufKontextVerwalter<T> aufrufKontextVerwalter;

    /** Resolver for AufrufKontextTo from parameter list. */
    private final AufrufKontextToResolver aufrufKontextToResolver;

    /** Order for interceptor execution. */
    private int order = DEFAULT_ORDER;

    public StelltAllgemeinenAufrufKontextBereitInterceptor(MapperFacade mapper,
        AufrufKontextFactory<T> aufrufKontextFactory,
        AufrufKontextVerwalter<T> aufrufKontextVerwalter,
        AufrufKontextToResolver aufrufKontextToResolver) {
        this.mapper = mapper;
        this.aufrufKontextFactory = aufrufKontextFactory;
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
        this.aufrufKontextToResolver = aufrufKontextToResolver;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        T alterAufrufKontext = aufrufKontextVerwalter.getAufrufKontext();

        Optional<AufrufKontextTo> aufrufKontextToOptional =
            aufrufKontextToResolver.leseAufrufKontextTo(invocation.getArguments());

        if (!aufrufKontextToOptional.isPresent()) {
            LOGISY.debug("Es wurde kein AufrufKontext uebermittelt.");
        }

        T aufrufKontext = aufrufKontextToOptional
            .map(this::mapToGenericAufrufKontext)
            .orElse(null);

        aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);

        try {
            return invocation.proceed();
        } finally {
            // Reset to old AufrufKontext
            aufrufKontextVerwalter.setAufrufKontext(alterAufrufKontext);
        }
    }

    private T mapToGenericAufrufKontext(AufrufKontextTo aufrufKontextTo) {
        T aufrufKontext = aufrufKontextFactory.erzeugeAufrufKontext();
        mapper.map(aufrufKontextTo, aufrufKontext);
        aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);
        return aufrufKontext;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
