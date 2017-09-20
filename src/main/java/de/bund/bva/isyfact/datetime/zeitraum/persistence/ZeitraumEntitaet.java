package de.bund.bva.isyfact.datetime.zeitraum.persistence;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import de.bund.bva.isyfact.datetime.zeitraum.core.Zeitraum;

// TODO Verschieben nach de.bund.bva.isyfact.datetime.persistence
/**
 * @author Björn Saxe, msg systems ag
 */
@Entity
public class ZeitraumEntitaet {

    @Id
    @GeneratedValue
    private long id;

    private ZonedDateTime anfang;

    private ZonedDateTime ende;

    private Boolean ohneDatum = false;

    public ZeitraumEntitaet() {
    }

    public ZeitraumEntitaet(ZonedDateTime anfang, ZonedDateTime ende, boolean ohneDatum) {
        this.anfang = anfang;
        this.ende = ende;
        this.ohneDatum = ohneDatum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ZonedDateTime getAnfang() {
        return anfang;
    }

    public void setAnfang(ZonedDateTime anfang) {
        this.anfang = anfang;
    }

    public ZonedDateTime getEnde() {
        return ende;
    }

    public void setEnde(ZonedDateTime ende) {
        this.ende = ende;
    }

    public boolean isOhneDatum() {
        return ohneDatum;
    }

    public void setOhneDatum(boolean ohneDatum) {
        this.ohneDatum = ohneDatum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZeitraumEntitaet)) {
            return false;
        }
        ZeitraumEntitaet zeitraumEntitaet = (ZeitraumEntitaet) o;
        return id == zeitraumEntitaet.id && ohneDatum == zeitraumEntitaet.ohneDatum && Objects
            .equals(anfang, zeitraumEntitaet.anfang) && Objects.equals(ende, zeitraumEntitaet.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, anfang, ende, ohneDatum);
    }

    public boolean equalsZeitraum(Zeitraum zeitraum) {
        if (ohneDatum && zeitraum.isOhneDatum()) {
            return anfang.toLocalTime().equals(zeitraum.getAnfangszeit()) && ende.toLocalTime()
                .equals(zeitraum.getEndzeit());
        } else if (!ohneDatum && !zeitraum.isOhneDatum()) {
            return anfang.equals(zeitraum.getAnfangsdatumzeit()) && ende.equals(zeitraum.getEndedatumzeit());
        } else {
            return false;
        }
    }
}
