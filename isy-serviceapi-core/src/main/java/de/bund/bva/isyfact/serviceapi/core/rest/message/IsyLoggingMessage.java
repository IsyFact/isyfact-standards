package de.bund.bva.isyfact.serviceapi.core.rest.message;

import org.apache.cxf.interceptor.LoggingMessage;

/**
 * Wrapper für finale Klasse {@link LoggingMessage}.
 */
public class IsyLoggingMessage {

    protected final LoggingMessage delegate;

    public IsyLoggingMessage() {
        this.delegate = new LoggingMessage("", "");
    }

    public LoggingMessage getDelegate() {
        return this.delegate;
    }
}
