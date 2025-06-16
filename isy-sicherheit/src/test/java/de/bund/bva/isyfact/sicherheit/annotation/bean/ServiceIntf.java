package de.bund.bva.isyfact.sicherheit.annotation.bean;

import de.bund.bva.isyfact.sicherheit.annotation.Gesichert;

/**
 * Schnittstelle einer Servicekomponente zum Testen.
 *
 */
public interface ServiceIntf {

    void gesichertDurch_Nichts();

    void gesichertDurch_RechtA();

    void gesichertDurch_RechtB();

    void gesichertDurch_RechtAundB();

    void gesichertDurch_leeresRecht();

    void nichtGesichert();

    @Gesichert("Recht_A")
    void gesichertAmInterface();
}