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
package de.bund.bva.isyfact.konfiguration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationParameterException;
import de.bund.bva.isyfact.konfiguration.common.impl.PropertyKonfiguration;
import de.bund.bva.isyfact.konfiguration.common.impl.ReloadablePropertyKonfiguration;

public class TestKonfiguration {

    private static final String CONFIG_A = "/config/config_A.properties";

    private static final String CONFIG_B = "/config/config_B.properties";

    private static final String CONFIG_CHANGE = "/config/config_Change.properties";

    private static final String CONFIG_ORDNER = "/config/config_ordner/*.properties";

    public void testKonfigLaden() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_A);
        assertEquals(false, konfig.getAsBoolean("parameter.boolean"));
        assertEquals(true, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals(true, konfig.getAsBoolean("parameter.boolean.3"));
        assertEquals(1000, konfig.getAsInteger("parameter.int"));
        assertEquals(1000, konfig.getAsInteger("parameter.int.2"));
        assertEquals(100.5, konfig.getAsDouble("parameter.double"), 0.0);
        assertEquals("Hans", konfig.getAsString("parameter.rawstring"));
        assertEquals("Hans ", konfig.getAsRawString("parameter.rawstring"));
        assertEquals("", konfig.getAsString("parameter.defined"));
        assertEquals("A", konfig.getAsString("parameter.null", "A"));
        assertNull(konfig.getAsString("parameter.null", null));
        assertEquals(8, konfig.getSchluessel().size());
    }

    public void testKonfigUeberschreiben() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_A, CONFIG_B);
        assertEquals(false, konfig.getAsBoolean("parameter.boolean"));
        assertEquals(false, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals(2000, konfig.getAsInteger("parameter.int"));
        assertEquals(100.5, konfig.getAsDouble("parameter.double"), 0.0);
        assertEquals("Hugo", konfig.getAsString("parameter.string"));
        assertEquals("Hans", konfig.getAsString("parameter.rawstring"));
        assertEquals("Hans ", konfig.getAsRawString("parameter.rawstring"));
        assertEquals(9, konfig.getSchluessel().size());
    }

    @Test
    public void testKonfigLadenError() throws Exception {
        assertThatThrownBy(() -> new ReloadablePropertyKonfiguration("ddd"))
            .isInstanceOf(KonfigurationDateiException.class);
    }

    public void testKonfigParamError() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_A);
        assertThatThrownBy(() -> konfig.getAsDouble("nicht.vorhanden"))
            .isInstanceOf(KonfigurationParameterException.class);

        assertThatThrownBy(() -> konfig.getAsInteger("parameter.boolean"))
            .isInstanceOf(KonfigurationParameterException.class);

    }

    public void testNeuLaden() throws Exception {
        URL configUrl = TestKonfiguration.class.getResource(CONFIG_CHANGE);
        File configFile = new File(configUrl.toURI());
        writeStringToFile(configFile, "parameter.a = Hello\n", UTF_8);

        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_CHANGE);

        assertEquals("Hello", konfig.getAsString("parameter.a"));

        final Set<String> myChangedKeys = new HashSet<>();
        konfig.addKonfigurationChangeListener(myChangedKeys::addAll);

        writeStringToFile(configFile, "parameter.a = World\n" + "parameter.b = Neu\n", UTF_8);

        assertTrue(konfig.checkAndUpdate());
        assertEquals("World", konfig.getAsString("parameter.a"));
        assertEquals("Neu", konfig.getAsString("parameter.b"));

        assertThat(myChangedKeys, containsInAnyOrder("parameter.a", "parameter.b"));
    }

    public void testPropertiyKonfigurationAlsStreamLaden() throws Exception {
        PropertyKonfiguration konfig = new PropertyKonfiguration(CONFIG_A);
        assertEquals(1000, konfig.getAsInteger("parameter.int"));
    }

    public void testKonfigLoeschenNeuLadenError() throws Exception {
        String classpathRoot = "/";
        String neueDateiName = "config_temp.properties";
        URL url = TestKonfiguration.class.getResource(classpathRoot);
        File configFile = new File(url.toURI().resolve(neueDateiName));

        writeStringToFile(configFile, "parameter.a = Hello\n", UTF_8);

        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(classpathRoot + neueDateiName);

        assertEquals("Hello", konfig.getAsString("parameter.a"));
        configFile.delete();
        assertThatThrownBy(() -> konfig.checkAndUpdate())
            .isInstanceOf(KonfigurationDateiException.class);
    }

    public void testKonfigAusPropertyKonfigurationLaden() {
        PropertyKonfiguration konfig = new PropertyKonfiguration(CONFIG_ORDNER, CONFIG_B);
        assertEquals(1000, konfig.getAsInteger("parameter.ordner.int"));
        assertEquals(2000, konfig.getAsInteger("parameter.int"));
        assertEquals(true, konfig.getAsBoolean("parameter.ordner.boolean"));
        assertEquals(false, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals("test", konfig.getAsString("parameter.ordner.string"));
        assertEquals("Hugo", konfig.getAsString("parameter.string"));

        assertEquals(6, konfig.getSchluessel().size());
    }

    public void testKonfigAusOrdnerLaden() {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_ORDNER);
        assertEquals(1000, konfig.getAsInteger("parameter.ordner.int"));
        assertEquals(true, konfig.getAsBoolean("parameter.ordner.boolean"));
        assertEquals("test", konfig.getAsString("parameter.ordner.string"));
        assertEquals(3, konfig.getSchluessel().size());
    }

    public void testKonfigAusOrdnerUndDateiLaden() {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_ORDNER, CONFIG_B);
        assertEquals(1000, konfig.getAsInteger("parameter.ordner.int"));
        assertEquals(2000, konfig.getAsInteger("parameter.int"));
        assertEquals(true, konfig.getAsBoolean("parameter.ordner.boolean"));
        assertEquals(false, konfig.getAsBoolean("parameter.boolean.2"));
        assertEquals("test", konfig.getAsString("parameter.ordner.string"));
        assertEquals("Hugo", konfig.getAsString("parameter.string"));

        assertEquals(6, konfig.getSchluessel().size());
    }

    public void testPropertiesDateiAusOrdnerLoeschen() throws Exception {
        String neueDateiName = "config_temp.properties";

        URL configUrl = TestKonfiguration.class.getResource(CONFIG_ORDNER);
        File configFile = new File(configUrl.toURI().resolve(neueDateiName));
        writeStringToFile(configFile, "parameter.a = Hello\n", UTF_8);

        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_ORDNER);

        int anzahlSchluessel = konfig.getSchluessel().size();
        assertEquals("Hello", konfig.getAsString("parameter.a"));

        final Set<String> myChangedKeys = new HashSet<>();
        konfig.addKonfigurationChangeListener(myChangedKeys::addAll);
        configFile.delete();

        assertTrue(konfig.checkAndUpdate());
        assertEquals(anzahlSchluessel - 1, konfig.getSchluessel().size());
        assertThat(myChangedKeys, containsInAnyOrder("parameter.a"));

        assertThatThrownBy(() -> konfig.getAsString("parameter.a"))
            .isInstanceOf(KonfigurationParameterException.class);
    }

    public void testPropertiesDateiInOrdnerHinzufuegen() throws Exception {
        ReloadablePropertyKonfiguration konfig =
            new ReloadablePropertyKonfiguration(CONFIG_ORDNER);

        int anzahlSchluessel = konfig.getSchluessel().size();
        final Set<String> myChangedKeys = new HashSet<>();
        konfig.addKonfigurationChangeListener(myChangedKeys::addAll);

        String neueDateiName = "config_temp.properties";
        URL configUrl = TestKonfiguration.class.getResource(CONFIG_ORDNER);
        File configFile = new File(configUrl.toURI().resolve(neueDateiName));
        writeStringToFile(configFile, "parameter.a = Hello\n", UTF_8);

        assertTrue(konfig.checkAndUpdate());

        assertEquals(konfig.getSchluessel().size(), anzahlSchluessel + 1);

        assertThat(myChangedKeys, containsInAnyOrder("parameter.a"));
    }
}
