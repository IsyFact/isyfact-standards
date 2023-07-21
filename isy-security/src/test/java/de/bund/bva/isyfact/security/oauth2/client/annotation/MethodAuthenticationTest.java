package de.bund.bva.isyfact.security.oauth2.client.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.security.example.service.ExampleMethodAuthentication;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

@SpringBootTest(classes = { AuthenticateInterceptor.class, ExampleMethodAuthentication.class })
@EnableAutoConfiguration
public class MethodAuthenticationTest {

    private static final String[] TEST_AUTHORITIES = { "PRIV_test", "ROLE_test" };

    private static final TestingAuthenticationToken TEST_AUTH_TOKEN =
            new TestingAuthenticationToken("user", "pass", TEST_AUTHORITIES);

    @MockBean
    private Authentifizierungsmanager authentifizierungsmanager;

    @Autowired
    private ExampleMethodAuthentication service;

    @BeforeEach
    public void setup() {
        // if the auth manager gets called it'll set the security context
        doAnswer(inv -> {
            SecurityContextHolder.getContext().setAuthentication(TEST_AUTH_TOKEN);
            return null;
        }).when(authentifizierungsmanager).authentifiziere(anyString());
    }

    @Test
    public void authenticatedPrincipalSetByValueInAnnotation() {
        // no principal before method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Authentication methodAuthentication = service.authenticateWithValue();
        assertEquals(TEST_AUTH_TOKEN, methodAuthentication);

        // no principal after method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void authenticatedPrincipalSetByOAuth2ClientRegistrationIdInAnnotation() {
        // no principal before method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Authentication methodAuthentication = service.authenticateWithOAut2ClientRegistrationId();
        assertEquals(TEST_AUTH_TOKEN, methodAuthentication);

        // no principal after method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    public void authenticatedPrincipalNullIfNoAnnotation() {
        // no principal before method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Authentication methodAuthentication = service.methodWithoutAuth();
        assertNull(methodAuthentication);

        // no principal after method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void authenticatedPrincipalSetBeforeCheckingValidPrivileges() {
        // no principal before method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Authentication methodAuthentication = service.authenticateAndSecuredWithValidPrivilege();
        assertEquals(TEST_AUTH_TOKEN, methodAuthentication);

        // no principal after method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void authenticatedPrincipalSetBeforeCheckingInvalidPrivileges() {
        // no principal before method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        assertThrows(AccessDeniedException.class, () -> service.authenticateAndSecuredWithInvalidPrivilege());

        // no principal after method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
