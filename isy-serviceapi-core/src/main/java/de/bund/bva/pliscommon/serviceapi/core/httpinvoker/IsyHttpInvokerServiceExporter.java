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

    /** Constant for the name of the HTTP authorization header. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Reference to the AufrufKontextVerwalter. */
    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /**
     * Creates a HttpInvokerServiceExporter with {@link #acceptProxyClasses} set to {@code false}.
     *
     * @param aufrufKontextVerwalter
     *         for updating the AufrufKontext when handling requests
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
