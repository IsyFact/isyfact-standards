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
package de.bund.bva.isyfact.konfiguration.common.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.KonfigurationChangeListener;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.isyfact.logging.util.MdcHelper;

public class TestReloadablePropertyKonfiguration {

    private static final String CONFIG_A = "/config/config_A.properties";

    private static final String CONFIG_B = "/config/config_B.properties";

    private static final String CONFIG_X_CLASSPATH = "target/test-classes/config/config_X.properties";

    private static final String CONFIG_X_RESOURCEPATH = "/config/config_X.properties";

    private static final String CONTENT = "parameter.string = Hello";

    private ReloadablePropertyKonfiguration konf;

    @Before
    public void setUp() {
        this.konf = new ReloadablePropertyKonfiguration(new String[] { CONFIG_A, CONFIG_B });
    }

    @Test
    public void testGetSchluessel() {
        Set<String> testSet = this.konf.getSchluessel();
        assertEquals(9, testSet.size());
        assertTrue(testSet.contains("parameter.string"));
        assertTrue(testSet.contains("parameter.rawstring"));
        assertTrue(testSet.contains("parameter.int.2"));
    }

    @Test(expected = KonfigurationDateiException.class)
    public void testInitializationOnException() {
        new ReloadablePropertyKonfiguration(new String[] { "foo bar" });
    }

    /**
     * Testen der getAs...() Methoden aus AbstractKonfiguration
     */

    @Test
    public void testAbstractKonfiguration() {
        assertFalse(this.konf.getAsBoolean("parameter.boolean", true));
        assertFalse(this.konf.getAsBoolean("parameter.boolean.2", true));
        assertTrue(this.konf.getAsBoolean("parameter.boolean.3", false));

        assertEquals(2000, this.konf.getAsInteger("parameter.int", 0));
        assertEquals(1000, this.konf.getAsLong("parameter.int.2", 0));
        assertEquals(100.5, this.konf.getAsDouble("parameter.double", 0.0), 0.0);

        assertEquals("", this.konf.getAsString("parameter.defined", null));
        // testet ob die AsString Methode trimmt
        assertEquals("Hans", this.konf.getAsString("parameter.rawstring", null));

        assertEquals("Hans ", this.konf.getAsRawString("parameter.rawstring", null));

        assertTrue(this.konf.getAsBoolean("parameter", true));
        assertEquals(0, this.konf.getAsInteger("parameter", 0));
        assertEquals(0l, this.konf.getAsLong("parameter", 0l));
        assertEquals(0.0, this.konf.getAsDouble("parameter", 0.0), 0.0);
        assertNull(this.konf.getAsString("parameter", null));
        assertNull(this.konf.getAsRawString("parameter", null));

        assertFalse(this.konf.getAsBoolean("parameter.boolean"));
        assertEquals(2000, this.konf.getAsInteger("parameter.int"));
        assertEquals(1000, this.konf.getAsLong("parameter.int.2"));
        assertEquals(100.5, this.konf.getAsDouble("parameter.double"), 0.0);
        assertEquals("Hans", this.konf.getAsString("parameter.rawstring"));
        assertEquals("Hans ", this.konf.getAsRawString("parameter.rawstring"));
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsStringOnException() {
        this.konf.getAsString("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsRawStringOnException() {
        this.konf.getAsRawString("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsBooleanOnException() {
        this.konf.getAsBoolean("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsBooleanWrongFormatOnException() {
        this.konf.getAsBoolean("parameter.int");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsLongOnException() {
        this.konf.getAsLong("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsLongWrongFormatOnException() {
        this.konf.getAsLong("parameter.string");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsDoubleOnException() {
        this.konf.getAsDouble("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsDoubleWrongFormatOnException() {
        this.konf.getAsDouble("parameter.string");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsIntegerOnException() {
        this.konf.getAsInteger("parameter");
    }

    @Test(expected = KonfigurationParameterException.class)
    public void testAsIntegernWrongFormatOnException() {
        this.konf.getAsInteger("parameter.string");
    }

    @Test
    public void testAddKonfigurationChangeListener() {
        KonfigurationChangeListener listener = changedKeys -> {
        };

        this.konf.addKonfigurationChangeListener(listener);
        assertTrue(this.konf.hasKonfigurationChangeListener(listener));
        assertFalse(this.konf.hasKonfigurationChangeListener(null));

        this.konf.addKonfigurationChangeListener(listener);
        this.konf.addKonfigurationChangeListener(null);

        this.konf.removeKonfigurationChangeListener(listener);
        assertFalse(this.konf.hasKonfigurationChangeListener(listener));
    }

    @Test
    public void testCheckAndUpdate() throws IOException {

        File f = new File(CONFIG_X_CLASSPATH);
        writeStringToFile(f, CONTENT + "\nparameter.world = hello", UTF_8);
        ReloadablePropertyKonfiguration other =
            new ReloadablePropertyKonfiguration(new String[] { CONFIG_X_RESOURCEPATH });
        assertFalse(other.checkAndUpdate());
        assertTrue(f.delete());
        assertTrue(f.createNewFile());

        writeStringToFile(f, CONTENT + "World" + "\nparameter.world = hello", UTF_8);

        other.addKonfigurationChangeListener(changedKeys -> {
        });

        assertTrue(other.checkAndUpdate());
    }

    @Test
    public void testCheckAndUpdateWithoutCorrelationId() {
        String correlationId = MdcHelper.liesKorrelationsId();
        this.konf.checkAndUpdate();
        assertEquals(correlationId, MdcHelper.liesKorrelationsId());
    }

    @Test
    public void testCheckAndUpdateWithCorrelationId() {
        MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());

        String correlationId = MdcHelper.liesKorrelationsId();
        this.konf.checkAndUpdate();
        assertEquals(correlationId, MdcHelper.liesKorrelationsId());

        MdcHelper.entferneKorrelationsId();
    }
}
