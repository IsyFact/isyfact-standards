package de.bund.bva.isyfact.security.oauth2.client.authentication;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import de.bund.bva.isyfact.security.test.oidcprovider.EmbeddedOidcProviderMock;

@ExtendWith(MockitoExtension.class)
public class IsyAccessTokenDecoderFactoryTest {

    @RegisterExtension
    static EmbeddedOidcProviderMock oidcProvider = new EmbeddedOidcProviderMock("localhost", 8080, "/auth/realms/test");

    @Test
    void shouldAcceptTokenWithAccountAudience() {
        oidcProvider.addClient("clientId", "secret", Set.of("Rolle_A"));

        IsyAccessTokenDecoderFactory factory = new IsyAccessTokenDecoderFactory();
        ClientRegistration clientRegistration = createClientRegistration("clientId");

        JwtDecoder decoder = factory.createDecoder(clientRegistration);

        String tokenWithAccountAud = oidcProvider.getAccessTokenString("clientId", "testuser", Optional.empty(), Set.of("Rolle_A"));

        assertThatCode(() -> {
            Jwt jwt = decoder.decode(tokenWithAccountAud);
            assertThat(jwt.getAudience()).contains("account");
        }).doesNotThrowAnyException();
    }


    @Test
    void shouldContainTwoValidatorsInCustomFactory() throws NoSuchFieldException, IllegalAccessException {
        IsyAccessTokenDecoderFactory isyAccessTokenDecoderFactory = new IsyAccessTokenDecoderFactory();
        ClientRegistration clientRegistration = createClientRegistration("clientId");

        JwtDecoder decoder = isyAccessTokenDecoderFactory.createDecoder(clientRegistration);

        List<?> newValidators = extractTokenValidators(decoder);

        assertThat(newValidators.size())
                .isEqualTo(2);
    }

    private ClientRegistration createClientRegistration(String clientId) {
        return ClientRegistration.withRegistrationId(clientId + "-registration")
                .clientId(clientId)
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri("http://localhost:8080/auth/realms/test/protocol/openid-connect/token")
                .jwkSetUri("http://localhost:8080/auth/realms/test/protocol/openid-connect/certs")
                .issuerUri("http://localhost:8080/auth/realms/test")
                .build();
    }

    private List<?> extractTokenValidators(JwtDecoder decoder) throws NoSuchFieldException, IllegalAccessException {
        Field validatorField = decoder.getClass().getDeclaredField("jwtValidator");
        validatorField.setAccessible(true);
        Object validator = validatorField.get(decoder);

        if (validator instanceof DelegatingOAuth2TokenValidator) {
            Field tokenValidatorsField = DelegatingOAuth2TokenValidator.class.getDeclaredField("tokenValidators");
            tokenValidatorsField.setAccessible(true);
            return (List<?>) tokenValidatorsField.get(validator);
        }

        return Collections.emptyList();
    }
}
