package de.bund.bva.isyfact.persistence.datetime.attributeconverter.test;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class TestLocalDateEntity extends AbstractTestEntity {

    private LocalDate localDate;

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}
