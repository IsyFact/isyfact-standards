package de.bund.bva.isyfact.datetime.format;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(Parameterized.class)
public class InFormatParseToPeriodInvalidTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: parseToPeriod({0})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "", 0 }, { "xxx", 0 }, { "xxx 7a 6M", 0 }, { "7a 6M xxx", 6 },
            { "7a 5d xh", 6 }, { "7a -5d 4h", 3 }, { "4a 3M 2D", 6 }, { "4a3M2d", 0 }, { "7h 5a 6s", 0 } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public long errorIndex;

    @Test
    public void parseToPeriod() throws Exception {
        try {
            InFormat.parseToPeriod(input);
            fail();
        } catch (DateTimeParseException e) {
            assertEquals(input, e.getParsedString());
            assertEquals(errorIndex, e.getErrorIndex());
        }
    }
}
