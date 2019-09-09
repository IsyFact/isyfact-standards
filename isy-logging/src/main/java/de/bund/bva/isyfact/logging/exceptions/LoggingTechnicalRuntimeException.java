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

import de.bund.bva.isyfact.exception.TechnicalRuntimeException;

/**
 * Oberklasse aller TechnicalRuntimeExceptions der Bibliothek.
 * 
 */
public class LoggingTechnicalRuntimeException extends TechnicalRuntimeException {

    /** Eindeutige UID. */
    private static final long serialVersionUID = 1L;

    /** Fehlertextprovider zum Auslesen von Fehlertexten. */
    private static final IsyLoggingFehlertextProvider FEHLERTEXT_PROVIDER =
        new IsyLoggingFehlertextProvider();

    /**
     * Konstruktor der Klasse zum Erstellen einer Exception mit den übergebenen Parametern.
     * 
     * @see TechnicalRuntimeException
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schl&uuml;ssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht f&uuml;r die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameter f&uml;r die m&ouml;glichen Variablen in einer
     *            Fehler-Nachricht.
     */
    public LoggingTechnicalRuntimeException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, FEHLERTEXT_PROVIDER, parameter);
    }

    /**
     * Konstruktor der Klasse zum Erstellen einer Exception mit den übergebenen Parametern.
     * 
     * @see TechnicalRuntimeException
     * 
     * @param ausnahmeId
     *            Die Ausnahme-ID. Der Schl&uuml;ssel, welcher verwendet wird, um einen Nachrichtentext,
     *            welcher als Fehler-Nachricht f&uuml;r die Exception verwendet wird aus einem ResourceBundle
     *            zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert hf&uuml;r die sp&auml;tere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameter f&uml;r die m&ouml;glichen Variablen in einer
     *            Fehler-Nachricht.
     */
    public LoggingTechnicalRuntimeException(String ausnahmeId, Throwable throwable, String... parameter) {
        super(ausnahmeId, throwable, FEHLERTEXT_PROVIDER, parameter);
    }

}
