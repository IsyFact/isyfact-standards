package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import java.net.URI;

import javax.validation.constraints.NotNull;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class NachbarsystemCheckImpl implements NachbarsystemCheck {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(NachbarsystemCheckImpl.class);

    /** WebClient zur Abfrage. */
    private WebClient webClient;

    public NachbarsystemCheckImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<NachbarsystemHealth> checkNachbarsystem(@NotNull Nachbarsystem nachbarsystem) {
        return checkUri(nachbarsystem.getHealthEndpoint())
            .retry(1)//retry once on failure
            .onErrorReturn(//map Error to default-Health "down"
                createDefaultHealth())
            .doOnNext(h -> h.setNachbarsystem(nachbarsystem))
            .doOnNext(this::logHealth);
    }

    private void logHealth(NachbarsystemHealth health) {
        Nachbarsystem nachbarsystem = health.getNachbarsystem();
        //Logge Status UP nur in DEBUG
        if (Status.UP.equals(health.getStatus())) {
            LOG.debug("Nachbarsystem verfügbar: {}", nachbarsystem.getSystemname());
            return;
        }

        //Logging, wenn Status "nicht UP" ist
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

    private Mono<NachbarsystemHealth> checkUri(URI url) {
        return webClient
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(NachbarsystemHealth.class);
    }

    private NachbarsystemHealth createDefaultHealth() {
        NachbarsystemHealth health = new NachbarsystemHealth();
        health.setStatus(Status.DOWN);
        health.getDetails().put("info", "Error bei Statusabfrage");
        return health;
    }
}
