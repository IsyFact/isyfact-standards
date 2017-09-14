package de.bund.bva.isyfact.datetime.ungewissesdatumzeit.persistence;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 * {@link javax.persistence.Embeddable} zum Speichern einer {@link UngewisseZeit} in einer Entität.
 *
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
