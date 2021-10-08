package de.bund.bva.isyfact.batchrahmen.batch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.isyfact.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.isyfact.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.isyfact.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
import de.bund.bva.isyfact.serviceapi.core.httpinvoker.IsyHttpInvokerClientInterceptor;
import de.bund.bva.isyfact.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * This batch is used to test if correlation-id in AufrufKontext has the same value as in MDC.
 */
public class AufrufKontextTestBatch extends BasicTestBatch{

    private AufrufKontext aufrufKontext;

    @Mock
    private MethodInvocation methodInvocation;

    @Autowired
    private AufrufKontextVerwalter aufrufKontextVerwalter;

    private IsyHttpInvokerClientInterceptor isyHttpInvokerClientInterceptor;

    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {
        MockitoAnnotations.initMocks(this);
        isyHttpInvokerClientInterceptor = new IsyHttpInvokerClientInterceptor();
        isyHttpInvokerClientInterceptor.setAufrufKontextToResolver(new DefaultAufrufKontextToResolver());
        isyHttpInvokerClientInterceptor.setRemoteSystemName("remoteSystem");
        return 0;
    }

    @Override
    public VerarbeitungsErgebnis verarbeiteSatz() throws BatchAusfuehrungsException {

        aufrufKontext = aufrufKontextVerwalter.getAufrufKontext();
        assertEquals(MdcHelper.liesKorrelationsId(), aufrufKontext.getKorrelationsId());

        Method toStringMethod = null;
        try {
            toStringMethod = Object.class.getMethod("toString");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        AufrufKontextTo aufrufKontextTo = new AufrufKontextTo();
        aufrufKontextTo.setKorrelationsId(aufrufKontext.getKorrelationsId());

        when(methodInvocation.getArguments()).thenReturn(new Object[] { aufrufKontextTo });
        when(methodInvocation.getMethod()).thenReturn(toStringMethod);

        try {
            isyHttpInvokerClientInterceptor.invoke(methodInvocation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return new VerarbeitungsErgebnis("", true);
    }

    public void batchBeendet() {
        // empty
    }
}
