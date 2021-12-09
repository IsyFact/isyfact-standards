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
 * Eine generische Exception-Fassade für Service- und Native-GUI-Komponenten. Diese Implementierung fängt alle
 * Exceptions und behandelt sie folgendermaßen:
 *
 * <ul>
 *
 * <li>Eine {@link BaseException} wird auf einem konfigurierbaren Loglevel geloggt (siehe
 * {@link #setLogLevelExceptions(String)}). Sie wird auf eine {@link ToException} abgebildet, die pro
 * konkreter {@link BaseException}-Subklasse konfigurierbar ist.</li>
 *
 * <li>Eine {@link TechnicalRuntimeException} wird auf Level ERROR geloggt und auf eine global
 * konfigurierbare {@link TechnicalToException} abgebildet.</li>
 *
 * <li>Eine sonstige Exception zunächst in eine global konfigurierbare {@link TechnicalRuntimeException}
 * gewrappt und dann auf Level ERROR geloggt. Durch das Wrapping werden eine Ausnahme-ID und Unique-ID
 * erzeugt, die dann sowohl im Server- als auch im Aufrufer-Log erscheinen. Die gewrappte Exception wird dann
 * auf die global konfigurierbare {@link TechnicalToException} abgebildet.</li>
 *
 * </ul>
 *
 */
public class ServiceExceptionFassade implements MethodInterceptor, Validatable {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ServiceExceptionFassade.class);

    private final MethodMappingSource methodMappingSource;

    private final ExceptionMappingSource exceptionMappingSource;

    private final AusnahmeIdErmittler ausnahmeIdErmittler;

    /** Konstruktor der generischen, technischen RuntimeException der Anwendung. */
    private Constructor<? extends TechnicalRuntimeException> appTechnicalRuntimeExceptionCon;

    /**
     * Das Log-Level, auf dem (checked) {@link BaseException Exceptions} geloggt werden. Checked
     * Exceptions sind vorhergesehene fachliche oder technische Fehler, die keinen unerwarteten
     * Betriebszustand darstellen. Sie sollen deshalb unter Umständen nicht ins ERROR-Log geschrieben werden,
     * um den Betrieb nicht grundlos zu alarmieren. Hier kann ein feinerer Log-Level konfiguriert werden oder
     * {@code null}, um checked Exceptions gar nicht zu loggen.
     */
    private String logLevelExceptions = "ERROR";

    /**
     * Erzeugt eine generische Exception-Fassade für Service- und Native-GUI-Komponenten.
     *
     * @param methodMappingSource
     *          Konfiguration für das Methoden-Mapping.
     * @param exceptionMappingSource
     *          Konfiguration für das Exception-Mapping.
     * @param ausnahmeIdErmittler
     *          Konfiguration für Ermittlung der Ausnahme-ID beim Wrapping.
     * @param appTechnicalRuntimeException
     *          Exception, auf die sonstige Exceptions gemappt werden.
     *
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
                // Workaround, da Isy-Logging keine Übergabe des Loglevels unterstützt.
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
            // In seltenen Fällen trat bei Lasttests ein NoClassDefFound-Fehler beim Erzeugen der
            // RuntimeException auf. Da dabei der ursprüngliche Fehler verloren ging, wird hier nochmal
            // gecatcht.
            TechnicalRuntimeException runtimeException = null;
            try {

                // Die gefangene Exception wird in eine generische RuntimeException gewrapped, damit die
                // Ausnahme-ID und Unique-ID, die zum Client übermittelt werden, auch im Server-Log stehen.
                runtimeException = appTechnicalRuntimeExceptionCon.newInstance(
                    ausnahmeIdErmittler.ermittleAusnahmeId(t), t,
                    new String[] { Optional.ofNullable(t.getMessage()).orElse("<Kein Fehlertext>") });
                LOG.error("Fehler in der Serviceoperation ", runtimeException,
                    getMethodSignatureString(invocation));

            } catch (Throwable t2) {
                // Loggen des Sonderfalls Fehler in Fehlerbehandlung
                LOG.error(EreignisSchluessel.FEHLER_FEHLERBEHANDLUNG, "Fehler bei der Fehlerbehandlung", t2);
                LOG.error(EreignisSchluessel.FEHLER_FEHLERBEHANDLUNG, "Usprünglicher Fehler war {}", t,
                    t.getMessage());
            }

            throw ExceptionMapper.mapException(runtimeException,
                exceptionMappingSource.getGenericTechnicalToException(remoteBeanMethod));
        }
    }

    /**
     * Erstellt den Signatur-String des gegebenen Aufrufs.
     *
     * @param invocation
     *            der Methodenaufruf
     *
     * @return der Signatur-String
     */
    protected String getMethodSignatureString(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        return getMethodSignatureString(method);
    }

    /**
     * Erstellt den Signatur-String des gegebenen Aufrufs.
     *
     * @param method
     *            die gerufene Methode
     *
     * @return der Signatur-String
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
