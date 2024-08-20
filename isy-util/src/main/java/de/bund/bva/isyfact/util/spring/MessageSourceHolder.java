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
 * This class holds a reference to the current {@link MessageSource} bean.
 * <p>
 * In addition, it offers convenience functions for reading resource bundle entries from the {@link MessageSource}.
 *
 * <p><b>WARNING: Due to the static implementation, it can happen that hints, events and error messages are not resolved correctly.
 *
 * @deprecated as of IsyFact 3 in favour of Spring's {@link MessageSource} bean.
 * Applications should provide a {@link MessageSource} bean with a unique name (indicating the originating application).
 * Class will be deleted in future version.
 */
@Deprecated
public final class MessageSourceHolder implements MessageSourceAware {

    /** Static reference to the MessageSource of the application. */
    private static MessageSource messageSource;

    /**
     * Reads a message from the resource bundles configured in Spring.
     *
     * @param schluessel the key of the resource bundle
     * @param parameter  the value for the placeholders to be replaced
     * @return the message
     */
    public static String getMessage(String schluessel, String... parameter) {
        return getMessage(schluessel, Locale.GERMANY, parameter);
    }

    /**
     * Reads a message from the resource bundles configured in Spring.
     *
     * @param schluessel the key of the resource bundle
     * @param parameter  the value for the placeholders to be replaced
     * @param locale     the language of the message
     * @return the message
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
     * Setter for Spring.
     *
     * @param messageSource MessageSource-Bean of the application.
     */
    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "Solved with IFS-805")
    public void setMessageSource(MessageSource messageSource) {
        MessageSourceHolder.messageSource = messageSource;
    }
}
