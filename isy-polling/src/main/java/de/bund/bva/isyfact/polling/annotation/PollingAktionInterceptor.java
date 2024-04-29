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
package de.bund.bva.isyfact.polling.annotation;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.polling.PollingVerwalter;

/**
 * Interceptor zum Durchführen einer Polling-Aktion.
 * Aktualisiert nach dem Aufruf der eigentlichen Polling-Aktion den
 * Zeitpunkt der letzten Ausführung.
 * 
 */
public class PollingAktionInterceptor implements MethodInterceptor {

    /** Zugriff auf den PollingVerwalter. Wird von Spring gesetzt */
    private final PollingVerwalter pollingVerwalter;

    /**
     * Erzeugt einen neuen Interceptor zum Durchführen einer Polling-Aktion.
     *
     * @param pollingVerwalter der {@link PollingVerwalter}
     */
    public PollingAktionInterceptor(PollingVerwalter pollingVerwalter) {
        this.pollingVerwalter = pollingVerwalter;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass =
            (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

        PollingAktion pollingAktion = ermittlePollingAktionAnnotation(invocation.getMethod(), targetClass);

        try {
            return invocation.proceed();
        } finally {
            // aktualisiere den Zeitpunkt der letzten Polling-Aktion.
            if (pollingAktion != null) {
                pollingVerwalter.aktualisiereZeitpunktLetztePollingAktivitaet(pollingAktion.pollingCluster());
            }
        }
    }

    /**
     * Ermittelt die PollingAktion-Annotation.
     * 
     * @param method
     *          Aufgerufene Methode.
     * @param targetClass
     *          Klasse, an der die Methode aufgerufen wurde.
     * @return Annotation PollingAktion
     */
    private PollingAktion ermittlePollingAktionAnnotation(Method method, Class<?> targetClass) {

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
        PollingAktion pollingAktion = specificMethod.getAnnotation(PollingAktion.class);
        if (pollingAktion != null) {
            return pollingAktion;
        }

        // Second try is the transaction attribute on the target class.
        pollingAktion = specificMethod.getDeclaringClass().getAnnotation(PollingAktion.class);
        if (pollingAktion != null) {
            return pollingAktion;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            pollingAktion = method.getAnnotation(PollingAktion.class);
            if (pollingAktion != null) {
                return pollingAktion;
            }

            // Last fallback is the class of the original method.
            return method.getDeclaringClass().getAnnotation(PollingAktion.class);
        }

        return null;        
    }

}



