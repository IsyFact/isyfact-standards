package de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test;

import java.time.Period;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestPeriodEntity extends AbstractTestEntity {

    private Period period;

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
