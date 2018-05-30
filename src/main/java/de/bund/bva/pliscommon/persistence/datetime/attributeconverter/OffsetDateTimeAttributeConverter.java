package de.bund.bva.pliscommon.persistence.datetime.attributeconverter;

import java.time.OffsetDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link OffsetDateTime}.
 * <p>
 * Das Speichern erfolgt in der ISO-8601-Darstellung als String.
 *
 */
@Converter(autoApply = true)
public class OffsetDateTimeAttributeConverter implements AttributeConverter<OffsetDateTime, String> {

    @Override
    public String convertToDatabaseColumn(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toString();
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(String s) {
        return s == null ? null : OffsetDateTime.parse(s);
    }
}
