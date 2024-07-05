package de.bund.bva.isyfact.persistence.datetime.attributeconverter.test;

import java.time.Period;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestPeriodEntity {

    @Id
    private long id;

    private Period period;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
