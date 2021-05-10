package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;

/**
 * {@link HttpInvokerServiceExporter} with deactivated {@link #isAcceptProxyClasses()}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /** Constant for the name of the authorisation header. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Reference to the object managing the current call context. */
    private final AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    /**
     * Default Constructor.
     *
     * @param aufrufKontextVerwalter reference to the object managing the current call context.
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
