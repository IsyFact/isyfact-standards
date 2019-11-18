package de.bund.bva.pliscommon.ueberwachung.task;

import java.util.concurrent.Callable;

import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.pliscommon.ueberwachung.admin.Watchdog;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAdministrationWatchdogTask {

    @Test(expected = NullPointerException.class)
    public void testExecuteWatchdogNull() {
        AdministrationWatchdogTask administrationWatchdogTask = new AdministrationWatchdogTask(new TaskMonitor());
        administrationWatchdogTask.setAdministrationWatchdog(null);

        administrationWatchdogTask.execute();
    }

    @Test
    public void testExecuteMitWatchdog() {
        AdministrationWatchdogTask administrationWatchdogTask = new AdministrationWatchdogTask(new TaskMonitor());

        AdministrationWatchdog administrationWatchdog = new AdministrationWatchdog();
        administrationWatchdog.setGeprueft(false);
        administrationWatchdogTask.setAdministrationWatchdog(administrationWatchdog);

        assertFalse(administrationWatchdog.isGeprueft());

        administrationWatchdogTask.execute();

        assertTrue(administrationWatchdog.isGeprueft());
    }

    private class AdministrationWatchdog implements Watchdog {

        private boolean geprueft;

        public void setGeprueft(boolean geprueft) {
            this.geprueft = geprueft;
        }

        public boolean isGeprueft() {
            return geprueft;
        }

        @Override
        public boolean pruefeSystem() {
            geprueft = true;
            return false;
        }

        @Override
        public void addPruefung(String beschreibung, Callable<Boolean> pruefung) {

        }
    }
}
