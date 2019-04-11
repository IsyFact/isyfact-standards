package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import java.lang.reflect.Method;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.util.LogHelper;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IsyHttpInvokerClientInterceptorTest {

    @Mock
    private AufrufKontextTo aufrufKontextTo;

    @Mock
    private MethodInvocation methodInvocation;

    @Mock
    private LogHelper logHelper;

    @Test
    public void invokeMitIsyFactLogging() throws Throwable {

        IsyHttpInvokerClientInterceptor isyHttpInvokerClientInterceptor =
            new IsyHttpInvokerClientInterceptor();

        Method toStringMethod = Object.class.getMethod("toString");

        when(logHelper.ermittleAktuellenZeitpunkt()).thenReturn(1L).thenReturn(2L);
        when(methodInvocation.getArguments()).thenReturn(new Object[] { aufrufKontextTo });
        when(methodInvocation.getMethod()).thenReturn(toStringMethod);
        when(aufrufKontextTo.getKorrelationsId()).thenReturn("korrelationsId");

        isyHttpInvokerClientInterceptor.setLogHelper(logHelper);
        isyHttpInvokerClientInterceptor.setRemoteSystemName("remoteSystem");

        isyHttpInvokerClientInterceptor.invoke(methodInvocation);

        verify(aufrufKontextTo).setKorrelationsId(anyString());
        verify(logHelper)
            .loggeNachbarsystemAufruf(any(IsyLogger.class), eq(toStringMethod), eq("remoteSystem"),
                eq((String) null));
        verify(logHelper)
            .loggeNachbarsystemErgebnis(any(IsyLogger.class), eq(toStringMethod), eq("remoteSystem"),
                eq((String) null), eq(true));
        verify(logHelper)
            .loggeNachbarsystemDauer(any(IsyLogger.class), eq(toStringMethod), eq(1L), eq("remoteSystem"),
                eq((String) null), eq(true));
    }
}