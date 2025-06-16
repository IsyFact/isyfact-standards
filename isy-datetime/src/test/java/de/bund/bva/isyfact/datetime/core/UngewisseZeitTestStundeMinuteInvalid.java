package de.bund.bva.isyfact.datetime.core;

import java.time.DateTimeException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class UngewisseZeitTestStundeMinuteInvalid {

    @Parameterized.Parameters(name = "{index}: of(Stunde={0}, Minute={1})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // Stunde, Minute
            { 1, 60 }, { 24, 1 }, { 24, 60 } });
    }

    @Parameterized.Parameter
    public int stunde;

    @Parameterized.Parameter(1)
    public int minute;

    @Test(expected = DateTimeException.class)
    public void ofStundeMinute() {
        UngewisseZeit.of(stunde, minute);
    }
}