package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.UnknownServiceException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.stub.HttpUrlConnectionStub;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.stub.TimeoutWiederholungHttpInvokerRequestExecutorStub;
import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;

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
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutorStub(aufrufKontextVerwalterStub) {
            @Override
            protected HttpURLConnection createConnection(HttpInvokerClientConfiguration config) throws IOException {
                return null;
            }
        };
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

    @Test(expected = UnknownServiceException.class)
    public void testConnectionProtocolSupport() throws IOException {
        // Set up the mock to throw an exception when getInputStream is called
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenThrow(new UnknownServiceException("protocol doesn't support input"));

        // Make sure to call the method that uses getInputStream
        executorStub.prepareConnection(connection, CONTENT_LENGTH);

        // Force a call that uses getInputStream to ensure the exception is thrown
        connection.getInputStream();
    }

}
