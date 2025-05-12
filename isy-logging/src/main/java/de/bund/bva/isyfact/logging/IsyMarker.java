package de.bund.bva.isyfact.logging;

import java.util.Collection;

import org.slf4j.Marker;

/**
 * Marker zum Kennzeichnen von Logeinträgen mit definierten Attributen. Der IsyFact-spezifische Marker erlaubt
 * die Angabe eines Werts zusätzlich zum Namen des Markers.
 * 
 */
public interface IsyMarker extends Marker {

    /**
     * Isy-Logging verwendet einen "Wurzel-Marker" in dem weitere Marker gesammelt werden selbst aber keine
     * relevanten Log-Informationen (Name/Wert) enthält. Dieses Flag gibt an, ob es sich um einen Root-Marker handelt.
     * 
     * @return <code>true</code> falls es sich um einen Root-Marker handelt, <code>false</code> sonst.
     */
    public boolean isRootMarker();

    /**
     * Liefert den Wert des Markers.
     * 
     * @return der Wert des Markers.
     */
    public String getValue();
    
    /**
     * Ergänze mehrere Referenzen zu anderen Markern.
     * 
     * @param references Referenzen zu anderen Markern.
     */
    public void addAll(Collection<Marker> references);

}
