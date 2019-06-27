package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_EMPFANGSZEITPUNKT;
import static de.bund.bva.isyfact.serviceapi.common.konstanten.KonstantenRest.KEY_SYSTEMNAME;
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

import ch.qos.logback.classic.Level;

/**
 * Testet den {@link IsyLoggingRestOutInterceptor} als Requestee.
 */
public class IsyLoggingRestOutInterceptorRequesteeTest extends AbstractInterceptorTest {

    @InjectMocks
    private IsyLoggingRestOutInterceptor interceptor;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        setLoggingLevel(Level.INFO);
        setRequestor(false);
    }

    /**
     * Die Verarbeitungsdauer und das aufrufendes System werden geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testVerarbeitungsdauerUndAufrufendesSystemGeloggt() throws Exception {
        long empfangszeitpunkt = 100L;
        long expectedDauer = 90L;
        String expectedSystem = "MyService";
        this.message.getExchange().put(KEY_EMPFANGSZEITPUNKT, empfangszeitpunkt);
        this.message.getExchange().put(KEY_SYSTEMNAME, expectedSystem);
        when(this.logHelper.ermittleAktuellenZeitpunkt()).thenReturn(empfangszeitpunkt + expectedDauer);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logDauerVerarbeitung(any(), eq(expectedSystem), eq(expectedDauer),
            eq(this.message));
    }

    /**
     * Eine gespeicherte Nachricht wird geloggt, wenn ein interner Serverfehler vorliegt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testGespeicherteNachrichtBeiInternerFehlerGeloggt() throws Exception {
        IsyLogger inLogger = IsyLoggerFactory.getLogger(IsyLoggingRestInInterceptor.class);
        storeLogMessage(inLogger, "test");
        this.message.put(Message.RESPONSE_CODE, 500);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logBodyInForce(eq(inLogger), eq("test"), any());
    }

    /**
     * Eine gespeicherte Nachricht wird nicht geloggt, wenn kein interner Serverfehler vorliegt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testGespeicherteNachrichtBeiOkayNichtGeloggt() throws Exception {
        IsyLogger inLogger = IsyLoggerFactory.getLogger(IsyLoggingRestInInterceptor.class);
        storeLogMessage(inLogger, "test");
        this.message.put(Message.RESPONSE_CODE, 200);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logBodyInForce(any(), any(), any());
    }

    /**
     * Falls ein interner Serverfehler vorliegt, aber keine Nachricht zwischengespeichert ist, entsteht keine
     * Exception.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testOhneGespeicherteNachrichtBeiInternerFehlerKeineException() throws Exception {
        this.message.put(Message.RESPONSE_CODE, 500);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logBodyInForce(any(), any(), any());
    }
}
