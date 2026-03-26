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
        Set<Object> alreadyCountedBeans = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Object> stack = initializeStack(bean);

        long sizeInBytes = 0L;
        while (!stack.isEmpty() && sizeInBytes <= maximalGroesse) {
            Object currentObject = stack.pop();

            if (shouldIgnore(currentObject, alreadyCountedBeans)) {
                continue;
            }

            sizeInBytes += processObjectAndPushChildren(currentObject, stack);
        }

        return sizeInBytes <= maximalGroesse;
    }

    private Deque<Object> initializeStack(Object bean) {
        Deque<Object> stack = new ArrayDeque<>();
        if (bean != null) {
            stack.push(bean);
        }
        return stack;
    }

    private boolean shouldIgnore(Object objekt, Set<Object> alreadyCountedBeans) {
        if (objekt == null) {
            return true;
        }
        if (isVmSingleton(objekt)) {
            return true;
        }
        return !alreadyCountedBeans.add(objekt);
    }

    private boolean isVmSingleton(Object objekt) {
        Class<?> clazz = objekt.getClass();
        return Enum.class.isAssignableFrom(clazz) || objekt instanceof Class<?>;
    }

    /**
     * Liefert die Byte-Anteile, die direkt gezählt werden können, und pushed ggf. Kindobjekte auf den Stack.
     */
    private long processObjectAndPushChildren(Object objekt, Deque<Object> stack) {
        Class<?> objectClass = objekt.getClass();

        Integer primitiveSize = PRIMITIVE_TYPEN_GROESSE.get(objectClass);
        if (primitiveSize != null) {
            return primitiveSize;
        }

        if (objectClass.isArray()) {
            return processArrayAndPushChildren(objekt, stack);
        }

        return processInstanceAndPushFields(objekt, stack);
    }

    private long processArrayAndPushChildren(Object arrayObj, Deque<Object> stack) {
        Class<?> componentType = arrayObj.getClass().getComponentType();
        int length = Array.getLength(arrayObj);

        Integer componentSize = PRIMITIVE_TYPEN_GROESSE.get(componentType);
        if (componentSize != null) {
            return componentSize * (long) length;
        }

        for (int i = length - 1; i >= 0; i--) {
            Object childValue = Array.get(arrayObj, i);
            if (childValue != null) {
                stack.push(childValue);
            }
        }
        return 0L;
    }

    private long processInstanceAndPushFields(Object objekt, Deque<Object> stack) {
        long sizeInBytes = 0L;

        for (Field field : ermittleAlleInstanzAttribute(objekt)) {
            Object fieldValue = getFieldValueOrNull(field, objekt);
            if (fieldValue == null) {
                continue;
            }

            Integer primitiveSize = PRIMITIVE_TYPEN_GROESSE.get(fieldValue.getClass());
            if (primitiveSize != null) {
                sizeInBytes += primitiveSize;
            } else {
                stack.push(fieldValue);
            }
        }

        return sizeInBytes;
    }

    private Object getFieldValueOrNull(Field attribut, Object objekt) {
        try {
            return attribut.get(objekt);
        } catch (IllegalAccessException _) {
            // Wenn Reflection nicht funktioniert, ist die Größenmessung deaktiviert.
            return null;
        }
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
        } catch (SecurityException _) {
            // Wenn es die Sicherheit nicht erlaubt, ist die Messung deaktiviert.
            return Collections.emptyList();
        }
        return fields;
    }

}
