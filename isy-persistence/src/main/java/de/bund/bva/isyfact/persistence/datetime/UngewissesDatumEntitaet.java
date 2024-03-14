package de.bund.bva.isyfact.persistence.datetime;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Embeddable;

import de.bund.bva.isyfact.datetime.core.UngewissesDatum;

/**
 * {@link jakarta.persistence.Embeddable} to store {@link UngewissesDatumEntitaet} in entity.
 *
 */
@Embeddable
public class UngewissesDatumEntitaet implements Serializable {

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

    /**
     * Create {@link UngewissesDatum} from this objekt.
     * @return
     *      {@link UngewissesDatum} with data from {@link UngewissesDatumEntitaet}
     */
    public UngewissesDatum toUngewissesDatum() {
        return UngewissesDatum.of(getAnfang(), getEnde());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UngewissesDatumEntitaet that = (UngewissesDatumEntitaet) o;
        return Objects.equals(anfang, that.anfang) &&
                Objects.equals(ende, that.ende);
    }

    @Override
    public int hashCode() {
        return Objects.hash(anfang, ende);
    }
}
