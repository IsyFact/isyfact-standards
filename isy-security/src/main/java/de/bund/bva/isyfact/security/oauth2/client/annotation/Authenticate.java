package de.bund.bva.isyfact.security.oauth2.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Marks a method that requires a fixed OAuth 2.0 client to be authenticated.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authenticate {

    @AliasFor("oauth2ClientRegistrationId")
    String value() default "";

    /**
     * The Client Registration ID of the OAuth 2.0 client to authenticate.
     * This has to match a client configured in Spring Security OAuth 2.
     */
    @AliasFor("value")
    String oauth2ClientRegistrationId() default "";

}
