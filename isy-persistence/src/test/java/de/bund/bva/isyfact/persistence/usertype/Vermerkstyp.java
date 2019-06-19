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
package de.bund.bva.isyfact.persistence.usertype;

import de.bund.bva.isyfact.persistence.annotation.EnumId;

/**
 * Test-Enum mit natürlichen Schlüssel.
 * 
 */
public enum Vermerkstyp {
    /** . */
    NACHRICHT_EMPFANGEN("TYP001"),
    /** . */
    NACHRICHT_GESENDET("TYP002"),
    /** . */
    VERARBEITUNG_GESTARTET("TYP003");

    /**
     * Der Schlüssel.
     */
    private final String id;

    private Vermerkstyp(String id) {
        this.id = id;
    }

    /**
     * Liefert den Schlüssel.
     * 
     * @return der Schlüssel
     */
    @EnumId
    public String getId() {
        return id;
    }

}
