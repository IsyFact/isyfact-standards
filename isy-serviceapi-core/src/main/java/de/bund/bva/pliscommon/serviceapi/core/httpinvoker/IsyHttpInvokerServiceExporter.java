package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;

/**
 * {@link HttpInvokerServiceExporter} with disabled {@link #isAcceptProxyClasses()}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /**
     * Default constructor.
     */
    public IsyHttpInvokerServiceExporter(AufrufKontextVerwalter<?> aufrufKontextVerwalter) {
        super();
        this.setAcceptProxyClasses(false);
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        this.aufrufKontextVerwalter.setBearerToken(token);
        super.handleRequest(request, response);
    }

}
