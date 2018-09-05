package de.bund.bva.pliscommon.persistence.datetime.attributeconverter;

import java.time.Duration;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link javax.persistence.AttributeConverter} für {@link Duration}.
 * <p>
 * Gespeichert wird die Zahl der Nanosekunden von {@link Duration#toNanos()} als {@code long}.
 * <p>
 * Die maximal speicherbare {@link Duration} hat damit eine Länge von 106751 Tagen &asymp; 292.4 Jahre.
 *
 */
@Converter(autoApply = true)
public class DurationAttributeConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        if (duration == null) {
            return null;
        }

        return duration.toNanos();
    }

    @Override
    public Duration convertToEntityAttribute(Long aLong) {
        if (aLong == null) {
            return null;
        }

        return Duration.ofNanos(aLong);
    }
}
