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
package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;

import org.springframework.http.HttpHeaders;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocationResult;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.serviceapi.common.konstanten.EreignisSchluessel;

/**
 * Extension to the Spring {@link SimpleHttpInvokerRequestExecutor} which allows
 * to configure the timeout and the times a call is repeated.
 */
public class TimeoutWiederholungHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor {

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory
            .getLogger(TimeoutWiederholungHttpInvokerRequestExecutor.class);

    /** {@link AufrufKontextVerwalter} to set the OAuth 2 Bearer Token. */
    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /** Timeout for the request. */
    private int timeout;

    /** Number of times the call is repeated in case of a timeout. */
    private int anzahlWiederholungen;

    /** Break between the call repetitions. */
    private int wiederholungenAbstand;

    /**
     * Constructor. Sets the {@link AufrufKontextVerwalter} bean.
     * @param aufrufKontextVerwalter new value for the {@link AufrufKontextVerwalter}.
     */
    public TimeoutWiederholungHttpInvokerRequestExecutor(AufrufKontextVerwalter<?> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

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
                if (versuch == anzahlWiederholungen) {
                    LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_ABBRUCH,
                        "Aufruf nach Timeout abgebrochen.");
                    throw requestException;
                }
                try {
                    if (wiederholungenAbstand > 0) {
                        LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_WARTEZEIT,
                            "Warte {}ms bis zur Wiederholung des Aufrufs.", wiederholungenAbstand);
                        Thread.sleep(wiederholungenAbstand);
                    }
                }  catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_WARTEZEIT_ABBRUCH,
                        "Warten auf Aufrufwiederholung abgebrochen", ex);
                throw new IOException("Thread wurde unterbrochen, während er auf den erneuten Versuch des HTTP-Aufrufs wartete", ex);
            }

            LOG.info(LogKategorie.PROFILING, EreignisSchluessel.TIMEOUT_WIEDERHOLUNG,
                    "Wiederhole Aufruf...");
            }
        }
    }

    @Override
    protected void prepareConnection(HttpURLConnection con, int contentLength) throws IOException {
        super.prepareConnection(con, contentLength);
        if (aufrufKontextVerwalter.getBearerToken() != null) {
            con.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + aufrufKontextVerwalter.getBearerToken());
        } else {
            LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.KEIN_BEARER_TOKEN_IM_AUFRUFKONTEXT,
                    "Kein Bearer-Token im AufrufKontextVerwalter. Der Authorization-Header wird nicht gesetzt.");
        }
        con.setReadTimeout(timeout);
        con.setConnectTimeout(timeout);
    }

    /**
     * Sets the timeout in milliseconds. The timeout is used when establishing and reading via the HTTP connection.
     * @see HttpURLConnection#setConnectTimeout(int)
     * @see HttpURLConnection#setReadTimeout(int)
     * @param timeout
     *            timeout in milliseconds.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * This defines how often the call is to be repeated in the event of a timeout. Default value is 0.
     * @param anzahlWiederholungen
     *            number of repetitions in case of a timeout.
     */
    public void setAnzahlWiederholungen(int anzahlWiederholungen) {
        this.anzahlWiederholungen = anzahlWiederholungen;
    }

    /**
     * This defines how long to wait between two call repetitions. Default value is 0.
     * @see #setAnzahlWiederholungen(int)
     * @param wiederholungenAbstand
     *            break between repetitions in milliseconds.
     */
    public void setWiederholungenAbstand(int wiederholungenAbstand) {
        this.wiederholungenAbstand = wiederholungenAbstand;
    }
}
