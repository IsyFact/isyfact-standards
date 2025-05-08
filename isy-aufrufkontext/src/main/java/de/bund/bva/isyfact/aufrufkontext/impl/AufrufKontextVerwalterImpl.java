package de.bund.bva.isyfact.aufrufkontext.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;

/**
 * Provides the call context to an application.
 *
 * @param <T>
 *         implementation of the {@link AufrufKontext} that is managed by the class
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public class AufrufKontextVerwalterImpl<T extends AufrufKontext> implements AufrufKontextVerwalter<T> {

    /** The current call context ('AufrufKontext' object). */
    private T aufrufKontext;

    /** The current OAuth 2 Bearer Token. */
    private String bearerToken;

    @Override
    public T getAufrufKontext() {
        return aufrufKontext;
    }

    @Override
    public void setAufrufKontext(T aufrufKontext) {
        this.aufrufKontext = aufrufKontext;
    }

    @Override
    public String getBearerToken() {
        return this.bearerToken;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

}
