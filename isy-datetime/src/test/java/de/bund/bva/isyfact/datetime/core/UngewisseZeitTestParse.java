package de.bund.bva.isyfact.datetime.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**

 */
@RunWith(Parameterized.class)
public class UngewisseZeitTestParse {

    @Parameterized.Parameters(name = "{index}: parse({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
            new Object[][] { { "xx:xx:xx", UngewisseZeit.leer() }, { "09:xx:xx", UngewisseZeit.of(9) },
                { "17:xx:xx", UngewisseZeit.of(17) }, { "09:09:xx", UngewisseZeit.of(9, 9) },
                { "09:30:xx", UngewisseZeit.of(9, 30) }, { "17:09:xx", UngewisseZeit.of(17, 9) },
                { "17:30:xx", UngewisseZeit.of(17, 30) }, { "17:30:45", UngewisseZeit.of(17, 30, 45) } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public UngewisseZeit expected;

    @Test
    public void parse() throws Exception {
        assertEquals(expected, UngewisseZeit.parse(input));
    }
}