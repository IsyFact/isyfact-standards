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

import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.logging.exceptions.IsyLoggingFehlertextProvider;

/**
 * Die Tests des Fehlertextproviders.
 */
public class FehlertextProviderTest {

    /**
     * Testet das Lesen einer Nachricht ohne Parameter.
     */
    @Test
    public void testGetMessageOhneParameter() {

        FehlertextProvider provider = new IsyLoggingFehlertextProvider();
        Assert.assertEquals("Fehler bei der Serialisierung der Aufrufparameter.",
                provider.getMessage("ISYLO01001"));
        Assert.assertEquals("Fehler bei der Serialisierung der Aufrufparameter.",
                provider.getMessage("ISYLO01001", new String[0]));

    }

}
