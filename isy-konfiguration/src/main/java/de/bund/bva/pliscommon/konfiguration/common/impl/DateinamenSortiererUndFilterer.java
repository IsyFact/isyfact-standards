package de.bund.bva.pliscommon.konfiguration.common.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Sortiert und Filtert die Liste an Dateinamen.
 */
public class DateinamenSortiererUndFilterer {

    final static String DEFAULTNAMENSSCHEMA = ".*[.]properties";

    private String namensSchema;

    public DateinamenSortiererUndFilterer() {
        this.namensSchema = DEFAULTNAMENSSCHEMA;
    }

    public DateinamenSortiererUndFilterer(String namensSchema) {
        this.namensSchema = namensSchema;
    }

    /**
     * Sortiert und Filtert die Liste an Dateinamen.
     *
     * @param propertyDateien
     *            List, welches die Property-Dateien enthält
     * @return sortierte und gefilterte Liste on Property-Dateinamen
     */
    public List<PropertyDatei> sortiereUndFiltereDateinamenAusPropertyDateiList(
        List<PropertyDatei> propertyDateien) {

        // Liste sortieren
        List<PropertyDatei> propertyDateienList = new ArrayList<>(propertyDateien);
        java.util.Collections.sort(propertyDateienList, new PropertyDateiComparator());

        // Namensschema anwenden
        propertyDateienList = namensSchemaAnwendenAufPropertyDateiList(propertyDateienList);

        return propertyDateienList;
    }

    /**
     * Sortiert und Filtert die Liste an Dateinamen.
     *
     * @param propertyDateien
     *            Set, welches die Property-Dateinamen enthält
     * @return sortierte und gefilterte Liste von Property-Dateinamen
     */
    public List<String> sortiereUndFiltereDateinamenAusStringSet(Set<String> propertyDateien) {

        // Liste sortieren
        List<String> propertyDateienList = new ArrayList<>(propertyDateien);
        java.util.Collections.sort(propertyDateienList);

        // Namensschema anwenden
        propertyDateienList = namensSchemaAnwendenAufStringList(propertyDateienList);

        return propertyDateienList;
    }

    /**
     * Erstellt eine Liste von Property-Dateinamen, welche dem NamensSchema entsprechen.
     *
     * @param propertyDateienList
     *            Liste von Property-Dateinamen
     * @return Liste von Property-Dateienamen, die dem NamensSchema entsprechen.
     */
    private List<String> namensSchemaAnwendenAufStringList(List<String> propertyDateienList) {

        List<String> newPropertyDateienList = new ArrayList<>();

        for (String propertyDatei : propertyDateienList) {
            if (Pattern.matches(this.namensSchema, propertyDatei)) {
                newPropertyDateienList.add(propertyDatei);
            }
        }

        return newPropertyDateienList;
    }

    /**
     * Erstellt eine Liste von Property-Dateinamen, welche dem NamensSchema entsprechen.
     *
     * @param propertyDateienList
     *            Liste von Property-Dateinamen
     * @return Liste von von Property-Dateien, die dem NamensSchema entsprechen.
     */
    private List<PropertyDatei> namensSchemaAnwendenAufPropertyDateiList(
        List<PropertyDatei> propertyDateiList) {

        List<PropertyDatei> newPropertyDateienList = new ArrayList<>();

        for (PropertyDatei propertyDatei : propertyDateiList) {
            if (Pattern.matches(this.namensSchema, propertyDatei.getDateiname())) {
                newPropertyDateienList.add(propertyDatei);
            }
        }

        return newPropertyDateienList;
    }

    public class PropertyDateiComparator implements Comparator<PropertyDatei> {

        @Override
        public int compare(PropertyDatei o1, PropertyDatei o2) {
            return o1.getDateiname().compareTo(o2.getDateiname());
        }

    }
}
