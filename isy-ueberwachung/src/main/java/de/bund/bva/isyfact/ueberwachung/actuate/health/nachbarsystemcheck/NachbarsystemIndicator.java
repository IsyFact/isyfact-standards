package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;

/**
 * Der NachbarsystemIndicator überprüft für alle in den NachbarsystemConfigurationProperties konfigurierten
 * Nachbarn, ob sie erreichbar sind. Ist ein essentieller Nachbar nicht erreichbar, gibt der Indicator
 * den Status "DOWN" zurück, sonst "UP".
 */
public class NachbarsystemIndicator implements HealthIndicator {

    /**
     * Bean zur Dürchführung des Checks.
     */
    private NachbarsystemCheck nachbarsystemCheck;

    /**
     * Bean mit den konfigurierten Nachbarsystemen.
     */
    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;


    public NachbarsystemIndicator(NachbarsystemCheck nachbarsystemCheck,
                                  NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        this.nachbarsystemCheck = nachbarsystemCheck;
        this.nachbarsystemConfigurationProperties = nachbarsystemConfigurationProperties;
    }

    @Override
    public Health health() {
        //Checke Health aller Nachbarn
        List<NachbarsystemHealth> healthresults = new ArrayList<>();
        for (Nachbarsystem nachbar : nachbarsystemConfigurationProperties.getNachbarsysteme().values()) {
            healthresults.add(nachbarsystemCheck.checkNachbarsystem(nachbar));
        }
        //Aggregiere Ergebnisse
        return aggregateHealth(healthresults);
    }

    /**
     * Aggregiert die Ergebnisse der Nachbarn in das Ergebnis des Healthchecks.
     */
    private Health aggregateHealth(List<NachbarsystemHealth> healthresults) {
        Health.Builder healthAggregated = Health.up(); //Default Rückgabewert: Nachbarn sind up.
        for (NachbarsystemHealth nachbarHealth : healthresults) {
            Nachbarsystem nachbar = nachbarHealth.getNachbarsystem();
            //wenn ein essentielles System nicht verfügbar ist,
            //gibt der NachbarsystemIndikator "DOWN" zurück
            healthAggregated.withDetail(nachbar.getSystemname(), nachbarHealth);
            if (nachbar.isEssentiell() && !Status.UP.equals(nachbarHealth.getStatus())) {
                healthAggregated.status(Status.DOWN);
            }
        }
        return healthAggregated.build();
    }

}
