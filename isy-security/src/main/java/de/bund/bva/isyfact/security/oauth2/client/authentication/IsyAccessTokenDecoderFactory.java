package de.bund.bva.isyfact.security.oauth2.client.authentication;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Factory class for configuration of the JwtDecoderFactory.
 * Uses {@link NimbusJwtDecoder} for token validation.
 */
public class IsyAccessTokenDecoderFactory implements JwtDecoderFactory<ClientRegistration> {
    /**
     * JWT decoder map used to create JWTDecoder.
     */
    private final Map<String, JwtDecoder> jwtDecoders = new ConcurrentHashMap<>();

    @Override
    public JwtDecoder createDecoder(ClientRegistration clientRegistration) {
        return this.jwtDecoders.computeIfAbsent(clientRegistration.getRegistrationId(), (key) -> {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                    .withJwkSetUri(clientRegistration.getProviderDetails().getJwkSetUri())
                    .build();

            jwtDecoder.setJwtValidator(JwtValidators.createDefault());

            return jwtDecoder;
        });
    }
}
