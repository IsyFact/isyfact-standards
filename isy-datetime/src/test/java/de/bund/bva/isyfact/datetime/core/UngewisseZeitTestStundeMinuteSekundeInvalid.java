package de.bund.bva.isyfact.datetime.core;

import java.time.DateTimeException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class UngewisseZeitTestStundeMinuteSekundeInvalid {

    @Parameterized.Parameters(name = "{index}: of(Stunde={0}, Minute={1}, Sekunde={2})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // Stunde,  Minute, Sekunde
            { 1, 1, 60 }, { 1, 60, 1 }, { 1, 60, 60 }, { 24, 1, 1 }, { 24, 1, 60 }, { 24, 60, 1 },
            { 24, 60, 60 } });
    }

    @Parameterized.Parameter
    public int stunde;

    @Parameterized.Parameter(1)
    public int minute;

    @Parameterized.Parameter(2)
    public int sekunde;

    @Test(expected = DateTimeException.class)
    public void ofStundeMinuteSekunde() {
        UngewisseZeit.of(stunde, minute, sekunde);
    }
}