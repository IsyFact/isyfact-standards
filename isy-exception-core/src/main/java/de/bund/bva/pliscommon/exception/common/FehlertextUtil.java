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
package de.bund.bva.pliscommon.exception.common;

/**
 * Diese Klasse bietet Methoden zur Erstellung von Fehlertexten auf Basis von AusnahmeIDs und Parametern,
 * sofern vorhanden.
 * <p>
 * <b>Anmerkung:</b> Die Methoden zur Erstellung von Fehlertexten in dieser Klasse, behandeln
 * {@link Throwable}. Dies ist notwendig, da diese Methoden in Konstruktoren für Exceptions aufgerufen
 * werden.
 * 
 */
public class FehlertextUtil {
    /**
     * Seperator fü die Ausnahme-ID und die UUID.
     */
    private static final String SEPERATOR = "#";

    /**
     * Leerzeichen.
     */
    private static final String SPACE = " ";

    /**
     * Erstellt aus einer AusnahmeID einen Fehlertext inklusive AusnahmeID nach dem Schema <i>#AusnahmeID
     * Fehlertext #UUID</i>.
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Fehlertext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird, aus einem ResourceBundle zu
     *            laden.
     * @param fehlertext
     *            Der original Fehlertext.
     * @param uniqueId
     *            Die UUID der Ausnahme.
     * @return Fehlertext inklusive Ausnahme-ID und UUID im Format <code>#Ausnahme-ID Fehlertext #UUID</code>
     */
    public static String erweitereFehlertext(String ausnahmeID, String fehlertext, String uniqueId) {
        return SEPERATOR + ausnahmeID + SPACE + fehlertext + SPACE + SEPERATOR + uniqueId;
    }
}
