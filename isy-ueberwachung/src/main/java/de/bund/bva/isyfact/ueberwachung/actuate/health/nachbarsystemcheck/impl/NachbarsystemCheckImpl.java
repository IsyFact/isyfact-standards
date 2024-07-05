package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import java.net.URI;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;

/**
 * Implementierung des NachbarsystemCheck.
 */
public class NachbarsystemCheckImpl implements NachbarsystemCheck {

    /**
     * Logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(NachbarsystemCheckImpl.class);

    /**
     * RestTemplate used for Querying.
     */
    private final RestTemplate restTemplate;


    public NachbarsystemCheckImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public NachbarsystemHealth checkNachbarsystem(@NotNull Nachbarsystem nachbarsystem) {
        NachbarsystemHealth health = check(nachbarsystem);
        health.setNachbarsystem(nachbarsystem);
        logHealth(health);
        return health;
    }

    private void logHealth(NachbarsystemHealth health) {
        Nachbarsystem nachbarsystem = health.getNachbarsystem();
        //Log Status UP in DEBUG
        if (Status.UP.equals(health.getStatus())) {
            LOG.debug("Nachbarsystem verfügbar: {}", nachbarsystem.getSystemname());
            return;
        }

        //Logging, if status is not "UP"
        if (nachbarsystem.isEssentiell()) {
            LOG.error(EreignisSchluessel.NACHBARSYSTEM_ESSENTIELL_NICHT_ERREICHBAR,
                "Essentielles Nachbarsystem {} nicht erreicht. Status: {}",
                nachbarsystem.getSystemname(), health.getStatus());
        } else {
            LOG.warn(EreignisSchluessel.NACHBARSYSTEM_NICHT_ESSENTIELL_NICHT_ERREICHBAR,
                "Nicht-essentielles Nachbarsystem {} nicht erreicht. Status: {}",
                nachbarsystem.getSystemname(), health.getStatus());
        }
    }

    private NachbarsystemHealth check(@NotNull Nachbarsystem nachbarsystem) {
        NachbarsystemHealth health = null;
        URI healthUri = nachbarsystem.getHealthEndpoint();

        try {
            ResponseEntity<NachbarsystemHealth> responseEntity =
                restTemplate.getForEntity(healthUri, NachbarsystemHealth.class);
            health = responseEntity.getBody();
        } catch (Exception e) {
            //If an Exception occured, log it, no further handling.
            LOG.error(EreignisSchluessel.NACHBARSYSTEM_ANFRAGEFEHLER,
                "Bei der Anfrage des Nachbarsystems {} ist ein Fehler aufgetreten: {}",
                e, nachbarsystem.getSystemname(), e.getMessage()
            );
        }

        //if the health endpoint couldn't be read (health empty) or an exception occured,
        //default assumed health for the neighbor will be "DOWN"
        if (health == null) {
            health = createDefaultHealthDown();
        }
        return health;
    }

    // default Health if the Health is Empty or an Error occured
    private NachbarsystemHealth createDefaultHealthDown() {
        NachbarsystemHealth health = new NachbarsystemHealth();
        health.setStatus(Status.DOWN);
        health.getDetails().put("info", "Fehler bei Statusabfrage");
        return health;
    }

}
