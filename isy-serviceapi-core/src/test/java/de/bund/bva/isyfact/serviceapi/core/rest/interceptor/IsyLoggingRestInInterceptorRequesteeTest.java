package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.cxf.message.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import de.bund.bva.isyfact.logging.util.LogHelperRest;
import de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest;
import de.bund.bva.isyfact.serviceapi.common.util.IsyHttpUtils;

import ch.qos.logback.classic.Level;

/**
 * Testet den {@link IsyLoggingRestInInterceptor} als Requestee.
 */
public class IsyLoggingRestInInterceptorRequesteeTest extends AbstractInterceptorTest {

    @InjectMocks
    private IsyLoggingRestInInterceptor interceptor;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setLoggingLevel(Level.INFO);
        setRequestor(false);
    }

    /**
     * Das aufrufende System wird geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testAufrufendesSystemVorhanden() throws Exception {
        IsyHttpUtils.setProtocolHeader(this.message, KonstantenRest.KEY_SYSTEMNAME, "test");
        String headerEntry = KonstantenRest.KEY_SYSTEMNAME + "=[test]";

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logHeaderIn(any(), contains(headerEntry), any());
    }

    /**
     * Das aufrufende System und der Erhaltszeitpunkt des Requests werden im Exchange gespeichert.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testExchangeMitStartzeitpunktUndAufrufendesSystem() throws Exception {
        String aufrufendesSystem = "Selbstreferenz";
        IsyHttpUtils.setProtocolHeader(this.message, KonstantenRest.KEY_SYSTEMNAME, aufrufendesSystem);
        long expectedEmpfangszeitpunkt = 100L;
        when(this.logHelper.ermittleAktuellenZeitpunkt()).thenReturn(expectedEmpfangszeitpunkt);

        this.interceptor.handleMessage(this.message);

        Exchange ex = this.message.getExchange();
        long actualEmpfangszeitpunkt = (long) ex.get(KonstantenRest.KEY_EMPFANGSZEITPUNKT);

        assertThat(actualEmpfangszeitpunkt, is(expectedEmpfangszeitpunkt));
        assertThat(ex.get(KonstantenRest.KEY_SYSTEMNAME), is(aufrufendesSystem));
        verify(this.logHelper, never()).logDauerAufruf(any(), any(), any(Long.class), any());
        verify(this.logHelper, never()).logDauerVerarbeitung(any(), any(), any(Long.class), any());
    }

    /**
     * Bei der Konfiguration {@link LogHelperRest#setLoggeDatenBeiServerFehler(boolean)} wird die Lognachricht
     * des Body zwischengespeichert, nicht direkt geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testLognachrichtWirdZwischengespeichert() throws Exception {
        loggeNurBeiException();

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logBodyIn(any(), any(), any());
        verify(this.logHelper, never()).logBodyInForce(any(), any(), any());
        assertHasStoredLogMessage();
    }
}
