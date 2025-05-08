package de.bund.bva.isyfact.datetime.format;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class InFormatParseToZonedDateTimeTest {

    private static ZonedDateTime expectedZonedDateTime =
        ZonedDateTime.of(2017, 8, 1, 1, 23, 0, 0, ZoneId.of("Europe/Berlin"));

    @Parameterized.Parameters(name = "{index}: parseToZonedDateTime({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "1.8.2017 1:23 Europe/Berlin", expectedZonedDateTime },
            { "1.8.2017 01:23 Europe/Berlin", expectedZonedDateTime },
            { "1.08.2017 1:23 Europe/Berlin", expectedZonedDateTime },
            { "1.08.2017 01:23 Europe/Berlin", expectedZonedDateTime },
            { "01.8.2017 1:23 Europe/Berlin", expectedZonedDateTime },
            { "01.8.2017 01:23 Europe/Berlin", expectedZonedDateTime },
            { "01.08.2017 1:23 Europe/Berlin", expectedZonedDateTime },
            { "01.08.2017 01:23 Europe/Berlin", expectedZonedDateTime } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public ZonedDateTime expected;

    @Test
    public void parseToZonedDateTime() throws Exception {
        assertEquals(expected, InFormat.parseToZonedDateTime(input));
    }
}
