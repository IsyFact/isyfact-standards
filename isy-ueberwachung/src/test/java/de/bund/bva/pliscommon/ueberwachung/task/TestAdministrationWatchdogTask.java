package de.bund.bva.pliscommon.ueberwachung.task;

import java.util.concurrent.Callable;

import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.pliscommon.ueberwachung.admin.Watchdog;
import org.junit.Test;

import static org.mockito.Mockito.*;

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

        Watchdog administrationWatchdog = mock(Watchdog.class);
        administrationWatchdogTask.setAdministrationWatchdog(administrationWatchdog);

        administrationWatchdogTask.execute();
        verify(administrationWatchdog, times(1)).pruefeSystem();
    }

}
