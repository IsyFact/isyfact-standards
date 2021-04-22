package de.bund.bva.isyfact.logging.hilfsklassen;

import java.util.Objects;

import de.bund.bva.isyfact.logging.util.LogHelper;

/**
 * Class with various method signatures for testing the {@link LogHelper}.
 */
public class TestSignaturKlasse {

    /**
     * Method which takes a String as a parameter and subsequently returns the same String.
     *
     * @param string Input String.
     * @return The input String.
     */
    public String method(String string) {
        return string;
    }

    /**
     * Method with a 'throws' signature which takes a String as a parameter and subsequently
     * returns the same String.
     *
     * @param string Input String.
     * @return The input String.
     * @throws Exception Thrown when the input String is null or empty.
     */
    public String methodWithException(String string) throws Exception {
        if (Objects.isNull(string) || string.isEmpty()) {
            throw new Exception();
        } else {
            return string;
        }
    }

}
