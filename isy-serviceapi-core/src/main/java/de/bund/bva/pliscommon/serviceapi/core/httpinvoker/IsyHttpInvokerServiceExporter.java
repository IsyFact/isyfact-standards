package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * {@link HttpInvokerServiceExporter} with disabled {@link #acceptProxyClasses}.
 */
public class IsyHttpInvokerServiceExporter extends HttpInvokerServiceExporter {

    /**
     * Default constructor.
     */
    public IsyHttpInvokerServiceExporter() {
        setAcceptProxyClasses(false);
    }
}
