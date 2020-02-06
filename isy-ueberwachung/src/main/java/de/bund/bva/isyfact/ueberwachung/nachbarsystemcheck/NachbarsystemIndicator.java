package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck;


import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

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
        Health.Builder healthAggregated = Health.up(); //Default Rückgabewert: Nachbarn sind up.

        for (Nachbarsystem nachbar : nachbarsystemConfigurationProperties.getNachbarsysteme().values()) {

            Health nachbarHealth = nachbarsystemCheck.checkNachbarsystem(nachbar).block();
            //Asynchronität ist durch Cache gewährleistet, wir wollen für die Überprüfung explizit blocken

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
