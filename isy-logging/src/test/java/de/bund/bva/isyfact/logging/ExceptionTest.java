package de.bund.bva.isyfact.logging;

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

import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * Testfälle der Erzeugung von Exceptions.
 * 
 */
public class ExceptionTest {

    /**
     * Testfall zum Testen der Fehlertextauflösung. Es wird keine Spring-Konfiguration, sondern die
     * "Init-Methode" der LoggerFactory verwendet.
     */
    @Test
    public void testFehlertextAufloesung() {

        try {
            throw new LogKonfigurationFehler(FehlerSchluessel.FALSCHES_LOGGING_FRAMEWORK, this.getClass()
                    .getName());
        } catch (LogKonfigurationFehler e) {
            Assert.assertEquals(
                    "Der Logger der bereitgestetllten SLF4J-Implementierung implementiert nicht das benötigte Inferface LocationAwareLogger. Bereitgestellt wurde: "
                            + this.getClass().getName(), e.getFehlertext());
        }

    }
}
