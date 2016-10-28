/*
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
 */
package de.bund.bva.pliscommon.serviceapi.core.serviceimpl;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ClassUtils;
import org.dozer.Mapper;

/**
 * Hilfsklasse für Dozer-Mappings, die Mappings von generischen Collections und Maps sowie von primitiven
 * Typen unterstützt.
 * 
 */
public final class MappingHelper {

    /** Klassen, die nicht gemappt werden sollen. */
    private static final List<Class<?>> MAPPING_BLACKLIST = Arrays.asList(new Class<?>[] { String.class,
        Class.class, Object.class });

    /**
     * Bildet ein Objekt mithilfe von Dozer auf einen gewünschten Zieltyp ab. Im Gegensatz zu
     * {@link Mapper#map(Object, Class)} können als Zieltyp auch generische Collections, String und primitive
     * Typen übergeben werden.
     * 
     * @param mapper
     *            der Dozer-Mapper
     * @param source
     *            das zu mappende Objekt
     * @param destinationType
     *            der Zieltyp
     * @return das gemappte Objekt
     */
    @SuppressWarnings("unchecked")
    public static Object map(Mapper mapper, Object source, Type destinationType) {
        if (source == null) {
            return null;
        }

        if (destinationType instanceof ParameterizedType) {
            ParameterizedType parDestinationType = (ParameterizedType) destinationType;

            Class<?> rawClass = (Class<?>) parDestinationType.getRawType();
            if (List.class.isAssignableFrom(rawClass)) {
                return mapCollection(mapper, source, parDestinationType, new ArrayList<Object>());
            } else if (SortedSet.class.isAssignableFrom(rawClass)) {
                return mapCollection(mapper, source, parDestinationType, new TreeSet<Object>());
            } else if (Set.class.isAssignableFrom(rawClass)) {
                return mapCollection(mapper, source, parDestinationType, new HashSet<Object>());
            } else if (SortedMap.class.isAssignableFrom(rawClass)) {
                return mapMap(mapper, source, parDestinationType, new TreeMap<Object, Object>());
            } else if (Map.class.isAssignableFrom(rawClass)) {
                return mapMap(mapper, source, parDestinationType, new HashMap<Object, Object>());
            }

            destinationType = parDestinationType.getRawType();
        }

        if (destinationType instanceof GenericArrayType) {
            if (!source.getClass().isArray()) {
                throw new IllegalArgumentException("Ein Mapping auf den Array-Typ " + destinationType
                    + " wird nicht unterstützt, wenn das Quellobjekt kein Array ist. Typ des Quellobjekts: "
                    + source.getClass());
            }

            // wir werden im Array Element pro Element mappen
            Type elementType = ((GenericArrayType) destinationType).getGenericComponentType();
            Object[] sourceArray = (Object[]) source;
            Object[] destinationArray =
                (Object[]) Array.newInstance((Class<?>) elementType, sourceArray.length);
            for (int i = 0; i < sourceArray.length; i++) {
                destinationArray[i] = MappingHelper.map(mapper, sourceArray[i], elementType);
            }
            return destinationArray;
        } else if ((destinationType instanceof Class<?>) && ((Class<?>) destinationType).isArray()) {
            if (!source.getClass().isArray()) {
                throw new IllegalArgumentException("Ein Mapping auf den Array-Typ " + destinationType
                    + " wird nicht unterstützt, wenn das Quellobjekt kein Array ist. Typ des Quellobjekts: "
                    + source.getClass());
            }
            Class<?> destinationTypeClass = (Class<?>) destinationType;
            // wir werden im Array Element pro Element mappen
            Type elementType = destinationTypeClass.getComponentType();
            Object[] sourceArray = (Object[]) source;
            Object[] destinationArray =
                (Object[]) Array.newInstance((Class<?>) elementType, sourceArray.length);
            for (int i = 0; i < sourceArray.length; i++) {
                destinationArray[i] = MappingHelper.map(mapper, sourceArray[i], elementType);
            }
            return destinationArray;
        }

        if (!(destinationType instanceof Class<?>)) {
            throw new IllegalArgumentException("Ein Mapping auf Typ " + destinationType
                + " wird nicht unterstützt");
        }

        Class<?> destinationClass = (Class<?>) destinationType;

        if (ClassUtils.isPrimitiveOrWrapper(destinationClass) || MAPPING_BLACKLIST.contains(destinationClass)) {
            return source;
        } else if (destinationClass.isEnum()) {
            // wir müssen auf dieser Ebene Enums leider manuell mappen
            if (!(source instanceof Enum)) {
                throw new IllegalArgumentException("Ein Mapping auf ein Enum " + destinationClass
                    + " wird nicht unterstützt, da das Quellobjekt kein Enumobjekt ist (Quellobjektstyp: "
                    + source.getClass().toString() + ").");
            }
            return Enum.valueOf((Class<Enum>) destinationClass, ((Enum<?>) source).name());
        } else {
            return mapper.map(source, destinationClass);
        }
    }

    /**
     * Mappt eine Collection.
     * 
     * @param mapper
     *            der Dozer-Mapper
     * @param source
     *            eine Collection
     * @param parDestinationType
     *            der Typ der Ziel-Collection
     * @param result
     *            die instanziierte, leere Ziel-Collection
     * @return die gemappte Collection
     */
    static Collection<Object> mapCollection(Mapper mapper, Object source,
        ParameterizedType parDestinationType, Collection<Object> result) {
        if (!(source instanceof Collection)) {
            throw new IllegalArgumentException("Ein Objekt vom Typ " + source.getClass() + " kann nicht auf "
                + parDestinationType + " gemappt werden");
        }
        Collection<?> sourceCollection = (Collection<?>) source;
        Type destElemType = parDestinationType.getActualTypeArguments()[0];

        for (Object sourceElem : sourceCollection) {
            Object destElem = map(mapper, sourceElem, destElemType);
            result.add(destElem);
        }

        return result;
    }

    /**
     * Mappt eine Map.
     * 
     * @param mapper
     *            der Dozer-Mapper
     * @param source
     *            eine Map
     * @param parDestinationType
     *            der Typ der Ziel-Map
     * @param result
     *            die instanziierte, leere Ziel-Map
     * @return die gemappte Map
     */
    static Map<Object, Object> mapMap(Mapper mapper, Object source, ParameterizedType parDestinationType,
        Map<Object, Object> result) {
        if (!(source instanceof Map)) {
            throw new IllegalArgumentException("Ein Objekt vom Typ " + source.getClass() + " kann nicht auf "
                + parDestinationType + " gemappt werden");
        }
        Map<?, ?> sourceMap = (Map<?, ?>) source;
        Type destKeyType = parDestinationType.getActualTypeArguments()[0];
        Type destValueType = parDestinationType.getActualTypeArguments()[1];

        for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
            Object key = map(mapper, entry.getKey(), destKeyType);
            Object value = map(mapper, entry.getValue(), destValueType);
            result.put(key, value);
        }

        return result;
    }

}
