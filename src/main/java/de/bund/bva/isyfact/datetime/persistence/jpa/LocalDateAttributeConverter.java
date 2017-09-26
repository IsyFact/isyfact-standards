package de.bund.bva.isyfact.datetime.persistence.jpa;

import java.sql.Date;
import java.time.LocalDate;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link LocalDate}.
 *
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }

        return Date.valueOf(localDate);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }
}
