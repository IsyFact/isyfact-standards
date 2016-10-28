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

import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.common.exception.AufrufKontextKeinDefaultKonstruktorException;

/**
 * Erzeugt einen {@link AufrufKontext}.
 * <p>
 * Wird nichts weriter konfiguriert, wird ein {@link AufrufKontextImpl} verwendet. Wird aber durch das
 * Property aufrufKontextKlasse eine Klasse definiert, wird diese verwendet.
 * 
 * @param <T>
 *            die anwendungsspezifische AufrufKontext-Klasse
 * 
 */
public class AufrufKontextFactoryImpl<T extends AufrufKontext> implements AufrufKontextFactory<T> {

    /** Die Ausprägung des zu erstellenden AufrufKontextes. */
    private Class<?> aufrufKontextKlasse = AufrufKontextImpl.class;

    /**
     * {@inheritDoc}
     * @return ein {@link AufrufKontextImpl} Objekt.
     */
    @SuppressWarnings("unchecked")
    public T erzeugeAufrufKontext() {
        T result;
        try {
            result = (T) aufrufKontextKlasse.newInstance();
        } catch (Exception e) {
            throw new AufrufKontextKeinDefaultKonstruktorException();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void nachAufrufKontextVerarbeitung(T aufrufKontext) {
        // es findet keine Nachbearbeitung statt.
    }

    /**
     * Setzt das Feld {@link #aufrufKontextKlasse}.
     * @param aufrufKontextKlasse
     *            Neuer Wert für aufrufKontextKlasse
     */
    public void setAufrufKontextKlasse(Class<?> aufrufKontextKlasse) {
        this.aufrufKontextKlasse = aufrufKontextKlasse;
    }

}
