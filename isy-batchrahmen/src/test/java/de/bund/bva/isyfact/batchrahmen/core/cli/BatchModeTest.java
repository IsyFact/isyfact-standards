package de.bund.bva.isyfact.batchrahmen.core.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class BatchModeTest {

    @Test
    public void testIsBatch_nullArgs() {
        assertThat(BatchMode.isBatch(null), is(false));
    }

    @Test
    public void testIsBatch_emptyArgs() {
        assertThat(BatchMode.isBatch(new String[0]), is(false));
    }

    @Test
    public void testIsBatch_noBatchAllArgsNonNull() {
        assertThat(BatchMode.isBatch(new String[] {"arg1", "arg2"}), is(false));
    }

    @Test
    public void testIsBatch_noBatchOneArgIsNull() {
        assertThat(BatchMode.isBatch(new String[] {"arg1", null}), is(false));
    }

    @Test
    public void testIsBatch_noBatchAllArgsAreNull() {
        assertThat(BatchMode.isBatch(new String[] {null, null}), is(false));
    }

    @Test
    public void testIsBatch_batchAllArgsNonNull() {
        assertThat(BatchMode.isBatch(new String[] {"arg1", BatchMode.BATCH_ARG}), is(true));
    }

    @Test
    public void testIsBatch_batchOneArgIsNull() {
        assertThat(BatchMode.isBatch(new String[] {BatchMode.BATCH_ARG, null}), is(true));
    }
}
