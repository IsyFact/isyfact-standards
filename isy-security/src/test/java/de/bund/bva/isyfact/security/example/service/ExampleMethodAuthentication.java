package de.bund.bva.isyfact.security.example.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.security.oauth2.client.annotation.Authenticate;

public class ExampleMethodAuthentication {

    @Authenticate("my-auth-client")
    public Authentication authenticateWithValue() {
        return getAuthentication();
    }

    @Authenticate("${test.auth.client-id}")
    public Authentication authenticateWithValueInPropertyPlaceholder() {
        return getAuthentication();
    }

    @Authenticate(oauth2ClientRegistrationId = "my-auth-client")
    public Authentication authenticateWithOAut2ClientRegistrationId() {
        return getAuthentication();
    }

    @Secured("PRIV_test")
    @Authenticate("my-auth-client")
    public Authentication authenticateAndSecuredWithValidPrivilege() {
        return getAuthentication();
    }

    @Secured("PRIV_invalid")
    @Authenticate("my-auth-client")
    public Authentication authenticateAndSecuredWithInvalidPrivilege() {
        return getAuthentication();
    }

    public Authentication methodWithoutAuth() {
        return getAuthentication();
    }

    @Authenticate("my-auth-client")
    public String authenticateCheckCorrelationId() {
        return MdcHelper.liesKorrelationsId();
    }

    // all methods should return the currently authenticated principal so tests can easily check if the annotation works
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
