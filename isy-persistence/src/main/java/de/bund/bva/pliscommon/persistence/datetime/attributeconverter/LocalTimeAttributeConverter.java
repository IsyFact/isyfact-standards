package de.bund.bva.pliscommon.persistence.datetime.attributeconverter;

import java.sql.Time;
import java.time.LocalTime;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link LocalTime}.
 *
 */
@Deprecated
@Converter//(autoApply = true) // Auskommentiert damit dieser nicht mehr angewandt wird.
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }

        return Time.valueOf(localTime);
    }

    @Override
    public LocalTime convertToEntityAttribute(Time time) {
        if (time == null) {
            return null;
        }

        return time.toLocalTime();
    }
}
