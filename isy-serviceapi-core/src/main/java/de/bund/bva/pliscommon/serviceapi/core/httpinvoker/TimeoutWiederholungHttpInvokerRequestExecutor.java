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
package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;

import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocationResult;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.serviceapi.common.konstanten.EreignisSchluessel;

/**
 * Erweiterung des {@link SimpleHttpInvokerRequestExecutor} von Spring. Diese Erweiterung erlaubt es den
 * Timeout und eine Aufrufwiederholung zu konfigurieren.
 *
 *
 */
public class TimeoutWiederholungHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory
        .getLogger(TimeoutWiederholungHttpInvokerRequestExecutor.class);

    /** Timeout für Request. */
    private int timeout;

    /** Anzahl Wiederholungen bei Timeouts. */
    private int anzahlWiederholungen;

    /** Pause zwischen Wiederholungen. */
    private int wiederholungenAbstand;

    /**
     * {@inheritDoc}
     */
    @Override
    protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config,
        ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        int versuch = 0;
        while (true) {
            try {
                return super.doExecuteRequest(config, baos);
            } catch (InterruptedIOException requestException) {
                LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT,
                    "Beim Aufrufen des Services [{}] ist ein Timeout aufgetreten.", config.getServiceUrl());
                versuch++;
                if (versuch == this.anzahlWiederholungen) {
                    LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_ABBRUCH,
                        "Aufruf nach Timeout abgebrochen.");
                    throw requestException;
                }
                try {
                    if (this.wiederholungenAbstand > 0) {
                        LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_WARTEZEIT,
                            "Warte {}ms bis zur Wiederholung des Aufrufs.", this.wiederholungenAbstand);
                        Thread.sleep(this.wiederholungenAbstand);
                    }
                } catch (InterruptedException ex) {
                    LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_WARTEZEIT_ABBRUCH,
                        "Warten auf Aufrufwiederholung abgebrochen", ex);
                    throw requestException;
                }
                LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_WIEDERHOLUNG,
                    "Wiederhole Aufruf...");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareConnection(HttpURLConnection con, int contentLength) throws IOException {
        super.prepareConnection(con, contentLength);
        con.setReadTimeout(this.timeout);
        con.setConnectTimeout(this.timeout);
    }

    /**
     * Setzt den Timeout in Millisekunden. Der Timeout wird beim Aufbau und beim Lesen über die
     * HTTP-Connection verwendet.
     * @see HttpURLConnection#setConnectTimeout(int)
     * @see HttpURLConnection#setReadTimeout(int)
     * @param timeout
     *            Timeout in Millisekunden.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Hierüber wird festgelegt, wie oft der Aufruf bei einem Timeout wiederholt werden soll. Default ist 0.
     * @param anzahlWiederholungen
     *            Anzahl Wiederholungen bei Timeouts.
     */
    public void setAnzahlWiederholungen(int anzahlWiederholungen) {
        this.anzahlWiederholungen = anzahlWiederholungen;
    }

    /**
     * Hierüber wird festgelegt, wie lange zwischen zwei Aufrufwiederholungen gewartet werden soll. Default
     * ist 0.
     * @see #setAnzahlWiederholungen(int)
     * @param wiederholungenAbstand
     *            Pause zwischen den Wiederholungen in Millisekunden.
     */
    public void setWiederholungenAbstand(int wiederholungenAbstand) {
        this.wiederholungenAbstand = wiederholungenAbstand;
    }
}
