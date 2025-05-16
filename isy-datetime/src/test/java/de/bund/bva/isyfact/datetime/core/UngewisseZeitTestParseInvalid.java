package de.bund.bva.isyfact.datetime.core;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class UngewisseZeitTestParseInvalid {

    @Parameterized.Parameters(name = "{index}: parse({0})")
    public static Collection<Object> data() {
        return Arrays.asList(
            new Object[] { "", "xxx", "01:20", "10:aa:20", "10:-5:20", "10:xx:20", "xx:10:xx", "xx:xx:10",
                " 10:08:20", });
    }

    @Parameterized.Parameter
    public String input;

    @Test(expected = DateTimeParseException.class)
    public void parse() throws Exception {
        UngewisseZeit.parse(input);
    }
}