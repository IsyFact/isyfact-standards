package de.bund.bva.isyfact.datetime.core;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ZeitraumParseInvalidTest {

    private static LocalTime zeitAnfang = LocalTime.of(14, 0);

    private static LocalTime zeitEnde = LocalTime.of(15, 45);

    private static LocalDate datumAnfang = LocalDate.of(2017, 7, 12);

    private static LocalDate datumEnde = LocalDate.of(2018, 9, 13);

    private static Duration dauerDuration = Duration.ofMinutes(90);

    private static Period dauerPeriod = Period.ofDays(7);

    private static ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Parameterized.Parameters(name = "{index}: parse({0})")
    public static Collection<Object> data() {
        return Arrays.asList("", ",", " , ", "12.7.2017 14:00", ", 13.09.2018 15:45",
            "12.7.2017 14:00; 13.09.2018 15:45", "12.7.2017 14:00, 15:45", "12.07.2017, 15:45",
            "12.07.2017, 5h", "14:00:00, 12.07.2017", "14:00, 7d");
    }

    @Parameterized.Parameter
    public String input;

    @Test(expected = DateTimeParseException.class)
    public void parse() throws Exception {
        Zeitraum.parse(input);
    }

}
