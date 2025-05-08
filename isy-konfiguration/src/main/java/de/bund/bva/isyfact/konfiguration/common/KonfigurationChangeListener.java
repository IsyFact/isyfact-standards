package de.bund.bva.isyfact.konfiguration.common;

import java.util.Set;

/**
 * Interface für Listener, die über Konfigurationsänderungen informiert werden wollen.
 * 
 * @see ReloadableKonfiguration
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public interface KonfigurationChangeListener {
    
    /**
     * Diese Methode wird aufgerufen, wenn die Konfiguration geändert wurde.
     * 
     * @param changedKeys Liste der Konfigurationsschlüssel, derren Werte sich geändert haben.
     */
    public void onKonfigurationChanged(Set<String> changedKeys);

}
