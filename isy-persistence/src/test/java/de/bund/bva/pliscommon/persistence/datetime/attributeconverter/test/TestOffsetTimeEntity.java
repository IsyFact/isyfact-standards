package de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test;

import java.time.Duration;
import java.time.OffsetTime;
import javax.persistence.Entity;

@Entity
public class TestOffsetTimeEntity extends AbstractTestEntity {

    private OffsetTime offsetTime;

    public OffsetTime getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(OffsetTime offsetTime) {
        this.offsetTime = offsetTime;
    }
}
