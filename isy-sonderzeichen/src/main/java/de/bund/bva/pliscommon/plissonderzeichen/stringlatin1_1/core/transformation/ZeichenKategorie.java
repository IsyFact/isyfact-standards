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
package de.bund.bva.pliscommon.plissonderzeichen.stringlatin1_1.core.transformation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ZeichenKategorie {

    public final static String LETTER = "LETTER";
    public final static String NUMBER = "NUMBER";
    public final static String OTHER = "OTHER";
    public final static String PUNCTUATION = "PUNCTUATION";
    public final static String SEPARATOR = "SEPARATOR";
    public final static String SYMBOL = "SYMBOL";
    public final static String ALLE = "ALLE";

    @SuppressFBWarnings(
            value = "MS_MUTABLE_ARRAY",
            justification = "solved with IFS-804"
    )
    public final static String[] ALLE_ZEICHEN_KATEGORIEN = new String[] {LETTER, NUMBER, OTHER, PUNCTUATION, SEPARATOR, SYMBOL, ALLE};

}
