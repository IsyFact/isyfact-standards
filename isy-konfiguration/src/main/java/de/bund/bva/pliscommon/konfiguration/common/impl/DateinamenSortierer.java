package de.bund.bva.pliscommon.konfiguration.common.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Sortiert die Liste an Dateinamen.
 */
public class DateinamenSortierer {

    /**
     * Sortiert die Liste an Dateinamen.
     *
     * @param propertyDateien
     *            Liste, welche die Property-Dateien enthält
     * @return sortierte Liste von Property-Dateinamen
     */
    public List<PropertyDatei> sortiereDateinamenAusPropertyDateiList(
        List<PropertyDatei> propertyDateien) {

        // Liste sortieren
        List<PropertyDatei> propertyDateienList = new ArrayList<>(propertyDateien);
        java.util.Collections.sort(propertyDateienList, new PropertyDateiComparator());

        return propertyDateienList;
    }

    /**
     * Sortiert die Liste an Dateinamen.
     *
     * @param propertyDateien
     *            Set, welches die Property-Dateinamen enthält
     * @return sortierte Liste von Property-Dateinamen
     */
    public List<String> sortiereDateinamenAusStringSet(Set<String> propertyDateien) {

        // Liste sortieren
        List<String> propertyDateienList = new ArrayList<>(propertyDateien);
        java.util.Collections.sort(propertyDateienList);

        return propertyDateienList;
    }

    public class PropertyDateiComparator implements Comparator<PropertyDatei> {

        @Override
        public int compare(PropertyDatei o1, PropertyDatei o2) {
            return o1.getDateiname().compareTo(o2.getDateiname());
        }
    }
}
