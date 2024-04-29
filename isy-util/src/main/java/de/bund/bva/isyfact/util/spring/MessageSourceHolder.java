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
package de.bund.bva.isyfact.util.spring;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Diese Klasse hält eine Referenz auf die aktuelle MessageSource-Bean.
 * <p>
 * Zusätzlich bietet sie Convenience-Funktionen zum Auslesen von Resourcebundle- Einträgen aus der
 * MessageSource.
 * 
 */
public final class MessageSourceHolder implements MessageSourceAware {

    /** Statischer Verweis auf die MessageSource der Anwendung. */
    private static MessageSource messageSource;

    /**
     * Liest eine Nachricht aus den in Spring konfigurierten Resource-Bundles aus.
     * 
     * @param schluessel
     *            der Schlüssel des Resource-Bundles
     * @param parameter
     *            der Wert fuer die zu ersetzenden Platzhalter
     * @return die Nachricht
     */
    public static String getMessage(String schluessel, String... parameter) {
        return getMessage(schluessel, Locale.GERMANY, parameter);
    }

    /**
     * Liest eine Nachricht aus den in Spring konfigurierten Resource-Bundles aus.
     * 
     * @param schluessel
     *            der Schlüssel des Resource-Bundles
     * @param parameter
     *            der Wert für die zu ersetzenden Platzhalter
     * @param locale
     *            die Sprache der Nachricht
     * @return die Nachricht
     */
    public static String getMessage(String schluessel, Locale locale, String... parameter) {
        try {
            return messageSource.getMessage(schluessel, parameter, locale);
        } catch (NoSuchMessageException ex) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(schluessel);
            if (parameter != null && parameter.length > 0) {
                buffer.append(": ");
                for (int i = 0; i < parameter.length; i++) {
                    buffer.append(parameter[i]);
                    if (i < parameter.length - 1) {
                        buffer.append(", ");
                    }
                }
            }
            return buffer.toString();
        }
    }

    /**
     * Setter für Spring.
     * 
     * @param messageSource
     *            MessageSource-Bean der Anwendung.
     */
    @SuppressFBWarnings(
            value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "Solved with IFS-805"
    )
    public void setMessageSource(MessageSource messageSource) {
        MessageSourceHolder.messageSource = messageSource;
    }
}
