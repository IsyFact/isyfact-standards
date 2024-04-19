package de.bund.bva.isyfact.datetime.core;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**

 */
@RunWith(Parameterized.class)
public class UngewissesDatumTestToString {

    @Parameterized.Parameters(name = "{index}: toString({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { UngewissesDatum.leer(), "xx.xx.xxxx" },
            { UngewissesDatum.of(2017, 8, 10), "10.08.2017" }, { UngewissesDatum.of(2017), "xx.xx.2017" },
            { UngewissesDatum.of(2017, 8), "xx.08.2017" },
            { UngewissesDatum.of(LocalDate.of(2017, 8, 10), LocalDate.of(2017, 8, 31)),
                "10.08.2017 - 31.08.2017" } });
    }

    @Parameterized.Parameter
    public UngewissesDatum input;

    @Parameterized.Parameter(1)
    public String expected;

    @Test
    public void parse() throws Exception {
        assertEquals(expected, input.toString());
    }
}