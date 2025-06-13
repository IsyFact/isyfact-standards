package de.bund.bva.isyfact.datetime.core;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class ZeitraumParseTest {

    private static LocalTime zeitAnfang = LocalTime.of(14, 0);

    private static LocalTime zeitEnde = LocalTime.of(15, 45);

    private static LocalDate datumAnfang = LocalDate.of(2017, 7, 12);

    private static LocalDate datumEnde = LocalDate.of(2018, 9, 13);

    private static Duration dauerDuration = Duration.ofMinutes(90);

    private static Period dauerPeriod = Period.ofDays(7);

    private static ZoneId MOSKAU = ZoneId.of("Europe/Moscow");

    @Parameterized.Parameters(name = "{index}: parse({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "12.7.2017 14:00 Europe/Moscow, 13.09.2018 15:45 Europe/Moscow",
                Zeitraum.of(ZonedDateTime.of(datumAnfang, zeitAnfang, MOSKAU),
                    ZonedDateTime.of(datumEnde, zeitEnde, MOSKAU)) }, { "12.7.2017 14:00 Europe/Moscow, 7d",
            Zeitraum.of(ZonedDateTime.of(datumAnfang, zeitAnfang, MOSKAU), dauerPeriod) },
            { "12.7.2017 14:00 Europe/Moscow, 1h 30min",
                Zeitraum.of(ZonedDateTime.of(datumAnfang, zeitAnfang, MOSKAU), dauerDuration) },
            { "12.7.2017 14:00, 13.09.2018 15:45", Zeitraum.of(LocalDateTime.of(datumAnfang, zeitAnfang),
                LocalDateTime.of(datumEnde, zeitEnde)) },
            { "12.7.2017 14:00, 7d", Zeitraum.of(LocalDateTime.of(datumAnfang, zeitAnfang), dauerPeriod) },
            { "12.7.2017 14:00, 1h 30min",
                Zeitraum.of(LocalDateTime.of(datumAnfang, zeitAnfang), dauerDuration) },
            { "12.07.2017, 13.09.2018", Zeitraum.of(datumAnfang, datumEnde) },
            { "12.07.2017, 7d", Zeitraum.of(datumAnfang, dauerPeriod) },
            { "14:00:00, 15:45:00", Zeitraum.of(zeitAnfang, zeitEnde) },
            { "14:00, 90min", Zeitraum.of(zeitAnfang, dauerDuration) } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public Zeitraum expected;

    @Test
    public void parse() throws Exception {
        assertEquals(expected, Zeitraum.parse(input));
    }

}
