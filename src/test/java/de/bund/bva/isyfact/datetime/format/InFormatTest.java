package de.bund.bva.isyfact.datetime.format;

import java.time.LocalDate;

import de.bund.bva.isyfact.datetime.format.InFormat;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Björn Saxe, msg systems ag
 */
public class InFormatTest {

    @Test
    public void testInput() {
        String in = "1.10.0999";

        LocalDate dateTime = LocalDate.parse(in, InFormat.DATUM);

        LocalDate expected = LocalDate.of(999, 10, 1);
        assertEquals(expected, dateTime);
    }

}