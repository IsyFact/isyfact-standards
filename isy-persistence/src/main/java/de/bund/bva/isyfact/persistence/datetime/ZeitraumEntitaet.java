package de.bund.bva.isyfact.persistence.datetime;

import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import de.bund.bva.isyfact.datetime.core.Zeitraum;

/**
 * JPA-Entität zum Speichern von Zeiträumen.
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

    /**
     * Erstellt einen {@link Zeitraum} aus diesem Objekt.
     *
     * @return ein {@link Zeitraum} mit den der {@link ZeitraumEntitaet}
     */
    public Zeitraum toZeitraum() {
        if (isOhneDatum()) {
            return Zeitraum.of(getAnfang().toLocalTime(), getEnde().toLocalTime());
        } else {
            return Zeitraum.of(getAnfang(), getEnde());
        }
    }
}
