package de.bund.bva.isyfact.logging;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import de.bund.bva.isyfact.logging.impl.IsyMarkerImpl;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;

/**
 * Test for class IsyMarker.
 */
public class IsyMarkerTest {

    /**
     * Tests for adding child-Markers.
     */
    @Test
    public void isyMarkerChildrenTest() {

        IsyMarker marker = new IsyMarkerImpl(MarkerSchluessel.DAUER, "123");
        IsyMarker childMarker1 = new IsyMarkerImpl(MarkerSchluessel.DATENTYP, "String");
        IsyMarker childMarker2 = new IsyMarkerImpl(MarkerSchluessel.DATENTYP, "String");

        // Marker has no children
        assertFalse(marker.hasChildren(), "Es sind unerwartet Children im Marker vorhanden.");
        assertFalse(marker.contains(childMarker1), "Es sind unerwartet Children im Marker vorhanden.");
        assertFalse(marker.contains(MarkerSchluessel.DAUER.toString()),
                "Es sind unerwartet Children im Marker vorhanden.");
        assertFalse(marker.contains(MarkerSchluessel.DATENTYP.toString()),
                "Es sind unerwartet Children im Marker vorhanden.");

        // add Marker as childs
        marker.add(childMarker1);
        assertTrue(marker.hasChildren(), "Marker besitzt keine Children.");
        assertTrue(marker.contains(childMarker1), "Children unerwartet nicht vorhanden.");
        assertTrue(marker.contains(MarkerSchluessel.DAUER.getWert()),
                "Children unerwartet nicht vorhanden.");
        assertTrue(marker.contains(MarkerSchluessel.DATENTYP.getWert()),
                "Children unerwartet nicht vorhanden.");
        assertFalse(marker.contains(MarkerSchluessel.KATEGORIE.getWert()),
                "Es sind unerwartet Children im Marker vorhanden.");

        // remove children
        assertTrue(marker.remove(childMarker1),
                "Entfernen eines vorhandenen Markers war nicht erfolgreich.");
        assertFalse(
                marker.remove(childMarker1),
                "Entfernen eines Markers, obwohl keine Marker vorhanden sind, war unerwartet erfolgreich.");
        assertFalse(marker.hasChildren(), "Es sind unerwartet Children im Marker vorhanden.");

        // Test for adding null
        marker.addAll(null);

        // add multiple children
        List<Marker> markers = new ArrayList<>();
        markers.add(childMarker1);
        markers.add(childMarker2);

        // add two Markers
        marker.addAll(markers);
        assertTrue(marker.contains(childMarker1), "Children unerwartet nicht vorhanden.");
        assertTrue(marker.contains(childMarker2), "Children unerwartet nicht vorhanden.");

    }

    /**
     * Tests for standard methods like 'equals'.
     * 
     * @throws Exception
     *             if an error occurs during the test.
     */
    @Test
    public void isyMarkerStandardmethodenTest() throws Exception {
        String marker1value = "123";
        IsyMarkerImpl marker1 = new IsyMarkerImpl(MarkerSchluessel.DAUER, marker1value);
        IsyMarkerImpl marker2 = new IsyMarkerImpl(MarkerSchluessel.DAUER, marker1value);
        IsyMarkerImpl marker3 = new IsyMarkerImpl(MarkerSchluessel.DATENTYP,"String");
        IsyMarkerImpl marker4 = new IsyMarkerImpl(MarkerSchluessel.DAUER, "5678");
        IsyMarkerImpl marker5 = new IsyMarkerImpl(MarkerSchluessel.DAUER, null);

        marker1.equals(marker2);

        // Test equals
        assertTrue(marker1.equals(marker2), "Marker sind fälschlicherweise ungleich.");
        assertFalse(marker1.equals(marker3), "Marker sind fälschlicherweise gleich.");
        assertFalse(marker1.equals(marker4), "Marker sind fälschlicherweise gleich.");
        assertFalse(marker1.equals(marker5), "Marker sind fälschlicherweise gleich.");
        assertFalse(marker1.equals(null), "Marker sind fälschlicherweise gleich.");
        assertFalse(marker5.equals(Integer.valueOf(1)), "Marker sind fälschlicherweise gleich.");

        // Test Compare-Method by reflection because the situation 'null' is first parameter
        // cannot happen yet.
        Method compareMethod = IsyMarkerImpl.class.getDeclaredMethod("compare", String.class, String.class);
        compareMethod.setAccessible(true);
        compareMethod.invoke(marker1, null, null);
        compareMethod.invoke(marker1, null, marker1value);

        // simple check of HashCodes
        assertTrue(marker1.hashCode() != 0, "Hashcode ist falsch.");
    }
}
