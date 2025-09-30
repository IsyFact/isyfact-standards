package de.bund.bva.isyfact.security.oauth2.client.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.security.example.service.ExampleMethodAuthentication;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

@SpringBootTest(
        classes = { AuthenticateInterceptor.class, ExampleMethodAuthentication.class },
        properties = { "test.auth.client-id = my-auth-client" }
)
@EnableAutoConfiguration
@EnableMethodSecurity(securedEnabled = true)
public class MethodAuthenticationTest {

    private static final String[] TEST_AUTHORITIES = { "PRIV_test", "ROLE_test" };

    private static final TestingAuthenticationToken TEST_AUTH_TOKEN =
            new TestingAuthenticationToken("user", "pass", TEST_AUTHORITIES);

    private static final String CLIENT_ID = "my-auth-client";

    @MockitoBean
    private Authentifizierungsmanager authentifizierungsmanager;

    @Autowired
    private ExampleMethodAuthentication service;

    @BeforeEach
    public void setup() {
        // if the auth manager gets called it'll set the security context
        doAnswer(inv -> {
            SecurityContextHolder.getContext().setAuthentication(TEST_AUTH_TOKEN);
            return null;
        }).when(authentifizierungsmanager).authentifiziere(eq(CLIENT_ID));
        doAnswer(inv -> {
            throw new IllegalArgumentException("invalid client id: " + inv.getArguments()[0]);
        }).when(authentifizierungsmanager).authentifiziere(not(eq(CLIENT_ID)));
    }

    @Test
    public void resetAuthenticationToInitialValue() {
        SecurityContextHolder.getContext().setAuthentication(TEST_AUTH_TOKEN);

        // initial principal is set before method call
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        Authentication methodAuthentication = service.authenticateWithValue();
        assertEquals(TEST_AUTH_TOKEN, methodAuthentication);

        // initial principal is set after method call
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(TEST_AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication());
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
    public void authenticatedPrincipalSetByValueInAnnotationWithPropertyPlaceholder() {
        // no principal before method call
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        Authentication methodAuthentication = service.authenticateWithValueInPropertyPlaceholder();
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

    @Test
    public void newCorrelationIdSetWhenEmpty() {
        // remove existing correlation id
        MdcHelper.entferneKorrelationsId();
        assertNull(MdcHelper.liesKorrelationsId());

        // new correlation id within method
        String correlationId = service.authenticateCheckCorrelationId();
        assertNotNull(correlationId);

        // no correlation id after method call
        assertNull(MdcHelper.liesKorrelationsId());
    }

    @Test
    public void existingCorrelationIdUsedWhenExists() {
        // set correlation id
        String expectedCorrelationId = UUID.randomUUID().toString();
        MdcHelper.pushKorrelationsId(expectedCorrelationId);
        assertEquals(expectedCorrelationId, MdcHelper.liesKorrelationsId());

        // same correlation id within method
        String actualCorrelationId = service.authenticateCheckCorrelationId();
        assertEquals(expectedCorrelationId, actualCorrelationId);

        // same correlation id after method
        assertEquals(expectedCorrelationId, MdcHelper.liesKorrelationsId());
    }
}
