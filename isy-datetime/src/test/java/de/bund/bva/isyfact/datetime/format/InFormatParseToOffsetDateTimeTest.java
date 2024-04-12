package de.bund.bva.isyfact.datetime.format;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**

 */
@RunWith(Parameterized.class)
public class InFormatParseToOffsetDateTimeTest {

    @Parameterized.Parameters(name = "{index}: parseToOffsetDateTime({0}) => {1}")
    public static Collection<Object[]> data() {
        ZoneOffset zo = ZoneOffset.ofHoursMinutes(2, 30);
        LocalDate datum = LocalDate.of(2017, 8, 1);
        LocalTime zeit = LocalTime.of(1, 23, 0);
        LocalTime zeitSek = LocalTime.of(1, 23, 45);
        LocalTime zeitMilliSek = LocalTime.of(1, 23, 45, 123000000);
        LocalTime zeitMikroSek = LocalTime.of(1, 23, 45, 123456000);
        LocalTime zeitNanoSek = LocalTime.of(1, 23, 45, 123456789);

        return Arrays.asList(new Object[][] { { "1.8.2017 1:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "1.8.2017 1:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "1.8.2017 1:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "1.8.2017 1:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "1.8.2017 1:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "1.8.2017 01:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "1.8.2017 01:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "1.8.2017 01:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "1.8.2017 01:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "1.8.2017 01:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "1.08.2017 1:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "1.08.2017 1:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "1.08.2017 1:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "1.08.2017 1:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "1.08.2017 1:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "1.08.2017 01:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "1.08.2017 01:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "1.08.2017 01:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "1.08.2017 01:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "1.08.2017 01:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "01.8.2017 1:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "01.8.2017 1:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "01.8.2017 1:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "01.8.2017 1:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "01.8.2017 1:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "01.8.2017 01:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "01.8.2017 01:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "01.8.2017 01:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "01.8.2017 01:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "01.8.2017 01:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "01.08.2017 1:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "01.08.2017 1:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "01.08.2017 1:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "01.08.2017 1:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "01.08.2017 1:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) },
            { "01.08.2017 01:23 +02:30", OffsetDateTime.of(datum, zeit, zo) },
            { "01.08.2017 01:23:45 +02:30", OffsetDateTime.of(datum, zeitSek, zo) },
            { "01.08.2017 01:23:45.123 +02:30", OffsetDateTime.of(datum, zeitMilliSek, zo) },
            { "01.08.2017 01:23:45.123456 +02:30", OffsetDateTime.of(datum, zeitMikroSek, zo) },
            { "01.08.2017 01:23:45.123456789 +02:30", OffsetDateTime.of(datum, zeitNanoSek, zo) } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public OffsetDateTime expected;

    @Test
    public void parseToOffsetDateTime() throws Exception {
        assertEquals(expected, InFormat.parseToOffsetDateTime(input));
    }
}
