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
package de.bund.bva.pliscommon.util.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;

import de.bund.bva.pliscommon.util.common.RecursiveToStringBuilder;

/**
 * Trace Interceptor, der aufgerufene Methoden mit ihren Parametern ins Trace-Log schreibt.
 * 
 */
public class LoggingTraceInterceptor extends AbstractTraceInterceptor {
    /** UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Gibt die Methode, deren Klasse und Parameter aus. {@inheritDoc}
     */
    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {

        // Vor Methodenaufruf: Logge Eingabe
        if (logger.isTraceEnabled()) {
            String msg = "Call " + getInvocationDescription(invocation) + "\n";
            if (invocation.getArguments() != null) {
                for (Object arg : invocation.getArguments()) {
                    if (arg != null) {
                        msg += arg.getClass().getSimpleName() + " ";
                        msg += RecursiveToStringBuilder.recursiveToString(arg) + "\n";
                    } else {
                        msg += "null\n";
                    }
                }
            }
            logger.trace(msg);
        }

        Object result = null;

        // Methode ausführen
        try {
            result = invocation.proceed();
            return result;

        } catch (Throwable t) {
            if (logger.isTraceEnabled()) {
                logger.trace("Exception occured " + t.toString());
            }
            throw t;

        } finally {
            // Nach Methodenaufruf: Logge Ausgabe
            if (logger.isTraceEnabled()) {
                String msg = "Result ";
                if (result != null) {
                    msg += result.getClass().getSimpleName() + " ";
                    msg += RecursiveToStringBuilder.recursiveToString(result);
                } else {
                    msg += "null";
                }
                logger.trace(msg);
            }
        }
    }

    /**
     * Return a description for the given method invocation.
     * @param invocation
     *            the invocation to describe
     * @return the description
     */
    protected String getInvocationDescription(MethodInvocation invocation) {
        return "method '" + invocation.getMethod().getName() + "' of class ["
            + getClassForLogging(invocation.getThis()) + "]";
    }
}
