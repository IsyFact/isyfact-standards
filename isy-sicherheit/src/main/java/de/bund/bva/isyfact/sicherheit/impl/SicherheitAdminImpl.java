package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.sicherheit.SicherheitAdmin;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.accessmgr.AuthentifzierungErgebnis;

/**
 * Implementierung von SicherheitAdmin zur Überprüfung des Verbindungsaufbaus zum Access Manager.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class SicherheitAdminImpl<T extends AufrufKontext> implements SicherheitAdmin {

    /** Referenz auf den AccessManager für den Zugriff auf Rollen/Rechte der Benutzer. */
    private final AccessManager<T, AuthentifzierungErgebnis> accessManager;

    /**
     * Erzeugt ein SicherheitAdmin, das den übergebenen AccessManager verwendet.
     *
     * @param accessManager der {@link AccessManager}, welcher für das Auslesen der Rollen und Rechte zuständig ist
     */
    public SicherheitAdminImpl(AccessManager<T, AuthentifzierungErgebnis> accessManager) {
        this.accessManager = accessManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean pingAccessManager() {
        return this.accessManager.pingAccessManager();
    }
}
