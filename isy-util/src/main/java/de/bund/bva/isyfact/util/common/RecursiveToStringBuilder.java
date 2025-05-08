package de.bund.bva.isyfact.util.common;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Diese Klasse erzeugt eine Textausgabe für Objekte, die keine geeignete toString-Methode implementieren.
 *
 */
public class RecursiveToStringBuilder {
    /**
     * Maximal Anzahl der java primitivtyp Einträge aus einer Array die in Ausgabe erscheinen wird.
     */
    private static final int PRIMITIVE_ARRAY_MAX_OUTPUT_SIZE = 16;

    /**
     * Rekursive Ausgabe von Objekten.
     *
     * @param o
     *            Das auszugebende Objekt
     * @return Das ausgegebende Objekt
     */
    public static String recursiveToString(final Object o) {
        final StringBuilder buffer = new StringBuilder();

        recursiveToString(new StringBuilder(), o, buffer, new LinkedList<Object>());

        return buffer.toString();
    }

    /**
     * Rekursive Ausgabe von Objekten.
     *
     * @param prefix
     *            Das Ausgabe-Präfix
     * @param o
     *            Das zu schreibende Objekt
     * @param buffer
     *            Der StringBuilder
     * @param seen
     *            Rückreferenzen
     */
    private static void recursiveToString(final StringBuilder prefix, final Object o,
        final StringBuilder buffer, final Collection<Object> seen) {
        if (null == o) {
            buffer.append("null\n");
            return;
        }

        // Mark back references with angle brackets
        if (seen.contains(o)) {
            if (shouldNotRecurse(o)) {
                buffer.append(o);
                buffer.append('\n');
            } else {
                buffer.append('<');
                objectToString(o, buffer);
                buffer.append(">\n");
            }
            return;
        }

        seen.add(o);
        if (o.getClass().isArray()) {
            appendArray(prefix, o, buffer, seen);
        } else if (o instanceof Map) {
            appendMap(prefix, (Map<?, ?>) o, buffer, seen);
        } else if (o instanceof Iterable) {
            appendIterable(prefix, (Iterable<?>) o, buffer, seen);
        } else if (shouldNotRecurse(o)) {
            buffer.append(o);
            buffer.append('\n');
        } else {
            appendGeneric(prefix, o, buffer, seen);
        }
    }

    /**
     * Hinzufügen Stringdarstellung von Array-Objekt.
     *
     * @param prefix
     *            Das Ausgabe-Präfix
     * @param o
     *            Das zu schreibende Array-Objekt
     * @param buffer
     *            Der StringBuilder
     * @param seen
     *            Rückreferenzen
     */
    private static void appendArray(final StringBuilder prefix, final Object o, final StringBuilder buffer,
        final Collection<Object> seen) {
        buffer.append("[\n");
        buffer.append(prefix);
        int arraySize = Array.getLength(o);
        int maxOutputSize = arraySize;
        int truncatedSize = 0;
        if (arraySize > 0) {
            // für primitiv typ.
            if (o.getClass().getComponentType().isPrimitive()) {
                maxOutputSize = Math.min(arraySize, PRIMITIVE_ARRAY_MAX_OUTPUT_SIZE);
                truncatedSize = arraySize - PRIMITIVE_ARRAY_MAX_OUTPUT_SIZE;
                buffer.append(prefix);
                for (int i = 0; i < maxOutputSize; i++) {
                    buffer.append(Array.get(o, i));
                    buffer.append(' ');
                }
                if (truncatedSize > 0) {
                    buffer.append("... ");
                    buffer.append(truncatedSize);
                    buffer.append(" more");
                }
                buffer.append("\n");
            } else {
                for (int i = 0; i < maxOutputSize; i++) {
                    buffer.append(prefix);
                    recursiveToString(prefix, Array.get(o, i), buffer, seen);
                    if (i < (maxOutputSize - 1)) {
                        buffer.append(prefix);
                    }
                }
            }
        }
        buffer.append(prefix);
        buffer.append("]\n");
    }

    /**
     * Hinzufügen Stringdarstellung von Iterable-Objekt.
     *
     * @param prefix
     *            Das Ausgabe-Präfix
     * @param o
     *            Das zu schreibende Iterable-Objekt
     * @param buffer
     *            Der StringBuilder
     * @param seen
     *            Rückreferenzen
     */
    private static void appendIterable(final StringBuilder prefix, final Iterable<?> o,
        final StringBuilder buffer, final Collection<Object> seen) {
        Iterator<?> it = o.iterator();
        buffer.append(o.getClass().getName()).append(" [\n");
        while (it.hasNext()) {
            Object obj = it.next();
            buffer.append(prefix);
            recursiveToString(prefix, obj, buffer, seen);
            if (it.hasNext()) {
                buffer.append(prefix);
            }
        }
        buffer.append(prefix);
        buffer.append("]\n");
    }

    /**
     * Hinzufügen Stringdarstellung von Map-Objekt.
     *
     * @param prefix
     *            Das Ausgabe-Präfix
     * @param o
     *            Das zu schreibende Map-Objekt
     * @param buffer
     *            Der StringBuilder
     * @param seen
     *            Rückreferenzen
     */
    private static void appendMap(final StringBuilder prefix, final Map<?, ?> o, final StringBuilder buffer,
        final Collection<Object> seen) {
        Iterator<?> it = o.entrySet().iterator();
        buffer.append(o.getClass().getName()).append(" [\n");
        buffer.append(prefix);
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = (Entry<?, ?>) it.next();
            buffer.append(prefix);
            recursiveToString(prefix, entry.getKey(), buffer, seen);
            buffer.append(prefix);
            buffer.append(" => ");
            buffer.append(prefix);
            recursiveToString(prefix, entry.getValue(), buffer, seen);
            if (it.hasNext()) {
                buffer.append(prefix);
            }
        }
        buffer.append(prefix);
        buffer.append("]\n");
    }


    /**
     * Hinzufügen Stringdarstellung des Objekts.
     *
     * @param prefix
     *            Das Ausgabe-Präfix
     * @param o
     *            Das zu schreibende Objekt
     * @param buffer
     *            Der StringBuilder
     * @param seen
     *            Rückreferenzen
     */
    private static void appendGeneric(final StringBuilder prefix, final Object o, final StringBuilder buffer,
        final Collection<Object> seen) {
        final StringBuilder fieldPrefix = new StringBuilder(prefix);
        fieldPrefix.append("  ");
        objectToString(o, buffer);
        buffer.append("{");
        buffer.append('\n');

        // Bestimme alle Felder - auch die der vererbendene Klassen
        Field[] fields = bestimmeAlleFelder(o);

        for (final Field field : fields) {
            field.setAccessible(true);
            buffer.append(fieldPrefix);
            buffer.append(field.getName());
            buffer.append("=");

            try {
                recursiveToString(fieldPrefix, field.get(o), buffer, seen);
            } catch (final IllegalAccessException e) {
                buffer.append("? (");
                buffer.append(e.getMessage());
                buffer.append(')');
            }
        }

        buffer.append(prefix);
        buffer.append("}");
        buffer.append('\n');
    }

    /**
     * ermittelt alle Felder eines übergenenen Objekts.
     *
     * @param o
     *            Das Objekt
     * @return Alle Felder.
     */
    private static Field[] bestimmeAlleFelder(Object o) {
        List<Field> fields = new LinkedList<>();
        Class<?> aktuelleKlasse = o.getClass();

        while (aktuelleKlasse != null) {
            Field[] declaredFields = aktuelleKlasse.getDeclaredFields();
            Collections.addAll(fields, declaredFields);
            aktuelleKlasse = aktuelleKlasse.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * ermittelt ob ein Objekt rekursiv ausgegeben werden soll.
     *
     * @param o
     *            Das zu prüfende Objekt
     * @return <code>true</code> falls das Objekt nit rekursiv ausgegeben werden soll, ansonsten
     *         <code>false</code>
     */
    private static boolean shouldNotRecurse(final Object o) {
        try {
            if (o.getClass().isArray()) {
                return true;
            }
            if (o.getClass().isEnum()) {
                return true;
            }
            String packageName = o.getClass().getPackage().getName();
            return (packageName.startsWith("java.") || packageName.startsWith("javax."))
                && !Object.class.getMethod("toString").equals(o.getClass().getMethod("toString"));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * schreibt ein Objekt.
     *
     * @param o
     *            Das zu schreibende Objekt
     * @param buffer
     *            Der StringBuilder der für die Ausgabe verwendet werden soll
     */
    private static void objectToString(final Object o, final StringBuilder buffer) {
        buffer.append(o.getClass().getName());
        buffer.append('@');
        buffer.append(Integer.toHexString(System.identityHashCode(o)));
    }
}
