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
package de.bund.bva.pliscommon.util.monitoring.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.bund.bva.pliscommon.util.monitoring.PerformanceMonitor;

/**
 * Implementierung des {@link PerformanceMonitor}-Interfaces auf der Basis von {@link ThreadLocal}. Die
 * Ausgabe der Ergebnisse erfolgt bei Request-Ende per Log4J auf dem Level INFO. Damit überhaupt gemessen
 * wird, muss jedoch der Log-Level für diese Klasse auf TRACE gesetzt werden. log4jproperties:
 *
 * ... # Diese Zeile aktiviert das Performance-Monitoring
 * log4j.logger.de.bund.bva.pliscommon.util.monitoring.impl.PerformanceMonitorImpl=TRACE ...
 *
 *
 */
public class PerformanceMonitorImpl implements PerformanceMonitor {
    /**
     * Startzeitpunkt des Requests.
     */
    private static final ThreadLocal<Long> REQUEST_START_TIME_THREAD_LOCAL = new ThreadLocal<Long>();

    /**
     * Performance-Daten pro Tag.
     */
    private static final ThreadLocal<Map<String, PerfData>> PERF_DATA_THREAD_LOCAL =
        new ThreadLocal<Map<String, PerfData>>();

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(PerformanceMonitorImpl.class);

    /**
     * Feste Liste von Tags, die in dieser Reihenfolge am Request-Ende ausgegeben werden. (Optional).
     */
    private List<String> fixedTags;

    /**
     *
     * {@inheritDoc}
     */
    public void beginRequest() {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        Map<String, PerfData> perfData = PERF_DATA_THREAD_LOCAL.get();
        if (perfData != null) {
            throw new IllegalStateException("Request bereits gestartet.");
        }
        REQUEST_START_TIME_THREAD_LOCAL.set(System.currentTimeMillis());
        PERF_DATA_THREAD_LOCAL.set(new HashMap<String, PerfData>());

    }

    /**
     *
     * {@inheritDoc}
     */
    public void endRequest(String logInfo) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        Map<String, PerfData> perfDataMap = PERF_DATA_THREAD_LOCAL.get();
        if (perfDataMap == null) {
            LOG.debug("Keine Request gestartet.");
            return;
        }
        long requestDuration = System.currentTimeMillis() - REQUEST_START_TIME_THREAD_LOCAL.get();
        List<String> tags;
        if (this.fixedTags != null) {
            tags = this.fixedTags;
        } else {
            tags = new ArrayList<String>(perfDataMap.keySet());
            Collections.sort(tags);
        }
        StringBuilder msg = new StringBuilder();
        if (logInfo != null) {
            msg.append("(").append(logInfo).append(") ");
        }
        msg.append("Total: ").append(requestDuration).append("; ");
        for (String tag : tags) {
            PerfData perfData = perfDataMap.get(tag);
            long duration = 0;
            long count = 0;
            if (perfData != null) {
                duration = perfData.totalDuration;
                count = perfData.count;
            }
            msg.append(tag).append(": ").append(duration).append("(").append(count).append("); ");
        }
        LOG.info(msg.toString());
        PERF_DATA_THREAD_LOCAL.set(null);
    }

    /**
     *
     * {@inheritDoc}
     */
    public void start(String tag) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        Map<String, PerfData> perfDataMap = PERF_DATA_THREAD_LOCAL.get();
        if (perfDataMap == null) {
            LOG.debug("Kein Request gestartet.");
            return;
        }
        PerfData perfData = perfDataMap.get(tag);
        if (perfData == null) {
            perfData = new PerfData();
            perfDataMap.put(tag, perfData);
        }
        if (perfData.startTime != 0) {
            LOG.debug("Start mehrfach ohne Ende aufgerufen.");
            return; // Mehrfache Starts ignorieren
        } else {
            perfData.startTime = System.currentTimeMillis();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(String tag) {
        if (!LOG.isTraceEnabled()) {
            return;
        }
        Map<String, PerfData> perfDataMap = PERF_DATA_THREAD_LOCAL.get();
        if (perfDataMap == null) {
            LOG.debug("Kein Request gestartet.");
            return;
        }
        PerfData perfData = perfDataMap.get(tag);
        if (perfData == null) {
            return; // Ende ohne Start ignorieren
        }
        if (perfData.startTime == 0) {
            LOG.debug("Ende ohne Start aufgerufen.");
            return; // Ende ohne Start ignorieren
        } else {
            perfData.totalDuration += System.currentTimeMillis() - perfData.startTime;
            perfData.count++;
            perfData.startTime = 0;
        }
    }

    /**
     * Private Klasse zum Speichern der Performance-Daten zu einem Tag.
     *
     */
    private class PerfData {
        /**
         * Startzeit der Messung, 0 wenn keine Messung läuft.
         */
        private long startTime;

        /**
         * Summe der Laufzeiten der Aktion.
         */
        private long totalDuration;

        /**
         * Anzahl der Aufrufe.
         */
        private long count;
    }

    /**
     * Setzt das Feld 'fixedTags'.
     * @param fixedTags
     *            Neuer Wert für fixedTags
     */
    public void setFixedTags(List<String> fixedTags) {
        this.fixedTags = fixedTags;
    }
}
