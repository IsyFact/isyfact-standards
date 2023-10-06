package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class LegacyTransformatorTabelleToDinSpec91379Test {

    /**
     * Test checks if "transformation_dinnorm91379_zu_dinspec91379.transform" only maps to valid DIN SPEC 91379 characters.
     * Test does not work if the LegacyTransformator maps onto a sequence of characters that contains multiple
     * DIN SPEC 91379 characters where one of those consists of multiple Unicode characters that are not
     * individually part of DIN SPEC 91379.
     * There is currently no case in which this could occur.
     * @throws IOException is thrown if file cannot be found
     */
    @Test
    public void tabelleTest() throws IOException, URISyntaxException {
        // Load file containing all DIN SPEC 91379 characters
        String path = Paths.get(ClassLoader.getSystemResource("resources/tabellen").toURI()).toString();
        List<String> identischLines = Files.readAllLines(Paths.get(path, "kategorie_dinspec91379.kat"));
        List<String> stringlatinChars = identischLines.stream().map(s->s.split(" = ")[0]).collect(Collectors.toList());

        // Load Legacy Transformation Table
        List<String> transformationTabelle = Files.readAllLines(Paths.get(path, "transformation_dinnorm91379_zu_dinspec91379.transform"));

        // Iterate over all entries in the transformation table
        for(int i = 0; i < transformationTabelle.size(); i++) {
            String transformiert = transformationTabelle.get(i).split(" = ")[1];
            // Check if whole line is part of DIN SPEC 91379
            if (!stringlatinChars.contains(transformiert)){
                String[] transformiertArray = transformiert.split("\\+");
                // Check if single symbol is part of DIN SPEC 91379
                for(String transformiertEinzeln:transformiertArray) {
                    if (!stringlatinChars.contains(transformiertEinzeln)) {
                        Assert.fail("Fehlerhaftes Symbol in Zeile " + (i+1) + ".");
                    }
                }
            }
        }
    }
}
