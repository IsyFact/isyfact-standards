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
package de.bund.bva.pliscommon.aufrufkontext;

/**
 * Container for the current Aufrufkontext.
 * <p>
 * This class should be used with the "thread" bean scope:
 *
 * <pre>{@code
 * <aop:aspectj-autoproxy/>
 * <bean id="aufrufKontextVerwalter" scope="thread" class="de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl">
 *     <aop:scoped-proxy/>
 * </bean>
 * }</pre>
 *
 * <p>
 * See http://www.springbyexample.org/static/0.91/html/ar20.html for Spring 2.5.x
 *
 * @param <T>
 *         implementation of the {@link AufrufKontext} that is managed by the class
 */
public interface AufrufKontextVerwalter<T extends AufrufKontext> {

    /**
     * Returns the current AufrufKontext.
     *
     * @return the current AufrufKontext
     */
    T getAufrufKontext();

    /**
     * Sets the current AufrufKontext.
     *
     * @param aufrufKontext the AufrufKontext to set
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
