package de.bund.bva.isyfact.persistence.datetime.attributeconverter.test;

import java.time.Period;
import javax.persistence.Entity;

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
