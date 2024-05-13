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

import de.bund.bva.isyfact.polling.PollingVerwalter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

/**
 * Interceptor for performing a polling action.
 * Updates the time of the last execution after the actual polling action is called.
 *
 */
public class PollingAktionInterceptor implements MethodInterceptor {

    /** Access to the Polling Manager. Set by Spring */
    private final PollingVerwalter pollingVerwalter;

    /**
     * Creates a new interceptor for performing a polling action.
     *
     * @param pollingVerwalter the {@link PollingVerwalter}
     */
    public PollingAktionInterceptor(PollingVerwalter pollingVerwalter) {
        this.pollingVerwalter = pollingVerwalter;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        PollingAktion pollingAktion = ermittlePollingAktionAnnotation(invocation.getMethod(), targetClass);
        Object result;
        boolean successful = false;

        try {
            result = invocation.proceed();
            successful = true;  // true if no exception was thrown
        } finally {
            if (successful && pollingAktion != null) {
                pollingVerwalter.aktualisiereZeitpunktLetztePollingAktivitaet(pollingAktion.pollingCluster());
            }
        }
        return result;
    }



    /**
     * Determines the PollingAktion annotation.
     *
     * @param method
     *          Called method.
     * @param targetClass
     *          Class on which the method was called.
     * @return PollingAktion annotation
     */
    private PollingAktion ermittlePollingAktionAnnotation(Method method, Class<?> targetClass) {
        if (targetClass == null) {
            return null;  // Check if a targetClass exists
        }

        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        PollingAktion pollingAktion = specificMethod.getAnnotation(PollingAktion.class);
        if (pollingAktion != null) {
            return pollingAktion;
        }

        pollingAktion = specificMethod.getDeclaringClass().getAnnotation(PollingAktion.class);
        if (pollingAktion != null) {
            return pollingAktion;
        }

        if (specificMethod != method) {
            pollingAktion = method.getAnnotation(PollingAktion.class);
            if (pollingAktion != null) {
                return pollingAktion;
            }
            return method.getDeclaringClass().getAnnotation(PollingAktion.class);
        }

        return null;
    }

}
