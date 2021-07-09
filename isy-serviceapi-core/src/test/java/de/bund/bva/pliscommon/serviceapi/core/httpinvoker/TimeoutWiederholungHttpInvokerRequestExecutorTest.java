package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.serviceapi.core.httpinvoker.stub.HttpUrlConnectionStub;
import de.bund.bva.pliscommon.serviceapi.core.httpinvoker.stub.TimeoutWiederholungHttpInvokerRequestExecutorStub;

/**
 * Plain unit tests for {@link TimeoutWiederholungHttpInvokerRequestExecutor}.
 */
public class TimeoutWiederholungHttpInvokerRequestExecutorTest {

    /** Some value for the content length, doesn't get actually used. */
    private static final int CONTENT_LENGTH = 1;

    /** AufrufKontextVerwalter to set bearer token in. */
    private AufrufKontextVerwalter<?> aufrufKontextVerwalterStub;

    /** Stubbed request executor. */
    private TimeoutWiederholungHttpInvokerRequestExecutor executorStub;

    /** Stubbed connection that doesn't connect to anything. */
    private HttpURLConnection connection;

    @Before
    public void setUp() {
        aufrufKontextVerwalterStub = new AufrufKontextVerwalterStub<>();
        aufrufKontextVerwalterStub.setBearerToken(null);
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutorStub(aufrufKontextVerwalterStub);
        connection = new HttpUrlConnectionStub(null);
    }

    /**
     * Test if the bearer token gets set in the Authorization header.
     */
    @Test
    public void testPrepareConnectionWithToken() {
        final String token = "testToken1234";

        aufrufKontextVerwalterStub.setBearerToken(token);
        try {
            executorStub.prepareConnection(connection, CONTENT_LENGTH);
        } catch (IOException e) {
            fail("Expected no exception.");
        }

        List<String> authHeader = connection.getRequestProperties().get(HttpHeaders.AUTHORIZATION);
        assertEquals(1, authHeader.size());
        assertEquals("Bearer " + token, authHeader.get(0));
    }

    /**
     * Test if the Authorization header is not set if the bearer token is {@code null}.
     */
    @Test
    public void testPrepareConnectionWithoutToken() {
        try {
            executorStub.prepareConnection(connection, CONTENT_LENGTH);
        } catch (IOException e) {
            fail("Expected no exception.");
        }

        assertNull(connection.getRequestProperties().get(HttpHeaders.AUTHORIZATION));
    }

    /**
     * Test if the read and connect timeouts get properly set.
     */
    @Test
    public void testTimeoutsGetSet() {
        final int timeout = 50;

        executorStub.setTimeout(timeout);
        try {
            executorStub.prepareConnection(connection, CONTENT_LENGTH);
        } catch (IOException e) {
            fail("Expected no exception.");
        }
        assertEquals(timeout, connection.getConnectTimeout());
        assertEquals(timeout, connection.getReadTimeout());
    }

}
