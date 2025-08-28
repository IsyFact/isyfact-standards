package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
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
     * Generates a cache key that includes the following fields.
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
    public byte[] generateCacheKey(String hashAlgorithm, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
            digest.update(salt);

            ClientRegistration clientReg = getClientRegistration();

            List<String> stringsToHash = Arrays.asList(
                String.valueOf(getPrincipal()),
                String.valueOf(getBhknz()),
                clientReg.getProviderDetails().getIssuerUri(),
                clientReg.getClientId(),
                clientReg.getClientSecret(),
                String.valueOf(clientReg.getAuthorizationGrantType())
            );

            byte[] serializedBytes = SerializationUtils.serialize(stringsToHash);
            digest.update(serializedBytes);

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(hashAlgorithm + " nicht verfügbar.", e);
        }
    }
}
