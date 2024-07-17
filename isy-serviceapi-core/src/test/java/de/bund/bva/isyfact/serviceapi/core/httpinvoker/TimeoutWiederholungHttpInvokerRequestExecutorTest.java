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
import org.springframework.security.authentication.AbstractAuthenticationToken;
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
     * Stubbed request executor.
     */
    private TimeoutWiederholungHttpInvokerRequestExecutor executorStub;

    /**
     * Stubbed connection that doesn't connect to anything.
     */
    private HttpURLConnection connection;

    @Before
    public void setUp() {
        connection = new HttpUrlConnectionStub(null);
    }

    /**
     * Test if the bearer token gets set in the Authorization header from AufrufKontextVerwalter.
     */
    @Test
    public void testPrepareConnectionWithTokenFromAufrufKontextVerwalter() {
        final String token = "testToken1234";
        AufrufKontextVerwalter<?> aufrufKontextVerwalterStub = new AufrufKontextVerwalterStub<>();
        aufrufKontextVerwalterStub.setBearerToken(token);
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutorStub(aufrufKontextVerwalterStub);

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
        final String tokenValue = "securityContextToken1234";

        Jwt jwt = Jwt.withTokenValue(tokenValue)
                .header("alg", "none")
                .claim("sub", "user1")
                .build();

        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, null);
        SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutor(null);

        try {
            executorStub.prepareConnection(connection, CONTENT_LENGTH);
        } catch (IOException e) {
            fail("Expected no exception.");
        }

        List<String> authHeader = connection.getRequestProperties().get(HttpHeaders.AUTHORIZATION);
        assertEquals(1, authHeader.size());
        assertEquals("Bearer " + tokenValue, authHeader.get(0));
    }

    /**
     * Test if coming with a different Context will cause a ClassCastException
     */
    @Test
    public void testPrepareConnectionWithCustomTokenFromAufrufKontextVerwalterUsingSecurityContext() {
        final String tokenValue = "test2Token1234";
        AufrufKontextVerwalter<?> aufrufKontextVerwalterStub = new AufrufKontextVerwalterStub<>();
        aufrufKontextVerwalterStub.setBearerToken(tokenValue);
        CustomAuthenticationToken authenticationToken = new CustomAuthenticationToken();
        SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutor(aufrufKontextVerwalterStub);

        try {
            executorStub.prepareConnection(connection, CONTENT_LENGTH);
            //executorStub.
        } catch (ClassCastException e) {
            fail("Class should be tested in Method");
        } catch (Exception e) {
            fail("Expected no exception.");
        }

        List<String> authHeader = connection.getRequestProperties().get(HttpHeaders.AUTHORIZATION);
        assertEquals(1, authHeader.size());
        assertEquals("Bearer " + tokenValue, authHeader.get(0));

    }

    /**
     * Test if the Authorization header is not set if the bearer token in SecurityContext and AufrufKontextVerwalter {@code null}.
     */
    @Test
    public void testPrepareConnectionWithoutToken() {
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutor(null);

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
        executorStub = new TimeoutWiederholungHttpInvokerRequestExecutor(null);
        executorStub.setTimeout(timeout);

        try {
            executorStub.prepareConnection(connection, CONTENT_LENGTH);
        } catch (IOException e) {
            fail("Expected no exception.");
        }

        assertEquals(timeout, connection.getConnectTimeout());
        assertEquals(timeout, connection.getReadTimeout());
    }

    private static class CustomAuthenticationToken extends AbstractAuthenticationToken {
        /**
         * Creates a custom token.
         */
        public CustomAuthenticationToken() {
            super(null);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }
    }
}
