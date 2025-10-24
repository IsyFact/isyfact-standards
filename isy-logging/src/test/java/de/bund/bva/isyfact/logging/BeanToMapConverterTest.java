package de.bund.bva.isyfact.logging;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bund.bva.isyfact.logging.exceptions.SerialisierungException;

import org.junit.jupiter.api.Test;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex;
import de.bund.bva.isyfact.logging.hilfsklassen.TestBeanMitException;
import de.bund.bva.isyfact.logging.impl.LogErrorKategorie;
import de.bund.bva.isyfact.logging.util.BeanToMapConverter;
import de.bund.bva.isyfact.logging.util.LogHelper;

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
     * Tests the error handling, that occurs in a conversion.
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
     * Tests the conversion of a simple bean, where includes and excludes have been defined.
     */
    @Test
    public void testIncludeExcludeEinfach() {

        // TestBeanEinfach is included
        // TestBeanKomplex is excluded
        BeanToMapConverter converter = new BeanToMapConverter(
            Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanEinfach"), Arrays.asList(
                        "de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex", "java.util"));

        // convert an instance of TestBeanEinfach.
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert = (Map<String, Object>) converter.convert(new TestBeanEinfach());
        List<String> keyList = new ArrayList<>(konvertiert.keySet());
        List<Object> valueList = new ArrayList<>(konvertiert.values());

        // test bean has 3 properties + a hashcode + 'Class' which will be serialized always.
        // One of them is of type TestBeanComplex, which should not be considered due to the exclude.
        assertEquals(5, konvertiert.size());

        // counter of the current attribute
        int currentPos = 0;
        
        // check of each property
        
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
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, eineListeValues.getFirst());
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
     * Simple test for the initialization of the include list with null and an empty include list.
     */
    @Test
    public void testLeererIncludeVarianten() {

        // Include = null
        BeanToMapConverter converter = new BeanToMapConverter(null,
                Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex"));
        TestBeanEinfach tbe = new TestBeanEinfach();
        Object konvertiert = converter.convert(tbe);
        assertEquals(tbe.toString(), konvertiert);

        // Include = empty list
        converter = new BeanToMapConverter(new ArrayList<>(),
            Collections.singletonList("de.bund.bva.isyfact.logging.hilfsklassen.TestBeanKomplex"));
        tbe = new TestBeanEinfach();
        konvertiert = converter.convert(tbe);
        assertEquals(tbe.toString(), konvertiert);

    }

    /**
     * Test the conversion of an object that is excluded.
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
     * Test the conversion of an object that is excluded - here 'de/bund' is excluded completely.
     */
    @Test
    public void testExcludeEinfachKomplett() {

        BeanToMapConverter converter = new BeanToMapConverter(null, Collections.singletonList("de.bund"));
        TestBeanEinfach tbe = new TestBeanEinfach();
        Object konvertiert = converter.convert(tbe);
        assertEquals(BeanToMapConverter.EXCLUDED_VALUE, konvertiert);

    }

    /**
     * Tomplex test of the standard converter. See details in the test steps.
     */
    @Test
    public void testStandardKonverter() {

        // creating a standard converter.
        BeanToMapConverter converter = LogHelper.erstelleStandardKonverter();
        // converting a complex bean.
        TestBeanKomplex bean = new TestBeanKomplex(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert = (Map<String, Object>) converter.convert(bean);
        // performing the checks.
        pruefeTestBeanStandard(bean, konvertiert, true);

        // Repeat the tests with a manually created converter (analogous the standard converter)
        converter = new BeanToMapConverter(Collections.singletonList("de.bund."), new ArrayList<>());
        @SuppressWarnings("unchecked")
        Map<String, Object> konvertiert2 = (Map<String, Object>) converter.convert(bean);
        pruefeTestBeanStandard(bean, konvertiert2, true);

    }

    /**
     * Helper method to check the correct conversion of a "Komplex-Beans".
     * 
     * @param bean
     *            the bean to convert.
     * @param konvertiert
     *            the converted bean.
     * @param rekursiv
     *            shows if 'KomplexeBean' has been created with flag "rekursiv" or not.
     */
    private void pruefeTestBeanStandard(TestBeanKomplex bean, Map<String, Object> konvertiert,
            boolean rekursiv) {

        // Key and attributes in separate lists to access them with an index
        List<String> keyList = new ArrayList<>(konvertiert.keySet());
        List<Object> valueList = new ArrayList<>(konvertiert.values());

        // 18 attributes expected which will be checked below one by one.
        assertEquals(19, keyList.size());

        // index of the actual attributs
        int currentPos = 0;
        
        // Property
        assertEquals("class", keyList.get(currentPos));
        assertEquals(TestBeanKomplex.class.toString(), valueList.get(currentPos));
        currentPos++;

        // Enum converted by "toString"
        assertEquals("einEnum", keyList.get(currentPos));
        assertEquals(bean.getEinEnum().toString(), valueList.get(currentPos));
        currentPos++;

        // Enum-Array converted to list of strings
        assertEquals("einEnumArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einEnumArray = (ArrayList<String>) valueList.get(currentPos);
        assertEquals(einEnumArray.getFirst(), LogErrorKategorie.ERROR.toString());
        assertEquals(einEnumArray.get(1), LogErrorKategorie.FATAL.toString());
        assertEquals(einEnumArray.get(2), LogErrorKategorie.ERROR.toString());
        currentPos++;

        // Integer converted to string
        assertEquals("einInteger", keyList.get(currentPos));
        assertEquals(bean.getEinInteger().toString(), valueList.get(currentPos));
        currentPos++;

        // result of an object array is a list in which all attributes have been individually converted
        assertEquals("einObjectArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<Object> einObjectArray = (ArrayList<Object>) valueList.get(currentPos);
        if (rekursiv) {
            assertEquals(4, einObjectArray.size());
        } else {
            assertEquals(3, einObjectArray.size());
        }
        // Objects from java.lang will be taken by toString
        assertEquals(bean.getJavaLang().toString(), einObjectArray.getFirst());
        // NULL is converted in a Null-String
        assertEquals(BeanToMapConverter.NULL_STRING, einObjectArray.get(1));
        // simple string
        assertEquals(bean.getEinString(), einObjectArray.get(2));
        if (rekursiv) {
            // list contains a complex bean, which has been created with param "rekursiv=false"
            // this will be here validated also by recursion
            @SuppressWarnings("unchecked")
            Map<String, Object> einObjecArrayBean = (Map<String, Object>) einObjectArray.get(3);
            pruefeTestBeanStandard(bean, einObjecArrayBean, false);
        }
        currentPos++;

        // simple string
        assertEquals("einString", keyList.get(currentPos));
        assertEquals(bean.getEinString(), valueList.get(currentPos));
        currentPos++;

        // String-Array will be taken over as String-List
        assertEquals("einStringArray", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einStringArray = (ArrayList<String>) valueList.get(currentPos);
        assertEquals("A", einStringArray.getFirst());
        assertEquals("B", einStringArray.get(1));
        assertEquals(BeanToMapConverter.NULL_STRING, einStringArray.get(2));
        assertEquals("C", einStringArray.get(3));
        currentPos++;

        // String without setter will be taken over as normal string
        assertEquals("einStringOhneSetter", keyList.get(currentPos));
        assertEquals(bean.getEinStringOhneSetter(), valueList.get(currentPos));
        currentPos++;

        // Enum-List will be taken over as String-List
        assertEquals("eineEnumListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> eineEnumListe = (ArrayList<String>) valueList.get(currentPos);
        assertEquals(eineEnumListe.getFirst(), LogErrorKategorie.ERROR.toString());
        assertEquals(eineEnumListe.get(1), LogErrorKategorie.FATAL.toString());
        assertEquals(eineEnumListe.get(2), LogErrorKategorie.ERROR.toString());
        currentPos++;

        // Object-List analogous to Object-Array (see above)
        assertEquals("eineObjectListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<Object> eineObjectListe = (ArrayList<Object>) valueList.get(currentPos);
        if (rekursiv) {
            assertEquals(4, eineObjectListe.size());
        } else {
            assertEquals(3, eineObjectListe.size());
        }
        assertEquals(bean.getJavaLang().toString(), eineObjectListe.getFirst());
        assertEquals(BeanToMapConverter.NULL_STRING, eineObjectListe.get(1));
        assertEquals(bean.getEinString(), eineObjectListe.get(2));
        if (rekursiv) {
            String einObjecListBean = (String) eineObjectListe.get(3);
            assertTrue(einObjecListBean.startsWith("Bereits verarbeitet"));
        }
        currentPos++;

        // String-List will be taken over as String-List
        assertEquals("eineStringListe", keyList.get(currentPos));
        @SuppressWarnings("unchecked")
        ArrayList<String> einStringListe = (ArrayList<String>) valueList.get(currentPos);
        assertEquals("A", einStringListe.getFirst());
        assertEquals("B", einStringListe.get(1));
        assertEquals(BeanToMapConverter.NULL_STRING, einStringListe.get(2));
        assertEquals("C", einStringListe.get(3));
        currentPos++;

        // Not included and not excluded objects will be taken over as toString
        assertEquals("extern", keyList.get(currentPos));
        assertEquals(bean.getExtern().toString(), valueList.get(currentPos));
        currentPos++;

        // HashCode will always be taken over (special field to map sericalized objects)
        assertEquals("hashCode", keyList.get(currentPos));
        currentPos++;

        // Not included and not excluded objects will be taken over as toString
        assertEquals("javaLang", keyList.get(currentPos));
        assertEquals(bean.getJavaLang().toString(), valueList.get(currentPos));
        currentPos++;

        // Not included and not excluded objects will be taken over as toString
        assertEquals("javaUtil", keyList.get(currentPos));
        assertEquals(bean.getJavaUtil().toString(), valueList.get(currentPos));
        currentPos++;

        // Null in a string attribute will be taken over as Null-String .
        assertEquals("nullString", keyList.get(currentPos));
        assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        currentPos++;

        // Self reference will be taken over as "Bereits verarbeitet"
        assertEquals("rekursiv", keyList.get(currentPos));
        assertTrue(((String) valueList.get(currentPos)).startsWith("Bereits verarbeitet"));
        currentPos++;

        if (rekursiv) {
            // if the flag "rekursiv" is true, rekursivNeu contains a further instance of a KomplexenTestBeans
            assertEquals("rekursivNeu", keyList.get(currentPos));
            @SuppressWarnings("unchecked")
            Map<String, Object> rekursivNeu = (Map<String, Object>) valueList.get(currentPos);
            pruefeTestBeanStandard(bean, rekursivNeu, false);
        } else {
            // else null
            assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        }
        currentPos++;

        if (rekursiv) {
            // if the flag "rekursiv" is true, rekursivObject contains a reference to the KomplexeTestBean
            // this will be taken over as 'Bereits verarbeitet' .
            assertEquals("rekursivObject", keyList.get(currentPos));
            assertTrue(((String) valueList.get(currentPos)).startsWith("Bereits verarbeitet"));
        } else {
            assertEquals(BeanToMapConverter.NULL_STRING, valueList.get(currentPos));
        }
    }

    /**
     * For a map entries will not be taken over if their key is excluded.
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
     * Converting a map with 'null' as key. This value will be taken over, but 'null' will be converted
     * to a null string.
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
