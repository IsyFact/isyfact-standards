package de.bund.bva.isyfact.serviceapi.core.httpinvoker.stub;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Stubbed {@link HttpURLConnection} that doesn't open a connection. Can be used for verifying its properties.
 */
public class HttpUrlConnectionStub extends HttpURLConnection {

    public HttpUrlConnectionStub(URL u) {
        super(u);
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean usingProxy() {
        return false;
    }

}
