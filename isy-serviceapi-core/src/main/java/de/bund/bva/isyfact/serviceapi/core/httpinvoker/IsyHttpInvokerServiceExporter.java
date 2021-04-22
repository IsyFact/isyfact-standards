package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * {@link HttpInvokerServiceExporter} with deactivated {@link #acceptProxyClasses}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /**
     * Default Constructor.
     */
    public IsyHttpInvokerServiceExporter() {
        setAcceptProxyClasses(false);
    }
}
