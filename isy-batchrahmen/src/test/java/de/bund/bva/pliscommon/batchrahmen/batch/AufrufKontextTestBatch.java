package de.bund.bva.pliscommon.batchrahmen.batch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.batchrahmen.batch.exception.BatchAusfuehrungsException;
import de.bund.bva.pliscommon.batchrahmen.batch.konfiguration.BatchKonfiguration;
import de.bund.bva.pliscommon.batchrahmen.batch.protokoll.BatchErgebnisProtokoll;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.BatchStartTyp;
import de.bund.bva.pliscommon.batchrahmen.batch.rahmen.VerarbeitungsErgebnis;
import de.bund.bva.pliscommon.serviceapi.core.httpinvoker.IsyHttpInvokerClientInterceptor;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class AufrufKontextTestBatch extends BasicTestBatch {

    private AufrufKontext aufrufKontext;

    @Mock
    private MethodInvocation methodInvocation;

    private AufrufKontextVerwalter<AufrufKontext> aufrufKontextVerwalter;

    private IsyHttpInvokerClientInterceptor isyHttpInvokerClientInterceptor;

    public int initialisieren(BatchKonfiguration konfiguration, long satzNummer, String dbKey,
                              BatchStartTyp startTyp, Date datumLetzterErfolg, BatchErgebnisProtokoll protokoll)
            throws BatchAusfuehrungsException {
        MockitoAnnotations.initMocks(this);
        isyHttpInvokerClientInterceptor = new IsyHttpInvokerClientInterceptor();
        isyHttpInvokerClientInterceptor.setRemoteSystemName("remoteSystem");
        return 0;
    }

    @Override
    public VerarbeitungsErgebnis verarbeiteSatz () {
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

        when(methodInvocation.getArguments()).thenReturn(new Object[]{aufrufKontextTo});
        when(methodInvocation.getMethod()).thenReturn(toStringMethod);

        try {
            isyHttpInvokerClientInterceptor.invoke(methodInvocation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return new VerarbeitungsErgebnis("", true);
    }

    @Override
    public void batchBeendet() {
        // empty
    }

    @Required
    public void setAufrufKontextVerwalter (AufrufKontextVerwalter verwalter) {
        this.aufrufKontextVerwalter = verwalter;
    }
}