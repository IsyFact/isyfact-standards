package de.bund.bva.isyfact.logging.http;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.filter.AbstractRequestLoggingFilter;

import de.bund.bva.isyfact.logging.autoconfigure.MdcFilterAutoConfiguration;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * A servlet filter which reads the Correlation-ID from the http header {@link #correlationIdHttpHeaderName}
 * and makes it accessible to the logging framework.
 * If the header is not set, a new Correlation-ID is generated.
 * <p>
 * Processing the Correlation-ID in a servlet filter ensures that it is set
 * as early as possible within the request.
 * Therefore all of spring's log messages contain the correct Correlation-ID.
 * </p>
 *
 * <p>
 * The Configuration is done automatically by {@link MdcFilterAutoConfiguration}.
 * </p>
 */
// REVIEW (JM) Sollte in HttpHeaderMappedDiagnosticContextFilter umbenannt werden
public class HttpHeaderNestedDiagnosticContextFilter extends AbstractRequestLoggingFilter {

    /** Logger. */
    private static final IsyLogger LOG =
        IsyLoggerFactory.getLogger(HttpHeaderNestedDiagnosticContextFilter.class);

    /** The name of the http header with the Correlation-ID*/
    private String correlationIdHttpHeaderName = "X-Correlation-Id";

    /**
     * Sets the name of the http header with the Correlation-ID.
     *
     * @param correlationIdHttpHeaderName
     *            the name of the http header with the Correlation-ID.
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
        if (correlationIdHttpHeaderName != null) {
            correlationId = request.getHeader(correlationIdHttpHeaderName);
        }

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();

            if (correlationIdHttpHeaderName != null) {
                LOG.debug("HTTP-Header {} enthält keine Correlation-ID. Neue Correlation-ID {} erzeugt.",
                        correlationIdHttpHeaderName, correlationId);
            } else {
                LOG.debug("Name des HTTP-Header mit Correlation-ID ist nicht definiert. "
                    + "Neue Correlation-ID {} erzeugt.", correlationId);
            }
        } else {
            LOG.debug("Übernehme Correlation-ID {} aus HTTP-Header {}", correlationId,
                    correlationIdHttpHeaderName);
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
