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
package de.bund.bva.pliscommon.util.monitoring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import de.bund.bva.pliscommon.util.monitoring.PerformanceMonitor;

/**
 * Dieser Interceptor mißt die Dauer eines Methodenaufrufs durch Aufruf der Start/Stop-Methoden des
 * {@link PerformanceMonitor}.
 * 
 * 
 */
public class PerformanceMonitorStartStopInterceptor extends AbstractPerformanceMonitorAdvice implements
    MethodInterceptor {

    /**
     * UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        try {
            performanceMonitor.start(getTag(invocation.getMethod(), invocation.getClass()));
            return invocation.proceed();
        } finally {
            performanceMonitor.stop(getTag(invocation.getMethod(), invocation.getClass()));
        }
    }

}
