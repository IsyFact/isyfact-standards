package de.bund.bva.isyfact.security.oauth2.client.authentication.token;

import java.util.Objects;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

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
    public Integer generateCacheKey() {
        return Objects.hash(
                getPrincipal(),
                getBhknz(),
                getClientRegistration().getProviderDetails().getIssuerUri(),
                getClientRegistration().getClientId(),
                getClientRegistration().getClientSecret(),
                getClientRegistration().getAuthorizationGrantType()
        );
    }
}
