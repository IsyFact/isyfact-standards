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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.logging.exceptions.LogKonfigurationFehler;

/**
 * Spezielle Tests der LoggerFactory.
 */
public class IsyLoggerFactoryTest {

    /**
     * Prüfung der korrekten Fehlermeldung, falls ein falsches Logging-Framework zum Einsatz kommt.
     * 
     * @throws Exception
     *             falls bei der Prüfung ein Fehler aufgetreten ist.
     */
    @Test
    public void falschesLoggingFrameworkTest() throws Exception {

        // Die Prüfung der Methode muss per Reflection erfolgen, da es unmöglich ist eine zweite
        // SLF4J-Implementierung in die Tests zuverlässig zu integrieren.
        Method pruefeLoggerImplementierungMethod = IsyLoggerFactory.class.getDeclaredMethod(
            "pruefeLoggerImplementierung", Object.class);
        pruefeLoggerImplementierungMethod.setAccessible(true);
        try {
            // Integer als "Logframework" zur Prüfung geben.
            pruefeLoggerImplementierungMethod.invoke(null, new Integer(5));
            Assert.fail("Erzeugung des Loggers erfolgreich, obwohl nicht "
                + "unterstütztes Logframework verwendet wird.");
        } catch (InvocationTargetException ite) {
            LogKonfigurationFehler lkf = (LogKonfigurationFehler) ite.getCause();
            Assert.assertEquals("ISYLO00000", lkf.getAusnahmeId());
        }

    }
}
