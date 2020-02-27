package de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck;

import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.nachbarsystemcheck.model.Nachbarsystem;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface NachbarsystemCheck {
    Mono<NachbarsystemHealth> checkNachbarsystem(Nachbarsystem nachbarsystem);
}
