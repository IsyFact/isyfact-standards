package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.serviceapi.common.exception.ExceptionMapper;
import de.bund.bva.isyfact.serviceapi.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.exception.service.PlisToException;


/**
 * A generic exception facade for service and native GUI components.
 * This implementation catches all exceptions and handles them as follows:
 *
 * <ul>
 *
 * <li> A {@link BaseException} is logged at a configurable log level (see
 * {@link #setLogLevelExceptions(String)}). It is mapped to a {@link PlisToException} which is
 * configurable per concrete {@link BaseException} subclass.</li>
 *
 * <li>A {@link TechnicalRuntimeException} is logged at level ERROR and mapped to a globally configurable {@link PlisTechnicalToException}.</li>
 *
 * <li>A miscellaneous exception is first wrapped in a globally configurable {@link TechnicalRuntimeException} and then logged at ERROR level.
 * The wrapping creates an exception ID and unique ID, which then appear in both the server log and the caller log.
 * The wrapped exception is then mapped to the globally configurable {@link PlisTechnicalToException}.</li>
 * </ul>
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ServiceExceptionFassade implements MethodInterceptor, Validatable {

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ServiceExceptionFassade.class);

    /** Method Mapping Source. */
    private final MethodMappingSource methodMappingSource;

    /** ExceptionMappingSource. */
    private final ExceptionMappingSource exceptionMappingSource;

    /** Exception ID Detector.  */
    private final AusnahmeIdErmittler ausnahmeIdErmittler;

    /**
     * Constructor of the generic, technical RuntimeException of the application.
     */
    private Constructor<? extends TechnicalRuntimeException> appTechnicalRuntimeExceptionCon;

    /**
     * The log level at which (checked) {@link BaseException Exceptions} are logged.
     * Checked exceptions are foreseen functional or technical errors that do not represent an unexpected operating condition.
     * They should therefore not be written to the ERROR log under certain circumstances, in order not to alarm the operation without reason.
     * A finer log level can be configured here or {@code null}, in order not to log checked exceptions at all.
     */
    private String logLevelExceptions = "ERROR";

    /**
     * Creates a generic exception facade for service and native GUI components.
     *
     * @param methodMappingSource          Configuration for method mapping.
     * @param exceptionMappingSource       Configuration for exception mapping.
     * @param ausnahmeIdErmittler          Configuration for exception ID determination during wrapping.
     * @param appTechnicalRuntimeException Exception to which other exceptions are mapped.
     */
    public ServiceExceptionFassade(MethodMappingSource methodMappingSource,
                                   ExceptionMappingSource exceptionMappingSource, AusnahmeIdErmittler ausnahmeIdErmittler,
                                   Class<? extends TechnicalRuntimeException> appTechnicalRuntimeException) {
        this.methodMappingSource = methodMappingSource;
        this.exceptionMappingSource = exceptionMappingSource;
        this.ausnahmeIdErmittler = ausnahmeIdErmittler;
        setAppTechnicalRuntimeException(appTechnicalRuntimeException);
    }

    private void setAppTechnicalRuntimeException(
            Class<? extends TechnicalRuntimeException> appTechnicalRuntimeException) {
        try {
            appTechnicalRuntimeExceptionCon =
                    appTechnicalRuntimeException.getConstructor(String.class, Throwable.class, String[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Die Klasse " + appTechnicalRuntimeException.getName()
                    + " hat nicht den benötigten Konstruktor "
                    + "(String ausnahmeId, Throwable cause, String... parameter)");
        }
    }

    public void setLogLevelExceptions(String logLevelExceptions) {
        this.logLevelExceptions = logLevelExceptions;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method remoteBeanMethod = invocation.getMethod();

        try {
            return invocation.proceed();
        } catch (BaseException e) {
            if (logLevelExceptions != null) {
                // Workaround, since isy logging does not support passing the log level.
                switch (logLevelExceptions) {
                    case "INFO":
                        LOG.info(LogKategorie.JOURNAL, "Fehler in der Serviceoperation {}", e,
                                getMethodSignatureString(invocation));
                        break;
                    case "DEBUG":
                        LOG.debug("Fehler in der Serviceoperation {}: {}", getMethodSignatureString(invocation),
                                e.getMessage());
                        break;
                    case "WARN":
                        LOG.warn("Fehler in der Serviceoperation {}", e, getMethodSignatureString(invocation));
                        break;
                    case "ERROR":
                        LOG.error("Fehler in der Serviceoperation {}", e, getMethodSignatureString(invocation));
                        break;
                    case "FATAL":
                        LOG.fatal("Fehler in der Serviceoperation {}", e, getMethodSignatureString(invocation));
                        break;
                    case "TRACE":
                        LOG.trace("Fehler in der Serviceoperation {}: {}", getMethodSignatureString(invocation),
                                e.getMessage());
                        break;
                }
            }

            Class<? extends PlisToException> targetExceptionClass =
                    exceptionMappingSource.getToExceptionClass(remoteBeanMethod, e.getClass());
            if (targetExceptionClass == null) {
                targetExceptionClass =
                        exceptionMappingSource.getGenericTechnicalToException(remoteBeanMethod);
                LOG.warn(EreignisSchluessel.KEIN_EXCEPTION_MAPPING_DEFINIERT,
                        "Für die Serviceoperation {} ist kein Exception-Mapping für Exceptionklasse {} definiert. Benutze stattdessen technische TO-Exception {}",
                        getMethodSignatureString(invocation), e.getClass(), targetExceptionClass.getName());
            }
            throw ExceptionMapper.mapException(e, targetExceptionClass);
        } catch (TechnicalRuntimeException e) {
            LOG.error("Fehler in der Serviceoperation {}", e, getMethodSignatureString(invocation));
            throw ExceptionMapper.mapException(e,
                    exceptionMappingSource.getGenericTechnicalToException(remoteBeanMethod));
        } catch (Throwable t) {
            // In rare cases a NoClassDefFound error occurred during load tests when generating the RuntimeException.
            // Since the original error was lost, it is cached again here.
            TechnicalRuntimeException runtimeException = null;
            try {

                // The caught exception is wrapped into a generic RuntimeException so that the exception ID and unique
                // ID that are transmitted to the client are also in the server log.
                runtimeException = appTechnicalRuntimeExceptionCon.newInstance(
                        ausnahmeIdErmittler.ermittleAusnahmeId(t), t,
                        new String[]{Optional.ofNullable(t.getMessage()).orElse("<Kein Fehlertext>")});
                LOG.error("Fehler in der Serviceoperation ", runtimeException,
                        getMethodSignatureString(invocation));

            } catch (Throwable t2) {
                // Logging of the special case error in error handling
                LOG.error(EreignisSchluessel.FEHLER_FEHLERBEHANDLUNG, "Fehler bei der Fehlerbehandlung", t2);
                LOG.error(EreignisSchluessel.FEHLER_FEHLERBEHANDLUNG, "Usprünglicher Fehler war {}", t,
                        t.getMessage());
            }

            throw ExceptionMapper.mapException(runtimeException,
                    exceptionMappingSource.getGenericTechnicalToException(remoteBeanMethod));
        }
    }

    /**
     * Creates the signature string of the given call.
     *
     * @param invocation the method call
     * @return the signature string
     */
    protected String getMethodSignatureString(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        return getMethodSignatureString(method);
    }

    /**
     * Creates the signature string of the given call.
     *
     * @param method the called method
     * @return the signature string
     */
    protected String getMethodSignatureString(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    @Override
    public void validateConfiguration(Class<?> remoteBeanInterface, Object target) {

        Class<?> targetClass = AopUtils.getTargetClass(target);

        for (Method remoteBeanMethod : remoteBeanInterface.getMethods()) {
            Class<? extends PlisTechnicalToException> genericTechnicalToExceptionClass =
                    exceptionMappingSource.getGenericTechnicalToException(remoteBeanMethod);
            if (genericTechnicalToExceptionClass == null) {
                throw new IllegalStateException(
                        "Fehler in der statischen Konfiguration der Exception-Fassade für "
                                + remoteBeanInterface.getName() + ": Keine generische TO-Exception definiert");
            }

            if (!Arrays.asList(remoteBeanMethod.getExceptionTypes())
                    .contains(genericTechnicalToExceptionClass)) {
                throw new IllegalStateException(
                        "Fehler in der statischen Konfiguration der Exception-Fassade für "
                                + getMethodSignatureString(remoteBeanMethod) + ": Die generische TO-Exception "
                                + genericTechnicalToExceptionClass.getSimpleName()
                                + " ist nicht im RemoteBean-Interface deklariert");
            }

            Method coreMethod = methodMappingSource.getTargetMethod(remoteBeanMethod, targetClass);

            for (Class<?> exceptionClass : coreMethod.getExceptionTypes()) {
                if (BaseException.class.isAssignableFrom(exceptionClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends BaseException> isyExceptionClass =
                            (Class<? extends BaseException>) exceptionClass;
                    Class<? extends PlisToException> toExceptionClass =
                            exceptionMappingSource.getToExceptionClass(remoteBeanMethod, isyExceptionClass);
                    if (toExceptionClass == null) {
                        throw new IllegalStateException(
                                "Fehler in der statischen Konfiguration der Exception-Fassade für "
                                        + getMethodSignatureString(remoteBeanMethod)
                                        + ": Keine TO-Exception für AWK-Exception "
                                        + isyExceptionClass.getSimpleName() + " definiert");
                    }
                    if (!Arrays.asList(remoteBeanMethod.getExceptionTypes()).contains(toExceptionClass)) {
                        throw new IllegalStateException(
                                "Fehler in der statischen Konfiguration der Exception-Fassade für "
                                        + getMethodSignatureString(remoteBeanMethod) + ": Die TO-Exception "
                                        + toExceptionClass.getSimpleName()
                                        + " ist nicht im RemoteBean-Interface deklariert");
                    }
                }
            }
        }
    }
}
