package de.bund.bva.pliscommon.plissonderzeichen.dinspec91379;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class CharacterUtilTest {

    public static final String TEST_STRING = "\u006C\u0302uivir%$";

    @Test
    public void testParseString() {
        assertThrowsNPE(() -> CharacterUtil.parseString(null));
        assertNull(CharacterUtil.parseString(""));
        assertArrayEquals(new char[]{ '\u0042' }, CharacterUtil.parseString("0042"));
        assertArrayEquals(new char[]{ '\u005A', '\u0300' }, CharacterUtil.parseString("005A+0300"));
        assertArrayEquals(new char[]{ '\u0063', '\u0328', '\u0306' }, CharacterUtil.parseString("0063+0328+0306"));
    }

    @Test
    public void testNPEIfAnyArgIsNull() {
        assertThrowsNPE(() -> CharacterUtil.containsOnlyCharsFromSet(null, Collections.emptySet()));
        assertThrowsNPE(() -> CharacterUtil.containsOnlyCharsFromSet("", null));
        assertThrowsNPE(() -> CharacterUtil.containsOnlyCharsFromSet(null, null));
    }

    @Test
    public void testCheckFailsIfEmptySet() {
        boolean success = CharacterUtil.containsOnlyCharsFromSet(TEST_STRING, Collections.emptySet());
        assertFalse(success);
    }

    @Test
    public void testCheckFailsIfNotAllCharsInSet() {
        Set<String> validCharacters = new HashSet<>(Arrays.asList("\u006C", "i", "r", "u", "v", "%", "$"));
        boolean success = CharacterUtil.containsOnlyCharsFromSet(TEST_STRING, validCharacters);
        assertFalse(success);
    }

    @Test
    public void testCheckSuccessfulIndividual() {
        Set<String> validCharacters = new HashSet<>(Arrays.asList("\u0302", "\u006C", "i", "r", "u", "v", "%", "$"));
        boolean success = CharacterUtil.containsOnlyCharsFromSet(TEST_STRING, validCharacters);
        assertTrue(success);
    }

    @Test
    public void testCheckSuccessfulCombined() {
        Set<String> validCharacters = new HashSet<>(Arrays.asList("\u006C\u0302", "i", "r", "u", "v", "%", "$"));
        boolean success = CharacterUtil.containsOnlyCharsFromSet(TEST_STRING, validCharacters);
        assertTrue(success);
    }

    private void assertThrowsNPE(Runnable runnable) {
        try {
            runnable.run();
            fail("NullPointerException expected.");
        } catch (NullPointerException e) {
            // expected
        }
    }

}
