package de.bund.bva.isyfact.konfiguration.common.impl;

import de.bund.bva.isyfact.konfiguration.common.Benutzerkonfiguration;

/**
 * Abstrakte Basisklasse für {@link Benutzerkonfiguration Benutzerkonfigurations}-Implementierungen, die alle
 * Typkonvertierungen erledigt, das Setzen von Konfigurationswerten aus einem Konfigurationsspeicher jedoch an
 * eine abstrakte Methode delegiert.
 * 
 */
public abstract class AbstractBenutzerkonfiguration extends AbstractKonfiguration implements
    Benutzerkonfiguration {

    /**
     * {@inheritDoc}
     */
    protected abstract boolean containsKey(String schluessel);

    /**
     * {@inheritDoc}
     */
    protected abstract String getValue(String schluessel);

    /**
     * {@inheritDoc}
     */
    public abstract void setValue(String schluessel, String wert);

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, int wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, long wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, double wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String schluessel, boolean wert) {
        setValue(schluessel, String.valueOf(wert));
    }

    /**
     * {@inheritDoc}
     */
    public abstract boolean removeValue(String schluessel);

}
