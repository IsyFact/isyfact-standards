package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.SerializationUtils;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Resource Owner Password Credentials Flow authentication.
 */
public class PasswordClientRegistrationAuthenticationToken extends AbstractClientRegistrationAuthenticationToken {

    /**
     * The resource owner's username.
     */
    private final String username;

    /**
     * The resource owner's password.
     */
    private final String password;

    public PasswordClientRegistrationAuthenticationToken(ClientRegistration clientRegistration, String username, String password, @Nullable String bhknz) {
        super(username, clientRegistration, bhknz);
        this.username = username;
        this.password = password;
        setAuthenticated(false);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Generates a cache key based on SHA-512 that includes the following fields.
     * <ul>
     *     <li>principal</li>
     *     <li>bhknz</li>
     *     <li>issuerLocation</li>
     *     <li>clientId</li>
     *     <li>clientSecret</li>
     *     <li>authorizationGrantType</li>
     *     <li>username</li>
     *     <li>password</li>
     * </ul>
     *
     * @return the generated cache key as hash code or null
     */
    @Override
    public byte[] generateCacheKey(byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");

            // use hash from super as base
            byte[] parentHash = super.generateCacheKey(salt);
            digest.update(parentHash);

            List<String> additionalValues = Arrays.asList(
                    String.valueOf(getUsername()),
                    String.valueOf(getPassword())
            );

            byte[] serializedAdditional = SerializationUtils.serialize(additionalValues);
            digest.update(serializedAdditional);

            return digest.digest();

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

