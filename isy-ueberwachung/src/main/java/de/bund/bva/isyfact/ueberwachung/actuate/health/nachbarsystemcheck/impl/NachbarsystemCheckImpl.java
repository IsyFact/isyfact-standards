package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.impl;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.springframework.boot.actuate.health.Status;
import org.springframework.web.reactive.function.client.WebClient;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.NachbarsystemCheck;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.ueberwachung.config.NachbarsystemConfigurationProperties;

import reactor.core.publisher.Mono;

/**
 * Implementierung des NachbarsystemCheck. Die Abfrage erfolgt via Spring-WebClient.
 * Für den WebClient
 */
public class NachbarsystemCheckImpl implements NachbarsystemCheck {

    /**
     * Logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(NachbarsystemCheckImpl.class);

    /**
     * WebClient zur Abfrage.
     */
    private WebClient webClient;

    /**
     * Properties für die Abfrage (für Retries).
     */
    private NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties;

    public NachbarsystemCheckImpl(WebClient webClient,
                                  NachbarsystemConfigurationProperties nachbarsystemConfigurationProperties) {
        this.webClient = webClient;
        this.nachbarsystemConfigurationProperties = nachbarsystemConfigurationProperties;
    }

    @Override
    public NachbarsystemHealth checkNachbarsystem(@NotNull Nachbarsystem nachbarsystem) {
        return checkUri(nachbarsystem.getHealthEndpoint())
                .doOnNext(h -> h.setNachbarsystem(nachbarsystem))
                .doOnNext(this::logHealth)
                .block();
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

    private Mono<NachbarsystemHealth> checkUri(URI uri) {
        return webClient
                .get()
                .uri(uri)
                .exchange()
                .retry(nachbarsystemConfigurationProperties.getNachbarsystemCheck().getAnzahlRetries())
                .flatMap(clientResponse -> clientResponse.bodyToMono(NachbarsystemHealth.class))
                .defaultIfEmpty(createDefaultHealth())
                .onErrorReturn(createDefaultHealth());
    }

    private NachbarsystemHealth createDefaultHealth() {
        NachbarsystemHealth health = new NachbarsystemHealth();
        health.setStatus(Status.DOWN);
        health.getDetails().put("info", "Error bei Statusabfrage");
        return health;
    }

}
