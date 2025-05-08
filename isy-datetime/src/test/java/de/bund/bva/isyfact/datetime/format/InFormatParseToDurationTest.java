package de.bund.bva.isyfact.datetime.format;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class InFormatParseToDurationTest {

    private static final Duration D4H3M2S1MS =
        Duration.ofHours(4).plusMinutes(3).plusSeconds(2).plusMillis(1);

    @Parameterized.Parameters(name = "{index}: parseToDuration({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new Object[][] { { "1ms", Duration.ofMillis(1) }, { "2s", Duration.ofSeconds(2) },
                { "3min", Duration.ofMinutes(3) }, { "4h", Duration.ofHours(4) },
                { "2s 1ms", Duration.ofSeconds(2).plusMillis(1) },
                { "3min 2s 1ms", Duration.ofMinutes(3).plusSeconds(2).plusMillis(1) },
                { "4h 3min 2s 1ms", D4H3M2S1MS }, { "  4h   3min  2s 1ms ", D4H3M2S1MS },
                { "0h 0min 0s 0ms", Duration.ZERO }, });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public Duration expected;

    @Test
    public void parseToDuration() throws Exception {
        assertEquals(expected, InFormat.parseToDuration(input));
    }
}
