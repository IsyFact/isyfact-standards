package de.bund.bva.isyfact.logging.layout;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ch.qos.logback.contrib.jackson.JacksonJsonFormatter;

/**
 * Erweiterung des JacksonJsonFormatters, um IsyLogging erweiterte Konfigurationsmöglichkeiten
 * bereitzustellen.
 *
 */
public class IsyJacksonJsonFormatter extends JacksonJsonFormatter {

    /**
     * Konstruktor der Klasse. Führt die notwendigen Konfigurationen durch.
     * 
     */
    public IsyJacksonJsonFormatter() {
        super();
        konfiguriereObjectMapper();
    }

    /**
     * Hilfsmethode zum Konfigurieren des ObjectMappers. Der Mapper bestimmt, wie Objekte in JSON abgebildet
     * werden. Der Formatter bietet jedoch keine Möglichkeit, den Mapper in der logback-Konfiguration zu
     * konfigurieren. Daher findet die Konfiguration in dieser Hilfmethode statt.
     */
    private void konfiguriereObjectMapper() {
        ObjectMapper mapper = getObjectMapper();
        // Wenn ein Objekt übergeben wird, welches als Wert eines JSON-Attributs verwendet werden soll, dieses
        // Attribut aber keine getter und setter und auch keine Annotationen enthält, wie dieses in JSON
        // umgewandelt werden soll, wirft der ObjectMapper standardmäßig einen Fehler. In den Logeinträgen
        // äußert sich das dann so, dass ein fehlerhaftes JSON (ohne Anführungszeichen) ausgegeben wird
        // (entspricht einem toString-Aufruf der Map mit den zu serialisierenden Attributen). Durch setzen
        // des Parameters FAIL_ON_EMPTY_BEANS auf false wird der Fehler verhindert und in diesem Fall nur die
        // Object-ID als Wert übernommen.
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

}
