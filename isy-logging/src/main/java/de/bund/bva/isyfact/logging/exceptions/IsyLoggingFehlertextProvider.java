package de.bund.bva.isyfact.logging.exceptions;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
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
 * #L%
 */

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * FehlertextProvider für IsyLogging, um die bibliotheksspezifischen Fehlermeldungen aufzulösen.
 * 
 */
public class IsyLoggingFehlertextProvider implements FehlertextProvider {

    /** Pfad zu den Fehlertexten. */
    private static final String NACHRICHTEN_RESOURCE = "resources/isylogging/nachrichten/fehler";

    /**
     * Das ResourceBoundle mit den Fehlertexten der Proxy-API.
     */
    private static final ResourceBundle FEHLERTEXT_BUNDLE = ResourceBundle.getBundle(
        NACHRICHTEN_RESOURCE, Locale.GERMANY);

    /**
     * liest Nachricht aus.
     * 
     * @param schluessel
     *            der Schl&uuml;ssel des Fehlertextes
     * @return die Nachricht
     */
    private static String getMessage(String schluessel) {
        return FEHLERTEXT_BUNDLE.getString(schluessel);
    }

    /**
     * liest Nachricht aus und ersetzt die Platzhalter durch die &uuml;bergebenen Parameter.
     * 
     * @param schluessel
     *            der Schl&uuml;ssel des Fehlertextes
     * @param parameter
     *            der Wert f&uuml;r die zu ersetzenden Platzhalter
     * @return die Nachricht
     */
    public String getMessage(String schluessel, String... parameter) {
        if (parameter != null && parameter.length > 0) {
            return MessageFormat.format(getMessage(schluessel), (Object[]) parameter);
        } else {
            return getMessage(schluessel);
        }
    }
}
