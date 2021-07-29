package de.bund.bva.isyfact.serviceapi.core.rest.interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * Testet den {@link IsyLoggingRestInAuthInterceptor}.
 */
public class IsyLoggingRestInAuthInterceptorTest extends AbstractInterceptorTest {

    @Mock
    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    @InjectMocks
    private IsyLoggingRestInAuthInterceptor interceptor;

    private AufrufKontext aufrufKontext;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.aufrufKontext = new AufrufKontextImpl();
        this.aufrufKontext.setDurchfuehrenderBenutzerKennung("testA");
        this.aufrufKontext.setDurchfuehrendeBehoerde("testB");
        when(this.aufrufKontextVerwalter.getAufrufKontext()).thenReturn(this.aufrufKontext);
    }

    /**
     * Logge Authentifizierungsdaten und das Behördenkennzeichen.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testLogs() throws Exception {
        setLoggingLevel(Level.INFO);
        setRequestor(false);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, times(1)).logBenutzer(
            eq(IsyLoggerFactory.getLogger(IsyLoggingRestInAuthInterceptor.class)),
            eq(this.aufrufKontext.getDurchfuehrenderSachbearbeiterName()),
            eq(this.aufrufKontext.getDurchfuehrenderBenutzerKennung()),
            eq(this.aufrufKontext.getDurchfuehrenderBenutzerInterneKennung()),
            eq(this.aufrufKontext.getDurchfuehrendeBehoerde()));
    }

    /**
     * Unter {@link Level#INFO} wird nicht geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testUnterLevelInfoWirdNichtGeloggt() throws Exception {
        setLoggingLevel(Level.WARN);
        setRequestor(false);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logBenutzer(any(), any(), any(), any(), any());
    }

    /**
     * Als Requestor wird nicht geloggt.
     * @throws Exception
     *             Unbehandelte Exception.
     */
    @Test
    public void testAlsRequestorWirdNichtGeloggt() throws Exception {
        setLoggingLevel(Level.INFO);
        setRequestor(true);

        this.interceptor.handleMessage(this.message);

        verify(this.logHelper, never()).logBenutzer(any(), any(), any(), any(), any());
    }
}
