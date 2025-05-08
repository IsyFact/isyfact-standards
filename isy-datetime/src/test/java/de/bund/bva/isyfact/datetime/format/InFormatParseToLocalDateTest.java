package de.bund.bva.isyfact.datetime.format;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class InFormatParseToLocalDateTest {

    @Parameterized.Parameters(name = "{index}: parseToLocalDate({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new Object[][] { { "1.1.1", LocalDate.of(1, 1, 1) }, { "1.1.11", LocalDate.of(11, 1, 1) },
                { "1.1.111", LocalDate.of(111, 1, 1) }, { "1.1.1111", LocalDate.of(1111, 1, 1) },
                { "01.01.1", LocalDate.of(1, 1, 1) }, { "01.01.11", LocalDate.of(11, 1, 1) },
                { "01.01.111", LocalDate.of(111, 1, 1) }, { "01.01.1111", LocalDate.of(1111, 1, 1) } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public LocalDate expected;

    @Test
    public void parseToLocalDate() throws Exception {
        assertEquals(expected, InFormat.parseToLocalDate(input));
    }
}
