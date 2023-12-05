package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.stub.HttpUrlConnectionStub;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.stub.TimeoutWiederholungHttpInvokerRequestExecutorStub;

/**
 * Plain unit tests for {@link TimeoutWiederholungHttpInvokerRequestExecutor}.
 */
public class TimeoutWiederholungHttpInvokerRequestExecutorTest {

    /**
     * Some value for the content length, doesn't get actually used.
     */
    private static final int CONTENT_LENGTH = 1;

    /**
     * AufrufKontextVerwalter to set bearer token in.
     */
    private AufrufKontextVerwalter<?> aufrufKontextVerwalterStub;

    /**
     * Stubbed request executor.
     */
    private TimeoutWiederholungHttpInvokerRequestExecutor executorStub;

    /**
     * Stubbed connection that doesn't connect to anything.
     */
    private HttpURLConnection connection;

    @Before
    public void setUp() {
        aufrufKontextVerwalterStub = new AufrufKontextVerwalterStub<>();
        aufrufKontextVerwalterStub.setBearerToken(null);
        SecurityContextHolder.createEmptyContext();
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutorStub(null);
        connection = new HttpUrlConnectionStub(null);
    }

    /**
     * Test if the bearer token gets set in the Authorization header from AufrufKontextVerwalter.
     */
    @Test
    public void testPrepareConnectionWithTokenFromAufrufKontextVerwalter() {
        final String token = "testToken1234";
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutorStub(aufrufKontextVerwalterStub);
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
     * Test if the bearer token gets set in the Authorization header from the SecurityContext.
     */
    @Test
    public void testPrepareConnectionWithTokenFromSecurityContext() {

        try {
            final String tokenValue = "securityContextToken1234";
            Jwt jwt = Jwt.withTokenValue(tokenValue)
                    .header("alg", "none")
                    .claim("sub", "user1")
                    .build();

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, null);
            SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            executorStub.prepareConnection(connection, CONTENT_LENGTH);

            List<String> authHeader = connection.getRequestProperties().get(HttpHeaders.AUTHORIZATION);
            assertEquals(1, authHeader.size());
            assertEquals("Bearer " + tokenValue, authHeader.get(0));
        } catch (IOException e) {
            fail("Expected no exception.");
        }
    }

    /**
     * Test if the Authorization header is not set if the bearer token in SecurityContext and AufrufKontextVerwalter {@code null}.
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
