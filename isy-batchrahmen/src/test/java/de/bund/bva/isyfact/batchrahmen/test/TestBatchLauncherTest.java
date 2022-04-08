package de.bund.bva.isyfact.batchrahmen.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;

import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchReturnCode;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;

public class TestBatchLauncherTest {

   @Test
    public void launchTestBatch() throws IOException {
        String batchConfig = "/resources/batch/basic-test-batch-1-config.properties";
        TestBatchLauncher sut = new TestBatchLauncher(batchConfig);

        BatchStartTyp type = BatchStartTyp.START;

        String[] params = {};

        int returnVal = sut.starteBatch(type, params);

        assertThat(returnVal).isEqualTo(BatchReturnCode.OK.getWert());
    }
}