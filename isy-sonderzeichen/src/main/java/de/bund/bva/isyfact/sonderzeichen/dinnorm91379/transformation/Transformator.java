/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.sonderzeichen.dinnorm91379.transformation;

/**
 * Interface for all possible transformers. Defines methods that every transformer can offer.
 *
 */
public interface Transformator {

    /**
     * Transforms a string. Ensures that the string has the specified length after the operation. There is no
     * distinction between whether the original string length has already exceeded the maximum or whether the
     * string was only lengthened by a transformation. <br> Returns {@code null} if the string is
     * <code> null </ code > was passed.
     *
     * Important! The same restrictions apply to non-BMP characters as we do to
     * {@link #transformiere(String)}
     *
     * @param zeichenkette
     *            The string to transform
     * @param maximaleLaenge
     *            The maximum length of the string
     * @return the transformed string, or null if null was passed
     */
    String transformiere(String zeichenkette, int maximaleLaenge);

    /**
     * Transforms a string and returns it. Spaces at the beginning and at the end of the character string are
     * cut off. Duplicate spaces within the string are converted to a space. <br> Returns {@code null}
     * if the string was passed as {@code null}.
     *
     * Important! The transformation function processes the character string char for char. If a Unicode
     * character, which consists of several char objects, is defined (non-BMP character, e.g. I with appended
     * circumflex (\\u006C\\u0302), the transformation function delivers the correct result, but cannot
     * switch between SL and NON-SL distinguish characters. Characters outside the definition range
     * (eg all \\u####\\u0302) of the transformation could be transformed. <br> To check whether a character
     * string is within the range valid for the transformer, the function
     * {@link #isGueltigerString(String, String[])} can be used.
     * @param zeichenkette
     *            The string to transform
     * @return the transformed string, or null if null was passed
     */
    String transformiere(String zeichenkette);

    /**
     * Transforms a string and returns it. Does not trim at the end.
     *
     * Important! The same restrictions apply for non-BMP characters as for {@link #transformiere(String)}
     *
     * @param zeichenkette
     *            The string to transform
     * @return the transformed string, or null if null was passed
     */
    String transformiereOhneTrim(String zeichenkette);

    /**
     * Transforms a string and returns the transformed string and the metadata of the transformation.
     * Ensures that the string has the specified length after the operation. There is no distinction
     * between whether the original string length has already exceeded the maximum or whether the
     * string was only lengthened by a transformation. <br> Returns {@code null} if the string is
     * <code> null </ code > was passed.
     *
     * Important! The same restrictions apply to non-BMP characters as we do to
     * {@link #transformiere(String)}
     *
     * @param zeichenkette
     *            The string to transform
     * @param maximaleLaenge
     *            The maximum length of the string
     * @return a Transformation object conatining the transformed string (or null if null was passed) and
     *              the metadata of the transformation
     */
    Transformation transformiereMitMetadaten(String zeichenkette, int maximaleLaenge);

    /**
     * Transforms a string and returns the transformed string and the metadata of the transformation.
     * Spaces at the beginning and at the end of the character string are cut off. Duplicate spaces within
     * the string are converted to a space. <br> Returns {@code null} if the string was passed as {@code null}.
     *
     * Important! The same restrictions apply to non-BMP characters as we do to
     * {@link #transformiere(String)}
     *
     * {@link #isGueltigerString(String, String[])} can be used.
     * @param zeichenkette
     *            The string to transform
     * @return a Transformation object conatining the transformed string (or null if null was passed) and
     *            the metadata of the transformation
     */
    Transformation transformiereMitMetadaten(String zeichenkette);

    /**
     * Transforms a string and returns the transformed string and the metadata of the transformation.
     * Does not trim at the end.
     *
     * Important! The same restrictions apply for non-BMP characters as for {@link #transformiere(String)}
     *
     * @param zeichenkette
     *            The string to transform
     * @return a Transformation object conatining the transformed string (or null if null was passed) and
     *            the metadata of the transformation
     */
    Transformation transformiereOhneTrimMitMetadaten(String zeichenkette);

    /**
     * Returns the regular expression for all allowed expressions within the character category.
     * @param kategorieListe
     *            the allowed expressions of the character category
     * @return the regular expression
     */
    String getRegulaererAusdruck(String[] kategorieListe);

    /**
     * Checks whether the transferred character string only contains characters that exist in the respective
     * category of the target character set of the transformer. The target character set consists of the set of
     * characters that can be mapped to.
     * @param zeichenkette
     *            the string to be checked
     * @param kategorieListe
     *            the list of categories
     * @return true if the transferred character string only contains characters from the respective category
     */
    boolean isGueltigerString(String zeichenkette, String[] kategorieListe);

    /**
     * Returns all valid characters of the transformer.
     * @param kategorie
     *            the category
     * @return all valid characters of the transformer
     */
    String[] getGueltigeZeichen(String kategorie);

    /**
     * Checks with the help of a rule whether a substring to be transliterated fulfills a certain condition.
     *
     * @param regel
     *            Number of the rule to be evaluated.
     * @param text
     *            String from which the substring to be tested originates.
     * @param position
     *            Position of the substring in the text.
     * @param laenge
     *            Length of the substring.
     * @return true if the condition is met, otherwise false.
     */
    boolean werteRegelAus(int regel, String text, int position, int laenge);

}
