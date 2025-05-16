package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation.impl;

import java.util.Set;

/**
 * Methods for checking transcription rules.
 *
 */
public final class Transskriptionsregeln {

    private Transskriptionsregeln() {
    }

    /**
     * Checks whether a position in the text is at the beginning of a word.
     *
     * @param text
     *            The text in which to examine the position.
     * @param position
     *            The position to be examined.
     * @param trennzeichen
     *            The set of all characters that are not considered part of a word.
     * @return true if the position is at the beginning of a word, otherwise false
     */
    public static boolean isWortanfang(String text, int position, Set<Character> trennzeichen) {
        if (text == null || position > text.length() - 1) {
            return false;
        }
        return position <= 0 || trennzeichen.contains(text.charAt(position - 1));
    }

    /**
     * Checks whether a substring in the text ends at the end of a word.
     *
     * @param text
     *            The text in which to examine the position.
     * @param position
     *            The starting position of the substring to be examined.
     * @param laenge
     *            The length of the substring to be examined.
     * @param trennzeichen
     *            The set of all characters that are not considered part of a word.
     * @return true if the substring is at the end of a word, otherwise false
     */
    public static boolean isWortende(String text, int position, int laenge, Set<Character> trennzeichen) {
        if (text == null) {
            return false;
        }
        return position + laenge == text.length() || isVorZeichen(text, position, laenge, trennzeichen);
    }

    /**
     * Checks whether there is a substring in the text in the middle of a word.
     *
     * @param text
     *            The text in which to examine the position.
     * @param position
     *            The starting position of the substring to be examined.
     * @param laenge
     *            The length of the substring to be examined.
     * @param trennzeichen
     *            The set of all characters that are not considered part of a word.
     * @return true if the substring is in the middle of a word, otherwise false
     */
    public static boolean isWortmitte(String text, int position, int laenge, Set<Character> trennzeichen) {
        if (text == null) {
            return false;
        }
        return !(isWortanfang(text, position, trennzeichen) || isWortende(text, position, laenge, trennzeichen));
    }

    /**
     * Checks whether a certain substring in the text is followed by one of a set of characters.
     *
     * @param text
     *            The text in which to examine the position.
     * @param position
     *            The starting position of the substring to be examined.
     * @param laenge
     *            The length of the substring to be examined.
     * @param pruefzeichen
     *            The set of all characters that are not considered part of a word.
     * @return true if the substring is in the middle of a word, otherwise false
     */
    public static boolean isVorZeichen(String text, int position, int laenge, Set<Character> pruefzeichen) {
        if (text == null || position + laenge >= text.length()) {
            return false;
        }
        return pruefzeichen.contains(text.charAt(position + laenge));

    }

}
