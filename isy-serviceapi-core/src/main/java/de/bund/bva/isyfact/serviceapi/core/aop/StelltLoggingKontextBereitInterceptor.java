package de.bund.bva.isyfact.serviceapi.core.aop;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.AufrufKontextToResolver;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * This aspect ensures that the logging context is automatically set in service methods.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class StelltLoggingKontextBereitInterceptor implements MethodInterceptor {

    /** Logger. */
    private static final IsyLogger LOG =
        IsyLoggerFactory.getLogger(StelltLoggingKontextBereitInterceptor.class);

    /** Resolver for reading AufrufKontextTo from parameter list. */
    private final AufrufKontextToResolver aufrufKontextToResolver;

    public StelltLoggingKontextBereitInterceptor(
        AufrufKontextToResolver aufrufKontextToResolver) {
        this.aufrufKontextToResolver = aufrufKontextToResolver;
    }

    /**
     * This aspect ensures that a correlation ID is generated if none is set in the AufrufKontext
     * and it is specified in the annotation that no AufrufKontext is passed as a parameter.
     * The correlation ID is set in the logging context before the actual call
     * and is automatically removed afterwards.
     *
     * @param invocation the method call
     * @return result of the called method
     * @throws Throwable if an exception occurred in the called method
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // The correlation ID is used to uniquely identify service calls.
        String korrelationsId;

        // determine if StelltLoggingKontextBereit annotation was set on Method
        Class<?> targetClass =
                invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null;
        StelltLoggingKontextBereit stelltLoggingKontextBereit =
            ermittleStelltLoggingKontextBereitAnnotation(invocation.getMethod(), targetClass);

        //is StelltLoggingKontextBereit present?
        if (stelltLoggingKontextBereit != null) {
            korrelationsId =
                determineKorrelationsIdAccordingToAnnotation(stelltLoggingKontextBereit, invocation);
        } else {
            korrelationsId = determineKorrelationsIdDefaultHandling(invocation);
        }

        try {
            LOG.debug("Setze Korrelations-ID: " + korrelationsId);
            MdcHelper.pushKorrelationsId(korrelationsId);
            if (stelltLoggingKontextBereit == null || stelltLoggingKontextBereit.nutzeAufrufKontext()) {
                Optional<AufrufKontextTo> aufrufKontextToOptional =
                        aufrufKontextToResolver.leseAufrufKontextTo(invocation.getArguments());
                aufrufKontextToOptional.ifPresent(aufrufKontextTo -> {
                    if (aufrufKontextTo.getKorrelationsId() == null || aufrufKontextTo.getKorrelationsId().isEmpty()) {
                        aufrufKontextTo.setKorrelationsId(korrelationsId);
                    }
                });
            }
            return invocation.proceed();
        } finally {
            // remove correlation id from MDC after service call
            MdcHelper.entferneKorrelationsIds();
        }
    }

    /**
     * Get Correlation-ID according to StelltLoggingKontextBereit-annotation.
     * If "nutzeAufrufKontext" is false, a new Korrelation-ID will be generated.
     * If "nutzeAufrufKontext" is true, the AufrufKontextTo will be read and the Korrelations-ID will be set accordingly.
     *
     * @throws IllegalArgumentException if "nutzeAufrufKontext" is true, but no AufrufKontext was found.
     */
    private String determineKorrelationsIdAccordingToAnnotation(
        StelltLoggingKontextBereit stelltLoggingKontextBereit, MethodInvocation invocation) {

        if (stelltLoggingKontextBereit.nutzeAufrufKontext()) {
            Optional<AufrufKontextTo> aufrufKontextToOptional =
                aufrufKontextToResolver.leseAufrufKontextTo(invocation.getArguments());

            if (!aufrufKontextToOptional.isPresent()) {
                throw new IllegalArgumentException(
                    "Die Annotation StelltLoggingKontextBereit gibt an, dass die Methode "
                        + invocation.getMethod()
                        + " einen Aufrufkontext als Parameter enthält. Dieser Parameter ist aber null oder nicht vorhanden.");
            }

            return aufrufKontextToOptional
                .map(AufrufKontextTo::getKorrelationsId)
                .filter(kor -> !kor.isEmpty())
                .orElseGet(StelltLoggingKontextBereitInterceptor::createNewCorrelationID);
        } else {
            return createNewCorrelationID();
        }
    }

    /**
     * Default handling if no Annotation is present.
     * Try to read from Correlation ID from AufrufKontextTo, if none is available create new one.
     */
    public String determineKorrelationsIdDefaultHandling(MethodInvocation invocation) {
        Optional<AufrufKontextTo> aufrufKontextToOptional =
            aufrufKontextToResolver.leseAufrufKontextTo(invocation.getArguments());

        return aufrufKontextToOptional
            .map(AufrufKontextTo::getKorrelationsId)
            .filter(kor -> !kor.isEmpty())
            .orElseGet(StelltLoggingKontextBereitInterceptor::createNewCorrelationID);
    }

    /** Create correlation ID. */
    private static String createNewCorrelationID() {
        LOG.debug(
            "Erzeuge neue Korrelations-ID.");
        return UUID.randomUUID().toString();
    }

    /**
     * Determines the StelltLoggingKontextBereit annotation.
     *
     * @param method      called method
     * @param targetClass class, on which the method was called
     * @return Annotation StelltLoggingKontextBereit
     */
    private StelltLoggingKontextBereit ermittleStelltLoggingKontextBereitAnnotation(Method method,
        Class<?> targetClass) {

        // Strategy for determining the annotation is taken from AnnotationTransactionAttributeSource.

        // Ignore CGLIB subclasses - introspect the actual user class.
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        StelltLoggingKontextBereit stelltLoggingKontextBereit =
            specificMethod.getAnnotation(StelltLoggingKontextBereit.class);
        if (stelltLoggingKontextBereit != null) {
            return stelltLoggingKontextBereit;
        }

        // Second try is the transaction attribute on the target class.
        stelltLoggingKontextBereit =
            specificMethod.getDeclaringClass().getAnnotation(StelltLoggingKontextBereit.class);
        if (stelltLoggingKontextBereit != null) {
            return stelltLoggingKontextBereit;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            stelltLoggingKontextBereit = method.getAnnotation(StelltLoggingKontextBereit.class);
            if (stelltLoggingKontextBereit != null) {
                return stelltLoggingKontextBereit;
            }

            // Last fallback is the class of the original method.
            return method.getDeclaringClass().getAnnotation(StelltLoggingKontextBereit.class);
        }

        return null;
    }
}
