package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * {@link HttpInvokerServiceExporter} with deactivated {@link #acceptProxyClasses}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /**
     * Default Constructor.
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
