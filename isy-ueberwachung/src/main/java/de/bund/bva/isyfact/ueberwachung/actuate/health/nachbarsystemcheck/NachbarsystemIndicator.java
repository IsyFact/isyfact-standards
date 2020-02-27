package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;


import java.util.ArrayList;
import java.util.List;

import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Der NachbarsystemIndicator überprüft für alle in den NachbarsystemConfigurationProperties konfigurierten
 * Nachbarn, ob sie erreichbar sind. Ist ein essentieller Nachbar nicht erreichbar, gibt der Indicator
 * den Status "OUT_OF_SERVICE" zurück, sonst "UP".
 */
public class NachbarsystemIndicator implements HealthIndicator {

    private NachbarsystemCheck nachbarsystemCheck;

    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    public NachbarsystemIndicator(
        NachbarsystemCheck nachbarsystemCheck,
        NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        this.nachbarsystemCheck = nachbarsystemCheck;
        this.nachbarsystemConfigurationProperties = nachbarsystemConfigurationProperties;
    }

    @Override
    public Health health() {
        //Checke Health aller Nachbarn
        List<Mono<NachbarsystemHealth>> healthresults = new ArrayList<>();
        for (Nachbarsystem nachbar : nachbarsystemConfigurationProperties.getNachbarsysteme().values()) {
            healthresults.add(nachbarsystemCheck.checkNachbarsystem(nachbar));
        }

        //Aggregiere Ergebnisse
        return Flux.merge(healthresults)
            .collectList()
            .map(this::aggregateHealth)
            .block();
    }

    //aggregiert die Ergebnisse der Nachbarn in das Ergebnis des Healthchecks
    private Health aggregateHealth(List<NachbarsystemHealth> healthresults) {
        Health.Builder healthAggregated = Health.up(); //Default Rückgabewert: Nachbarn sind up.
        for (NachbarsystemHealth nachbarHealth : healthresults) {
            Nachbarsystem nachbar = nachbarHealth.getNachbarsystem();
            //wenn ein essentielles System nicht verfügbar ist,
            //gibt der NachbarsystemIndikator "Out of Service" zurück
            healthAggregated.withDetail(nachbar.getSystemname(), nachbarHealth);
            if (nachbar.isEssentiell() && !Status.UP.equals(nachbarHealth.getStatus())) {
                healthAggregated.status(Status.OUT_OF_SERVICE);
            }
        }
        return healthAggregated.build();

    }
}
