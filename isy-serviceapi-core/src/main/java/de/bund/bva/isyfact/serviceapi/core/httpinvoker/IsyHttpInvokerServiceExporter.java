package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.serviceapi.common.RequestUtil;
import de.bund.bva.isyfact.serviceapi.common.konstanten.EreignisSchluessel;

/**
 * {@link HttpInvokerServiceExporter} with disabled {@link #isAcceptProxyClasses()}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /** The Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyHttpInvokerServiceExporter.class);

    /** Reference to the object managing the current call context. */
    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /**
     * Creates a HttpInvokerServiceExporter with {@link #isAcceptProxyClasses()} set to {@code false}.
     *
     * @param aufrufKontextVerwalter
     *         for accessing the AufrufKontextVerwalter when handling requests
     */
    public IsyHttpInvokerServiceExporter(AufrufKontextVerwalter<?> aufrufKontextVerwalter) {
        setAcceptProxyClasses(false);
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bearerToken = RequestUtil.extractBearerToken(request);
        if (bearerToken != null) {
            aufrufKontextVerwalter.setBearerToken(bearerToken);
        } else {
            LOG.warn(EreignisSchluessel.KEIN_BEARER_TOKEN_UEBERMITTELT,
                    "Keinen Authorization-Header mit Bearer-Token empfangen. Es wird nicht in den AufrufKontextVerwalter gesetzt.");
        }

        super.handleRequest(request, response);
    }

}
