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

/**
 * Diese Exception signalisiert einen Konfigurationsfehler im Polling.
 * 
 */
public class PollingClusterKonfigurationException extends PollingTechnicalRuntimeException {

    /** SerialVersionUID. **/
    private static final long serialVersionUID = 0L;

    /**
     * Erstellt eine neue technische <i>unchecked</i> Exception mit einer Ausnahme-ID für den Fehlertext und
     * einem Array mit Werten für die Variablenersetzung im Fehlertext.
     * <p>
     * Der Grund wird nicht initialisiert und kann später durch den Aufruf der Methode
     * {@link #initCause(Throwable)} initialisiert werden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu
     *            laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     */
    public PollingClusterKonfigurationException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, parameter);
    }
    
    /**
     * Erstellt eine neue technische <i>unchecked</i> Exception mit einer Ausnahme-ID für den Fehlertext,
     * einem Array mit Werten für die Variablenersetzung im Fehlertext und mit dem übergebenen Grund.
     * <p>
     * Anmerkung: Der Fehlertext von <code>cause</code> (dem Grund) ist <i>nicht</i> automatisch mit dem
     * übergebenen Fehlertext verbunden.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     *            <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Variablen zu ersetzen sind.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     */
    public PollingClusterKonfigurationException(String ausnahmeID, Throwable throwable,
        String... parameter) {
        super(ausnahmeID, throwable, parameter);
    }
}



