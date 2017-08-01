package de.bund.bva.isyfact.datetime.format;

import java.time.LocalDateTime;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Björn Saxe, msg systems ag
 */
public class OutFormatTest {

    @Test
    public void outputTest() {
        LocalDateTime jetzt = LocalDateTime.now();

        String result = jetzt.format(OutFormat.DATUM_ZEIT_LANG_TAG_ZONE);

    }
}