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
package de.bund.bva.isyfact.sicherheit.common.exception;

/**
 * Exceptions dieser Klasse werden geworfen wenn ein technischer Fehler beim Autorisieren auftritt.
 * 
 */
public class AutorisierungTechnicalException extends SicherheitTechnicalRuntimeException {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5240353914116814801L;

    /**
     * Erstellt eine neue technische <i>checked</i> Exception mit einer Ausnahme-ID für den Fehlertext und dem
     * übergebenen Grund mit dem Fehlertext basierend auf <tt>(cause==null ? null : cause.toString())</tt>
     * (was normalerweise die Klasse und den Fehlertext von <tt>cause</tt> enthält).
     * <p>
     * Dieser Konstruktor ist sinnvoll für Fehler, die durch diese Exception gewrapped werden sollen, z.B.
     * {@link java.security.PrivilegedActionException}).
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param throwable
     *            Der Grund. Throwable wird gespeichert für die spätere Nutzung durch die Methode
     *            {@link #getCause()}. <tt>null</tt> als Wert ist erlaubt und bedeutet, dass kein Grund
     *            existiert oder der Grund nicht bekannt ist.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     */
    public AutorisierungTechnicalException(String ausnahmeID, Throwable throwable, String... parameter) {
        super(ausnahmeID, throwable, parameter);
    }

    /**
     * Erzeugt die Berechtigungs-Exception ueber Ausnahme-ID und Parameter.
     * 
     * @param ausnahmeID
     *            Die Ausnahme-ID. Der Schlüssel, welcher verwendet wird, um einen Nachrichtentext, welcher
     *            als Fehler-Nachricht für die Exception verwendet wird aus einem ResourceBundle zu laden.
     * @param parameter
     *            Die Parameter. Parameter für die möglichen Variablen in einer Fehler-Nachricht.
     */
    public AutorisierungTechnicalException(String ausnahmeID, String... parameter) {
        super(ausnahmeID, parameter);
    }

}
