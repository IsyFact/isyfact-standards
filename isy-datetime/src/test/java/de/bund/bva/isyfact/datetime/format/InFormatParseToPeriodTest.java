package de.bund.bva.isyfact.datetime.format;

import static org.junit.Assert.assertEquals;

import java.time.Period;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**

 */
@RunWith(Parameterized.class)
public class InFormatParseToPeriodTest {

    private static final Period P7A6M5D = Period.of(7, 6, 5);

    @Parameterized.Parameters(name = "{index}: parseToPeriod({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "5d", Period.ofDays(5) }, { "6M", Period.ofMonths(6) },
            { "7a", Period.ofYears(7) }, { "6M 5d", Period.of(0, 6, 5) }, { "7a 6M 5d", Period.of(7, 6, 5) },
            { " 7a   6M 5d ", Period.of(7, 6, 5) }, { "0a 0M 0d", Period.ZERO }, });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public Period expected;

    @Test
    public void parseToPeriod() throws Exception {
        assertEquals(expected, InFormat.parseToPeriod(input));
    }
}