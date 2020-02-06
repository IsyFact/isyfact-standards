package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.impl;

import java.net.URI;

import javax.validation.constraints.NotNull;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.HealthTO;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import org.springframework.boot.actuate.health.Health;
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
    public Mono<Health> checkNachbarsystem(@NotNull Nachbarsystem nachbarsystem) {
        return checkUri(nachbarsystem.getHealthEndpoint())
            .retry(1) //retry once on failure
            .onErrorReturn(//map Error to default-Health "down"
                Health.down().withDetail("Error bei Statusabfrage", "").build())
            .doOnNext(health -> {
                logHealth(nachbarsystem, health);
            });
    }

    private void logHealth(Nachbarsystem nachbarsystem, Health health) {
        //Logge Status UP nur in DEBUG
        if (Status.UP.equals(health.getStatus())) {
            LOG.debug("Nachbarsystem verfügbar: {}", nachbarsystem.getSystemname());
            return;
        }

        //Logging, wenn Status "nicht UP" ist
        if (nachbarsystem.isEssentiell()) { //TODO schluessel erstellen?
            LOG.error("schluessel", "Essentielles Nachbarsystem {} nicht erreicht.",
                nachbarsystem.getSystemname());
        } else {
            LOG.warn("schluessel", "Nachbarsystem {} nicht erreicht.", nachbarsystem.getSystemname());
        }
    }

    private Mono<Health> checkUri(URI url) {
        return webClient
            .get()
            .uri(url)
            .retrieve()
            .bodyToMono(HealthTO.class)
            .map(HealthTO::convertToHealth);
    }
}
