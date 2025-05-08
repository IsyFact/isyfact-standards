package de.bund.bva.isyfact.logging.layout;



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
