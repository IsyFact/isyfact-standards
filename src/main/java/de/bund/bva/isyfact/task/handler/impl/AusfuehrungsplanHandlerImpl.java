package de.bund.bva.isyfact.task.handler.impl;

import de.bund.bva.isyfact.task.handler.AusfuehrungsplanHandler;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;

import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.AUSFUEHRUNGSPLAN;
import static de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel.PRAEFIX;

/**
 * Der AusfuehrungsplanHandler liefert den Ausfuehrungsplan für einen TaskRunner.
 *
 * @author Alexander Salvanos, msg systems ag
 *
 */
public class AusfuehrungsplanHandlerImpl implements AusfuehrungsplanHandler {

    /**
     *
     * @param id
     * @param konfiguration
     * @return
     */
    public synchronized Ausfuehrungsplan getAusfuehrungsplan(String id, Konfiguration konfiguration) {
        String ausfuehrungsplan = konfiguration.getAsString(PRAEFIX + id + AUSFUEHRUNGSPLAN);
        return ausfuehrungsplan == null ? null : Ausfuehrungsplan.valueOf(ausfuehrungsplan);
    }
}
