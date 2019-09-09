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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestBusinessToException {

    private static final String message = "Testen";

    private static final String ausnahmeId = "von ToException";

    private static final String uniqueId = "und deren Ableitungen";

    private static final String SEPERATOR = "#";

    private static final String SPACE = " ";

    @Test
    public void testBusinessToException() {
        ToException pte = new TestBusinessToInstanz(message, ausnahmeId, uniqueId);
        assertEquals(message, pte.getFehlertext());
        assertEquals(ausnahmeId, pte.getAusnahmeId());
        assertEquals(uniqueId, pte.getUniqueId());

        String exception = SEPERATOR + ausnahmeId + SPACE + message + SPACE + SEPERATOR + uniqueId;
        assertEquals(exception, pte.getMessage());
    }

    class TestBusinessToInstanz extends BusinessToException {
        public TestBusinessToInstanz(String message, String ausnahmeId, String uniqueId) {
            super(message, ausnahmeId, uniqueId);
        }
    }

}
