package de.bund.bva.isyfact.konfiguration.common;

/**
 * Interface für Konfigurationen, die Aktualisierungen unterstützen.
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public interface ReloadableKonfiguration extends Konfiguration {

    /**
     * Prüft ob eine der Konfiguration aktualisiert wurde. Dazu wird der Timestamp der Dateien verglichen.
     * Falls eine Datei aktualisiert wurde wird die Konfiguration aktualisiert und alle Listener
     * benachrichtigt.
     * 
     * @return <code>true</code> falls eine der Konfigurationsdateien aktualisiert wurde.
     */
    public boolean checkAndUpdate();

    /**
     * Registriert einen neuen Listener. Wenn die Instanz bereits registiert ist, wird sie nicht erneut
     * hinzugefügt um doppelte Benachrichtigungen zu vermeiden.
     * @param listener
     *            {@link KonfigurationChangeListener}, der über Änderungen der Konfigurationsparameter
     *            informiert werden möchte.
     */
    public void addKonfigurationChangeListener(KonfigurationChangeListener listener);

    /**
     * Entfernt einen Listener aus der Liste.
     * @param listener
     *            zu entfernender Listener.
     */
    public void removeKonfigurationChangeListener(KonfigurationChangeListener listener);

    /**
     * Überprüft, ob ein Listener bereits registriert wurde.
     * @param listener
     *            zu überprüfender Listener
     * @return <code>true</code>, wenn der Listener registriert ist. <code>false</code>, wenn der Listener
     *         nicht registriert ist.
     */
    public boolean hasKonfigurationChangeListener(KonfigurationChangeListener listener);

}
