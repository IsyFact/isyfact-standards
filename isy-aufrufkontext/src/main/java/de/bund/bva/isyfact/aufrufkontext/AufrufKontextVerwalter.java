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
 */
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
     * Returns the OAuth2 Bearer Token.
     *
     * @return the OAuth2 Bearer Token.
     */
    String getBearerToken();

    /**
     * Sets the OAuth 2 Bearer Token. Removes the prefix "Bearer " if it exists.
     *
     * @param bearerToken the Base64 encoded OAuth 2 bearer token
     */
    void setBearerToken(String bearerToken);

}
