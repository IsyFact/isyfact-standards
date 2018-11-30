package de.bund.bva.isyfact.persistence.datetime.attributeconverter;

import java.time.OffsetTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link OffsetTime}.
 * <p>
 * Das Speichern erfolgt in der ISO-8601-Darstellung als String.
 *
 */
@Converter(autoApply = true)
public class OffsetTimeAttributeConverter implements AttributeConverter<OffsetTime, String> {

    @Override
    public String convertToDatabaseColumn(OffsetTime offsetTime) {
        return offsetTime == null ? null : offsetTime.toString();
    }

    @Override
    public OffsetTime convertToEntityAttribute(String s) {
        return s == null ? null : OffsetTime.parse(s);
    }
}
