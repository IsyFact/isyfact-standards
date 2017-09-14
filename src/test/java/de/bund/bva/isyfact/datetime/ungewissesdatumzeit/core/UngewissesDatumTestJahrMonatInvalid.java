package de.bund.bva.isyfact.datetime.ungewissesdatumzeit.core;

import java.time.DateTimeException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

/**
 * @author Björn Saxe, msg systems ag
 */
@RunWith(Parameterized.class)
public class UngewissesDatumTestJahrMonatInvalid {

    @Parameterized.Parameters(name = "{index}: of(Jahr={0}, Monat={1})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // Jahr,  Monat
            { -1, 0 }, { 0, -1 }, { -1, -1 }, { 10000, 1 }, { 1, 10000 }, { 10000, 10000 }, { 0, 1 } });
    }

    @Parameterized.Parameter
    public int jahr;

    @Parameterized.Parameter(1)
    public int monat;

    @Test(expected = DateTimeException.class)
    public void ofJahrMonat() {
        UngewissesDatum.of(jahr, monat);
    }
}