package de.bund.bva.isyfact.sonderzeichen.dinnorm91379;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class CharacterUtilTest {

    public static final String TEST_STRING = "\u006C\u0302uivir%$";

    @Test
    public void testParseString() {
        assertNull(CharacterUtil.parseString(""));
        assertArrayEquals(new char[]{ '\u0042' }, CharacterUtil.parseString("0042"));
        assertArrayEquals(new char[]{ '\u005A', '\u0300' }, CharacterUtil.parseString("005A+0300"));
        assertArrayEquals(new char[]{ '\u0063', '\u0328', '\u0306' }, CharacterUtil.parseString("0063+0328+0306"));
    }

    @Test(expected = NullPointerException.class)
    public void testNPEIfParseNullString() {
        CharacterUtil.parseString(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNPEIfCollectionIsEmpty() {
        CharacterUtil.containsOnlyCharsFromSet(null, Collections.emptySet());
    }

    @Test(expected = NullPointerException.class)
    public void testNPEIfCharactersAreNull() {
        CharacterUtil.containsOnlyCharsFromSet("", null);
    }

    @Test(expected = NullPointerException.class)
    public void testNPEIfNull() {
        CharacterUtil.containsOnlyCharsFromSet(null, null);
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

}
