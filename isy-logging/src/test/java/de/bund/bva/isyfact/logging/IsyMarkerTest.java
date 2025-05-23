package de.bund.bva.isyfact.logging;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Marker;

import de.bund.bva.isyfact.logging.impl.IsyMarkerImpl;
import de.bund.bva.isyfact.logging.impl.MarkerSchluessel;

/**
 * Tests der Klasse IsyMarker.
 */
public class IsyMarkerTest {

    /**
     * Testfälle für das Hinzufügen von Child-Markern.
     */
    @Test
    public void isyMarkerChildrenTest() {

        IsyMarker marker = new IsyMarkerImpl(MarkerSchluessel.DAUER, "123");
        IsyMarker childMarker1 = new IsyMarkerImpl(MarkerSchluessel.DATENTYP, "String");
        IsyMarker childMarker2 = new IsyMarkerImpl(MarkerSchluessel.DATENTYP, "String");

        // Keine Children im Marker vorhanden
        assertFalse("Es sind unerwartet Children im Marker vorhanden.", marker.hasChildren());
        assertFalse("Es sind unerwartet Children im Marker vorhanden.", marker.contains(childMarker1));
        assertFalse("Es sind unerwartet Children im Marker vorhanden.",
                marker.contains(MarkerSchluessel.DAUER.toString()));
        assertFalse("Es sind unerwartet Children im Marker vorhanden.",
                marker.contains(MarkerSchluessel.DATENTYP.toString()));

        // Marker als Children ergänzen
        marker.add(childMarker1);
        assertTrue("Marker besitzt keine Children.", marker.hasChildren());
        assertTrue("Children unerwartet nicht vorhanden.", marker.contains(childMarker1));
        assertTrue("Children unerwartet nicht vorhanden.",
                marker.contains(MarkerSchluessel.DAUER.getWert()));
        assertTrue("Children unerwartet nicht vorhanden.",
                marker.contains(MarkerSchluessel.DATENTYP.getWert()));
        assertFalse("Es sind unerwartet Children im Marker vorhanden.",
                marker.contains(MarkerSchluessel.KATEGORIE.getWert()));

        // Children entfernen
        assertTrue("Entfernen eines vorhandenen Markers war nicht erfolgreich.",
                marker.remove(childMarker1));
        assertFalse(
                "Entfernen eines Markers, obwohl keine Marker vorhanden sind, war unerwartet erfolgreich.",
                marker.remove(childMarker1));
        assertFalse("Es sind unerwartet Children im Marker vorhanden.", marker.hasChildren());

        // Test mit der Ergänzung von null
        marker.addAll(null);

        // Mehrere Children ergänzen
        List<Marker> markers = new ArrayList<>();
        markers.add(childMarker1);
        markers.add(childMarker2);

        // Zwei Marker ergänzen
        marker.addAll(markers);
        assertTrue("Children unerwartet nicht vorhanden.", marker.contains(childMarker1));
        assertTrue("Children unerwartet nicht vorhanden.", marker.contains(childMarker2));

    }

    /**
     * Tests für Standardmethoden wie bspw. 'equals'.
     * 
     * @throws Exception
     *             wenn bei der Testausführung eine Exception auftritt.
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

        // Teste Equals
        assertTrue("Marker sind fälschlicherweise ungleich.", marker1.equals(marker2));
        assertFalse("Marker sind fälschlicherweise gleich.", marker1.equals(marker3));
        assertFalse("Marker sind fälschlicherweise gleich.", marker1.equals(marker4));
        assertFalse("Marker sind fälschlicherweise gleich.", marker1.equals(marker5));
        assertFalse("Marker sind fälschlicherweise gleich.", marker1.equals(null));
        assertFalse("Marker sind fälschlicherweise gleich.", marker5.equals(new Integer(1)));

        // Teste Compare-Methode per Reflection da die Konstellation "null" als erster Parameter bisher nicht
        // auftreten
        // kann.
        Method compareMethod = IsyMarkerImpl.class.getDeclaredMethod("compare", String.class, String.class);
        compareMethod.setAccessible(true);
        compareMethod.invoke(marker1, null, null);
        compareMethod.invoke(marker1, null, marker1value);

        // Einfache Prüfung des HashCodes
        assertTrue("Hashcode ist falsch.", marker1.hashCode() != 0);
    }
}
