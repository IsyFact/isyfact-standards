package de.bund.bva.pliscommon.persistence.datetime.attributeconverter;

import java.time.Period;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link Period}.
 * <p>
 * Das Speichern erfolgt in der ISO-8601-Darstellung als String.
 *
 */
@Converter(autoApply = true)
public class PeriodAttributeConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period period) {
        return Objects.toString(period, null);
    }

    @Override
    public Period convertToEntityAttribute(String aString) {
        if (aString == null) {
            return null;
        }

        return Period.parse(aString);
    }
}
