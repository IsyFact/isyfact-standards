package de.bund.bva.isyfact.persistence.datetime.attributeconverter.test;

import java.time.LocalDateTime;
import javax.persistence.Entity;

@Entity
public class TestLocalDateTimeEntity extends AbstractTestEntity {

    private LocalDateTime localDateTime;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
