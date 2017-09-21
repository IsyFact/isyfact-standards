package de.bund.bva.isyfact.datetime.ungewissesdatumzeit.persistence;

import java.time.LocalDate;
import javax.persistence.Embeddable;

// TODO Verschieben nach de.bund.bva.isyfact.datetime.persistence
// TODO Generell: Umbenennen in *Entitaet?
/**
 * {@link javax.persistence.Embeddable} zum Speichern eines {@link UngewissesDatum} in einer Entität.
 *
 * @author Björn Saxe, msg systems ag
 */
@Embeddable
public class UngewissesDatum {

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
