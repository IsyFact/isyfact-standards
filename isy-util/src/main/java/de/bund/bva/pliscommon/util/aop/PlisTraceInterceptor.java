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
import org.springframework.util.StopWatch;

/**
 * TraceInterceptor zum Tracing auf der Basis von Spring-AOP. Gibt Beginn, Ende und Dauer eines
 * Methodenaufufs aus.
 * 
 * 
 */
public class PlisTraceInterceptor extends AbstractTraceInterceptor {

    /**
     * Die Serial-ID der Klasse.
     */
    private static final long serialVersionUID = 1141571728731746888L;

    /**
     * Konstruktor der Klasse <code>PlisTraceInterceptor</code>. Benutzt eine statische
     * Logger-Instanz zum Schreiben der Trace-Einträge.
     */
    public PlisTraceInterceptor() {
        this.dynamicLogger = false;
    }

    /**
     * Parametrierter Konstruktor der Klasse <code>PlisTraceInterceptor</code>. Erlaubt das
     * Setzen eines Steuerparameters zur Festlegung, ob zum Schreiben der Trace-Einträge ein
     * statischer Logger oder die Logger-Instanz der aufgerufenen Klasse verwendet werden soll.
     * 
     * @param dynamicLogger
     *            Steuerparameter
     */
    public PlisTraceInterceptor(boolean dynamicLogger) {
        this.dynamicLogger = dynamicLogger;
        this.setUseDynamicLogger(dynamicLogger);
    }

    /**
     * Steuerparameter zur Festlegung, ob die Logger-Instanz der aufgerufenen Klasse zum Schreiben
     * der Trace-Einträge verwendet werden soll.
     */
    private boolean dynamicLogger;

    /**
     * {@inheritDoc}
     */
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {

        String invocationDescription = getInvocationDescription(invocation);
        StopWatch stopWatch = new StopWatch(invocationDescription);
        boolean exitThroughException = false;

        try {
            stopWatch.start(invocationDescription);
            logger.trace("Beginn " + invocationDescription);
            return invocation.proceed();
        } catch (Throwable ex) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            exitThroughException = true;
            logger.trace("Ein Ausnahmefehler ist aufgetreten in " + invocationDescription, ex);
            throw ex;
        } finally {
            if (!exitThroughException) {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                String exitMsg =
                    "Ende " + invocationDescription + " (Dauer: " + stopWatch.getTotalTimeMillis() + " ms)";
                logger.trace(exitMsg);
            }
        }
    }

    /**
     * Liefert eine Beschreibung der aufgerufenen Methode zurück. Falls ein dynamischer Logger
     * verwendet wird, wird nur der Methodenname zurück geliefert, ansonsten Methoden- und
     * Klassenname.
     * 
     * @param invocation
     *            Der Methodenaufruf
     * @return Beschreibung der aufgerufenen Methode
     */
    protected String getInvocationDescription(MethodInvocation invocation) {
        String description = "Methode '" + invocation.getMethod().getName() + "'";
        if (!dynamicLogger) {
            description = description + " der Klasse [" + invocation.getThis().getClass().getName() + "]";
        }

        return description;
    }

}
