package de.bund.bva.isyfact.logging.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Prüft Beans, die als Parameter automatisch an Komponenten- oder Außengrenzen des Systems ins Log
 * geschrieben werden sollen, auf ihre potentielle Größe im Log. Dazu wird der Speicherbedarf aller primitiven
 * Typen im Objektgraph gezählt.
 */
public class BeanGroessePruefer {

    private static final Map<Class<?>, Integer> PRIMITIVE_TYPEN_GROESSE;

    static {
        Map<Class<?>, Integer> typen = new HashMap<>();
        // Tatsächliche Größe unklar, sollte aber gezählt werden.
        typen.put(boolean.class, 1);
        typen.put(Boolean.class, 1);
        typen.put(byte.class, 1);
        typen.put(Byte.class, 1);
        typen.put(char.class, 2);
        typen.put(Character.class, 2);
        typen.put(short.class, 2);
        typen.put(Short.class, 2);
        typen.put(int.class, 4);
        typen.put(Integer.class, 4);
        typen.put(float.class, 4);
        typen.put(Float.class, 4);
        typen.put(long.class, 8);
        typen.put(Long.class, 8);
        typen.put(double.class, 8);
        typen.put(Double.class, 8);
        PRIMITIVE_TYPEN_GROESSE = Collections.unmodifiableMap(typen);
    }

    /**
     * Prüft die Größe eines Log-Parameters gegen einen Maximalwert.
     *
     * @param bean        Log-Parameter
     * @param maximalSize maximal erlaubte Größe (in Byte)
     * @return {@code true} falls der Parameter innerhalb der erlaubten Größe liegt oder kein Maximalwert
     * definiert ist, {@code false} falls der Parameter zu groß ist.
     */
    public boolean pruefeGroesse(Object bean, Long maximalSize) {
        return maximalSize == null || maximalSize <= 0 || ermittleGroesseInBytes(bean, maximalSize);
    }

    /**
     * Ermittelt die Größe eines Log-Parameters in Byte. Bricht ab, wenn die Größe des Beans die Maximalgröße
     * übersteigt.
     *
     * @param bean           Log-Parameter
     * @param maximalGroesse maximal erlaubte Größe (in Byte)
     * @return {@code true} falls der Parameter innerhalb der erlaubten Größe liegt, {@code false} falls der
     * Parameter zu groß ist.
     */
    private boolean ermittleGroesseInBytes(Object bean, Long maximalGroesse) {
        Set<Object> bereitsGezaehlteBeans = Collections.newSetFromMap(new IdentityHashMap<>());
        long beanGroesse = 0L;

        Deque<Object> stack = new ArrayDeque<>();
        if (bean != null) {
            stack.push(bean);
        }

        while (!stack.isEmpty() && beanGroesse <= maximalGroesse) {
            Object objekt = stack.pop();

            // Ignoriere die Instanz, falls sie:
            // - eine Enum oder Klasse ist (dann liegt sie einmal pro VM im Speicher)
            // - schon einmal gezählt wurde
            // - null ist, da sie dann nichts zur Größe beiträgt
            if (objekt != null && !(Enum.class.isAssignableFrom(objekt.getClass())
                || objekt instanceof Class<?>) && bereitsGezaehlteBeans.add(objekt)) {
                Class<?> klasse = objekt.getClass();
                if (PRIMITIVE_TYPEN_GROESSE.containsKey(klasse)) {
                    beanGroesse += PRIMITIVE_TYPEN_GROESSE.get(klasse);
                } else if (klasse.isArray()) {
                    if (PRIMITIVE_TYPEN_GROESSE.containsKey(klasse.getComponentType())) {
                        beanGroesse +=
                            PRIMITIVE_TYPEN_GROESSE.get(klasse.getComponentType()) * Array.getLength(objekt);
                    } else {
                        for (int i = Array.getLength(objekt) - 1; i >= 0; i--) {
                            Object childValue = Array.get(objekt, i);
                            if (childValue != null) {
                                stack.push(childValue);
                            }
                        }
                    }
                } else {
                    for (Field attribut : ermittleAlleInstanzAttribute(objekt)) {
                        try {
                            Object attributWert = attribut.get(objekt);
                            if (attributWert != null) {
                                if (PRIMITIVE_TYPEN_GROESSE.containsKey(attributWert.getClass())) {
                                    beanGroesse += PRIMITIVE_TYPEN_GROESSE.get(attributWert.getClass());
                                } else {
                                    stack.push(attributWert);
                                }
                            }
                        } catch (IllegalAccessException e) {
                            // Wenn Reflection nicht funktioniert, ist die Größenmessung deaktiviert.
                        }
                    }
                }
            }
        }

        return beanGroesse <= maximalGroesse;
    }

    /**
     * Ermittelt alle Attribute einer Instanz, die nicht statisch sind. Schaut dazu auch in die Superklassen.
     * Setzt die Felder alle "accessible", so dass via Reflection darauf zugegriffen werden kann.
     *
     * @param instanz Instanz
     * @return eine Liste aller nicht-statischen Attribute.
     */
    private List<Field> ermittleAlleInstanzAttribute(Object instanz) {
        List<Field> fields = new ArrayList<>();

        Class<?> clazz = instanz.getClass();
        while (clazz != null) {
            for (Field field : Arrays.asList(clazz.getDeclaredFields())) {
                // nur nicht-statische Attribute aufnehmen
                if (!Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        // So gibt es nur eine Anfrage an den SecurityManager
        try {
            AccessibleObject.setAccessible(fields.toArray(new AccessibleObject[fields.size()]), true);
        } catch (SecurityException e) {
            // Wenn es die Sicherheit nicht erlaubt, ist die Messung deaktiviert.
            return Collections.emptyList();
        }
        return fields;
    }

}
