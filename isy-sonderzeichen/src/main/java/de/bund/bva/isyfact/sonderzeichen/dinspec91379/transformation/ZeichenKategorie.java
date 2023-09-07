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
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *  @deprecated This class is deprecated and will be removed in a future release.
 *  It is recommended to use {@link de.bund.bva.isyfact.sonderzeichen.dinnorm91379} instead.
 */
@Deprecated
public final class ZeichenKategorie {

    /** Character of the category 'letter'. */
    public static final String LETTER = "LETTER";

    /** Character of the category 'number'. */
    public static final String NUMBER = "NUMBER";

    /** Character of the category 'other'. */
    public static final String OTHER = "OTHER";

    /** Character of the category 'punctuation'. */
    public static final String PUNCTUATION = "PUNCTUATION";

    /** Character of the category 'separator'. */
    public static final String SEPARATOR = "SEPARATOR";

    /** Character of the category 'symbol'. */
    public static final String SYMBOL = "SYMBOL";

    /** Character of the category 'all'. */
    public static final String ALLE = "ALLE";

    /** Array containing all possible categories of characters.*/
    @SuppressFBWarnings(
            value = "MS_MUTABLE_ARRAY",
            justification = "Solved with IFS-804"
    )
    public static final String[] ALLE_ZEICHEN_KATEGORIEN =
            {LETTER, NUMBER, OTHER, PUNCTUATION, SEPARATOR, SYMBOL, ALLE};

    private ZeichenKategorie() {
    }
}
