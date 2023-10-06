package de.bund.bva.isyfact.sonderzeichen.dinnorm91379;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CharacterUtil {

    /**
     * Checks if a given string contains only the characters present in the provided character set. A "character" is a
     * string containing a single Unicode character, which may consist of multiple primitive {@code char} elements.
     *
     * @param fullString
     *         string to check (must not be null)
     * @param validCharacters
     *         set of characters that are allowed in the string (must not be null)
     * @return {@code true} if the string is valid, otherwise {@code false}
     */
    public static boolean containsOnlyCharsFromSet(String fullString, Set<String> validCharacters) {
        Objects.requireNonNull(fullString, "fullString must not be null");
        Objects.requireNonNull(validCharacters, "validCharacters must not be null");

        // Create a map grouping the characters by their length.
        // This is necessary to check characters by descending length and easily skip those whose length would exceed the full string.
        Map<Integer, Set<String>> validCharsByLength = validCharacters.stream()
                .collect(Collectors.groupingBy(String::length, Collectors.toSet()));
        // Get max length of a character or 0, if the set of valid characters is empty
        int maximumValidCharLength = validCharsByLength.keySet().stream()
                .mapToInt(Integer::intValue).max().orElse(0);

        // Iterate through string
        for (int charIndexInFullString = 0; charIndexInFullString < fullString.length(); charIndexInFullString++) {

            // Iterate step by step over the length of possible characters, length of the mappable characters
            // must be taken into account
            boolean charMatches = false;
            // Check from longest to shortest to get the biggest match first
            for (int charLength = maximumValidCharLength; charLength > 0; charLength--) {
                boolean lengthDoesNotExceedFullString = charIndexInFullString + charLength <= fullString.length();
                if (lengthDoesNotExceedFullString) {
                    for (String validChar : validCharsByLength.get(charLength)) {
                        charMatches = matchesCharactersAtIndex(validChar, fullString, charIndexInFullString);

                        if (charMatches) {
                            // Advance the loop to the index for the next character
                            charIndexInFullString += charLength - 1;
                            break;
                        }
                    }
                    if (charMatches) {
                        break;
                    }
                }
            }

            // If a character could not be mapped, this is an error
            if (!charMatches) {
                return false;
            }
        }

        return true;
    }

    private static boolean matchesCharactersAtIndex(String validChar, String fullString, int fullStringIndex) {
        if (validChar.isEmpty()) {
            return false;
        }

        // check chars the strings against the valid char
        for (int gueltigesZeichenIteration = 0; gueltigesZeichenIteration < validChar.length(); gueltigesZeichenIteration++) {
            if (validChar.charAt(gueltigesZeichenIteration) !=
                    fullString.charAt(fullStringIndex + gueltigesZeichenIteration)) {
                // if the primitive chars don't match, return false
                return false;
            }
        }

        return true;
    }

    /**
     * Converts a string of "+" seperated hexadecimal Unicode code point to a char array of the characters represented
     * by those code points.
     * <p>
     * Examples:
     * <ul>
     *     <li>{@code "0041"} will be converted to {@code ['\u0041']} (latin capital letter A)</li>
     *     <li>{@code "0043+0304"} will be converted to {@code ['\u0043','\u0304']} (latin capital letter C, followed by the combining grave accent)</li>
     * </ul>
     *
     * @param codePoints
     *         hexadecimal Unicode code points separated with "+"
     * @return char array of the actual characters represented by the code points
     */
    public static char[] parseString(String codePoints) {
        if (codePoints.length() == 0) {
            return null;
        }

        String[] splits = codePoints.split("[+]");

        char[] toReturn = new char[splits.length];

        for (int i = 0; i < splits.length; i++) {
            String hexString = splits[i];
            hexString = hexString.trim();
            int hexChar = Integer.parseInt(hexString, 16);
            char toChar = (char) hexChar;
            toReturn[i] = toChar;
        }

        return toReturn;
    }

}
