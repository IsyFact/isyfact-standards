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
package de.bund.bva.pliscommon.batchrahmen.batch.rahmen;

import de.bund.bva.pliscommon.batchrahmen.core.konstanten.NachrichtenSchluessel;
import de.bund.bva.pliscommon.batchrahmen.core.rahmen.impl.NachrichtenProvider;

/**
 * Enumeration der Batch-Returncodes.
 * 
 * 
 */
public enum BatchReturnCode {
    /** Verarbeitung ohne Fehler durchgeführt. */
    OK(0, NachrichtenProvider.getMessage(NachrichtenSchluessel.MSG_RC_OK)),

    /** Verarbeitung mit Fehlern durchgeführt. */
    FEHLER_AUSGEFUEHRT(1, NachrichtenProvider.getMessage(NachrichtenSchluessel.MSG_RC_FEHLER_AUSGEFUEHRT)),

    /** Verarbeitung mit Fehlern abgebrochen. */
    FEHLER_ABBRUCH(2, NachrichtenProvider.getMessage(NachrichtenSchluessel.MSG_RC_FEHLER_ABBRUCH)),

    /**
     * Batch konnte wegen Fehlern in den Aufrufparametern nicht gestartet
     * werden.
     */
    FEHLER_PARAMETER(3, NachrichtenProvider.getMessage(NachrichtenSchluessel.MSG_RC_FEHLER_PARAMETER)),

    /**
     * Batch konnte wegen Fehlern in der Batch-Konfiguration nicht gestartet
     * werden.
     */
    FEHLER_KONFIGURATION(4, NachrichtenProvider.getMessage(
            NachrichtenSchluessel.MSG_RC_FEHLER_KONFIGURATION)),
    
    /**
     * Batch wurde währends des Betriebs vom Nutzer abgebrochen. (kill -15 bzw. STRG+C)
     */
    FEHLER_MANUELLER_ABBRUCH(143, NachrichtenProvider.getMessage(
            NachrichtenSchluessel.MSG_RC_FEHLER_MANUELLER_ABBRUCH)), 
    
    /**
     * Batch wurde wegen der Überschreitung der maximalen Laufzeit abgebrochen.
     */
    FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN(144, NachrichtenProvider
        .getMessage(NachrichtenSchluessel.MSG_RC_FEHLER_MAX_LAUFZEIT_UEBERSCHRITTEN));

    /**
     * Zahlenwert des ReturnCodes. Wird letzendlich per System.exit()
     * zurückgeliefert.
     */
    private final int returnCode;

    /**
     * Textuelle Beschreibung des ReturnCodes.
     */
    private final String text;

    /**
     * Initialisierung eines ReturnCodes.
     * @param returnCode
     *            Zahlen Wert
     * @param text
     *            Beschreibung
     */
    private BatchReturnCode(int returnCode, String text) {
        this.returnCode = returnCode;
        this.text = text;
    }

    /**
     * Liefert den Zahlen-Wert des ReturnCodes.
     * @return Der Wert.
     */
    public int getWert() {
        return returnCode;
    }

    /**
     * Liefert die textuelle Beschreibung des RetunrCodes.
     * @return Der Text.
     */
    public String getText() {
        return text;
    }

}
