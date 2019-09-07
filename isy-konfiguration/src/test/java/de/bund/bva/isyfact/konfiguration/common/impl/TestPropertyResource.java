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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import de.bund.bva.isyfact.konfiguration.common.exception.KonfigurationDateiException;

public class TestPropertyResource {

    private static final String CONFIG_X_CLASSPATH = "target/test-classes/config/config_X.properties";

    private static final String CONFIG_X_RESOURCEPATH = "/config/config_X.properties";

    private static final String KEY = "parameter.string";

    private static final String CONTENT = KEY + " = Hello";

    private static final String CONTENT_UPDATE = KEY + " = World";

    private DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

    @Test
    public void testNeuLaden() throws IOException {
        File f = new File(CONFIG_X_CLASSPATH);

        writeStringToFile(f, CONTENT, UTF_8);

        Resource resource = this.resourceLoader.getResource(CONFIG_X_RESOURCEPATH);
        PropertyResource datei = new PropertyResource(resource);

        assertEquals("Hello", datei.getProperties().get(KEY));

        writeStringToFile(f, CONTENT_UPDATE, UTF_8);

        assertThat(datei.isNeueVersionVerfuegbar()).isTrue();

        datei.reload();

        assertEquals("World", datei.getProperties().get(KEY));
    }

    @Test
    public void testNeuLadenWennDateiGeloescht() throws IOException {
        File f = new File(CONFIG_X_CLASSPATH);

        writeStringToFile(f, CONTENT, UTF_8);

        Resource resource = this.resourceLoader.getResource(CONFIG_X_RESOURCEPATH);
        PropertyResource datei = new PropertyResource(resource);

        assertTrue(f.delete());
        assertThatThrownBy(() -> datei.reload()).isInstanceOf(KonfigurationDateiException.class);
    }

    @Test
    public void testIsNeueVersionVerfuegbar() throws IOException {
        File f = new File(CONFIG_X_CLASSPATH);

        writeStringToFile(f, CONTENT, UTF_8);

        Resource resource = this.resourceLoader.getResource(CONFIG_X_RESOURCEPATH);
        PropertyResource datei = new PropertyResource(resource);

        assertFalse(datei.isNeueVersionVerfuegbar());

        writeStringToFile(f, CONTENT, UTF_8);

        // datei.neuLaden();
        assertTrue(datei.isNeueVersionVerfuegbar());
        assertTrue(f.delete());
        assertEquals("class path resource [config/config_X.properties]", datei.getDescription());
    }

    @Test
    public void testLadeVonJar() {
        Resource resource = this.resourceLoader
            .getResource("/org/apache/el/Messages.properties");

        PropertyResource datei = new PropertyResource(resource);

        assertFalse(datei.isNeueVersionVerfuegbar());

        assertEquals("Cannot convert [{0}] of type [{1}] to [{2}]",
            datei.getProperties().get("error.convert"));
    }
}
