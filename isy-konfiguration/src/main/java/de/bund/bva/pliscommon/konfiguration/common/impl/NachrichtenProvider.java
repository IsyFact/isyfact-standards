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
package de.bund.bva.pliscommon.konfiguration.common.impl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Kapselt den Zugriff auf das Resources-Bundle mit den Nachrichten.
 * 
 * 
 */
public class NachrichtenProvider {

    /**
     * Das ResourceBoundle mit den Texten.
     */
    private static final ResourceBundle FEHLERTEXT_BUNDLE =
            ResourceBundle
                    .getBundle("resources/isy-konfiguration/nachrichten/konfiguration", Locale.GERMANY);

    /**
     * Liefert die Nachricht mit dem Schlüssel.
     * @param schluessel
     *            Nachrichten-Schlüssel.
     * @return Die Nachricht.
     */
    public static String getMessage(String schluessel) {
        return FEHLERTEXT_BUNDLE.getString(schluessel);
    }

    /**
     * Liefert die Nachricht mit dem Schlüssel. Darin werden die angegebenen
     * Parameter ersetzt.
     * @param schluessel
     *            Nachrichten-Schlüssel.
     * @param parameter
     *            Parameter für die Nachricht.
     * @return Die Nachricht.
     */
    public static String getMessage(String schluessel, String... parameter) {
        return MessageFormat.format(getMessage(schluessel), (Object[]) parameter);
    }

}
