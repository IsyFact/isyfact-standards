package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link OffsetDateTime}.
 * <p>
 * Das Speichern erfolgt in der ISO-8601-Darstellung als String.
 *
 * @author Björn Saxe, msg systems ag
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
