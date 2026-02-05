package de.bund.bva.isyfact.persistence.usertype;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import de.bund.bva.isyfact.persistence.annotation.EnumId;
import de.bund.bva.isyfact.persistence.exception.FehlerSchluessel;
import de.bund.bva.isyfact.persistence.exception.PersistenzException;

/**
 * An {@link UserType} for persistence of custom enum types, saved as strings (VARCHAR-fields in DB).
 * The getEnumID method of given enum class must be annotated with {@link EnumId}.
 */
public class EnumWithIdUserType extends AbstractImmutableStringUserType<Enum<?>> implements ParameterizedType {

    /** Enum class type. */
    private Class<Enum<?>> enumClass;

    /** Map string representation to enum values. */
    private final Map<String, Enum<?>> stringToEnum = new HashMap<>();

    /** The getEnumID method. */
    private Method idGetter;

    /**
     * Set enum type.
     *
     * @param enumClass enum type.
     */
    public void setEnumClass(Class<? extends Enum<?>> enumClass) {
        this.enumClass = (Class<Enum<?>>) enumClass;

        for (Method m : enumClass.getMethods()) {
            if (m.getAnnotation(EnumId.class) != null) {
                if (idGetter != null) {
                    throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION,
                        "Mehr als eine Methode in " + enumClass.getName() + " ist mit "
                            + EnumId.class.getSimpleName() + " annotiert.");
                }
                idGetter = m;
            }
        }
        if (idGetter == null) {
            throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, "Keine Methode in "
                + enumClass.getName() + " ist mit " + EnumId.class.getSimpleName() + " annotiert.");
        }

        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            String enumId = getEnumId(enumValue);
            Enum<?> oldValue = stringToEnum.put(enumId, enumValue);
            if (oldValue != null) {
                throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, "Im Enum "
                    + enumClass + " wird zweimal der Schlüssel '" + enumId + "' benutzt");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<Enum<?>> returnedClass() {
        return enumClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enum<?> convertStringToInstance(String value) {
        Enum<?> e = stringToEnum.get(value);
        if (e == null) {
            throw new PersistenzException(FehlerSchluessel.UNBEKANNTER_STRING, value, enumClass.getName());
        }
        return e;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertInstanceToString(Enum<?> value) {
        String enumId = getEnumId(value);
        if (enumId == null) {
            throw new PersistenzException(FehlerSchluessel.UNBEKANNTE_AUSPRAEGUNG, value.toString(),
                enumClass.getName());
        }
        return enumId;
    }

    /**
     * Get enum value ID.
     * 
     * @param enumValue given enum value.
     * @return enum ID.
     */
    private String getEnumId(Enum<?> enumValue) {
        String enumId;
        try {
            enumId = (String) idGetter.invoke(enumValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return enumId;
    }

    /**
     * {@inheritDoc}
     */
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClass");
        if (enumClassName == null) {
            throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION,
                "Parameter 'enumClass' fehlt für " + EnumWithIdUserType.class.getSimpleName());
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
            throw new PersistenzException(FehlerSchluessel.FALSCHE_ENUM_KONFIGURATION, "Enum-Klasse "
                + enumClassName + " nicht gefunden");
        }
    }
}
