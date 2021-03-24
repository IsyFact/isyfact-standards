package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;

/**
 * Health-Überprüfung eines Nachbarsystems.
 */
@FunctionalInterface
public interface NachbarsystemCheck {

    /**
     * Health-Überprüfung eines Nachbarsystems.
     * Der Health-Endpoint des Nachbarsystems wird abgefragt
     * und das Ergebnis in ein NachbarsystemHealth-Objekt gekapselt.
     *
     * @param nachbarsystem das zu überprüfende Nachbarsystem
     * @return Ergebnis der Überprüfung
     */
    NachbarsystemHealth checkNachbarsystem(Nachbarsystem nachbarsystem);

}
