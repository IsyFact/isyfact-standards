package de.bund.bva.isyfact.konfiguration.common;

/**
 * Bietet Zugriff auf die Benutzerkonfiguration des aktuellen Benutzers.
 * <p>
 * The module isy-konfiguration is deprecated and will be removed in a future release.
 * Please use the built-in mechanism of the springframework instead.
 * <p>
 * https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
 *
 * @deprecated since IsyFact 3.1.0
 */
@Deprecated
public interface Benutzerkonfiguration extends Konfiguration {

    /**
     * Setzt einen String-Wert in der Konfiguration des aktuellen Benutzers.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @param wert
     *            der zu setzende Wert
     */
    public void setValue(String schluessel, String wert);

    /**
     * Setzt einen Integer-Wert in der Konfiguration des aktuellen Benutzers.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @param wert
     *            der zu setzende Wert
     */
    public void setValue(String schluessel, int wert);

    /**
     * Setzt einen Long-Wert in der Konfiguration des aktuellen Benutzers.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @param wert
     *            der zu setzende Wert
     */
    public void setValue(String schluessel, long wert);

    /**
     * Setzt einen Double-Wert in der Konfiguration des aktuellen Benutzers.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @param wert
     *            der zu setzende Wert
     */
    public void setValue(String schluessel, double wert);

    /**
     * Setzt einen Boolean-Wert in der Konfiguration des aktuellen Benutzers.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @param wert
     *            der zu setzende Wert
     */
    public void setValue(String schluessel, boolean wert);

    /**
     * Entfernt den gegebenen Konfigurationsschlüssel aus der Konfiguration des aktuellen Benutzers.
     * 
     * @param schluessel
     *            der Name des Konfigurationsparameters
     * @return {@code true}, wenn der Konfigurationsschlüssel entfernt wurde, {@code false} wenn er nicht
     *         vorhanden war.
     */
    public boolean removeValue(String schluessel);
}
