package de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test;

import java.time.LocalTime;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestLocalTimeEntity extends AbstractTestEntity {

    private LocalTime localTime;

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }
}
