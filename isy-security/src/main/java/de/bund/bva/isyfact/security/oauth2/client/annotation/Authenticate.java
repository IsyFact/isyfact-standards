package de.bund.bva.isyfact.security.oauth2.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Marks a method that requires a fixed OAuth 2.0 client to be authenticated.
 * <p>
 * The only thing required for authentication to work is the Client Registration ID of an OAuth 2.0 client.
 * It can be provided as is or via a property placeholder in a couple of ways.
 * For example:
 *
 * <pre class="code">
 *     &#064;Authenticate("my-auth-client")
 *     void methodThatNeedsAuthentication() { }

 *     &#064;Authenticate("${test.auth.client-id}")
 *     void methodThatNeedsAuthentication() { }
 *
 *     &#064;Authenticate(value = "my-auth-client")
 *     void methodThatNeedsAuthentication() { }
 *
 *     &#064;Authenticate(oauth2ClientRegistrationId = "my-auth-client")
 *     void methodThatNeedsAuthentication() { }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authenticate {

    /**
     * Alias for {@link #oauth2ClientRegistrationId}.
     *
     * @see #oauth2ClientRegistrationId
     */
    @AliasFor("oauth2ClientRegistrationId")
    String value() default "";

    /**
     * The Client Registration ID of the OAuth 2.0 client to authenticate.
     * Can be provided as is or via a property placeholder.
     * This has to match a client configured in the application properties via Spring Security OAuth 2 configuration.
     * <p>
     * May also be configured via {@link #value} if a less descriptive annotation is desired.
     *
     * @see #value
     */
    @AliasFor("value")
    String oauth2ClientRegistrationId() default "";

}
