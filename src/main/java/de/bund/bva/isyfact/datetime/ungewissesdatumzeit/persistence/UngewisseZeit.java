package de.bund.bva.isyfact.datetime.ungewissesdatumzeit.persistence;

import java.time.LocalTime;
import javax.persistence.Embeddable;

// TODO Verschieben nach de.bund.bva.isyfact.datetime.persistence
/**
 * {@link javax.persistence.Embeddable} zum Speichern einer {@link UngewisseZeit} in einer Entität.
 *
 * // TODO Generell: @author-Tags entfernen. Autorenschaft wird ab IF 1.4 über git bestimmt.
 * @author Björn Saxe, msg systems ag
 */
@Embeddable
public class UngewisseZeit {

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
