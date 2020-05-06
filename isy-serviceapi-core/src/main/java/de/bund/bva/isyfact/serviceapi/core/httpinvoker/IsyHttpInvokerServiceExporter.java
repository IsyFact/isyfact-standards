package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    public IsyHttpInvokerServiceExporter() {
        super();
        this.setAcceptProxyClasses(false);
    }
}
