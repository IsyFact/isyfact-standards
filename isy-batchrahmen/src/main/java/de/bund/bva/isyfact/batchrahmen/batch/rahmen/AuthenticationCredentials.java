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
package de.bund.bva.isyfact.batchrahmen.batch.rahmen;

/**
 * This class contains all the data necessary to authenticate a batch.
 */
public class AuthenticationCredentials {
    /**
     * Registration-ID for authentication.
     **/
    private String registrationId;

    /**
     * Contains the user ID of the batch user.
     */
    private String benutzerkennung;

    /**
     * Contains the Bhknz of the batch user.
     */
    private String behoerdenkennzeichen;

    /**
     * Contains the password of the batch user.
     */
    private String passwort;

    /**
     * Returns the field {@link #benutzerkennung}.
     *
     * @return value of benutzerkennung
     */
    public String getBenutzerkennung() {
        return benutzerkennung;
    }

    /**
     * Creates a new unfilled {@link AuthenticationCredentials} object.
     */
    public AuthenticationCredentials() {
        super();
    }

    /**
     * Creates a new filled {@link AuthenticationCredentials} object.
     *
     * @param benutzerkennung      the value for {@link #benutzerkennung}
     * @param behoerdenkennzeichen the value for {@link #behoerdenkennzeichen}
     * @param passwort             the value for {@link #passwort}
     */
    public AuthenticationCredentials(String benutzerkennung, String behoerdenkennzeichen, String passwort) {
        super();
        this.benutzerkennung = benutzerkennung;
        this.behoerdenkennzeichen = behoerdenkennzeichen;
        this.passwort = passwort;
    }

    /**
     * Sets the field {@link #benutzerkennung}.
     *
     * @param benutzerkennung New value for benutzerkennung
     */
    public void setBenutzerkennung(String benutzerkennung) {
        this.benutzerkennung = benutzerkennung;
    }

    /**
     * Returns the field {@link #behoerdenkennzeichen}.
     *
     * @return value of behoerdenkennzeichen
     */
    public String getBehoerdenkennzeichen() {
        return behoerdenkennzeichen;
    }

    /**
     * Sets the field {@link #behoerdenkennzeichen}.
     *
     * @param behoerdenkennzeichen New value for behoerdenkennzeichen
     */
    public void setBehoerdenkennzeichen(String behoerdenkennzeichen) {
        this.behoerdenkennzeichen = behoerdenkennzeichen;
    }

    /**
     * Returns the field {@link #passwort}.
     *
     * @return value of passwort
     */
    public String getPasswort() {
        return passwort;
    }

    /**
     * Sets the field {@link #passwort}.
     *
     * @param passwort New value for passwort
     */
    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    /**
     * Returns the field {@link #registrationId}
     *
     * @return value of registrationId
     */
    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * Sets the field {@link #registrationId}.
     *
     * @param passwort New value for registrationId
     */
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
