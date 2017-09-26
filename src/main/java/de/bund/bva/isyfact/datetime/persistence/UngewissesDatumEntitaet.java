package de.bund.bva.isyfact.datetime.persistence;

import java.time.LocalDate;
import javax.persistence.Embeddable;

/**
 * {@link javax.persistence.Embeddable} zum Speichern eines {@link UngewissesDatumEntitaet} in einer Entität.
 *
 */
@Embeddable
public class UngewissesDatumEntitaet {

    private LocalDate anfang;

    private LocalDate ende;

    public LocalDate getAnfang() {
        return anfang;
    }

    public void setAnfang(LocalDate anfang) {
        this.anfang = anfang;
    }

    public LocalDate getEnde() {
        return ende;
    }

    public void setEnde(LocalDate ende) {
        this.ende = ende;
    }
}
