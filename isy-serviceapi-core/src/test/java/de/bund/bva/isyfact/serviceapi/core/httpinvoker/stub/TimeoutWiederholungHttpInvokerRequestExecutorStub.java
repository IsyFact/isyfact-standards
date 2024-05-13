package de.bund.bva.isyfact.serviceapi.core.httpinvoker.stub;

import java.io.IOException;
import java.net.HttpURLConnection;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.TimeoutWiederholungHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;

/**
 * Stubbed {@link TimeoutWiederholungHttpInvokerRequestExecutor} with a public
 * {@link #prepareConnection(HttpURLConnection, int)} method.
 */
public abstract class TimeoutWiederholungHttpInvokerRequestExecutorStub extends TimeoutWiederholungHttpInvokerRequestExecutor {

    public TimeoutWiederholungHttpInvokerRequestExecutorStub(AufrufKontextVerwalter<?> aufrufKontextVerwalter) {
        super(aufrufKontextVerwalter);
    }

    @Override
    public void prepareConnection(HttpURLConnection con, int contentLength) throws IOException {
        super.prepareConnection(con, contentLength);
    }

    protected abstract HttpURLConnection createConnection(HttpInvokerClientConfiguration config) throws IOException;
}
