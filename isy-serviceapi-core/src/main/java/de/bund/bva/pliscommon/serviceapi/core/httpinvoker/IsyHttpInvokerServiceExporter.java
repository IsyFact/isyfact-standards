package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;

/**
 * {@link HttpInvokerServiceExporter} with disabled {@link #isAcceptProxyClasses()}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /** Reference to the AufrufKontextVerwalter. */
    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /**
     * Creates a HttpInvokerServiceExporter with {@link #isAcceptProxyClasses()} set to {@code false}.
     *
     * @param aufrufKontextVerwalter
     *         for accessing the AufrufKontextVerwalter when handling requests
     */
    public IsyHttpInvokerServiceExporter(AufrufKontextVerwalter<?> aufrufKontextVerwalter) {
        super();
        this.setAcceptProxyClasses(false);
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        this.aufrufKontextVerwalter.setBearerToken(token);
        super.handleRequest(request, response);
    }

}
