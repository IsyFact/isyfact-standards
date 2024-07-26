package de.bund.bva.isyfact.logging.util;

/*
 * #%L
 * isy-logging
 * %%
 *
 * %%
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * #L%
 */

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
 * Converter to transform a bean into a map, which serves as input for serialization into JSON.
 */
public class BeanToMapConverter implements BeanConverter {

    /** List of packages to consider. This is used for filtering individual properties. */
    private final List<String> includes;

    /** List of packages to ignore. This is used for filtering individual properties. */
    private final List<String> excludes;

    /** The string to use for null values. */
    public static final String NULL_STRING = "null";

    /** The string to use for outputting the hash code. */
    public static final String HASHCODE_KEY = "hashCode";

    /** The string to use when outputting an excluded object. */
    public static final String EXCLUDED_VALUE = "NICHT_SERIALISIERT";

    /**
     * Constructor of the class. Initializes the passed properties.
     *
     * @param includes
     *            List of packages to consider.
     * @param excludes
     *            List of packages to ignore.
     */
    public BeanToMapConverter(List<String> includes, List<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    /** Enum to determine the manner in which property processing should occur. */
    protected enum ConversionStyle {
        /** The property is processed recursively. */
        RECURSIVE,
        /** The toString method is called on the property. */
        TOSTRING,
        /** The property is ignored. */
        IGNORE;
    }

    /**
     * {@inheritDoc}
     *
     * @see de.bund.bva.isyfact.logging.util.BeanConverter#convert(java.lang.Object)
     */
    @Override
    public Object convert(Object bean) {

        // Null is given as 'null' in serialization.
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
     * Recursively collects all properties in the beans. For each bean, a map is created in which
     * the names of the properties are mapped to their respective values. Properties that are themselves beans
     * and are considered during serialization are themselves included as maps in the parent
     * map (of the containing bean).
     *
     * @param bean
     *            the bean to be converted.
     * @param seen
     *            List of already processed objects to prevent recursion from ending in an infinite loop.
     * @return the map structure with the values of the bean to be serialized.
     */
    private Map<String, Object> collectAttributesRecursive(Object bean, Set<Object> seen) {

        // The result map. TreeMap is used for a stable sorting order.
        Map<String, Object> propertyMap = new TreeMap<>();

        propertyMap.put(HASHCODE_KEY, "" + bean.hashCode());

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass());
        } catch (IntrospectionException e) {
            throw new SerialisierungException(FehlerSchluessel.FEHLER_SERIALISIERUNG_AUFRUFPARAMETER, e);
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        // Iteration over all properties of the bean.
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

            // Name of the property
            String name = propertyDescriptor.getName();

            Method readMethod = propertyDescriptor.getReadMethod();

            // Skip if there is no public read method for the property.
            if (readMethod == null) {
                continue;
            }

            // Value of the property
            Object value;
            try {
                value = readMethod.invoke(bean);
            } catch (Exception e) {
                throw new SerialisierungException(FehlerSchluessel.FEHLER_SERIALISIERUNG_AUFRUFPARAMETER, e);
            }

            Object converted = processValue(value, seen);
            // Empty values are not included - Null values that are to be included are already converted
            // to a string during conversion.
            if (converted != null) {
                propertyMap.put(name, converted);
            }
        }

        return propertyMap;
    }

    /**
     * Takes the given value of a property.
     *
     * @param value
     *            the value to process.
     * @param seen
     *            List of already processed objects to prevent recursion from ending in an infinite loop.
     *
     * @return the value in serialized form.
     */
    private Object processSimpleValue(Object value, Set<Object> seen) {

        // Determines the type of serialization
        ConversionStyle serialisierungsart = determineConversionStyle(value);

        switch (serialisierungsart) {
            case TOSTRING:
                return convertToString(value);
            case RECURSIVE:
                if (seen.contains(value)) {
                    // Break if the object has already been included to prevent infinite loops
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
     * Takes the given value.
     *
     * @param value
     *            the value to take (any data type).
     * @param seen
     *            List of already processed objects to prevent recursion from ending in an infinite loop.
     * @return the values in converted form.
     */
    private Object processValue(Object value, Set<Object> seen) {

        if (value == null) {
            return processSimpleValue(value, seen);
        }

        // Arrays are treated like lists
        if (value.getClass().isArray()) {
            List<Object> valueList = new ArrayList<>();

            // Explicitly not using Arrays.asList here as it leads to problems with arrays of
            // primitive types.
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
     * Takes the given map value of a property.
     *
     * @param value
     *            the value to process.
     * @param seen
     *            List of already processed objects to prevent recursion from ending in an infinite loop.
     *
     * @return the values in converted form.
     */
    private Object processMapValue(Map<?, ?> value, Set<Object> seen) {
        Map<Object, Object> convertedMap = new TreeMap<>(Comparator.comparing(Object::toString));

        for (Object mapKey : value.keySet()) {
            Object mapValue = value.get(mapKey);

            // Ignore the value if the key is 'null' - this only happens if the value really
            // should be ignored, otherwise the "NULL_STRING" is returned.
            Object convertedKey = processValue(mapKey, seen);
            if (convertedKey != null) {
                Object convertedValue = processValue(mapValue, seen);
                convertedMap.put(convertedKey, convertedValue);
            }

        }

        return convertedMap;
    }

    /**
     * Takes the given iterable value of a property.
     *
     * @param iterable
     *            the value to process.
     * @param seen
     *            List of already processed objects to prevent recursion from ending in an infinite loop.
     *
     * @return the values in converted form.
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
     * Helper method for converting a value to a string.
     *
     * @param value
     *            the value to be converted.
     * @return the converted value.
     */
    private String convertToString(Object value) {
        if (value == null) {
            return NULL_STRING;
        } else {
            return value.toString();
        }
    }

    /**
     * Determines in what form the given object should be serialized. This method can be used as
     * an extension point for more specific logic.
     *
     * @param value
     *            the value to be converted.
     * @return the type of conversion.
     */
    protected ConversionStyle determineConversionStyle(Object value) {

        // Null values are ignored.
        if (value == null) {
            return ConversionStyle.TOSTRING;
        }

        Class<? extends Object> classObj = value.getClass();
        String className = classObj.getName();

        // Simple data types are taken as a string (Primitives cannot occur as they are
        // automatically wrapped in INTEGER etc.
        if (classObj.isEnum()) {
            return ConversionStyle.TOSTRING;
        }

        boolean included = checkIsInclude(className);
        boolean excluded = checkIsExclude(className);

        if (excluded) {
            // Excluded classes are always ignored
            return ConversionStyle.IGNORE;
        } else if (included) {
            // Included but not excluded are always processed recursively
            return ConversionStyle.RECURSIVE;
        } else {
            // Classes without specification are processed using ToString
            return ConversionStyle.TOSTRING;
        }

    }

    /**
     * Checks if the given class should be included in the serialization (Include).
     *
     * @param className
     *            Name of the class to check.
     * @return <code>true</code> if the class should be included, <code>false</code> otherwise.
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
     * Checks if the given class should explicitly not be included in the serialization (Excluded).
     *
     * @param className
     *            Name of the class to check.
     * @return <code>true</code> if the class should be excluded, <code>false</code> otherwise.
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
     * Returns the value of the 'includes' attribute.
     *
     * @return Value of the attribute.
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Returns the value of the 'excludes' attribute.
     *
     * @return Value of the attribute.
     */
    public List<String> getExcludes() {
        return excludes;
    }
}
