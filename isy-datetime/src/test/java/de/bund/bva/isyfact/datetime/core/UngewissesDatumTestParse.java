package de.bund.bva.isyfact.datetime.core;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class UngewissesDatumTestParse {

    @Parameterized.Parameters(name = "{index}: parse({0}) => {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { "00.00.0000", UngewissesDatum.leer() },
            { "00.00.2017", UngewissesDatum.of(2017) }, { "00.01.2017", UngewissesDatum.of(2017, 1) },
            { "00.10.2017", UngewissesDatum.of(2017, 10) }, { "xx.xx.xxxx", UngewissesDatum.leer() },
            { "xx.xx.2017", UngewissesDatum.of(2017) }, { "xx.01.2017", UngewissesDatum.of(2017, 1) },
            { "xx.10.2017", UngewissesDatum.of(2017, 10) }, { "01.01.2017", UngewissesDatum.of(2017, 1, 1) },
            { "01.10.2017", UngewissesDatum.of(2017, 10, 1) },
            { "10.10.2017", UngewissesDatum.of(2017, 10, 10) } });
    }

    @Parameterized.Parameter
    public String input;

    @Parameterized.Parameter(1)
    public UngewissesDatum expected;

    @Test
    public void parse() throws Exception {
        assertEquals(expected, UngewissesDatum.parse(input));
    }
}