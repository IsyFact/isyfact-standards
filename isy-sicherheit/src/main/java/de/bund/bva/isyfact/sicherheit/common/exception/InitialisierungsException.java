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
 * Diese Exception wird von Methoden geworfen, denen ein benötigter Parameter als null oder leerer String
 * übergeben wird.
 * 
 */
public class InitialisierungsException extends SicherheitTechnicalException {

    /**
     * Serial UId.
     */
    private static final long serialVersionUID = 9167280068984160593L;

    /**
     * Erstellt die Exception mit der AusnahmeId und Paramtern für den Fehlertext.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param parameter
     *            Die Parameter
     */
    public InitialisierungsException(String ausnahmeId, String... parameter) {
        super(ausnahmeId, parameter);
    }

    /**
     * Erstellt die Exception mit der AusnahmeId, verursachender Exception und Paramtern für den Fehlertext.
     * 
     * @param ausnahmeId
     *            Die AusnahmeId
     * @param t
     *            Verursachende Exception
     * @param parameter
     *            Die Parameter
     */
    public InitialisierungsException(String ausnahmeId, Throwable t, String... parameter) {
        super(ausnahmeId, t, parameter);
    }
}
