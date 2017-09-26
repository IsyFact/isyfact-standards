package de.bund.bva.isyfact.datetime.persistence;

import java.time.LocalTime;
import javax.persistence.Embeddable;

/**
 * {@link javax.persistence.Embeddable} zum Speichern einer {@link UngewisseZeitEntitaet} in einer Entität.
 *
 */
@Embeddable
public class UngewisseZeitEntitaet {

    private LocalTime anfang;

    private LocalTime ende;

    public LocalTime getAnfang() {
        return anfang;
    }

    public void setAnfang(LocalTime anfang) {
        this.anfang = anfang;
    }

    public LocalTime getEnde() {
        return ende;
    }

    public void setEnde(LocalTime ende) {
        this.ende = ende;
    }
}
