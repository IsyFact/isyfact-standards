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
package de.bund.bva.pliscommon.serviceapi.core.aufrufkontext.helper;

import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;

/**
 * Speichert den AufrufKontext zum späteren Abruf für die Testfälle.
 * 
 * 
 */
public class DebugAufrufKontextVerwalter extends AufrufKontextVerwalterImpl<AufrufKontextImpl> {

    /** der letzte gesetzte AufrufKontext, der nicht {@code null} war. */
    private AufrufKontextImpl letzterAufrufKontext;

    @Override
    public void setAufrufKontext(AufrufKontextImpl aufrufKontext) {
        if (aufrufKontext != null) {
            letzterAufrufKontext = aufrufKontext;
        }
        super.setAufrufKontext(aufrufKontext);
    }

    /**
     * Liefert das Feld {@link #letzterAufrufKontext} zurück.
     * @return Wert von letzterAufrufKontext
     */
    public AufrufKontextImpl getLetzterAufrufKontext() {
        return letzterAufrufKontext;
    }
    
    /**
     * Setzt den AufrufKontext und den letztenAufrufKontext auf null.
     */
    public void resetAufrufKontext() {
        letzterAufrufKontext = null;
        super.setAufrufKontext(null);
    }

}
