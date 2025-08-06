package de.bund.bva.isyfact.security.oauth2.client;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.security.config.AdditionalCredentials;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties;
import de.bund.bva.isyfact.security.config.IsyOAuth2ClientConfigurationProperties.AdditionalRegistrationProperties;
import de.bund.bva.isyfact.security.config.IsySecurityConfigurationProperties;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.AbstractIsyAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.ClientCredentialsRegistrationIdAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.client.authentication.token.PasswordClientRegistrationAuthenticationToken;
import de.bund.bva.isyfact.security.oauth2.util.IsySecurityTokenUtil;

/**
 * Default implementation of the {@link Authentifizierungsmanager} that should suffice for most use cases.
 * <p>
 * It provides different ways to authorize OAuth 2.0 Clients (via the Client Credentials flow) and
 * System users (representing resource owners) via the Resource Owner Password Credentials flow.
 * <p>
 * The primary way for authentication is {@link #authentifiziere(String)}, which depends on OAuth 2.0 Client Registrations
 * to be configured in the application properties.
 * The other {@link #authentifiziere(ClientRegistration) method takes a Client Registration
 * with the provided credentials and thus do not depend on any Registration to be configured in the application properties.
 * Both {@link #authentifiziere(String, AdditionalCredentials) and
 * {@link #authentifiziere(ClientRegistration, AdditionalCredentials) offer the option of passing
 * authentication credentials like username, password and bhknz.
 * The methods {@code authentifiziereClient}/{@code authentifiziereSystem} construct the necessary
 * Client Registration with the provided credentials and issuer location and thus do not depend on any to be
 * configured in the application properties. Note that these are marked {@link Deprecated}.
 */
public class IsyOAuth2Authentifizierungsmanager implements Authentifizierungsmanager, DisposableBean {

    /**
     * ProviderManager configured with supported {@link AuthenticationProvider}s.
     * It is intended behavior that the ProviderManager may not support all authentication methods made available by the methods of
     * this class, in which case an {@link AuthenticationException} is thrown.
     *
     * @see IsyOAuth2Authentifizierungsmanager more details in the class doc
     */
    private final ProviderManager providerManager;

    /**
     * Salt value to increase security of token hash.
     */
    private final byte[] salt;

    /**
     * Used to build a cache.
     */
    private final CacheManager cacheManager;

    /**
     * Cache used to provide authentication data for repeated requests.
     * Can be configured via properties 'ttl' and 'maxelements'.
     */
    private final Cache<String, Authentication> authenticationCache;

    /**
     * Returns whether the cache is enabled or not.
     */
    private final boolean cacheEnabled;

    /**
     * The alias for the cache.
     */
    private static final String CACHE_ALIAS = "de.bund.bva.isyfact.security.oauth2.authentifizierung";


    /**
     * Global isy-security Configuration properties.
     */
    private final IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps;

    /**
     * Repository containing the OAuth 2.0 client registrations.
     * Used to get the authorization grant type for a client registration ID.
     * Might be {@code null} if there are no configured client registrations in the application.
     */
    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Minimal remaining token validity in seconds of a cached token.
     */
    private final int tokenExpirationTimeOffset;

    public IsyOAuth2Authentifizierungsmanager(ProviderManager providerManager,
                                              IsyOAuth2ClientConfigurationProperties isyOAuth2ClientProps,
                                              @Nullable ClientRegistrationRepository clientRegistrationRepository,
                                              IsySecurityConfigurationProperties isySecurityConfigurationProps) {
        this.providerManager = providerManager;
        this.isyOAuth2ClientProps = isyOAuth2ClientProps;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.salt = generateSalt();

        CacheSetupResult cacheSetupResult = setupCache(isySecurityConfigurationProps);
        this.cacheManager = cacheSetupResult.cacheManager;
        this.authenticationCache = cacheSetupResult.cache;
        this.cacheEnabled = isySecurityConfigurationProps.getCache().getTtl() > 0;
        this.tokenExpirationTimeOffset = isySecurityConfigurationProps.getCache().getTokenExpirationTimeOffset();
    }

    @Override
    public void authentifiziere(String oauth2ClientRegistrationId) throws AuthenticationException {
        Authentication unauthenticatedToken = getAuthenticationTokenForRegistrationId(oauth2ClientRegistrationId);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    public void authentifiziere(String oauth2ClientRegistrationId, AdditionalCredentials credentials) throws AuthenticationException {
        Authentication unauthenticatedToken = getAuthTokenForRegistrationIdAndAdditionalCredentials(oauth2ClientRegistrationId, credentials);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    public void authentifiziere(String oauth2ClientRegistrationId, Duration expirationTimeOffset) throws AuthenticationException {
        if (!IsySecurityTokenUtil.hasOAuth2Token() || IsySecurityTokenUtil.hasTokenExpired(expirationTimeOffset)) {
            authentifiziere(oauth2ClientRegistrationId);
        }
    }

    @Override
    public void authentifiziere(ClientRegistration clientRegistration) throws AuthenticationException {
        Authentication unauthenticatedToken = getAuthTokenForClientRegistration(clientRegistration);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    public void authentifiziere(ClientRegistration clientRegistration, AdditionalCredentials credentials) throws AuthenticationException {
        Authentication unauthenticatedToken = getAuthTokenForClientRegistrationAndAdditionalCredentials(clientRegistration, credentials);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    @Deprecated
    public void authentifiziereClient(String issuerLocation, String clientId, String clientSecret) throws AuthenticationException {
        authentifiziereClient(issuerLocation, clientId, clientSecret, null);
    }

    @Override
    @Deprecated
    public void authentifiziereClient(String issuerLocation, String clientId, String clientSecret, @Nullable String bhknz) throws AuthenticationException {
        Assert.notNull(issuerLocation, "issuerLocation cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");

        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();

        Authentication unauthenticatedToken = new ClientCredentialsClientRegistrationAuthenticationToken(clientRegistration, bhknz);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    @Override
    @Deprecated
    public void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password) throws AuthenticationException {
        authentifiziereSystem(issuerLocation, clientId, clientSecret, username, password, null);
    }

    @Override
    @Deprecated
    public void authentifiziereSystem(String issuerLocation, String clientId, String clientSecret, String username, String password, @Nullable String bhknz)
            throws AuthenticationException {
        Assert.notNull(issuerLocation, "issuerLocation cannot be null");
        Assert.notNull(clientId, "clientId cannot be null");
        Assert.notNull(clientSecret, "clientSecret cannot be null");
        Assert.notNull(username, "username cannot be null");
        Assert.notNull(password, "password cannot be null");

        ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation(issuerLocation)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .build();

        Authentication unauthenticatedToken = new PasswordClientRegistrationAuthenticationToken(clientRegistration, username, password, bhknz);
        authenticateAndChangeAuthenticatedPrincipal(unauthenticatedToken);
    }

    /**
     * Clears the cache when cache is enabled and not null.
     */
    public void clearCache() {
        if (cacheEnabled && authenticationCache != null) {
            authenticationCache.clear();
        }
    }

    /**
     * Closes the cache manager when the bean is destroyed.
     * Called by Spring when application context is being shut down.
     */
    @Override
    public void destroy() {
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    /**
     * Creates an appropriate authentication token for the authorization grant type configured for the registration ID.
     *
     * @param oauth2ClientRegistrationId registration ID to create the token for
     * @return an unauthenticated token that can be passed to the provider manager
     */
    private Authentication getAuthenticationTokenForRegistrationId(String oauth2ClientRegistrationId) {
        ClientRegistration clientRegistration = null;
        if (clientRegistrationRepository != null) {
            clientRegistration = clientRegistrationRepository.findByRegistrationId(oauth2ClientRegistrationId);
        }
        Assert.notNull(clientRegistration, "Could not find ClientRegistration with id '" + oauth2ClientRegistrationId + "'");

        // load additional props for this registration ID, can be null
        AdditionalRegistrationProperties props = isyOAuth2ClientProps.getRegistration().get(clientRegistration.getRegistrationId());
        String bhknz = null;
        if (props != null) {
            // the BHKNZ is optional but can be set for CC or ROPC
            bhknz = props.getBhknz();
        }

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            return new ClientCredentialsRegistrationIdAuthenticationToken(oauth2ClientRegistrationId, bhknz);
        } else if (AuthorizationGrantType.PASSWORD.equals(grantType)) {
            // ROPC requires the username and password to be set in the additional properties
            if (props != null && props.getUsername() != null && props.getPassword() != null) {
                return new PasswordClientRegistrationAuthenticationToken(clientRegistration, props.getUsername(), props.getPassword(), bhknz);
            } else {
                throw new BadCredentialsException(
                        String.format("No configured credentials (username, password) found for client with registrationId: %s.",
                                clientRegistration.getRegistrationId()));
            }


        } else {
            throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() + "' is not supported.");
        }
    }

    /**
     * Creates an appropriate authentication token for the authorization grant type configured for the registration ID
     * and additional credentials.
     *
     * @param oauth2ClientRegistrationId registration ID to create the token for
     * @param credentials                credentials used to create the token
     * @return an unauthenticated token that can be passed to the provider manager
     */
    private Authentication getAuthTokenForRegistrationIdAndAdditionalCredentials(String oauth2ClientRegistrationId, AdditionalCredentials credentials) {
        Assert.notNull(oauth2ClientRegistrationId, "Parameter oauth2ClientRegistrationId cannot be null");
        Assert.notNull(credentials, "Parameter credentials cannot be null");

        ClientRegistration clientRegistration = null;
        if (clientRegistrationRepository != null) {
            clientRegistration = clientRegistrationRepository.findByRegistrationId(oauth2ClientRegistrationId);
        }
        Assert.notNull(clientRegistration, "Could not find ClientRegistration with id '" + oauth2ClientRegistrationId + "'");

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();

        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            if (credentials.hasUsernamePassword()) {
                throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() + "' incorrectly contains credentials.");
            } else {
                return new ClientCredentialsRegistrationIdAuthenticationToken(oauth2ClientRegistrationId, credentials.getBhknz());
            }
        } else if (AuthorizationGrantType.PASSWORD.equals(grantType)) {
            if (!credentials.hasUsernamePassword()) {
                throw new BadCredentialsException(
                        String.format("No configured credentials (username, password) found for client with registrationId: %s.",
                                clientRegistration.getRegistrationId()));
            } else {
                return new PasswordClientRegistrationAuthenticationToken(clientRegistration,
                        credentials.getUsername(), credentials.getPassword(), credentials.getBhknz());
            }
        } else {
            throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() + "' is not supported.");
        }
    }

    /**
     * Creates an appropriate authentication token for the authorization grant type configured for the passed
     * client registration.
     *
     * @param clientRegistration registration used to create the authentication object
     * @return an unauthenticated token that can be passed to the provider manager
     */
    private Authentication getAuthTokenForClientRegistration(ClientRegistration clientRegistration) {
        Assert.notNull(clientRegistration, "Parameter clientRegistration cannot be null");

        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            return new ClientCredentialsClientRegistrationAuthenticationToken(clientRegistration, null);
        } else {
            throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() +
                    "' requires additional credentials.");
        }
    }

    /**
     * Creates an appropriate authentication token for the authorization grant type configured for the passed
     * client registration and additional credentials.
     *
     * @param clientRegistration registration used to create the authentication object
     * @param credentials        credentials used to create the token
     * @return an unauthenticated token that can be passed to the provider manager.
     */
    private Authentication getAuthTokenForClientRegistrationAndAdditionalCredentials(
            ClientRegistration clientRegistration,
            AdditionalCredentials credentials) {
        Assert.notNull(clientRegistration, "Parameter clientRegistration cannot be null");
        Assert.notNull(credentials, "Parameter credentials cannot be null");

        Authentication unauthenticatedToken;
        AuthorizationGrantType grantType = clientRegistration.getAuthorizationGrantType();

        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(grantType)) {
            unauthenticatedToken = new ClientCredentialsClientRegistrationAuthenticationToken(clientRegistration, credentials.getBhknz());
        } else if (AuthorizationGrantType.PASSWORD.equals(grantType)) {
            if (credentials.hasUsernamePassword()) {
                unauthenticatedToken = new PasswordClientRegistrationAuthenticationToken(clientRegistration,
                        credentials.getUsername(), credentials.getPassword(), credentials.getBhknz());
            } else {
                throw new BadCredentialsException(
                        "No credentials (username, password) provided for client with password grant type.");
            }
        } else {
            throw new IllegalArgumentException("The AuthorizationGrantType '" + grantType.getValue() + "' is not supported.");
        }
        return unauthenticatedToken;
    }

    private byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[64];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Initializes the authentication cache based on configured properties.
     * If time to live (TTL) equals 0, caching is disabled and null-values are returned.
     *
     * @param properties security configuration properties containing cache settings
     * @return CacheSetupResult containing both cacheManager and cache or null if caching is disabled
     */
    private CacheSetupResult setupCache(IsySecurityConfigurationProperties properties) {
        if (properties.getCache().getTtl() == 0) {
            return new CacheSetupResult(null, null);
        }

        CacheConfiguration<String, Authentication> cacheConfiguration =
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                String.class,
                                Authentication.class,
                                ResourcePoolsBuilder.heap(properties.getCache().getMaxelements()))
                        .withExpiry(
                                ExpiryPolicyBuilder.timeToLiveExpiration(
                                        Duration.ofSeconds(properties.getCache().getTtl())))
                        .build();

        CacheManager configuredCacheManager =
                CacheManagerBuilder.newCacheManagerBuilder()
                        .withCache(CACHE_ALIAS, cacheConfiguration)
                        .build(true);

        Cache<String, Authentication> cache = configuredCacheManager.getCache(CACHE_ALIAS, String.class, Authentication.class);

        return new CacheSetupResult(configuredCacheManager, cache);
    }

    /**
     * Tries to authorize the given request with one of the providers configured in the {@link #providerManager} and update
     * the authenticated principal.
     *
     * @param unauthenticatedToken the authentication request object
     * @throws AuthenticationException if no provider supports the authentication request or the authentication failed
     */
    private void authenticateAndChangeAuthenticatedPrincipal(Authentication unauthenticatedToken) throws AuthenticationException {
        Authentication authentication;

        if (cacheEnabled) {
            authentication = authenticateWithCache(unauthenticatedToken);
        } else {
            authentication = performAuthentication(unauthenticatedToken);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Performs authentication with prior check of the cache for existing successful authentication data.
     * If cached authentication exists and matches the incoming authentication, it returns the cached value.
     * Otherwise, it performs a new authentication and caches the result if successful.
     *
     * @param unauthenticatedToken the authentication token to process
     */
    private Authentication authenticateWithCache(Authentication unauthenticatedToken) throws AuthenticationException {
        AbstractIsyAuthenticationToken isyAuthenticationToken = (AbstractIsyAuthenticationToken) unauthenticatedToken;
        // ClientCredentialsRegistrationIdAuthenticationToken will return null-value for cacheKey
        // and so it will not be cached by the logic of Isy-Security
        // because it is cached by Spring's OAuth2AuthorizedClientManager
        byte[] cacheKeyBytes = isyAuthenticationToken.generateCacheKey(this.salt);

        if (cacheKeyBytes == null) {
            return performAuthentication(unauthenticatedToken);
        }

        String cacheKey = Base64.getEncoder().encodeToString(cacheKeyBytes);

        Authentication cachedAuthentication = authenticationCache.get(cacheKey);
        if (cachedAuthentication != null) {
            SecurityContextHolder.getContext().setAuthentication(cachedAuthentication);
            if (IsySecurityTokenUtil.hasTokenExpired(Duration.ofSeconds(tokenExpirationTimeOffset))) {
                authenticationCache.remove(cacheKey);
            } else {
                return cachedAuthentication;
            }
        }

        Authentication authentication = performAuthentication(unauthenticatedToken);
        if (authentication != null && authentication.isAuthenticated()) {
            authenticationCache.put(cacheKey, authentication);
        }

        return authentication;
    }

    /**
     * Performs the actual authentication process by delegating to the provider manager.
     *
     * @param unauthenticatedToken the authentication token to authenticate
     */
    private Authentication performAuthentication(Authentication unauthenticatedToken) {
        return providerManager.authenticate(unauthenticatedToken);
    }

    /**
     * Helper class to return both cache and cacheManager from setupCache method.
     */
    private static class CacheSetupResult {
        /**
         * The Cache Manager.
         */
        private final CacheManager cacheManager;

        /**
         * The Cache.
         */
        private final Cache<String, Authentication> cache;

        CacheSetupResult(CacheManager cacheManager, Cache<String, Authentication> cache) {
            this.cacheManager = cacheManager;
            this.cache = cache;
        }

        public CacheManager getCacheManager() {
            return cacheManager;
        }

        public Cache<String, Authentication> getCache() {
            return cache;
        }
    }
}
