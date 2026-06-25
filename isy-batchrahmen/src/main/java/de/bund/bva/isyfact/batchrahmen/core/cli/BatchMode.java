package de.bund.bva.isyfact.batchrahmen.core.cli;

import java.util.Arrays;
import java.util.Objects;

public final class BatchMode {

    /**
     * The command-line argument by which to decide, whether the current execution
     * is a batch-execution or not.
     */
    public static final String BATCH_ARG = "-batch";

    private BatchMode() {
    }

    public static boolean isBatch(String[] args) {
        return Objects.nonNull(args) && args.length > 0 && Arrays.asList(args).contains(BATCH_ARG);
    }
}
