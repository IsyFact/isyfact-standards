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
package de.bund.bva.isyfact.polling.common.exception;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.pliscommon.exception.FehlertextProvider;

/**
 * Diese Klasse stellt Methoden zum Auslesen von Fehlertexten auf Basis von AusnahmeIDs und
 * Parametern, zur Verfügung.
 * 
 */
public class FehlertextProviderImpl implements FehlertextProvider {

    /**
     * Das ResourceBoundle mit den Fehlertexten der Proxy-API.
     */
    private static final ResourceBundle FEHLERTEXT_BUNDLE =
        ResourceBundle.getBundle("resources/isy-polling/nachrichten/fehler",
            Locale.GERMANY);

    /**
     * liest Nachricht aus und ersetzt die Platzhalter durch die übergebenen Parameter.
     * 
     * @param schluessel
     *            der Schlüssel des Fehlertextes
     * @return die Nachricht
     */
    public String getMessage(String schluessel) {
        return FEHLERTEXT_BUNDLE.getString(schluessel);
    }

    /**
     * liest Nachricht aus und ersetzt die Platzhalter durch die übergebenen Parameter.
     * 
     * @param schluessel
     *            der Schlüssel des Fehlertextes
     * @param parameter
     *            der Wert für die zu ersetzenden Platzhalter
     * @return die Nachricht
     */
    public String getMessage(String schluessel, String parameter) {
        if (parameter != null) {
            return MessageFormat.format(getMessage(schluessel), parameter);
        } else {
            return getMessage(schluessel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage(String schluessel, String... parameter) {
        if (parameter != null && parameter.length > 0) {
            return MessageFormat.format(getMessage(schluessel), (Object[]) parameter);
        } else {
            return getMessage(schluessel);
        }
    }
}
