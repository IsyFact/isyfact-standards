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
package de.bund.bva.isyfact.persistence.usertype;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import de.bund.bva.isyfact.persistence.annotation.PersistentValue;
import de.bund.bva.isyfact.persistence.exception.FehlerSchluessel;
import de.bund.bva.isyfact.persistence.exception.PersistenzException;

/**
 * Ein {@link UserType} zur Persistierung beliebiger Enumtypen als String, d.h. in eine VARCHAR-Spalte. Die
 * Ausprägungen des Enums müssen mit {@link PersistentValue} annotiert sein, um den zu persistierenden String
 * festzulegen.
 * 
 */
public class EnumUserType extends AbstractImmutableStringUserType implements ParameterizedType {

    /** Die Enum-Klasse. */
    private Class<? extends Enum<?>> enumClass;

    /** Abbildung von Enum-Ausprägung nach String. */
    private Map<Enum<?>, String> enumToString = new HashMap<Enum<?>, String>();

    /** Abbildung von String nach Enum-Ausprägung. */
    private Map<String, Enum<?>> stringToEnum = new HashMap<String, Enum<?>>();

    /**
     * Setzt die Enum-Klasse.
     * 
     * @param enumClass
     *            die Enum-Klasse.
     */
    public void setEnumClass(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;

        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            Field field;
            try {
                field = enumClass.getField(enumValue.name());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            PersistentValue persValue = field.getAnnotation(PersistentValue.class);
            if (persValue == null) {
                throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, "Der Enumwert "
                    + field.getType() + "." + field.getName() + " ist nicht mit "
                    + PersistentValue.class.getSimpleName() + " annotiert");
            }
            enumToString.put(enumValue, persValue.value());
            Enum<?> oldValue = stringToEnum.put(persValue.value(), enumValue);
            if (oldValue != null) {
                throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, "Im Enum "
                    + field.getType() + " ist der Persistenzwert '" + persValue.value()
                    + "' zweimal annotiert");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends Enum<?>> returnedClass() {
        return enumClass;
    }

    @Override
    public Object convertStringToInstance(String value) {
        Enum<?> e = stringToEnum.get(value);
        if (e == null) {
            throw new PersistenzException(FehlerSchluessel.UNBEKANNTER_STRING, value, enumClass.getName());
        }
        return e;
    }

    @Override
    public String convertInstanceToString(Object value) {
        String s = enumToString.get(value);
        if (s == null) {
            throw new PersistenzException(FehlerSchluessel.UNBEKANNTE_AUSPRAEGUNG, value.toString(),
                enumClass.getName());
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        if (enumClassName == null) {
            throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION,
                "Parameter 'enumClass' fehlt für " + EnumUserType.class.getSimpleName());
        }
        try {
            Class<?> clazz = Class.forName(enumClassName);
            if (!clazz.isEnum()) {
                throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, "Klasse "
                    + clazz.getName() + " ist kein Enum");
            }
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClazz = (Class<? extends Enum<?>>) clazz;
            setEnumClass(enumClazz);
        } catch (ClassNotFoundException e) {
            throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, e, "Enum-Klasse "
                + enumClassName + " nicht gefunden");
        }
    }
}
