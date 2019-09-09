package de.bund.bva.isyfact.persistence.datetime;

import java.time.LocalDate;
import javax.persistence.Embeddable;

import de.bund.bva.isyfact.datetime.core.UngewissesDatum;

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

    /**
     * Erstellt ein {@link UngewissesDatum} aus diesem Objekt.
     * @return ein {@link UngewissesDatum} mit den Daten der {@link UngewissesDatumEntitaet}
     */
    public UngewissesDatum toUngewissesDatum() {
        return UngewissesDatum.of(getAnfang(), getEnde());
    }
}
