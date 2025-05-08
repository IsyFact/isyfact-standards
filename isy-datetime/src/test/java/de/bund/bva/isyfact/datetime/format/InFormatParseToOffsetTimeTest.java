package de.bund.bva.isyfact.datetime.format;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class InFormatParseToOffsetTimeTest {

    @Parameterized.Parameters(name = "{index}: parseToOffsetTime({0}) => {1}")
    public static Collection<Object[]> data() {
        ZoneOffset zo = ZoneOffset.ofHoursMinutes(2, 30);

        return Arrays.asList(new Object[][] { { "1:23 +02:30", OffsetTime.of(1, 23, 0, 0, zo) },
            { "1:23:45 +02:30", OffsetTime.of(1, 23, 45, 0, zo) },
            { "1:23:45.123 +02:30", OffsetTime.of(1, 23, 45, 123000000, zo) },
            { "1:23:45.123456 +02:30", OffsetTime.of(1, 23, 45, 123456000, zo) },
            { "1:23:45.123456789 +02:30", OffsetTime.of(1, 23, 45, 123456789, zo) },
            { "01:23 +02:30", OffsetTime.of(1, 23, 0, 0, zo) },
            { "01:23:45 +02:30", OffsetTime.of(1, 23, 45, 0, zo) },
            { "01:23:45.123 +02:30", OffsetTime.of(1, 23, 45, 123000000, zo) },
            { "01:23:45.123456 +02:30", OffsetTime.of(1, 23, 45, 123456000, zo) },
            { "01:23:45.123456789 +02:30", OffsetTime.of(1, 23, 45, 123456789, zo) } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public OffsetTime expected;

    @Test
    public void parseToOffsetTime() throws Exception {
        assertEquals(expected, InFormat.parseToOffsetTime(input));
    }
}
