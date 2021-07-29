package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import de.bund.bva.isyfact.logging.util.LogHelperRest;
import de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest;
import de.bund.bva.isyfact.serviceapi.common.util.IsyHttpUtils;

import ch.qos.logback.classic.Level;

/**
 * Testet den {@link IsyLoggingRestOutInterceptor} als Requestor.
 */
public class IsyLoggingRestOutInterceptorRequestorTest extends AbstractInterceptorTest {

    @InjectMocks
    private IsyLoggingRestOutInterceptor interceptor;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setLoggingLevel(Level.INFO);
        setRequestor(true);
    }

    /**
     * Es wird der Name des aufrufenden System in den Header geschrieben. Es wird das aufgerufene System in
     * den Exchange geschrieben. Es wird der Sendezeitpunkt in den Exchange geschrieben.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testSetAufrufendesSystemUndSendezeitpunkt() throws Exception {
        String expectedSystem = RandomStringUtils.randomAlphanumeric(10);
        String expectedRemote = RandomStringUtils.randomAlphanumeric(11);
        long expectedSendezeitpunkt = 100L;
        this.interceptor.setSystemname(expectedSystem);
        IsyHttpUtils.setProtocolHeader(this.message, KonstantenRest.KEY_SYSTEMNAME_REMOTE, expectedRemote);
        when(this.logHelper.ermittleAktuellenZeitpunkt()).thenReturn(expectedSendezeitpunkt);

        this.interceptor.handleMessage(this.message);

        String acutalSystem = IsyHttpUtils.getProtocolHeader(this.message, KonstantenRest.KEY_SYSTEMNAME);
        String actualRemote = (String) this.message.getExchange().get(KonstantenRest.KEY_SYSTEMNAME_REMOTE);
        long actualSendezeitpunkt = (long) this.message.getExchange().get(KonstantenRest.KEY_SENDEZEITPUNKT);

        assertThat(acutalSystem, is(expectedSystem));
        assertThat(actualRemote, is(expectedRemote));
        assertThat(actualSendezeitpunkt, is(expectedSendezeitpunkt));
    }

    /**
     * Bei der Konfiguration {@link LogHelperRest#setLoggeDatenBeiServerFehler(boolean)} wird die Lognachricht
     * des Body zwischengespeichert, nicht direkt geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testLognachrichtWirdZwischengespeichert() throws Exception {
        this.logHelper.setLoggeDaten(false);
        this.logHelper.setLoggeDatenBeiServerFehler(true);
        prepareOutputStreamContent();

        this.interceptor.handleMessage(this.message);
        closeOutputStreamContent();

        verify(this.logHelper, times(1)).logBodyOut(any(), any(), any());
        verify(this.logHelper, never()).logBodyOutForce(any(), any(), any());
        assertHasStoredLogMessage();
    }
}
