/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.pliscommon.aufrufkontext.stub;

import org.springframework.beans.factory.InitializingBean;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;

/**
 * Provides a fixed AufrufKontext to an application.
 * <p>
 * This class is meant for development purposes and should not be used in production.
 *
 * @param <T>
 *         implementation of an {@link AufrufKontext}
 */
public class AufrufKontextVerwalterStub<T extends AufrufKontext>
        implements AufrufKontextVerwalter<T>, InitializingBean {

    /** Value for the executing authority. */
    private String durchfuehrendeBehoerde = "123456";

    /** Value for the executing user's login. */
    private String durchfuehrenderBenutzerKennung = "martha.mustermann@bva.bund.de";

    /** Value for the executing user's password. */
    private String durchfuehrenderBenutzerPasswort = null;

    /** Value for the executing Sachbearbeiter's name. */
    private String durchfuehrenderSachbearbeiterName = "Martha Mustermann";

    /** Value for the executing user's internal unique id. */
    private String durchfuehrenderBenutzerInterneKennung = "Martha007";

    /** Value for the roles of the user. */
    private String[] rollen = {};

    /** If {@code ture} a new AufrufKontext is created for each request, making it effectively immutable. */
    private boolean festerAufrufKontext;

    /** Access to the AufrufKontextFactory, for creating application specific contexts. */
    private AufrufKontextFactory<T> aufrufKontextFactory;

    /** AufrufKontext that will be returned. */
    private T aufrufKontext;

    /** Dummy value for the OAuth 2 Bearer Token. */
    private String bearerToken = "AUFRUFKONTEXTVERWALTER_STUB_BEARER_TOKEN";

    /**
     * Sets the field {@link #festerAufrufKontext}.
     *
     * @param festerAufrufKontext
     *         {@code true} for a fixed AufrufKontext, otherwise {@code false}
     */
    public void setFesterAufrufKontext(boolean festerAufrufKontext) {
        this.festerAufrufKontext = festerAufrufKontext;
    }

    /**
     * Sets the field {@link #aufrufKontextFactory}.
     *
     * @param aufrufKontextFactory
     *         the AufrufKontextFactory to use
     */
    public void setAufrufKontextFactory(AufrufKontextFactory<T> aufrufKontextFactory) {
        this.aufrufKontextFactory = aufrufKontextFactory;
    }

    /**
     * Sets the field {@link #durchfuehrendeBehoerde}.
     *
     * @param durchfuehrendeBehoerde
     *         name of the executing authority
     */
    public void setDurchfuehrendeBehoerde(String durchfuehrendeBehoerde) {
        this.durchfuehrendeBehoerde = durchfuehrendeBehoerde;
    }

    /**
     * Sets the field {@link #durchfuehrenderBenutzerKennung}.
     *
     * @param durchfuehrenderBenutzerKennung
     *         login of the executing user
     */
    public void setDurchfuehrenderBenutzerKennung(String durchfuehrenderBenutzerKennung) {
        this.durchfuehrenderBenutzerKennung = durchfuehrenderBenutzerKennung;
    }

    /**
     * Sets the field {@link #durchfuehrenderBenutzerPasswort}.
     *
     * @param durchfuehrenderBenutzerPasswort
     *         password of the executing user
     */
    public void setDurchfuehrenderBenutzerPasswort(String durchfuehrenderBenutzerPasswort) {
        this.durchfuehrenderBenutzerPasswort = durchfuehrenderBenutzerPasswort;
    }

    /**
     * Sets the field {@link #durchfuehrenderSachbearbeiterName}.
     *
     * @param durchfuehrenderSachbearbeiterName
     *         name of the executing Sachbearbeiter
     */
    public void setDurchfuehrenderSachbearbeiterName(String durchfuehrenderSachbearbeiterName) {
        this.durchfuehrenderSachbearbeiterName = durchfuehrenderSachbearbeiterName;
    }

    /**
     * Sets the field {@link #durchfuehrenderBenutzerKennung}.
     *
     * @param durchfuehrenderBenutzerInterneKennung
     *         login for the executing user
     */
    public void setDurchfuehrenderBenutzerInterneKennung(String durchfuehrenderBenutzerInterneKennung) {
        this.durchfuehrenderBenutzerInterneKennung = durchfuehrenderBenutzerInterneKennung;
    }

    /**
     * Sets the field {@link #rollen}.
     *
     * @param rollen
     *         roles to set
     */
    public void setRollen(String... rollen) {
        this.rollen = rollen;
    }

    @Override
    public T getAufrufKontext() {
        if (festerAufrufKontext) {
            return erzeugeAufrufKontext();
        } else {
            return aufrufKontext;
        }
    }

    @Override
    public void setAufrufKontext(T aufrufKontext) {
        this.aufrufKontext = aufrufKontext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        aufrufKontext = erzeugeAufrufKontext();
    }

    /**
     * Creates the AufrufKontext with the configured values.
     *
     * @return the AufrufKontext
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
