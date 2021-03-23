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
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.core.transformation.impl;

import java.util.Set;

/**
 * Methoden zur Prüfung von Transskriptionsregeln.
 * 
 */
public abstract class Transskriptionsregeln {

    /**
     * Prüft, ob sich eine Position im Text an einem Wortanfang befindet.
     * 
     * @param text
     *            Der Text, in dem die Position untersucht werden soll.
     * @param position
     *            Die zu untersuchende Position.
     * @param trennzeichen
     *            Die Menge aller Zeichen, die nicht als Teil eines Wortes gelten.
     * @return true, wenn die Position an einem Wortanfang steht, sonst false
     */
    public static boolean isWortanfang(String text, int position, Set trennzeichen) {
        if (text == null || position > text.length() - 1) {
            return false;
        }
        if (position <= 0) {
            return true;
        }
        if (trennzeichen.contains((text.charAt(position - 1)))) {
            return true;
        }
        return false;
    }

    /**
     * Prüft, ob ein Substring im Text an einem Wortende endet.
     * 
     * @param text
     *            Der Text, in dem die Position untersucht werden soll.
     * @param position
     *            Die Startposition des zu untersuchenden Substrings.
     * @param laenge
     *            Die Länge des zu untersuchenden Substrings.
     * @param trennzeichen
     *            Die Menge aller Zeichen, die nicht als Teil eines Wortes gelten.
     * @return true, wenn der Substring an einem Wortende steht, sonst false
     */
    public static boolean isWortende(String text, int position, int laenge, Set trennzeichen) {
        if (text == null) {
            return false;
        }
        if (position + laenge == text.length()) {
            return true;
        }
        return isVorZeichen(text, position, laenge, trennzeichen);
    }

    /**
     * Prüft, ob sich ein Substring im Text in der Mitte eines Wortes befindet.
     * 
     * @param text
     *            Der Text, in dem die Position untersucht werden soll.
     * @param position
     *            Die Startposition des zu untersuchenden Substrings.
     * @param laenge
     *            Die Länge des zu untersuchenden Substrings.
     * @param trennzeichen
     *            Die Menge aller Zeichen, die nicht als Teil eines Wortes gelten.
     * @return true, wenn der Substring in der Mitte eines Wortes steht, sonst false
     */
    public static boolean isWortmitte(String text, int position, int laenge, Set trennzeichen) {
        if (text == null) {
            return false;
        }
        return (!isWortanfang(text, position, trennzeichen) && !isWortende(text, position, laenge,
            trennzeichen));
    }

    /**
     * Prüft, ob auf einen bestimmten Substring im Text eines aus einer Menge von Zeichen folgt.
     * 
     * @param text
     *            Der Text, in dem die Position untersucht werden soll.
     * @param position
     *            Die Startposition des zu untersuchenden Substrings.
     * @param laenge
     *            Die Länge des zu untersuchenden Substrings.
     * @param pruefzeichen
     *            Die Menge aller Zeichen, die nicht als Teil eines Wortes gelten.
     * @return true, wenn der Substring in der Mitte eines Wortes steht, sonst false
     */
    public static boolean isVorZeichen(String text, int position, int laenge, Set pruefzeichen) {
        if (text == null) {
            return false;
        }
        if (position + laenge >= text.length()) {
            return false;
        }
        if (pruefzeichen.contains((text.charAt(position + laenge)))) {
            return true;
        }
        return false;

    }

}
