package de.bund.bva.isyfact.persistence.datetime;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Embeddable;

import de.bund.bva.isyfact.datetime.core.UngewisseZeit;

/**
 * {@link jakarta.persistence.Embeddable} to store {@link UngewisseZeitEntitaet} in entity.
 *
 */
@Embeddable
public class UngewisseZeitEntitaet implements Serializable {

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

    /**
     * Create {@link UngewisseZeit} from this Objekt.
     *
     * @return
     *      {@link UngewisseZeit} with data from {@link UngewisseZeitEntitaet}
     */
    public UngewisseZeit toUngewisseZeit() {
        return UngewisseZeit.of(getAnfang(), getEnde());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UngewisseZeitEntitaet)) {
            return false;
        }
        UngewisseZeitEntitaet that = (UngewisseZeitEntitaet) o;
        return Objects.equals(anfang, that.anfang) &&
                Objects.equals(ende, that.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anfang, ende);
    }
}
