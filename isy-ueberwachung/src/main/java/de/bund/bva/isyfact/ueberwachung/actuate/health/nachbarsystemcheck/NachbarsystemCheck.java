package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface NachbarsystemCheck {
    Mono<NachbarsystemHealth> checkNachbarsystem(Nachbarsystem nachbarsystem);
}
