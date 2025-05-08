package de.bund.bva.isyfact.aufrufkontext.stub;

import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;

/**
 * Provides a call context with fixed values to an application.
 * <p>
 * This class is meant for testing and should not be used in productive system.
 *
 * @param <T>
 *         implementation of the {@link AufrufKontext} that is managed by the class
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public class AufrufKontextVerwalterStub<T extends AufrufKontext>
        implements AufrufKontextVerwalter<T>, InitializingBean {

    /** Value for the implementing authority ('Durchführende Behörde'). */
    private String durchfuehrendeBehoerde = "123456";

    /** Value for the user indicator ('Benutzerkennung'). */
    private String durchfuehrenderBenutzerKennung = "martha.mustermann@bva.bund.de";

    /** Value for the user password. */
    private String durchfuehrenderBenutzerPasswort;

    /** Value for the executing agent ('durchführender Sachbearbeiter). */
    private String durchfuehrenderSachbearbeiterName = "Martha Mustermann";

    /** Value for the internal unique user id ('interne Kennung des Benutzers'). */
    private String durchfuehrenderBenutzerInterneKennung = "Martha007";

    /** Value for the roles of the user. */
    private String[] rollen = {};

    /**
     * If {@code true} then a new call context is created for each query - so it is not mutable.
     */
    private boolean festerAufrufKontext;

    /** Access to the AufrufKontextFactory to be able to create application-specific contexts. */
    private AufrufKontextFactory<T> aufrufKontextFactory;

    /** Call context which will be returned. */
    private T aufrufKontext;

    /** Test value of the OAuth 2 Bearer Token. */
    private String bearerToken = "AUFRUFKONTEXTVERWALTER_STUB_BEARER_TOKEN";

    /**
     * Sets the field {@link #festerAufrufKontext}.
     * @param festerAufrufKontext
     *            New value for field {@link #festerAufrufKontext}.
     */
    public void setFesterAufrufKontext(boolean festerAufrufKontext) {
        this.festerAufrufKontext = festerAufrufKontext;
    }

    /**
     * Sets the field {@link #aufrufKontextFactory}.
     * @param aufrufKontextFactory
     *            New value for field {@link #aufrufKontextFactory}.
     */

    public void setAufrufKontextFactory(AufrufKontextFactory<T> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

    /**
     * Sets the field {@link #durchfuehrendeBehoerde}.
     * @param durchfuehrendeBehoerde
     *            New value for field {@link #durchfuehrendeBehoerde}.
     */
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
        this.durchfuehrendeBehoerde = durchfuehrendeBehoerde;
    }

    /**
     * Sets the field {@link #durchfuehrenderBenutzerKennung}.
     * @param durchfuehrenderBenutzerKennung
     *            New value for field {@link #durchfuehrenderBenutzerKennung}.
     */
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
        this.durchfuehrenderBenutzerKennung = durchfuehrenderBenutzerKennung;
    }

    /**
     * Sets the field {@link #durchfuehrenderBenutzerPasswort}.
     * @param durchfuehrenderBenutzerPasswort
     *            New value for {@link #durchfuehrenderBenutzerPasswort}.
     */
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
        this.durchfuehrenderBenutzerPasswort = durchfuehrenderBenutzerPasswort;
    }

    /**
     * Sets the field {@link #durchfuehrenderSachbearbeiterName}.
     * @param durchfuehrenderSachbearbeiterName
     *            New value for field {@link #durchfuehrenderSachbearbeiterName}.
     */
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
        this.durchfuehrenderSachbearbeiterName = durchfuehrenderSachbearbeiterName;
    }

    /**
     * Sets the field {@link #durchfuehrenderBenutzerInterneKennung}.
     * @param durchfuehrenderBenutzerInterneKennung
     *            New value for {@link #durchfuehrenderBenutzerInterneKennung}.
     */
    public void setDurchfuehrenderBenutzerInterneKennung(String durchfuehrenderBenutzerInterneKennung) {
        this.durchfuehrenderBenutzerInterneKennung = durchfuehrenderBenutzerInterneKennung;
    }

    /**
     * Sets the field {@link #rollen}.
     * @param rollen
     *            New value for field {@link #rollen}.
     */
    public void setRollen(String... rollen) {
        this.rollen = rollen;
    }

    /**
     * Returns the current call context ("AufrufKontext').
     * @return the current call context.
     */
    public T getAufrufKontext() {
        if (festerAufrufKontext) {
            return erzeugeAufrufKontext();
        } else {
            return aufrufKontext;
        }
    }

    /**
     * Sets the field {@link #aufrufKontext}.
     * @param aufrufKontext new call context ('AufrufKontext').
     */
    public void setAufrufKontext(T aufrufKontext) {
        this.aufrufKontext = aufrufKontext;
    }

    /**
     * Creates call context ('Aufrufkontext') after all properties are set.
     */
    public void afterPropertiesSet() {
        aufrufKontext = erzeugeAufrufKontext();
    }

    /**
     * Creates a call context ('AufrufKontext') with values.
     *
     * @return the created call context ('AufrufKontext').
     */
    private T erzeugeAufrufKontext() {
        T result = aufrufKontextFactory.erzeugeAufrufKontext();

        result.setDurchfuehrendeBehoerde(durchfuehrendeBehoerde);
        result.setDurchfuehrenderBenutzerKennung(durchfuehrenderBenutzerKennung);
        result.setDurchfuehrenderBenutzerPasswort(durchfuehrenderBenutzerPasswort);
        result.setDurchfuehrenderSachbearbeiterName(durchfuehrenderSachbearbeiterName);
        result.setDurchfuehrenderBenutzerInterneKennung(durchfuehrenderBenutzerInterneKennung);
        result.setRolle(rollen);
        result.setRollenErmittelt(rollen != null);

        aufrufKontextFactory.nachAufrufKontextVerarbeitung(result);

        return result;
    }

    @Override
    public String getBearerToken() {
        return bearerToken;
    }

    @Override
    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

}
