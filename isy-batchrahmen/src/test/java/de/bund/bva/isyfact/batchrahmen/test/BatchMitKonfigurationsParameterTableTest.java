package de.bund.bva.isyfact.batchrahmen.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import de.bund.bva.isyfact.batchrahmen.AnwendungMitKonfigurationsParameterTestConfig;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;

class BatchMitKonfigurationsParameterTableTest {

    private static void restartMitMaxWiederholungenSchreibtInTabelle(String cfgPfad, String tabelle,
                                                                     JdbcTemplate jdbcTemplate) {
        assertEquals(
            BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[] { "-start", "-cfg", cfgPfad, "-laufError", "true" })
        );

        assertEquals(
            BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[] { "-restart", "-cfg", cfgPfad, "-laufError", "true" })
        );

        assertEquals(
            BatchReturnCode.FEHLER_ABBRUCH.getWert(), BatchLauncher.run(
                new String[] { "-restart", "-cfg", cfgPfad, "-laufError", "true" })
        );

        Integer rowCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM " + tabelle, Integer.class);
        assertTrue(
            rowCount != null && rowCount > 0,
            "Restart-Zähler wurde nicht in " + tabelle + " abgelegt"
        );
    }

    @Nested
    @SpringBootTest(
        classes = AnwendungMitKonfigurationsParameterTestConfig.TabellenErzeugenIf2Config.class,
        properties = {
            "spring.main.web-application-type = none",
            "spring.jpa.hibernate.ddl-auto = none"
        })
    class If2Scheme {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Test
        void restartMitMaxWiederholungenSchreibtInAusgelieferteTabelle() {
            restartMitMaxWiederholungenSchreibtInTabelle(
                "/resources/batch/error-test-batch-max-wiederholungen-mit-tabelle-config.properties",
                "BATCHSTATUS_KONFIGURATIONSPARAMETER", jdbcTemplate
            );
        }
    }

    @Nested
    @SpringBootTest(
        classes = AnwendungMitKonfigurationsParameterTestConfig.TabellenErzeugenConfig.class,
        properties = {
            "spring.main.web-application-type = none",
            "spring.jpa.hibernate.ddl-auto = none"
        })
    class CurrentScheme {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Test
        void restartMitMaxWiederholungenSchreibtInAusgelieferteTabelle() {
            restartMitMaxWiederholungenSchreibtInTabelle(
                "/resources/batch/error-test-batch-max-wiederholungen-mit-tabelle-tabellen-erzeugen-config.properties",
                "BATCH_STATUS_KONFIGURATIONS_PARAMETER", jdbcTemplate
            );
        }
    }
}