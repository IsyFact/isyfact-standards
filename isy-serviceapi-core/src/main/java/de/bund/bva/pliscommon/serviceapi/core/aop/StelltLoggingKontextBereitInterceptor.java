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
package de.bund.bva.pliscommon.serviceapi.core.aop;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;

import de.bund.bva.pliscommon.serviceapi.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Dieser Aspekt sorgt dafür, dass in Service-Methoden automatisch der Logging-Kontext gesetzt wird.
 *
 */
public class StelltLoggingKontextBereitInterceptor implements MethodInterceptor {

    /** Logger. */
    private static final IsyLogger LOG =
        IsyLoggerFactory.getLogger(StelltLoggingKontextBereitInterceptor.class);

    /**
     * Dieser Aspekt sorgt dafür, dass eine Korrelation-ID erzeugt wird, falls im AufrufKontext keine gesetzt
     * ist und in der Annotation angegeben ist, dass kein Aufrufkontext als Parameter übergeben wird.
     * Die Korrelation ID wird vor dem eigentlichen Aufruf im Logging-Kontext gesetzt und danach automatisch
     * wieder entfernt.
     *
     * @param invocation
     *            der Methodenaufruf
     *
     * @return das Ergebnis der eigentlichen Methode.
     * @throws Throwable
     *             wenn die eigentliche Methode einen Fehler wirft.
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        /** Die Korrelations-ID wird zur eindeutigen Identifikation von Service-Aufrufen verwendet. **/
         String korrelationsId = null;

        AufrufKontextTo aufrufKontextTo = leseAufrufKontextTo(invocation.getArguments());

        boolean nutzeAufrufKontext = false;

        Class<?> targetClass =
            (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

        StelltLoggingKontextBereit stelltLoggingKontextBereit =
            ermittleStelltLoggingKontextBereitAnnotation(invocation.getMethod(), targetClass);

        if (stelltLoggingKontextBereit != null) {
            // Wenn stelltLogginKontextBereit != null ist, haben wir es mit einer annotierten Klasse zu tun.
            // Dann entscheidet die Methode nutzeAufrufKontext darüber, ob AufrufKontextTo verwendet wird
            // oder nicht.
            nutzeAufrufKontext = stelltLoggingKontextBereit.nutzeAufrufKontext();
        }else{
            // Es bleibt die Frage, ob ein AufrufKontextTo ohne Annotation übergeben wurde.
            if(aufrufKontextTo != null){
                nutzeAufrufKontext = true;
            }else{
                nutzeAufrufKontext = false;
            }
        }

        if (nutzeAufrufKontext) {
            if (aufrufKontextTo != null) {
                if (StringUtils.isEmpty(aufrufKontextTo.getKorrelationsId())) {
                    LOG.warn(EreignisSchluessel.KEINE_KORRELATIONSID_IM_AUFRUFKONTEXT_UEBERMITTELT,
                        "Es wurde keine Korrelations-ID im AufrufKontext übermittelt. Erzeuge neue Korrelations-ID.");
                    korrelationsId = UUID.randomUUID().toString();
                    aufrufKontextTo.setKorrelationsId(korrelationsId);
                } else {
                    LOG.debug("Setze Korrelations-ID aus AufrufKontext.");
                    korrelationsId = aufrufKontextTo.getKorrelationsId();
                }

            } else {
                throw new IllegalArgumentException(
                    "Die Annotation StelltLoggingKontextBereit gibt an, dass die Methode "
                        + invocation.getMethod()
                        + " einen Aufrufkontext als Parameter enthält. Dieser Parameter ist aber null oder nicht vorhanden.");
            }
        } else {
            korrelationsId = UUID.randomUUID().toString();
            LOG.debug("Es wurde kein AufrufKontext übermittelt. Erzeuge neue Korrelations-ID.");
        }

        try {
            MdcHelper.pushKorrelationsId(korrelationsId);
            return invocation.proceed();
        } finally {
            // Nach dem Aufruf alle Korrelations-IDs vom MDC entfernen.
            MdcHelper.entferneKorrelationsIds();
        }
    }

    /**
     * Lädt den ersten gefundenen {@link AufrufKontextTo} aus den Parametern der aufgerufenen Funktion.
     *
     * @param args
     *            die Argumente der Service-Operation
     *
     * @return das AufrufKontextTo Objekt
     */
    private AufrufKontextTo leseAufrufKontextTo(Object[] args) {

        if (ArrayUtils.isNotEmpty(args)) {
            for (Object parameter : args) {
                if (parameter instanceof AufrufKontextTo) {
                    return (AufrufKontextTo) parameter;
                }
            }
        }

        return null;
    }

    /**
     * Ermittelt die StelltLoggingKontextBereit-Annotation.
     *
     * @param method
     *            Aufgerufene Methode.
     * @param targetClass
     *            Klasse, an der die Methode aufgerufen wurde.
     * @return Annotation StelltLoggingKontextBereit
     */
    private StelltLoggingKontextBereit ermittleStelltLoggingKontextBereitAnnotation(Method method,
        Class<?> targetClass) {

        // Strategie für die Ermittlung der Annotation ist aus AnnotationTransactionAttributeSource
        // übernommen.

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
