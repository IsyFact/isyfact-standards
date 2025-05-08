package de.bund.bva.isyfact.logging.util;



import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.bund.bva.isyfact.logging.exceptions.SerialisierungException;
import de.bund.bva.isyfact.logging.impl.FehlerSchluessel;

/**
 * Konverter, um ein Bean in eine Map umzuwandeln, die als Eingabe der Serialisierung in JSON dient.
 */
public class BeanToMapConverter implements BeanConverter {

    /** List mit zu berücksichtigenden Packages. Diese wird für die Filterung einzelner Propterties verwendet. */
    private final List<String> includes;

    /** List mit zu ignorierenden Packages. Diese wird für die Filterung einzelner Propterties verwendet. */
    private final List<String> excludes;

    /** Der zu verwendende String für Null-Werte. */
    public static final String NULL_STRING = "null";

    /** Der zu verwendende String für die Ausgabe des HashCodes. */
    public static final String HASHCODE_KEY = "hashCode";
    
    /** Der zu verwendende String für bei der Ausgabe eines excludierten Objects.. */
    public static final String EXCLUDED_VALUE = "NICHT_SERIALISIERT";

    /**
     * Konstruktor der Klasse. Initialisiert die übergebenen Properties.
     * 
     * @param includes
     *            List der zu berücksichtigenden Packages.
     * @param excludes
     *            List der zu ignorierenden Packages.
     */
    public BeanToMapConverter(List<String> includes, List<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    /** Enum zum bestimmen der Art in der die Verarbeitung einer Property stattfinden soll. */
    protected enum ConversionStyle {
        /** Die Property wird rekursiv durchlaufen. */
        RECURSIVE,
        /** Auf der Property wird die "toString-Methode" aufgerufen. */
        TOSTRING,
        /** Die Property wird ignoriert. */
        IGNORE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.bund.bva.isyfact.logging.util.BeanConverter#convert(java.lang.Object)
     */
    @Override
    public Object convert(Object bean) {

        // Null wird als 'null' in die Serialisierung gegeben.
        if (bean == null) {
            return NULL_STRING;
        }

        try {
            return processValue(bean, new HashSet<Object>());
        } catch (Exception e) {
            throw new SerialisierungException(FehlerSchluessel.FEHLER_SERIALISIERUNG_AUFRUFPARAMETER, e);
        }
    }

    /**
     * Sammelt rekursiv, alle zu Properties in den Beans. Dabei wird für jedes Bean eine Map erstellt, in der
     * der Name der Properties auf die jeweiligen Werte abgebildet werden. Properties, die selbst Beans sind
     * und bei der Serialisierung berücksichtigt werden, werden selbst wiederum als Map in die übergeordnete
     * Map (des enthaltenden Beans) mitaufgenommen.
     * 
     * @param bean
     *            das zu konveriterende Bean.
     * @param seen
     *            Liste der bereits verarbeiteten Objekte, um zu vermeiden das die Rekursion nicht in einer
     *            Endlosschleife landet.
     * @return die Map-Struktur mit den Werten des zu serialisierenden Beans.
     */
    private Map<String, Object> collectAttributesRecursive(Object bean, Set<Object> seen) {

        // Die Ergebnismap. Treemap wird verwendet, für eine stabile Sortierungsreihenfolge.
        Map<String, Object> propertyMap = new TreeMap<>();

        propertyMap.put(HASHCODE_KEY, "" + bean.hashCode());

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException e) {
            throw new SerialisierungException(FehlerSchluessel.FEHLER_SERIALISIERUNG_AUFRUFPARAMETER, e);
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        // Iteration über alle Properties des Beans.
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            // Name der Property
            String name = propertyDescriptor.getName();

            Method readMethod = propertyDescriptor.getReadMethod();

            // Überspringen, falls keine öffentliche Read-Methode für die Property vorhanden ist.
            if (readMethod == null) {
                continue;
            }

            // Wert der Property
            Object value;
            try {
                value = readMethod.invoke(bean);
            } catch (Exception e) {
                throw new SerialisierungException(FehlerSchluessel.FEHLER_SERIALISIERUNG_AUFRUFPARAMETER, e);
            }

            // Leerwerte werden nicht übernommen
            Object converted = processValue(value, seen);

            // Leerwerte werden nicht übernommen - Null-Werte, die übernommen werden sollen, werden bereits in
            // der Konvertierung zu einem String umgewandelt.
            if (converted != null) {
                propertyMap.put(name, converted);
            }
        }

        return propertyMap;
    }

    /**
     * Übernimmt den übergebenen Wert einer Property.
     * 
     * @param value
     *            der zu verarbeitende Wert.
     * @param seen
     *            Liste der bereits verarbeiteten Objekte, um zu vermeiden das die Rekursion nicht in einer
     *            Endlosschleife landet.
     * 
     * @return der Wert in serialisierter Form.
     */
    private Object processSimpleValue(Object value, Set<Object> seen) {

        // Ermittelt die Art der Serialisierung
        ConversionStyle serialisierungsart = determineConversionStyle(value);

        switch (serialisierungsart) {
        case TOSTRING:
            return convertToString(value);
        case RECURSIVE:
            if (seen.contains(value)) {
                // Abbruch, falls das Object bereits aufgenommen wurde, um Endlosschleifen zu vermeiden
                return "Bereits verarbeitet: " + value.hashCode();
            } else {
                seen.add(value);
                return collectAttributesRecursive(value, seen);
            }
        default:
            return EXCLUDED_VALUE;
        }

    }

    /**
     * Übernimmt den übergebenen Wert.
     * 
     * @param value
     *            der zu übernehmende Wert (beliebiger Datentyp).
     * @param seen
     *            Liste der bereits verarbeiteten Objekte, um zu vermeiden das die Rekursion nicht in einer
     *            Endlosschleife landet.
     * @return der Werte in konvertierter Form.
     */
    private Object processValue(Object value, Set<Object> seen) {

        if (value == null) {
            return processSimpleValue(value, seen);
        }

        // Arrays werden wie Listen behandelt
        if (value.getClass().isArray()) {
            List<Object> valueList = new ArrayList<>();

            // Hier wird explizit nicht Arrays.asList verwendet, da dies zu problemen bei Arrays von
            // primitiven Datentypen führt.
            for (int i = 0; i < Array.getLength(value); i++) {
                Object entry = Array.get(value, i);
                valueList.add(entry);
            }
            value = valueList;
        }

        if (value instanceof Iterable<?>) {
            return processIterableValue((Iterable<?>) value, seen);
        } else if (value instanceof Map<?, ?>) {
            return processMapValue((Map<?, ?>) value, seen);
        } else {
            return processSimpleValue(value, seen);
        }

    }

    /**
     * Übernimmt den übergebenen Map-Wert einer Property.
     * 
     * @param value
     *            der zu verarbeitende Wert.
     * @param seen
     *            Liste der bereits verarbeiteten Objekte, um zu vermeiden das die Rekursion nicht in einer
     *            Endlosschleife landet.
     * 
     * @return die Werte in konvertierter Form.
     */
    private Object processMapValue(Map<?, ?> value, Set<Object> seen) {
        Map<Object, Object> convertedMap = new TreeMap<>(Comparator.comparing(Object::toString));

        for (Object mapKey : value.keySet()) {
            Object mapValue = value.get(mapKey);

            // Wert Ignorieren, wenn der Key 'null' ist - dies ist nur dann der Fall wenn der Wert wirklich
            // ignoriert werden soll, bei anderen wird der "NULL-String" zurückgegeben.
            Object convertedKey = processValue(mapKey, seen);
            if (convertedKey != null) {
                Object convertedValue = processValue(mapValue, seen);
                convertedMap.put(convertedKey, convertedValue);
            }

        }

        return convertedMap;
    }

    /**
     * Übernimmt den übergebenen Iterable-Wert einer Property.
     * 
     * @param iterable
     *            der zu verarbeitende Wert.
     * @param seen
     *            Liste der bereits verarbeiteten Objekte, um zu vermeiden das die Rekursion nicht in einer
     *            Endlosschleife landet.
     * 
     * @return die Werte in konvertierter Form.
     */
    private List<Object> processIterableValue(Iterable<?> iterable, Set<Object> seen) {

        List<Object> convertedMap = new ArrayList<>();

        for (Object value : iterable) {
            Object converted = processValue(value, seen);
            if (converted != null) {
                convertedMap.add(converted);
            }
        }

        return convertedMap;
    }

    /**
     * Hilfsmethode zum Konvertieren eines Werts zu einem String.
     * 
     * @param value
     *            der zu konvertirende Wert.
     * @return der konvertierte Wert.
     */
    private String convertToString(Object value) {
        if (value == null) {
            return NULL_STRING;
        } else {
            return value.toString();
        }
    }

    /**
     * Bestimmung in welcher Form das übergebene Objekt serialisiert werden soll. Diese Methode kann als
     * Erweiterungspunkt für eine spezifischere Logik genutzt werden.
     * 
     * @param value
     *            der zu konvertierende Wert.
     * @return die Art der Konvertierung.
     */
    protected ConversionStyle determineConversionStyle(Object value) {

        // Null-Werte werden ignoriert.
        if (value == null) {
            return ConversionStyle.TOSTRING;
        }

        Class<? extends Object> classObj = value.getClass();
        String className = classObj.getName();

        // Einfache Datentypen werden als String übernommen (Primitives können nicht vorkommen, da diese
        // automatisch gewrapped werden in INTEGER etc.
        if (classObj.isEnum()) {
            return ConversionStyle.TOSTRING;
        }

        boolean included = checkIsInclude(className);
        boolean excluded = checkIsExclude(className);

        if (excluded) {
            // Excludierte Klassen werden immer ignoriert
            return ConversionStyle.IGNORE;
        } else if (included) {
            // Includierte die nicht excludiert sind werden immer Rekursiv durchlaufen
            return ConversionStyle.RECURSIVE;
        } else {
            // Bei Klassen ohne Angabe wird ToString aufgerufen
            return ConversionStyle.TOSTRING;
        }

    }

    /**
     * Prüft ob die übergebene Klasse in die Serialisierung mit einbezogen (Include) werden soll.
     * 
     * @param className
     *            Name der zu prüfenden Klasse.
     * @return <code>true</code> wenn die Klasse included werden soll, <code>false</code> sonst.
     */
    private boolean checkIsInclude(String className) {
        if (includes != null && !includes.isEmpty()) {
            for (String include : includes) {
                if (className.startsWith(include)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prüft ob die übergebene Klasse in die Serialisierung explizit nicht mit einbezogen (Excluded) werden
     * soll.
     * 
     * @param className
     *            Name der zu prüfenden Klasse.
     * @return <code>true</code> wenn die Klasse excluded werden soll, <code>false</code> sonst.
     */
    private boolean checkIsExclude(String className) {
        if (excludes != null && !excludes.isEmpty()) {
            for (String exclude : excludes) {
                if (className.startsWith(exclude)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Liefert den Wert des Attributs 'includes'.
     * 
     * @return Wert des Attributs.
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Liefert den Wert des Attributs 'excludes'.
     * 
     * @return Wert des Attributs.
     */
    public List<String> getExcludes() {
        return excludes;
    }
}
