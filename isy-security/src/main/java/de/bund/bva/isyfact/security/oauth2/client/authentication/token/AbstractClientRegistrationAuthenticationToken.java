package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.DigestUtils;
import org.springframework.util.SerializationUtils;

/**
 * Token that holds a {@link ClientRegistration}.
 */
public abstract class AbstractClientRegistrationAuthenticationToken extends AbstractIsyAuthenticationToken {

    /** Client Registration of the OAuth 2.0 client. */
    private final ClientRegistration clientRegistration;

    protected AbstractClientRegistrationAuthenticationToken(String principal, ClientRegistration clientRegistration, @Nullable String bhknz) {
        super(principal, bhknz);
        this.clientRegistration = clientRegistration;
        setAuthenticated(false);
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
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
     * </ul>
     *
     * @return the generated cache key as hash code or null
     */
    @Override
    public byte[] generateCacheKey(byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(salt);

            List<String> values = Arrays.asList(
                    String.valueOf(getPrincipal()),
                    String.valueOf(getBhknz()),
                    getClientRegistration().getProviderDetails().getIssuerUri(),
                    getClientRegistration().getClientId(),
                    getClientRegistration().getClientSecret(),
                    String.valueOf(getClientRegistration().getAuthorizationGrantType())
            );

            byte[] serializedData = SerializationUtils.serialize(values);

            return digest.digest(serializedData);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
