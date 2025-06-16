package de.bund.bva.isyfact.aufrufkontext;

/**
 * Container for the current call context ('AufrufKontext').
 *
 * This class should be used as a bean with scope "thread":
 *
 * <pre>{@code
 * <aop:aspectj-autoproxy/>
 * <bean id="aufrufKontextVerwalter" scope="thread" class="de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextVerwalterImpl">
 *     <aop:scoped-proxy/>
 * </bean>
 * }</pre>
 *
 * @param <T>
 *         implementation of the {@link AufrufKontext} that is managed by the class
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public interface AufrufKontextVerwalter<T extends AufrufKontext> {

    /**
     * Returns the current call context ('AufrufKontext').
     *
     * @return the current call context ('AufrufKontext').
     */
    T getAufrufKontext();

    /**
     * Sets the current call context ('AufrufKontext').
     *
     * @param aufrufKontext new call context ("AufrufKontext').
     */
    void setAufrufKontext(T aufrufKontext);

    /**
     * Returns the OAuth 2 bearer token.
     *
     * @return the Base64 encoded OAuth 2 bearer token, or {@code null} if it is not set
     */
    String getBearerToken();

    /**
     * Sets the OAuth 2 bearer token.
     *
     * @param bearerToken the Base64 encoded OAuth 2 bearer token
     */
    void setBearerToken(String bearerToken);

}
