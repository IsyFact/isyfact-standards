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

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;

/**
 * {@link MethodInterceptor}, der einen Methodenaufruf inklusive Laufzeit loggt.
 * 
 */
public class LogTimingInterceptor implements MethodInterceptor, Ordered, InitializingBean {
    /** Der Logger. */
    private static final Logger LOG = Logger.getLogger(LogTimingInterceptor.class);

    /** Der Logger für die Laufzeitausgabe im CSV-Format. */
    private Logger laufzeitLog;

    /** Interceptor-Reihenfolge. */
    private int order;

    /** Log-Level, auf dem der Beginn eines Methodenaufrufs geloggt wird. */
    private Level levelBegin = Level.DEBUG;

    /** Log-Level, auf dem der Abschluss eines Methodenaufrufs geloggt wird. */
    private Level levelEnd = Level.DEBUG;

    /** Log-Level, auf dem Parameter und Rückgabewert eines Methodenaufrufs geloggt werden. */
    private Level levelVerbose = Level.TRACE;

    /** Gibt an, ob in der Logmeldung das gerufene Interface anstelle der Implementierungsklasse erscheint. */
    private boolean logAsInterface;

    /** Der zu benutzende Logger für das Laufzeit-Log. */
    private String laufzeitLogger = "laufzeit";

    /** Log-Level, auf dem ein Laufzeit-Logeintrag geschrieben wird. */
    private Level levelLaufzeit = Level.DEBUG;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setLevelBegin(Level levelBegin) {
        this.levelBegin = levelBegin;
    }

    public void setLevelEnd(Level levelEnd) {
        this.levelEnd = levelEnd;
    }

    public void setLevelVerbose(Level levelVerbose) {
        this.levelVerbose = levelVerbose;
    }

    public void setLogAsInterface(boolean logAsInterface) {
        this.logAsInterface = logAsInterface;
    }

    public void setLaufzeitLogger(String laufzeitLogger) {
        this.laufzeitLogger = laufzeitLogger;
    }

    public void setLevelLaufzeit(Level levelLaufzeit) {
        this.levelLaufzeit = levelLaufzeit;
    }

    public void afterPropertiesSet() throws Exception {
        laufzeitLog = Logger.getLogger(laufzeitLogger);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {

        // Optimierung
        if (!LOG.isEnabledFor(levelBegin) && !LOG.isEnabledFor(levelEnd)
            && !laufzeitLog.isEnabledFor(levelLaufzeit)) {
            return invocation.proceed();
        }

        final String methodSignature = getMethodSignatureString(invocation);
        final long startTime = System.nanoTime();
        try {
            LOG.log(levelBegin, "Aufruf von " + methodSignature);
            if (LOG.isEnabledFor(levelVerbose)) {
                LOG.log(levelVerbose, "   mit Parametern " + getParameterString(invocation));
            }
            final Object ergebnis = invocation.proceed();
            final long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            LOG.log(levelEnd, "Aufruf von " + methodSignature + " war erfolgreich in " + ms + " ms");
            if (LOG.isEnabledFor(levelVerbose)) {
                LOG.log(levelVerbose, "   mit Ergebnis " + getErgebnisString(ergebnis));
            }
            if (laufzeitLog.isEnabledFor(levelLaufzeit)) {
                laufzeitLog.log(levelLaufzeit, methodSignature + ";E;" + ms);
            }
            return ergebnis;
        } catch (Throwable e) {
            final long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            LOG.log(levelEnd, "Aufruf von " + methodSignature + " ist fehlgeschlagen in " + ms + " ms");
            if (LOG.isEnabledFor(levelVerbose)) {
                LOG.log(levelVerbose, "   mit Exception " + getExceptionString(e));
            }
            if (laufzeitLog.isEnabledFor(levelLaufzeit)) {
                laufzeitLog.log(levelLaufzeit, methodSignature + ";F;" + ms);
            }
            throw e;
        }
    }

    /**
     * Ermittelt den Logger, auf den der gegebene Aufruf geloggt werden soll.
     * 
     * @param invocation
     *            die {@link MethodInvocation}
     * @return der Logger
     */
    protected Logger getLogger(MethodInvocation invocation) {
        Class<?> clazz;
        if (logAsInterface) {
            Method method = invocation.getMethod();
            clazz = method.getDeclaringClass();
        } else {
            Object target = invocation.getThis();
            clazz = target.getClass();
        }
        return Logger.getLogger(clazz);
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
        Class<?> clazz;
        if (logAsInterface) {
            clazz = method.getDeclaringClass();
        } else {
            Object target = invocation.getThis();
            clazz = target.getClass();
        }
        return clazz.getName() + "." + method.getName();
    }

    /**
     * Erstellt die Parameterliste des gegebenen Aufrufs.
     * 
     * @param invocation
     *            der Methodenaufruf
     * 
     * @return die Parameterliste
     */
    protected Object getParameterString(MethodInvocation invocation) {
        StringBuilder s = new StringBuilder();
        for (Object arg : invocation.getArguments()) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append(arg != null ? arg.toString() : "null");
        }
        return s.toString();
    }

    /**
     * Erstellt die Ergebnisdarstellung.
     * 
     * @param ergebnis
     *            das Methodenaufruf-Ergebnis
     * 
     * @return die Ergebnisdarstellung
     */
    protected String getErgebnisString(Object ergebnis) {
        return ergebnis != null ? ergebnis.toString() : "null";
    }

    /**
     * Erstellt die Exceptiondarstellung.
     * 
     * @param e
     *            die Exception
     * 
     * @return die Exceptiondarstellung
     */
    protected String getExceptionString(Throwable e) {
        return e.toString();
    }

}
