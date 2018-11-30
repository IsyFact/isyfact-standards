package de.bund.bva.isyfact.persistence.datetime.attributeconverter.test;

import java.time.Duration;
import javax.persistence.Entity;

@Entity
public class TestDurationEntity extends AbstractTestEntity {

    private Duration duration;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
