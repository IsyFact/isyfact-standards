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
package de.bund.bva.pliscommon.persistence.exception;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.bund.bva.pliscommon.exception.FehlertextProvider;

/**
 * Fehlertext-Provider für die plis-persistence.
 * 
 */
public class PersistenzFehlertextProvider implements FehlertextProvider {
    /**
     * Das ResourceBoundle mit den Fehlertexten der AufrufkontextAPI.
     */
    public static final ResourceBundle FEHLERTEXT_BUNDLE = ResourceBundle.getBundle(
        "resources/plis-persistence/nachrichten/fehler", Locale.GERMANY);

    /**
     * {@inheritDoc}
     */
    public String getMessage(String schluessel, String... parameter) {
        return MessageFormat.format(FEHLERTEXT_BUNDLE.getString(schluessel), (Object[]) parameter);
    }

}
