package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.time.ZonedDateTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link ZonedDateTime}.
 * <p>
 * Das Speichern erfolgt in der ISO-8601-Darstellung als String.
 *
 */
@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverter implements AttributeConverter<ZonedDateTime, String> {

    @Override
    public String convertToDatabaseColumn(ZonedDateTime dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(String s) {
        return s == null ? null : ZonedDateTime.parse(s);
    }
}
