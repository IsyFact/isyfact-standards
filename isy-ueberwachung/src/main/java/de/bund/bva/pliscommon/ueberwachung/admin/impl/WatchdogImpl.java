/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.pliscommon.ueberwachung.admin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.ueberwachung.admin.Watchdog;
import de.bund.bva.pliscommon.ueberwachung.common.jmx.StatusMonitorMBean;
import de.bund.bva.pliscommon.ueberwachung.common.konstanten.EreignisSchluessel;

/**
 * Implementierung des Systemselbstests.
 *
 * @author Capgemini, Simon Spielmann
 * @version $Id: WatchdogImpl.java 137712 2015-05-28 07:31:54Z sdm_apheino $
 */
public abstract class WatchdogImpl implements Watchdog {

    /** Der Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(WatchdogImpl.class);

    /** Referenz auf die MBean des Watchdogs. */
    private StatusMonitorMBean watchdogMBean;

    /** ExecutorService zum Ausführen der Aufrufe auf den Futures. */
    private ExecutorService executor;

    private List<PruefRoutine> pruefRoutinen = new ArrayList<PruefRoutine>();

    /**
     * Dauer in Sekunden, die gewartet wird bis ein Timeout für einen Systemprüfungslauf erzeugt wird. Default
     * 30
     */
    private int watchDogTimeOut = 30;

    @Override
    public boolean pruefeSystem() {
        final String korrelationsid = UUID.randomUUID().toString();
        MdcHelper.pushKorrelationsId(korrelationsid);
        Future<Boolean> future = null;
        boolean erfolgreich = false;
        try {
            future = this.executor.submit(new Callable<Boolean>() {

                @Override
                public Boolean call() {
                    MdcHelper.pushKorrelationsId(korrelationsid);
                    boolean pruefungErfolgreich = true;
                    for (PruefRoutine pruefRoutine : WatchdogImpl.this.pruefRoutinen) {
                        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001,
                            "Führe Prüfung durch: {}", pruefRoutine.getBeschreibung());
                        try {
                            if (!pruefRoutine.getPruefung().call()) {
                                LOG.error(EreignisSchluessel.PLUEB00002, "Prüfung gescheitert: {}",
                                    pruefRoutine.getBeschreibung());
                                pruefungErfolgreich = false;
                            }
                        } catch (Throwable t) {
                            LOG.error(EreignisSchluessel.PLUEB00003, "Prüfung gescheitert: {}", t,
                                pruefRoutine.getBeschreibung());
                            pruefungErfolgreich = false;
                        }
                    }
                    MdcHelper.entferneKorrelationsId();
                    return pruefungErfolgreich;
                }
            });
            erfolgreich = future.get(this.watchDogTimeOut, TimeUnit.SECONDS);
        } catch (Throwable t) {
            LOG.warn(EreignisSchluessel.PLUEB00004, "Selbsttest fehlgeschlagen: ", t);
        } finally {
            if (erfolgreich) {
                LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Selbsttest erfolgreich.");
            }
            this.watchdogMBean.registrierePruefung(erfolgreich);
            try {
                if (future != null) {
                    future.cancel(true);
                }
            } catch (Throwable t) {
                LOG.debug("Abbrechen der Watchdogausführung gescheitert: {}", t.getMessage());
            }
            MdcHelper.entferneKorrelationsId();
        }

        return erfolgreich;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPruefung(String beschreibung, Callable<Boolean> pruefung) {
        this.pruefRoutinen.add(new PruefRoutine(beschreibung, pruefung));
    }

    @Required
    public void setWatchdogMBean(StatusMonitorMBean watchdogMBean) {
        this.watchdogMBean = watchdogMBean;
    }

    @Required
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void setWatchDogTimeOut(int watchDogTimeOut) {
        this.watchDogTimeOut = watchDogTimeOut;
    }

}