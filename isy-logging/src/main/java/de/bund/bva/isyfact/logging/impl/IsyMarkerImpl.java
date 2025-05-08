package de.bund.bva.isyfact.logging.impl;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Marker;

import de.bund.bva.isyfact.logging.IsyMarker;

/**
 * Standardimplementierung des IsyMarker-Interface.
 * 
 * @see IsyMarker
 * 
 */
public class IsyMarkerImpl implements IsyMarker {

    /** Eindeutige UID. */
    private static final long serialVersionUID = 1L;

    /** Name des Markers. */
    private final String name;

    /** Wert des Markers. */
    private final String value;

    /** Wert des Root-Markers. */
    private static final String ROOT_MARKER_VALUE = null;

    /** Gibt an, ob es sich um einen Root-Marker handelt. */
    private final boolean root;

    /** Referenzen auf enthaltene Marker. */
    private final List<Marker> references = new ArrayList<>();

    /**
     * Konstruktor der Klasse. Er initialisiert die übergebenen Klassenattribute.
     * 
     * @param name
     *            Name des Markers.
     * @param value
     *            Wert des Markers.
     */
    public IsyMarkerImpl(MarkerSchluessel name, String value) {
        this(name.getWert(), value, false);
    }

    /**
     * Erzeugt einen Root-Marker.
     * 
     * @return der erzeugte Root-Marker.
     */
    public static IsyMarker createRootMarker() {
        return new IsyMarkerImpl(MarkerSchluessel.ROOTMARKER.getWert(), ROOT_MARKER_VALUE, true);
    }

    /**
     * Konstruktor der Klasse. Er initialisiert die übergebenen Klassenattribute.
     * 
     * @param name
     *            Name des Markers.
     * @param value
     *            Wert des Markers.
     * @param root
     *            Flag zum Kennzeichnen eines Root-Markers.
     */
    IsyMarkerImpl(String name, String value, boolean root) {
        this.name = name;
        this.value = value;
        this.root = root;
    }

    /**
     * {@inheritDoc}
     * 
     * @see IsyMarker#getValue()
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#remove(Marker)
     */
    public boolean remove(Marker reference) {
        return references.remove(reference);
    }

    /**
     * {@inheritDoc}
     *
     */
    public boolean hasChildren() {
        return hasReferences();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#hasReferences()
     */
    public boolean hasReferences() {
        return !references.isEmpty();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#iterator()
     */
    public Iterator<Marker> iterator() {
        return references.iterator();
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#contains(String)
     */
    public boolean contains(String markerName) {
        if (this.name.equals(markerName)) {
            return true;
        } else {
            for (Marker reference : references) {
                if (reference.contains(markerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#add(Marker)
     */
    public void add(Marker reference) {
        references.add(reference);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Marker#contains(Marker)
     */
    public boolean contains(Marker other) {
        return references.contains(other);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        
        if (obj instanceof IsyMarker) {
            IsyMarker comp = (IsyMarker) obj;
            return compare(this.name, comp.getName()) && compare(this.value, comp.getValue());
        } else {
            return super.equals(obj);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (name + value).hashCode();
    }

    /**
     * Hilfsmethode zum Vergleichen zweier Strings.
     * 
     * @param str1
     *            String 1.
     * @param str2
     *            String 2.
     * @return <code>true</code> wenn die Strings gleich sind, <code>false</code> sonst.
     */
    private boolean compare(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        } else {
            return str1.equals(str2);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see IsyMarker#isRootMarker()
     */
    public boolean isRootMarker() {
        return root;
    }

    /**
     * {@inheritDoc}
     * 
     * @see IsyMarker#addAll(Collection)
     */
    public void addAll(Collection<Marker> markerReferences) {
        if (markerReferences != null) {
            for (Marker marker : markerReferences) {
                this.references.add(marker);
            }
        }
    }

}
