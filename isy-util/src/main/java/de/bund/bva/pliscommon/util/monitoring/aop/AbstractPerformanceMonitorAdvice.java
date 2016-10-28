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

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

import de.bund.bva.pliscommon.util.monitoring.PerformanceMonitor;
import de.bund.bva.pliscommon.util.monitoring.PerformanceTagSource;

/**
 * Abstracte Klasse für das Performance-Monitoring.
 * 
 */
public abstract class AbstractPerformanceMonitorAdvice extends AbstractMonitoringInterceptor {
    /** Die UUID. */
    private static final long serialVersionUID = 1L;

    /** Referenz auf den PerformanceMonitor. */
    protected PerformanceMonitor performanceMonitor;

    /** Referenz auf die PerformanceTagSource. */
    protected PerformanceTagSource performanceTagSource;

    /** Konstante für den Default-Tag. */
    protected static final String DEFAULT_TAG = "DEFAULT";

    /** Hält den Logger. */
    protected static final Logger LOG = Logger.getLogger(AbstractPerformanceMonitorAdvice.class);

    /**
     * Liefert den Tag zur übergeben Klasse und Methode zurück.
     * 
     * @param method
     *            die Methode
     * @param target
     *            die Target-Klasse
     * @return der Tag oder der Default, wenn keiner gefunden wurde.
     */
    protected String getTag(Method method, Class<? extends Object> target) {
        String tag = performanceTagSource.getTag(method, target);
        if (tag == null) {
            LOG.debug("Für den Aufruf " + target + "." + method
                + ", wurde kein Performance-Tag vergeben. Verwende " + DEFAULT_TAG);
            tag = DEFAULT_TAG;
        }
        return tag;
    }

    /**
     * Setzt das Feld 'performanceMonitor'.
     * @param performanceMonitor
     *            Neuer Wert für performanceMonitor
     */
    public void setPerformanceMonitor(PerformanceMonitor performanceMonitor) {
        this.performanceMonitor = performanceMonitor;
    }

    /**
     * Setzt das Feld 'performanceTagSource'.
     * @param performanceTagSource
     *            Neuer Wert für performanceTagSource
     */
    public void setPerformanceTagSource(PerformanceTagSource performanceTagSource) {
        this.performanceTagSource = performanceTagSource;
    }
}
