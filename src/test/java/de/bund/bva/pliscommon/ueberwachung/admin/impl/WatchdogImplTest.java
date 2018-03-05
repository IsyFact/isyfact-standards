package de.bund.bva.pliscommon.ueberwachung.admin.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.bund.bva.pliscommon.ueberwachung.common.jmx.StatusMonitorMBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WatchdogImplTest {

    @Mock
    StatusMonitorMBean statusMonitorMBean;

    @Mock
    private Callable<Boolean> pruefung1, pruefung2;

    @Test
    public void pruefeSystemErfolgreich() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        when(pruefung1.call()).thenReturn(true);
        when(pruefung2.call()).thenReturn(true);

        WatchdogImpl watchdog = new WatchdogTestImpl();

        watchdog.setExecutor(executorService);
        watchdog.setWatchdogMBean(statusMonitorMBean);

        watchdog.addPruefung("pruefung1", pruefung1);
        watchdog.addPruefung("pruefung2", pruefung2);

        assertTrue(watchdog.pruefeSystem());

        verify(pruefung1, times(1)).call();
        verify(pruefung2, times(1)).call();

        verify(statusMonitorMBean, times(1)).registrierePruefung(true);
    }

    @Test
    public void pruefeSystemNichtErfolgreich() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        when(pruefung1.call()).thenReturn(true);
        when(pruefung2.call()).thenReturn(false);

        WatchdogImpl watchdog = new WatchdogTestImpl();

        watchdog.setExecutor(executorService);
        watchdog.setWatchdogMBean(statusMonitorMBean);

        watchdog.addPruefung("pruefung1", pruefung1);
        watchdog.addPruefung("pruefung2", pruefung2);

        assertFalse(watchdog.pruefeSystem());

        verify(pruefung1, times(1)).call();
        verify(pruefung2, times(1)).call();

        verify(statusMonitorMBean, times(1)).registrierePruefung(false);
    }

    @Test
    public void pruefeSystemNichtErfolgreichMitException() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        when(pruefung1.call()).thenReturn(true);
        when(pruefung2.call()).thenThrow(new Exception());

        WatchdogImpl watchdog = new WatchdogTestImpl();

        watchdog.setExecutor(executorService);
        watchdog.setWatchdogMBean(statusMonitorMBean);

        watchdog.addPruefung("pruefung1", pruefung1);
        watchdog.addPruefung("pruefung2", pruefung2);

        assertFalse(watchdog.pruefeSystem());

        verify(pruefung1, times(1)).call();
        verify(pruefung2, times(1)).call();

        verify(statusMonitorMBean, times(1)).registrierePruefung(false);
    }

    @Test
    public void pruefeSystemNichtErfolgreichMitTimeout() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        when(pruefung1.call()).thenReturn(true);
        when(pruefung2.call()).thenAnswer(new Answer<Boolean>() {
            public Boolean answer(InvocationOnMock invocation) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    // leer
                }
                return true;
            }
        });

        WatchdogImpl watchdog = new WatchdogTestImpl();

        watchdog.setExecutor(executorService);
        watchdog.setWatchdogMBean(statusMonitorMBean);
        watchdog.setWatchDogTimeOut(1);

        watchdog.addPruefung("pruefung1", pruefung1);
        watchdog.addPruefung("pruefung2", pruefung2);

        assertFalse(watchdog.pruefeSystem());

        verify(pruefung1, times(1)).call();
        verify(pruefung2, times(1)).call();

        verify(statusMonitorMBean, times(1)).registrierePruefung(false);
    }

    private class WatchdogTestImpl extends WatchdogImpl {

    }
}