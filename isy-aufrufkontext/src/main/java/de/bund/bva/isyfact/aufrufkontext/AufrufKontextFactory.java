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
 * Erzeugt einen neuen AufrufKontext und stellt einen Hook bereit, um diesen nach der automatischen
 * Initialisierung nachzubearbeiten.
 * 
 * @param <T>
 *            die anwendungsspezifische AufrufKontext-Klasse
 *
 * @deprecated since IsyFact 3.1.0 in favor of Spring Security OAuth2 and the isy-security library.
 */
@Deprecated
public interface AufrufKontextFactory<T extends AufrufKontext> {

    /**
     * Erzeugt einen AufrufKontext.
     * @return der AufrufKontext.
     */
    T erzeugeAufrufKontext();

    /**
     * Hook-Methode, um zusätzliche Informationen in den AufrufKontext zu schreiben.
     * 
     * @param aufrufKontext
     *            der AufrufKontext
     */
    void nachAufrufKontextVerarbeitung(T aufrufKontext);
}
