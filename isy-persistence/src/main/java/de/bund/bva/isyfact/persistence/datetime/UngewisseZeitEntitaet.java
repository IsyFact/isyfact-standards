package de.bund.bva.isyfact.persistence.datetime;

import java.time.LocalTime;
import javax.persistence.Embeddable;

import de.bund.bva.isyfact.datetime.core.UngewisseZeit;

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

    /**
     * Erstellt ein {@link UngewisseZeit} aus diesem Objekt.
     *
     * @return
     *      ein {@link UngewisseZeit} mit den Daten der {@link UngewisseZeitEntitaet}
     */
    public UngewisseZeit toUngewisseZeit() {
        return UngewisseZeit.of(getAnfang(), getEnde());
    }
}
