package de.bund.bva.isyfact.batchrahmen;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.isyfact.batchrahmen.sicherheit.AccessManagerStub;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungFehlgeschlagenException;

/**
 * Tests whether the reauthentication (token renewal) in Batchrahmen works correctly.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { AnwendungTestConfig.class, BatchrahmenTestConfig.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BatchrahmenReauthTest {

    @Autowired
    private Batchrahmen batchrahmen;

    @Autowired
    private AufrufKontextVerwalter<?> aufrufKontextVerwalter;

    @SpyBean
    private Sicherheit<?> sicherheit;

    @SpyBean
    private AccessManagerStub accessManager;

    @Test
    public void reauthGesichertBatch1() throws BatchAusfuehrungsException {
        batchrahmen.runBatch(new BatchKonfiguration(new String[] { "-start", "-cfg",
                "/resources/batch/gesicherter-test-batch-1-config.properties" }), mock(BatchErgebnisProtokoll.class));

        // called each time in @Gesichert
        verify(sicherheit, times(10000)).getBerechtigungsManager();
        // only called if AK is unauthenticated or during reauth
        // times: 1 (@Gesichert during step), 9999 (reauth before step), 1 (reauth before finalize)
        verify(sicherheit, times(10001)).getBerechtigungsManagerUndAuthentifiziere(any());
        verify(accessManager, times(10001)).authentifiziere(any());

        assertEquals("Token 10001", aufrufKontextVerwalter.getBearerToken());
    }

    @Test
    public void reauthGesichertBatch2() throws BatchAusfuehrungsException {
        batchrahmen.runBatch(new BatchKonfiguration(new String[] { "-start", "-cfg",
                "/resources/batch/gesicherter-test-batch2-1-config.properties" }), mock(BatchErgebnisProtokoll.class));

        // called each time in @Gesichert
        verify(sicherheit, times(1)).getBerechtigungsManager();
        // only called if AK is unauthenticated or during reauth
        // times: 1 (@Gesichert during init), 10000 (reauth before step), 1 (reauth before finalize)
        verify(sicherheit, times(10002)).getBerechtigungsManagerUndAuthentifiziere(any());
        verify(accessManager, times(10002)).authentifiziere(any());

        assertEquals("Token 10002", aufrufKontextVerwalter.getBearerToken());
    }

    @Test
    public void authWithInvalidCredentials() {
        assertThrows(AuthentifizierungFehlgeschlagenException.class, () ->
                batchrahmen.runBatch(new BatchKonfiguration(new String[] { "-start", "-cfg",
                        "/resources/batch/gesicherter-test-batch-2-config.properties" }), mock(BatchErgebnisProtokoll.class)));

        verify(sicherheit).getBerechtigungsManager();
        verify(sicherheit).getBerechtigungsManagerUndAuthentifiziere(any());
        verify(accessManager).authentifiziere(any());

        assertNull(aufrufKontextVerwalter.getBearerToken());
    }

    @Test
    public void noReauthIfDeactivated() throws BatchAusfuehrungsException {
        batchrahmen.runBatch(new BatchKonfiguration(new String[] { "-start", "-cfg",
                "/resources/batch/gesicherter-no-reauth-test-batch-1-config.properties" }), mock(BatchErgebnisProtokoll.class));

        // called each time in @Gesichert
        verify(sicherheit, times(10000)).getBerechtigungsManager();
        // only called once because reauth is deactivated
        verify(sicherheit, times(1)).getBerechtigungsManagerUndAuthentifiziere(any());
        verify(accessManager, times(1)).authentifiziere(any());

        assertEquals("Token 1", aufrufKontextVerwalter.getBearerToken());
    }

    @Test
    public void noReauthInBasicBatch() throws BatchAusfuehrungsException {
        batchrahmen.runBatch(new BatchKonfiguration(new String[] { "-start", "-cfg",
                "/resources/batch/basic-test-batch-1-config.properties" }), mock(BatchErgebnisProtokoll.class));

        verify(sicherheit, never()).getBerechtigungsManager();
        verify(sicherheit, never()).getBerechtigungsManagerUndAuthentifiziere(any());
        verify(accessManager, never()).authentifiziere(any());

        assertNull(aufrufKontextVerwalter.getBearerToken());
    }
}
