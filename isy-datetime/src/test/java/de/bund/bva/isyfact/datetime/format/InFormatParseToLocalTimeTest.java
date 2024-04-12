package de.bund.bva.isyfact.datetime.format;

import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**

 */
@RunWith(Parameterized.class)
public class InFormatParseToLocalTimeTest {

    @Parameters(name = "{index}: parseToLocalTime({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new Object[][] { { "1:23", LocalTime.of(1, 23) }, { "1:23:45", LocalTime.of(1, 23, 45) },
                { "1:23:45.123", LocalTime.of(1, 23, 45, 123000000) },
                { "1:23:45.123456", LocalTime.of(1, 23, 45, 123456000) },
                { "1:23:45.123456789", LocalTime.of(1, 23, 45, 123456789) }, { "01:23", LocalTime.of(1, 23) },
                { "01:23:45", LocalTime.of(1, 23, 45) },
                { "01:23:45.123", LocalTime.of(1, 23, 45, 123000000) },
                { "01:23:45.123456", LocalTime.of(1, 23, 45, 123456000) },
                { "01:23:45.123456789", LocalTime.of(1, 23, 45, 123456789) } });
    }

    @Parameter
    public String input;

    @Parameter(1)
    public LocalTime expected;

    @Test
    public void parseToLocalTime() throws Exception {
        assertEquals(expected, InFormat.parseToLocalTime(input));
    }
}