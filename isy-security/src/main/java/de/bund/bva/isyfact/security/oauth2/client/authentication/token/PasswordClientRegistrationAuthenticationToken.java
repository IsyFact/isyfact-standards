package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

/**
 * AuthenticationToken holding parameters required for creating a Client to use with Resource Owner Password Credentials Flow authentication.
 */
public class PasswordClientRegistrationAuthenticationToken extends AbstractClientRegistrationAuthenticationToken {

    /** The resource owner's username. */
    private final String username;

    /** The resource owner's password. */
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
     * Generates a cache key that includes the following fields.
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
    public byte[] generateCacheKey(String hashAlgorithm, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);

            digest.update(super.generateCacheKey(hashAlgorithm, salt));
            digest.update(getUsername().getBytes());
            digest.update(getPassword().getBytes());

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(hashAlgorithm + " nicht verfügbar.", e);
        }
    }
}
