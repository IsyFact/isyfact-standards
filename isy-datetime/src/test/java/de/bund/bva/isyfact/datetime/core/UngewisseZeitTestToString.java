package de.bund.bva.isyfact.datetime.core;

import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**

 */
@RunWith(Parameterized.class)
public class UngewisseZeitTestToString {

    @Parameterized.Parameters(name = "{index}: toString({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { UngewisseZeit.leer(), "xx:xx:xx" },
            { UngewisseZeit.of(17, 30, 01), "17:30:01" }, { UngewisseZeit.of(17), "17:xx:xx" },
            { UngewisseZeit.of(17, 30), "17:30:xx" },
            { UngewisseZeit.of(LocalTime.of(17, 30, 1), LocalTime.of(17, 45, 45)), "17:30:01 - 17:45:45" } });
    }

    @Parameterized.Parameter
    public UngewisseZeit input;

    @Parameterized.Parameter(1)
    public String expected;

    @Test
    public void parse() throws Exception {
        assertEquals(expected, input.toString());
    }
}