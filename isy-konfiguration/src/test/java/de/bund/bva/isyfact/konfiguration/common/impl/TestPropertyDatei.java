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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;

public class TestPropertyDatei {

    private static final String CONFIG_X_CLASSPATH = "target/test-classes/config/config_X.properties";

    private static final String CONFIG_X_RESOURCEPATH = "/config/config_X.properties";

    private static final String CONTENT = "parameter.string = Hello";

    @Test
    public void testNeuLaden() throws IOException {
        File f = new File(CONFIG_X_CLASSPATH);
        assertTrue(f.createNewFile());
        FileWriter writer = new FileWriter(f);
        writer.write(CONTENT);
        writer.close();

        PropertyDatei datei = new PropertyDatei(CONFIG_X_RESOURCEPATH);

        assertTrue(f.delete());
        try {
            datei.neuLaden();
            fail("KonfigurationDateiException nicht geworfen.");
        } catch (KonfigurationDateiException e) {
            // Ziel des Tests
        }
    }

    @Test
    public void testIsNeueVersionVerfuegbar() throws IOException, InterruptedException {
        File f = new File(CONFIG_X_CLASSPATH);
        assertTrue(f.createNewFile());
        FileWriter writer = new FileWriter(f);
        writer.write(CONTENT);
        writer.close();

        PropertyDatei datei = new PropertyDatei(CONFIG_X_RESOURCEPATH);
        assertFalse(datei.isNeueVersionVerfuegbar());
        Thread.sleep(1000);
        writer = new FileWriter(f);
        writer.write(CONTENT);
        writer.close();

        // datei.neuLaden();
        assertTrue(datei.isNeueVersionVerfuegbar());
        assertTrue(f.delete());
        assertEquals(CONFIG_X_RESOURCEPATH, datei.getDateiname());
    }
}
