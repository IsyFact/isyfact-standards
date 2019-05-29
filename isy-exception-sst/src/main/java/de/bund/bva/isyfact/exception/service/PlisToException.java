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
package de.bund.bva.isyfact.exception.service;

/**
 * Abstrakte Implementierung, welche als Basis für die abstrakten fachlichen und technischen
 * PLIS-SchnittstellenExceptions verwendet wird.
 * <p>
 * Von dieser Exception darf nicht direkt geerbet werden. Alle Exception in einer Anwendung, die
 * SchnittstellenExceptions sind, müssen von den Exceptions {@link PlisTechnicalToException} und
 * {@link PlisBusinessToException} erben.
 * 
 */
public class PlisToException extends Exception {
    /** Die UID. */
    private static final long serialVersionUID = -8496566533430654282L;

    /**
     * Seperator für die Ausnahme-ID und die UUID.
     */
    private static final String SEPERATOR = "#";

    /**
     * Leerzeichen.
     */
    private static final String SPACE = " ";

    /**
     * Die Ausnahme-ID.
     */
    private String ausnahmeId;

    /**
     * Die eineindeutige ID, die den aufgetretenen Fehler referenziert (nicht die Fehlerart).
     */
    private String uniqueId;

    /**
     * Einziger Konstruktor. Es ist notwendig die Nachricht direkt zu übergeben, da diese nicht
     * nachträglich gesetzt werden kann. Zusätzlich nimmt dieser Konstrukt noch die Ausnahme-ID und
     * die Unique-ID entgegen.
     * 
     * @param message
     *            Der Fehlertext.
     * @param ausnahmeId
     *            Die Ausnahme-ID
     * @param uniqueId
     *            Die eineindeutige ID des Fehlers
     */
    protected PlisToException(String message, String ausnahmeId, String uniqueId) {
        super(message);
        this.ausnahmeId = ausnahmeId;
        this.uniqueId = uniqueId;
    }

    /**
     * @return Die Ausnahme-ID
     */
    public String getAusnahmeId() {
        return ausnahmeId;
    }

    /**
     * @return Die eineindeutige ID (UUID)
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * @return Den unformattierten Fehlertext
     */
    public String getFehlertext() {
        return super.getMessage();
    }

    /**
     * {@inheritDoc}
     */
    public String getMessage() {
        return SEPERATOR + ausnahmeId + SPACE + super.getMessage() + SPACE + SEPERATOR + uniqueId;
    }
}
