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
public class InFormatParseToDurationInvalidTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Parameterized.Parameters(name = "{index}: parseToDuration({0})")
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new Object[][] { { "", 0 }, { "xxx", 0 }, { "xxx 7h 6min", 0 }, { "7h 6min xxx", 8 },
                { "7h 5min xs", 8 }, { "7h -5min 4s", 3 }, { "4h 3min 2S", 8 }, { "4h3min2s", 0 },
                { "7a 5h 6min", 0 } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public long errorIndex;

    @Test
    public void parseToDuration() throws Exception {
        try {
            InFormat.parseToDuration(input);
            fail();
        } catch (DateTimeParseException e) {
            assertEquals(errorIndex, e.getErrorIndex());
        }
    }
}
