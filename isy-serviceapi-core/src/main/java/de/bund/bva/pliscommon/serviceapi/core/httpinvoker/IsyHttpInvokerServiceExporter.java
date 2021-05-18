package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.serviceapi.common.konstanten.EreignisSchluessel;

/**
 * {@link HttpInvokerServiceExporter} with disabled {@link #isAcceptProxyClasses()}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /** The Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyHttpInvokerServiceExporter.class);

    /** Reference to the AufrufKontextVerwalter. */
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
        aufrufKontextVerwalter.setBearerToken(extractBearerToken(request));
        super.handleRequest(request, response);
    }

    /**
     * Extracts the OAuth 2 bearer token from the request.
     *
     * @param request
     *         the request to extract the token from
     * @return the Base64 encoded token, or {@code null} if no token was found
     */
    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String tokenString = null;
        if (authHeader != null) {
            String[] split = authHeader.trim().split("\\s+");
            if (split.length == 2 && split[0].equalsIgnoreCase("Bearer")) {
                tokenString = split[1];
            }
        }

        if (tokenString == null) {
            LOG.warn(EreignisSchluessel.KEIN_BEARER_TOKEN_UEBERMITTELT,
                    "Keinen Authorization-Header mit Bearer-Token empfangen. Es wird nicht in den AufrufKontextVerwalter gesetzt.");
        }

        return tokenString;
    }

}
