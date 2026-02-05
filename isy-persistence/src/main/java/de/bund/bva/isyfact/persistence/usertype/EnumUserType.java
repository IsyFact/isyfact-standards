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
 * An {@link UserType} for persistence of custom enum types, saved as strings (VARCHAR-fields in DB).
 * Declared values of given enum need to be annotated with {@link PersistentValue} to describe the string for persistence.
 */
public class EnumUserType extends AbstractImmutableStringUserType<Enum<?>>
        implements ParameterizedType {

    /** Enum class type. */
    private Class<Enum<?>> enumClass;

    /** Map enum values to their string representation for persistence. */
    private final Map<Enum<?>, String> enumToString = new HashMap<>();

    /** Map string representation to enum values. */
    private final Map<String, Enum<?>> stringToEnum = new HashMap<>();

    /**
     * Set enum type.
     *
     * @param enumClass enum type.
     */
    public void setEnumClass(Class<? extends Enum<?>> enumClass) {
        this.enumClass = (Class<Enum<?>>) enumClass;

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
            this.enumToString.put(enumValue, persValue.value());
            Enum<?> oldValue = this.stringToEnum.put(persValue.value(), enumValue);
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
    @Override
    public Class<Enum<?>> returnedClass() {
        return this.enumClass;
    }

    /**
     * Get string representation of an enum value to according enum value.
     *
     * @param value the string representation.
     * @return mapped enum value.
     */
    @Override
    public Enum<?> convertStringToInstance(String value) {
        Enum<?> e = this.stringToEnum.get(value);
        if (e == null) {
            throw new PersistenzException(FehlerSchluessel.UNBEKANNTER_STRING, value,
                    this.enumClass.getName());
        }
        return e;
    }

    /**
     * Get enum value of given string representation.
     *
     * @param value the enum value.
     * @return string representation.
     */
    @Override
    public String convertInstanceToString(Enum<?> value) {
        String s = this.enumToString.get(value);
        if (s == null) {
            throw new PersistenzException(FehlerSchluessel.UNBEKANNTE_AUSPRAEGUNG, value.toString(),
                    this.enumClass.getName());
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            Class<Enum<?>> enumClazz = (Class<Enum<?>>) clazz;
            setEnumClass(enumClazz);
        } catch (ClassNotFoundException e) {
            throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, e, "Enum-Klasse "
                    + enumClassName + " nicht gefunden");
        }
    }
}
