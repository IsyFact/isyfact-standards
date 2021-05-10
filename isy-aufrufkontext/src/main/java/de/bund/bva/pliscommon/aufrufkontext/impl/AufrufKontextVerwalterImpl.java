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
package de.bund.bva.pliscommon.aufrufkontext.impl;

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;

/**
 * Provides the AufrufKontext to an application.
 *
 * @param <T>
 *         implementation of an {@link AufrufKontextImpl}
 */
public class AufrufKontextVerwalterImpl<T extends AufrufKontextImpl> implements AufrufKontextVerwalter<T> {

    /** Matcher for the "bearer " prefix. */
    private static final String BEARER_MATCHER = "[bB][eE][aA][rR][eE][rR] ";

    /** The current AufrufKontext. */
    private T aufrufKontext;

    /** The current OAuth 2 Bearer Token. */
    private String bearerToken;

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
        this.bearerToken = bearerToken.replaceFirst(BEARER_MATCHER, "");
    }

}
