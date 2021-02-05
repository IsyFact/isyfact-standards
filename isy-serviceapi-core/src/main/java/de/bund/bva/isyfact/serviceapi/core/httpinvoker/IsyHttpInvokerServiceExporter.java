package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * {@link HttpInvokerServiceExporter} mit deaktiviertem {@link acceptProxyClasses}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /**
     * Default Konstruktor.
     */
    public IsyHttpInvokerServiceExporter() {
        super();
        this.setAcceptProxyClasses(false);
    }
}
