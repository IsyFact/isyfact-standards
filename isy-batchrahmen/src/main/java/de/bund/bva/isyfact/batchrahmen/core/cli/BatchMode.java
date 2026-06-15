package de.bund.bva.isyfact.batchrahmen.core.cli;

import java.util.Arrays;

public class BatchMode {
    public static boolean isBatch(String[] args) {
        return Arrays.asList(args).contains("-batch");
    }
}