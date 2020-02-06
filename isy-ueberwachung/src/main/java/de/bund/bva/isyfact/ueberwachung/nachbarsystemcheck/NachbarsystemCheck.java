package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck;

import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface NachbarsystemCheck {
    Mono<Health> checkNachbarsystem(Nachbarsystem nachbarsystem);
}
