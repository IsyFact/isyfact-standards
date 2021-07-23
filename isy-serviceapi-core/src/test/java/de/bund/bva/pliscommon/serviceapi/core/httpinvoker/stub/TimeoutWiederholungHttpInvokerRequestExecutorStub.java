package de.bund.bva.pliscommon.serviceapi.core.httpinvoker.stub;

import java.io.IOException;
import java.net.HttpURLConnection;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.serviceapi.core.httpinvoker.TimeoutWiederholungHttpInvokerRequestExecutor;

/**
 * Stubbed {@link TimeoutWiederholungHttpInvokerRequestExecutor} with a public {@link
 * #prepareConnection(HttpURLConnection, int)} method.
 */
public class TimeoutWiederholungHttpInvokerRequestExecutorStub extends TimeoutWiederholungHttpInvokerRequestExecutor {

    public TimeoutWiederholungHttpInvokerRequestExecutorStub(AufrufKontextVerwalter<?> aufrufKontextVerwalter) {
        super(aufrufKontextVerwalter);
    }

    @Override
    public void prepareConnection(HttpURLConnection con, int contentLength) throws IOException {
        super.prepareConnection(con, contentLength);
    }

}
