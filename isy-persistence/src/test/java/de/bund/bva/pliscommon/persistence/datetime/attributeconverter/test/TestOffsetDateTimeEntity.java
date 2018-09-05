package de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test;

import java.time.OffsetDateTime;
import javax.persistence.Entity;

@Entity
public class TestOffsetDateTimeEntity extends AbstractTestEntity {

    private OffsetDateTime offsetDateTime;

    public OffsetDateTime getOffsetDateTime() {
        return offsetDateTime;
    }

    public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
        this.offsetDateTime = offsetDateTime;
    }
}
