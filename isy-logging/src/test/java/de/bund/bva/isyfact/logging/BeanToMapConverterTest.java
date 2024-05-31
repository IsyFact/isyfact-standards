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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bund.bva.isyfact.logging.exceptions.SerialisierungException;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanMitException;
import de.bund.bva.isyfact.logging.impl.LogErrorKategorie;
import de.bund.bva.isyfact.logging.util.BeanToMapConverter;
import de.bund.bva.isyfact.logging.util.LogHelper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases for the BeanToMapConverter.
 */
public class BeanToMapConverterTest {

    /**
     * Tests the conversion of a simple data type.
     */
    @Test
    public void testPrimitives() {
        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        assertEquals("5", converter.convert(5));
    }

    /**
     * Tests the conversion of <code>null</code>.
     */
    @Test
    public void testNull() {

        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        assertEquals(BeanToMapConverter.NULL_STRING, converter.convert(null));

    }

    /**
     * Tests the handling of an error that occurs during conversion.
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
     * Tests the conversion of a simple bean, where both includes and excludes are specified.
     */
    @Test
    public void testIncludeExcludeEinfach() {

        // TestBeanEinfach is included
        // TestBeanKomplex is excluded
        BeanToMapConverter converter = new BeanToMapConverter(
            Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach"), Arrays.asList(
                        "de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex", "java.util"));

        // Convert an instance of TestBeanEinfach.
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert = (Map<String, Object>) converter.convert(new TestBeanEinfach());
        List<String> keyList = new ArrayList<>(konvertiert.keySet());
        List<Object> valueList = new ArrayList<>(konvertiert.values());

        // The test bean has 3 properties + the hashCode + 'Class' which is always serialized. One of these is of
        // type TestBeanKomplex, which should not be considered due to the exclude.
        assertEquals(5, konvertiert.size());

        // Counter of the current attribute
        int currentPos = 0;

        // Checking the individual properties

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
     * Simple test to verify the initialization of the Include list with null and an empty list.
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
     * Tests the conversion of an object that is excluded.
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
     * Tests the conversion of an object that is excluded - in this case, the entire 'de/bund' is excluded.
     */
    @Test
    public void testExcludeEinfachKomplett() {

        BeanToMapConverter converter = new BeanToMapConverter(null, Collections.singletonList("de.bund"));
        TestBeanEinfach tbe = new TestBeanEinfach();
        Object konvertiert = converter.convert(tbe);
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, konvertiert);

    }

    /**
     * Comprehensive test of the standard converter. See individual test steps for details.
     */
    @Test
    public void testStandardKonverter() {

        // First, the standard converter is created.
        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        // A complex bean is converted
        TestBeanKomplex bean = new TestBeanKomplex(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert = (Map<String, Object>) converter.convert(bean);
        // Conduct the actual checks
        pruefeTestBeanStandard(bean, konvertiert, true);

        // Repeat the test with a manually created converter (similar to the standard converter
        converter = new BeanToMapConverter(Collections.singletonList("de.bund."), new ArrayList<>());
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert2 = (Map<String, Object>) converter.convert(bean);
        pruefeTestBeanStandard(bean, konvertiert2, true);

    }

    /**
     * Helper method for verifying the correct conversion of a "Complex Bean".
     *
     * @param bean
     *            the converted bean.
     * @param konvertiert
     *            the bean in its converted form.
     * @param rekursiv
     *            indicates whether the ComplexBean was created with the flag "recursive" or not.
     */
    private void pruefeTestBeanStandard(TestBeanKomplex bean, Map<String, Object> konvertiert,
                                        boolean rekursiv) {

        // Key and attributes in separate lists to be able to access them by index
        List<String> keyList = new ArrayList<>(konvertiert.keySet());
        List<Object> valueList = new ArrayList<>(konvertiert.values());

        // 18 attributes are expected to be checked individually below.
        assertEquals(19, keyList.size());

        // Counter of the current attribute
        int currentPos = 0;

        // Property
        assertEquals("class", keyList.get(currentPos));
        assertEquals(TestBeanKomplex.class.toString(), valueList.get(currentPos));
        currentPos++;

        // Enums are converted with "toString
        assertEquals("einEnum", keyList.get(currentPos));
        assertEquals(bean.getEinEnum().toString(), valueList.get(currentPos));
        currentPos++;

        // Enum array is converted to a list of strings
        assertEquals("einEnumArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einEnumArray = (ArrayList<String>) valueList.get(currentPos);
        assertEquals(einEnumArray.get(0), LogErrorKategorie.ERROR.toString());
        assertEquals(einEnumArray.get(1), LogErrorKategorie.FATAL.toString());
        assertEquals(einEnumArray.get(2), LogErrorKategorie.ERROR.toString());
        currentPos++;

        // Integers are converted to strings
        assertEquals("einInteger", keyList.get(currentPos));
        assertEquals(bean.getEinInteger().toString(), valueList.get(currentPos));
        currentPos++;

        // For an object array, the result is a list in which all attributes are individually converted.
        assertEquals("einObjectArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<Object> einObjectArray = (ArrayList<Object>) valueList.get(currentPos);
        if (rekursiv) {
            assertEquals(4, einObjectArray.size());
        } else {
            assertEquals(3, einObjectArray.size());
        }
        // Objects from java.lang are taken with toString
        assertEquals(bean.getJavaLang().toString(), einObjectArray.get(0));
        // NULL is converted to Null-String
        assertEquals(BeanToMapConverter.NULL_STRING, einObjectArray.get(1));
        // Simple string
        assertEquals(bean.getEinString(), einObjectArray.get(2));
        if (rekursiv) {
            // The list contains a complex bean itself, which was created with the parameter "recursive=false"
            // This is also recursively checked here
            @SuppressWarnings("unchecked")
            Map<String, Object> einObjecArrayBean = (Map<String, Object>) einObjectArray.get(3);
            pruefeTestBeanStandard(bean, einObjecArrayBean, false);
        }
        currentPos++;

        // Simple string
        assertEquals("einString", keyList.get(currentPos));
        assertEquals(bean.getEinString(), valueList.get(currentPos));
        currentPos++;

        // String array is taken as a list of strings
        assertEquals("einStringArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einStringArray = (ArrayList<String>) valueList.get(currentPos);
        assertEquals("A", einStringArray.get(0));
        assertEquals("B", einStringArray.get(1));
        assertEquals(BeanToMapConverter.NULL_STRING, einStringArray.get(2));
        assertEquals("C", einStringArray.get(3));
        currentPos++;

        // String without setter is taken as a normal string
        assertEquals("einStringOhneSetter", keyList.get(currentPos));
        assertEquals(bean.getEinStringOhneSetter(), valueList.get(currentPos));
        currentPos++;

        // Enum list is taken as a list of strings
        assertEquals("eineEnumListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> eineEnumListe = (ArrayList<String>) valueList.get(currentPos);
        assertEquals(eineEnumListe.get(0), LogErrorKategorie.ERROR.toString());
        assertEquals(eineEnumListe.get(1), LogErrorKategorie.FATAL.toString());
        assertEquals(eineEnumListe.get(2), LogErrorKategorie.ERROR.toString());
        currentPos++;

        // Object list analogous to object array (above)
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

        // String list is taken as a list of strings
        assertEquals("eineStringListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einStringListe = (ArrayList<String>) valueList.get(currentPos);
        assertEquals("A", einStringListe.get(0));
        assertEquals("B", einStringListe.get(1));
        assertEquals(BeanToMapConverter.NULL_STRING, einStringListe.get(2));
        assertEquals("C", einStringListe.get(3));
        currentPos++;

        // Non-included and non-excluded objects are taken as toString
        assertEquals("extern", keyList.get(currentPos));
        assertEquals(bean.getExtern().toString(), valueList.get(currentPos));
        currentPos++;

        // HashCode is always included (special field to assign already serialized objects)
        assertEquals("hashCode", keyList.get(currentPos));
        currentPos++;

        // Non-included and non-excluded objects are taken as toString
        assertEquals("javaLang", keyList.get(currentPos));
        assertEquals(bean.getJavaLang().toString(), valueList.get(currentPos));
        currentPos++;

        // Non-included and non-excluded objects are taken as toString
        assertEquals("javaUtil", keyList.get(currentPos));
        assertEquals(bean.getJavaUtil().toString(), valueList.get(currentPos));
        currentPos++;

        // Null in a string field is taken as Null-String.
        assertEquals("nullString", keyList.get(currentPos));
        assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        currentPos++;

        // Reference to itself is taken as "Already processed"
        assertEquals("rekursiv", keyList.get(currentPos));
        assertTrue(((String) valueList.get(currentPos)).startsWith("Bereits verarbeitet"));
        currentPos++;

        if (rekursiv) {
            // If the "recursive=true" flag is set, rekursivNeu contains another instance of a
            // ComplexTestBeans
            assertEquals("rekursivNeu", keyList.get(currentPos));
            @SuppressWarnings("unchecked")
            Map<String, Object> rekursivNeu = (Map<String, Object>) valueList.get(currentPos);
            pruefeTestBeanStandard(bean, rekursivNeu, false);
        } else {
            // otherwise null
            assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        }
        currentPos++;

        if (rekursiv) {
            // If the "recursive=true" flag is set, rekursivObject contains a reference to the ComplexTestBean
            // itself - this is taken as Already processed.
            assertEquals("rekursivObject", keyList.get(currentPos));
            assertTrue(((String) valueList.get(currentPos)).startsWith("Bereits verarbeitet"));
        } else {
            assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        }
    }

    /**
     * When converting a map, entries are not included whose keys are excluded.
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
     * Convert a map with "null" as key. This value is taken, however, "null" is converted to a NullString.
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

    @Test
    public void testMapWithNullValue() {
        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        Map<Object, Object> map = new HashMap<>();
        map.put("key", null);
        Map<String, Object> converted = (Map<String, Object>) converter.convert(map);
        assertEquals(BeanToMapConverter.NULL_STRING, converted.get("key")); // Verify that the value null is converted to NULL_STRING
    }

}
