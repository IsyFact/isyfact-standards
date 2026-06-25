package de.bund.bva.isyfact.batchrahmen.batch.konfiguration;

import org.junit.jupiter.api.Test;

public class KommandozeilenParserTest {

    /**
     * We only want to make sure, that the method {@link KommandozeilenParser#printCommandLineHelp()}
     * doesn't throw an exception.
     */
    @Test
    public void testPrintCommandLineHelp() {
        final KommandozeilenParser parser = new KommandozeilenParser();
        parser.printCommandLineHelp();
    }
}
