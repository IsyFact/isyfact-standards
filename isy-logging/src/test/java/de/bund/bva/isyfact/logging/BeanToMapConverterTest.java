package de.bund.bva.isyfact.logging;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
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
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.bund.bva.isyfact.logging.exceptions.SerialisierungException;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanMitException;
import de.bund.bva.isyfact.logging.impl.LogErrorKategorie;
import de.bund.bva.isyfact.logging.util.BeanToMapConverter;
import de.bund.bva.isyfact.logging.util.LogHelper;

/**
 * Testfälle des BeanToMapConverters.
 */
public class BeanToMapConverterTest {

    /**
     * Testet die Konvertierung eines einfachen Datentyps.
     */
    @Test
    public void testPrimitives() {
        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        assertEquals("5", converter.convert(5));
    }

    /**
     * Testet die Konvertierung von <code>null</code>.
     */
    @Test
    public void testNull() {

        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        assertEquals(BeanToMapConverter.NULL_STRING, converter.convert(null));

    }

    /**
     * Testet die Behandlung eines Fehlers, der bei der Konvertierung auftritt..
     */
    @Test
    public void testException() {

        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        try {
            converter.convert(new TestBeanMitException());
            fail("Es wurde keine SerialisierungException geworfen.");
        } catch (SerialisierungException se) {
            assertEquals("ISYLO01001", se.getAusnahmeId());
        }
    }

    /**
     * Test der Konvertierung eines einfachen Beans, bei dem sowohl includes als auch excludes angegeben
     * wurden.
     */
    @Test
    public void testIncludeExcludeEinfach() {

        // TestBeanEinfach wird includiert
        // TestBeanKomplex wird excludiert
        BeanToMapConverter converter = new BeanToMapConverter(
            Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach"), Arrays.asList(
                        "de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex", "java.util"));

        // Konvertiere eine Instanz von TestBeanEinfach.
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert = (Map<String, Object>) converter.convert(new TestBeanEinfach());
        List<String> keyList = new ArrayList<>(konvertiert.keySet());
        List<Object> valueList = new ArrayList<>(konvertiert.values());

        // Das Testbean besitzt 3 Properties + den HashCode + 'Class' der immer serialisiert wird. Eines davon ist vom
        // Typ TestBeanKomplex, welches auf Grund des Excludes nicht berücksichtigt werden soll.
        assertEquals(5, konvertiert.size());

        // Zähler des aktuellen Attributs
        int currentPos = 0;
        
        // Prüfung der einzelnen Properties
        
        // Property
        assertEquals("class", keyList.get(currentPos));
        assertEquals(TestBeanEinfach.class.toString(), valueList.get(currentPos));
        
        // Property
        currentPos++;
        assertEquals("einString", keyList.get(currentPos));
        assertEquals("einString", valueList.get(currentPos));

        // Property
        currentPos++;
        assertEquals("eineListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        List<Object> eineListeValues = (List<Object>) valueList.get(currentPos);
        assertEquals(2, eineListeValues.size());
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, eineListeValues.get(0));
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, eineListeValues.get(1));

        // Property
        currentPos++;
        assertEquals("hashCode", keyList.get(currentPos));

        // Property
        currentPos++;
        assertEquals("inneresBean", keyList.get(currentPos));
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, valueList.get(currentPos));

    }

    /**
     * Einfacher Test um die Initialisierung der Include-Liste mit null und einer Leerenliste zu testen.
     */
    @Test
    public void testLeererIncludeVarianten() {

        // Include = null
        BeanToMapConverter converter = new BeanToMapConverter(null,
                Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex"));
        TestBeanEinfach tbe = new TestBeanEinfach();
        Object konvertiert = converter.convert(tbe);
        assertEquals(tbe.toString(), konvertiert);

        // Include = leereListe
        converter = new BeanToMapConverter(new ArrayList<>(),
            Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex"));
        tbe = new TestBeanEinfach();
        konvertiert = converter.convert(tbe);
        assertEquals(tbe.toString(), konvertiert);

    }

    /**
     * Testet die Konvertierung eines Objects, das excludiert ist.
     */
    @Test
    public void testExcludeEinfach() {

        BeanToMapConverter converter = new BeanToMapConverter(null,
            Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex"));
        TestBeanKomplex tbk = new TestBeanKomplex(false);
        Object konvertiert = converter.convert(tbk);
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, konvertiert);

    }

    /**
     * Testet die Konvertierung eines Objects, das excludiert ist - dabei wird komplett de/bund excludiert.
     */
    @Test
    public void testExcludeEinfachKomplett() {

        BeanToMapConverter converter = new BeanToMapConverter(null, Collections.singletonList("de.bund"));
        TestBeanEinfach tbe = new TestBeanEinfach();
        Object konvertiert = converter.convert(tbe);
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, konvertiert);

    }

    /**
     * Umfangreicher Test des Standardkonverters. Siehe einzelne Testschritte für Details.
     */
    @Test
    public void testStandardKonverter() {

        // Zunächst wird der Standardkonverter erzeugt.
        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        // Es wird ein Komplexes Bean konvertiert
        TestBeanKomplex bean = new TestBeanKomplex(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert = (Map<String, Object>) converter.convert(bean);
        // Durchführung der eigentlichen Prüfungen
        pruefeTestBeanStandard(bean, konvertiert, true);

        // Wiederholung des Tests mit einem manuel erstellten Konverter (analog zum Standardkonverter
        converter = new BeanToMapConverter(Collections.singletonList("de.bund."), new ArrayList<>());
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert2 = (Map<String, Object>) converter.convert(bean);
        pruefeTestBeanStandard(bean, konvertiert2, true);

    }

    /**
     * Hilfsmethode zur Prüfung der korrekten Konvertierung eines "Komplex-Beans".
     * 
     * @param bean
     *            das konvertierte Bean.
     * @param konvertiert
     *            das Bean in seiner konvertierten Form.
     * @param rekursiv
     *            gibt an, ob das KomplexeBean mit dem Flag "rekursiv" erstellt wurde oder nicht.
     */
    private void pruefeTestBeanStandard(TestBeanKomplex bean, Map<String, Object> konvertiert,
            boolean rekursiv) {

        // Key und Attribute in separaten Listen um mit dem Index darauf zugreifen zu können
        List<String> keyList = new ArrayList<>(konvertiert.keySet());
        List<Object> valueList = new ArrayList<>(konvertiert.values());

        // Es werden 18 Attribute erwartet die nachfolgend einzeln überprüft werden.
        assertEquals(19, keyList.size());

        // Zähler des aktuellen Attributs
        int currentPos = 0;
        
        // Property
        assertEquals("class", keyList.get(currentPos));
        assertEquals(TestBeanKomplex.class.toString(), valueList.get(currentPos));
        currentPos++;

        // Enum werden mit "toString umgewandelt
        assertEquals("einEnum", keyList.get(currentPos));
        assertEquals(bean.getEinEnum().toString(), valueList.get(currentPos));
        currentPos++;

        // Enum-Array wird zu Liste aus Strings umgewandelt
        assertEquals("einEnumArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einEnumArray = (ArrayList<String>) valueList.get(currentPos);
        assertEquals(einEnumArray.get(0), LogErrorKategorie.ERROR.toString());
        assertEquals(einEnumArray.get(1), LogErrorKategorie.FATAL.toString());
        assertEquals(einEnumArray.get(2), LogErrorKategorie.ERROR.toString());
        currentPos++;

        // Integer werden in String umgewandelt
        assertEquals("einInteger", keyList.get(currentPos));
        assertEquals(bean.getEinInteger().toString(), valueList.get(currentPos));
        currentPos++;

        // Bei einem Object-Array ist das Ergebnis eine Liste, in der alle Attribute einzeln konvertiert
        // wurden.
        assertEquals("einObjectArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<Object> einObjectArray = (ArrayList<Object>) valueList.get(currentPos);
        if (rekursiv) {
            assertEquals(4, einObjectArray.size());
        } else {
            assertEquals(3, einObjectArray.size());
        }
        // Objecte aus java.lang wird mit toString übernommen
        assertEquals(bean.getJavaLang().toString(), einObjectArray.get(0));
        // NULL wird in Null-String konvertiert
        assertEquals(BeanToMapConverter.NULL_STRING, einObjectArray.get(1));
        // Einfacher String
        assertEquals(bean.getEinString(), einObjectArray.get(2));
        if (rekursiv) {
            // Die Liste enthält selbst ein komplexes Bean, welches mit dem Parameter "rekursiv=false"
            // erstellt
            // wurde. Dies wird an dieser Stelle ebenfalls rekursiv überprüft
            @SuppressWarnings("unchecked")
            Map<String, Object> einObjecArrayBean = (Map<String, Object>) einObjectArray.get(3);
            pruefeTestBeanStandard(bean, einObjecArrayBean, false);
        }
        currentPos++;

        // Einfacher String
        assertEquals("einString", keyList.get(currentPos));
        assertEquals(bean.getEinString(), valueList.get(currentPos));
        currentPos++;

        // String-Array wird als String-Liste übernommen
        assertEquals("einStringArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einStringArray = (ArrayList<String>) valueList.get(currentPos);
        assertEquals("A", einStringArray.get(0));
        assertEquals("B", einStringArray.get(1));
        assertEquals(BeanToMapConverter.NULL_STRING, einStringArray.get(2));
        assertEquals("C", einStringArray.get(3));
        currentPos++;

        // String ohne Setter wird als normaler String übernommen
        assertEquals("einStringOhneSetter", keyList.get(currentPos));
        assertEquals(bean.getEinStringOhneSetter(), valueList.get(currentPos));
        currentPos++;

        // Enum-Liste wird als String-Liste übernommen
        assertEquals("eineEnumListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> eineEnumListe = (ArrayList<String>) valueList.get(currentPos);
        assertEquals(eineEnumListe.get(0), LogErrorKategorie.ERROR.toString());
        assertEquals(eineEnumListe.get(1), LogErrorKategorie.FATAL.toString());
        assertEquals(eineEnumListe.get(2), LogErrorKategorie.ERROR.toString());
        currentPos++;

        // Object-List analog zu Onject-Array (oben)
        assertEquals("eineObjectListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<Object> eineObjectListe = (ArrayList<Object>) valueList.get(currentPos);
        if (rekursiv) {
            assertEquals(4, eineObjectListe.size());
        } else {
            assertEquals(3, eineObjectListe.size());
        }
        assertEquals(bean.getJavaLang().toString(), eineObjectListe.get(0));
        assertEquals(BeanToMapConverter.NULL_STRING, eineObjectListe.get(1));
        assertEquals(bean.getEinString(), eineObjectListe.get(2));
        if (rekursiv) {
            String einObjecListBean = (String) eineObjectListe.get(3);
            assertTrue(einObjecListBean.startsWith("Bereits verarbeitet"));
        }
        currentPos++;

        // String-Liste wird als String-Liste übernommen
        assertEquals("eineStringListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einStringListe = (ArrayList<String>) valueList.get(currentPos);
        assertEquals("A", einStringListe.get(0));
        assertEquals("B", einStringListe.get(1));
        assertEquals(BeanToMapConverter.NULL_STRING, einStringListe.get(2));
        assertEquals("C", einStringListe.get(3));
        currentPos++;

        // Nicht includierte und nicht excludierte Objecte werden als toString übernommen
        assertEquals("extern", keyList.get(currentPos));
        assertEquals(bean.getExtern().toString(), valueList.get(currentPos));
        currentPos++;

        // HashCode wird immer übernommen (spezielles Feld, um bereits serialisierte Objekte zuordnen zu
        // können)
        assertEquals("hashCode", keyList.get(currentPos));
        currentPos++;

        // Nicht includierte und nicht excludierte Objecte werden als toString übernommen
        assertEquals("javaLang", keyList.get(currentPos));
        assertEquals(bean.getJavaLang().toString(), valueList.get(currentPos));
        currentPos++;

        // Nicht includierte und nicht excludierte Objecte werden als toString übernommen
        assertEquals("javaUtil", keyList.get(currentPos));
        assertEquals(bean.getJavaUtil().toString(), valueList.get(currentPos));
        currentPos++;

        // Null in einem String-Feld wird als Null-String übernommen.
        assertEquals("nullString", keyList.get(currentPos));
        assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        currentPos++;

        // Verweis auf sich selbst wird als "Bereits verarbeitet" übernommen
        assertEquals("rekursiv", keyList.get(currentPos));
        assertTrue(((String) valueList.get(currentPos)).startsWith("Bereits verarbeitet"));
        currentPos++;

        if (rekursiv) {
            // Wenn das Flag "rekursiv=true" ist, enthält rekursivNeu eine weitere Instanz eines
            // KomplexenTestBeans
            assertEquals("rekursivNeu", keyList.get(currentPos));
            @SuppressWarnings("unchecked")
            Map<String, Object> rekursivNeu = (Map<String, Object>) valueList.get(currentPos);
            pruefeTestBeanStandard(bean, rekursivNeu, false);
        } else {
            // andernfalls null
            assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        }
        currentPos++;

        if (rekursiv) {
            // Wenn das Flag "rekursiv=true" ist, enthält rekursivObject eine verweis auf das KomplexeTestBean
            // selbst - dieser wird als Bereits verarbeitet übernommen.
            assertEquals("rekursivObject", keyList.get(currentPos));
            assertTrue(((String) valueList.get(currentPos)).startsWith("Bereits verarbeitet"));
        } else {
            assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        }
    }

    /**
     * Beim Konvertieren einer Map, werden Einträge nicht übernommen deren Keys excluded.
     */
    @Test
    public void testMapExcludedKey() {
        TestBeanEinfach tbe = new TestBeanEinfach();
        BeanToMapConverter converter = new BeanToMapConverter(null,
                Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach"));

        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(tbe, tbe);

        @SuppressWarnings("unchecked")
        Map<String, Object> converted = (Map<String, Object>) converter.convert(map);
        assertEquals(1, converted.size());
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, converted.keySet().iterator().next());
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, converted.values().iterator().next());

    }

    /**
     * Konvertieren einer Map mit "null" als Key. Dieser Wert wird übernommen, allerdings wird "null" zu eine
     * NullString umgewandelt.
     */
    @Test
    public void testMapNull() {
        TestBeanEinfach tbe = new TestBeanEinfach();
        BeanToMapConverter converter = new BeanToMapConverter(null,
                Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach"));

        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(null, tbe);

        @SuppressWarnings("unchecked")
        Map<String, Object> converted = (Map<String, Object>) converter.convert(map);
        assertEquals(1, converted.size());
        assertEquals(BeanToMapConverter.NULL_STRING, converted.keySet().iterator().next());
    }

}
