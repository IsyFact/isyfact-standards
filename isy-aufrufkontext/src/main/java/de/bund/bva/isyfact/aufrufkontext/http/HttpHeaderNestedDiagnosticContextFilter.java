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
package de.bund.bva.isyfact.aufrufkontext.http;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * Ein Servlet-Filter, der die Correlation-ID aus dem Http-Header {@link #correlationIdHttpHeaderName}
 * ausliest und im Logging-Framework verfügbar macht.
 * Ist der Header nicht gesetzt wird eine neue Correlation-ID generiert.
 * <p>
 * Die Correlation-ID in einem Servlet-Filter zu verarbeiten bietet den Vorteil, dass der zum frühest
 * möglichen Zeitpunkt innerhalb eines Requests gesetzt ist. Dadurch enthalten auch alle Logausgaben von
 * Spring bereits den korrekten Kontext.
 * </p>
 *
 * <p>
 * Die Konfiguration erfolgt automatisch durch {@link de.bund.bva.isyfact.aufrufkontext.autoconfigure.MdcFilterConfiguration}.
 * </p>
 *
 */
// REVIEW (JM) Sollte in HttpHeaderMappedDiagnosticContextFilter umbenannt werden
public class HttpHeaderNestedDiagnosticContextFilter extends AbstractRequestLoggingFilter {

    /** Logger. */
    private static final IsyLogger LOG =
        IsyLoggerFactory.getLogger(HttpHeaderNestedDiagnosticContextFilter.class);

    /** Der Name des HTTP-Headers mit der Correlation-ID. */
    private String correlationIdHttpHeaderName = "X-Correlation-Id";

    /**
     * Setzt den Namen des HTTP-Headers mit der Correlation-ID.
     *
     * @param correlationIdHttpHeaderName
     *            der Name des HTTP-Headers mit der Correlation-ID.
     */
    public void setCorrelationIdHttpHeaderName(String correlationIdHttpHeaderName) {
        this.correlationIdHttpHeaderName = correlationIdHttpHeaderName;
    }

    /**
     * Determine the message to be pushed onto the SLF4J nested diagnostic context.
     * <p>
     * Default is a plain request log message without prefix or suffix.
     * @param request
     *            current HTTP request
     * @return the message to be pushed onto the Slf4j MDC
     * @see #createMessage
     */
    protected String getNestedDiagnosticContextMessage(HttpServletRequest request) {

        String correlationId = null;
        if (this.correlationIdHttpHeaderName != null) {
            correlationId = request.getHeader(this.correlationIdHttpHeaderName);
        }

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();

            if (this.correlationIdHttpHeaderName != null) {
                LOG.debug("HTTP-Header " + this.correlationIdHttpHeaderName + " enthält keine "
                    + "Correlation-ID. Neue Correlation-ID {} erzeugt.", correlationId);
            } else {
                LOG.debug("Name des HTTP-Header mit Correlation-ID ist nicht definiert. "
                    + "Neue Correlation-ID {} erzeugt.", correlationId);
            }
        } else {
            LOG.debug("Übernehme Correlation-ID {} aus HTTP-Header ", correlationId,
                this.correlationIdHttpHeaderName);
        }
        return correlationId;
    }

    /**
     * Logs the before-request message through Slf4j and adds a message the Slf4j MDC before the request is
     * processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        LOG.debug(message);
        MdcHelper.pushKorrelationsId(getNestedDiagnosticContextMessage(request));
    }

    /**
     * Removes the log message from the Slf4j MDC after the request is processed and logs the after-request
     * message through SLF4J.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        MdcHelper.entferneKorrelationsIds();
        LOG.debug(message);
    }
}
