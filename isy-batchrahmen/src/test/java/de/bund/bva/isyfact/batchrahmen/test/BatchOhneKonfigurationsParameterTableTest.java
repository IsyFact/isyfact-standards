package de.bund.bva.isyfact.batchrahmen.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.core.launcher.BatchLauncher;

/**
 * Regression test for IFS-5834: A batch in which the {@code Batchrahmen.MaxWiederholungen}
 * feature is not configured must also be runnable when the
 * BATCHSTATUS_KONFIGURATIONSPARAMETER table does not exist (only BATCHSTATUS is present).
 */
class BatchOhneKonfigurationsParameterTableTest {

    @Test
    void batchOhneMaxWiederholungenLaeuftOhneKonfigurationsParameterTabelle() {
        int returnCode = BatchLauncher.run(new String[]{"-start", "-cfg",
                "/resources/batch/basic-test-batch-ohne-konfigurationsparameter-config.properties"});

        assertEquals(BatchReturnCode.OK.getWert(), returnCode);
    }
}