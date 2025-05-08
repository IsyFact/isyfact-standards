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
 * This class generates a text output for objects that do not implement a suitable toString method.
 */
public class RecursiveToStringBuilder {

    private RecursiveToStringBuilder () {
        // hide the constructor as it is a utility class
    }

    /**
     * Maximum number of java primitive type entries from an array that will appear in output.
     */
    private static final int PRIMITIVE_ARRAY_MAX_OUTPUT_SIZE = 16;

    /**
     * Recursive output of objects.
     *
     * @param o The object to be output
     * @return The object to be output
     */
    public static String recursiveToString(final Object o) {
        final StringBuilder buffer = new StringBuilder();

        recursiveToString(new StringBuilder(), o, buffer, new LinkedList<Object>());

        return buffer.toString();
    }

    /**
     * Recursive output of objects.
     *
     * @param prefix The output prefix
     * @param o The object to be written
     * @param buffer The StringBuilder
     * @param seen Backreferences
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
            appendMap(prefix, (Map<?, ?>)o, buffer, seen);
        } else if (o instanceof Iterable) {
            appendIterable(prefix, (Iterable<?>)o, buffer, seen);
        } else if (shouldNotRecurse(o)) {
            buffer.append(o);
            buffer.append('\n');
            return;
        } else {
            appendGeneric(prefix, o, buffer, seen);
        }
    }

    /**
     * Add string representation of array object.
     *
     * @param prefix The output prefix
     * @param o The array object to be written
     * @param buffer The StringBuilder
     * @param seen references
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
     * Add string representation of Iterable object.
     *
     * @param prefix The output prefix
     * @param o The iterable object to be written
     * @param buffer The StringBuilder
     * @param seen references
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
     * Add string representation of map object.
     *
     * @param prefix The output prefix
     * @param o The map object to be written
     * @param buffer The StringBuilder
     * @param seen references
     */
    private static void appendMap(final StringBuilder prefix, final Map<?, ?> o, final StringBuilder buffer,
        final Collection<Object> seen) {
        Iterator<?> it = o.entrySet().iterator();
        buffer.append(o.getClass().getName()).append(" [\n");
        buffer.append(prefix);
        while (it.hasNext()) {
            Map.Entry<?,?> entry = (Entry<?, ?>) it.next();
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
     * Add string representation of the object.
     *
     * @param prefix The output prefix
     * @param o The object to be written
     * @param buffer The StringBuilder
     * @param seen references
     */
    private static void appendGeneric(final StringBuilder prefix, final Object o, final StringBuilder buffer,
        final Collection<Object> seen) {
        final StringBuilder fieldPrefix = new StringBuilder(prefix);
        fieldPrefix.append("  ");
        objectToString(o, buffer);
        buffer.append("{");
        buffer.append('\n');

        // Determine all fields - including those of the inheriting classes
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
     * Determines all fields of a superordinate object.
     *
     * @param o The object
     * @return All fields.
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
     * determines whether an object is to be output recursively.
     *
     * @param o The object to be checked
     * @return {@code true} if the object is not to be output recursively, otherwise {@code false}
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
     * writes an object.
     *
     * @param o The object to be written
     * @param buffer The StringBuilder to be used for the output
     */
    private static void objectToString(final Object o, final StringBuilder buffer) {
        buffer.append(o.getClass().getName());
        buffer.append('@');
        buffer.append(Integer.toHexString(System.identityHashCode(o)));
    }
}
