package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.cxf.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest;

import ch.qos.logback.classic.Level;

/**
 * Testet den {@link IsyLoggingRestInInterceptor} als Requestor.
 */
public class IsyLoggingRestInInterceptorRequestorTest extends AbstractInterceptorTest {

    @InjectMocks
    private IsyLoggingRestInInterceptor interceptor;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setLoggingLevel(Level.INFO);
        setRequestor(true);
    }

    /**
     * Die Aufrufdauer und das aufgerufenes System werden geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testAufrufdauerUndAufgerufenesSystemGeloggt() throws Exception {
        long sendezeitpunkt = 100L;
        long expectedDauer = 90L;
        String expectedRemote = "Nachbarsystem";
        this.message.getExchange().put(KonstantenRest.KEY_SENDEZEITPUNKT, sendezeitpunkt);
        this.message.getExchange().put(KonstantenRest.KEY_SYSTEMNAME_REMOTE, expectedRemote);

        when(this.logHelper.ermittleAktuellenZeitpunkt()).thenReturn(sendezeitpunkt + expectedDauer);

        this.interceptor.handleMessage(this.message);
        verify(this.logHelper, times(1)).logDauerAufruf(any(), eq(expectedRemote), eq(expectedDauer), any());
    }

    /**
     * Eine gespeicherte Nachricht wird geloggt, wenn ein interner Serverfehler vorliegt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testGespeicherteNachrichtGeloggt() throws Exception {
        IsyLogger outLogger = IsyLoggerFactory.getLogger(IsyLoggingRestOutInterceptor.class);
        storeLogMessage(outLogger, "test");
        this.message.put(Message.RESPONSE_CODE, 500);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logBodyOutForce(eq(outLogger), eq("test"), any());
    }

    /**
     * Eine gespeicherte Nachricht wird nicht geloggt, wenn kein interner Serverfehler vorliegt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testGespeicherteNachrichtNichtGeloggt() throws Exception {
        IsyLogger outLogger = IsyLoggerFactory.getLogger(IsyLoggingRestOutInterceptor.class);
        storeLogMessage(outLogger, "test");
        this.message.put(Message.RESPONSE_CODE, 200);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logBodyOutForce(any(), any(), any());
    }

    /**
     * Falls ein interner Serverfehler vorliegt, aber keine Nachricht zwischengespeichert ist, entsteht keine
     * Exception.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testKeineGespeicherteNachrichtBeiFehler() throws Exception {
        this.message.put(Message.RESPONSE_CODE, 500);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logBodyOutForce(any(), any(), any());
    }
}
