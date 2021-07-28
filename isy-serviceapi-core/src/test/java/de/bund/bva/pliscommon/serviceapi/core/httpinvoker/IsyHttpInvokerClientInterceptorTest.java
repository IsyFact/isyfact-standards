package de.bund.bva.pliscommon.serviceapi.core.httpinvoker;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.util.LogHelper;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

@RunWith(MockitoJUnitRunner.class)
public class IsyHttpInvokerClientInterceptorTest {

    @Mock
    private AufrufKontextTo aufrufKontextTo;

    @Mock
    private MethodInvocation methodInvocation;

    @Mock
    private LogHelper logHelper;

    private IsyHttpInvokerClientInterceptor isyHttpInvokerClientInterceptor;
    private Method toStringMethod;
    private final static String KORRELATIONS_ID = "korrelationsId";
    private final static String REMOTE_SYSTEM = "remoteSystem";
    private final static String REGEX_WITHOUT_KORRELATIONS = "[a-z0-9-]{36}";

    @Before
    public void init () throws Throwable{
        isyHttpInvokerClientInterceptor = new IsyHttpInvokerClientInterceptor();
        toStringMethod = Object.class.getMethod("toString");

        when(logHelper.ermittleAktuellenZeitpunkt()).thenReturn(1L).thenReturn(2L);
        when(methodInvocation.getArguments()).thenReturn(new Object[] { aufrufKontextTo });
        when(methodInvocation.getMethod()).thenReturn(toStringMethod);
        when(aufrufKontextTo.getKorrelationsId()).thenReturn(KORRELATIONS_ID);

        isyHttpInvokerClientInterceptor.setLogHelper(logHelper);
        isyHttpInvokerClientInterceptor.setRemoteSystemName(REMOTE_SYSTEM);
    }

    @Test
    public void invokeMitIsyFactLogging() throws Throwable {
        isyHttpInvokerClientInterceptor.invoke(methodInvocation);

        verify(aufrufKontextTo, Mockito.atLeast(1)).setKorrelationsId(anyString());
        verify(logHelper)
                .loggeNachbarsystemAufruf(any(IsyLogger.class), eq(toStringMethod), eq("remoteSystem"),
                        eq(null));
        verify(logHelper)
                .loggeNachbarsystemErgebnis(any(IsyLogger.class), eq(toStringMethod), eq("remoteSystem"),
                        eq(null), eq(true));
        verify(logHelper)
                .loggeNachbarsystemDauer(any(IsyLogger.class), eq(toStringMethod), eq(1L), eq("remoteSystem"),
                        eq(null), eq(true));
    }

    @Test
    public void invokeResetKorrelationId() throws Throwable {
        isyHttpInvokerClientInterceptor.invoke(methodInvocation);

        // The korrelationsSetter will be called 2 times
        verify(aufrufKontextTo, Mockito.times(2)).setKorrelationsId(anyString());

        // korrelationsId = UUID
        verify(aufrufKontextTo).setKorrelationsId(Mockito.matches(REGEX_WITHOUT_KORRELATIONS));

        // Reset of korrelationsId
        verify(aufrufKontextTo).setKorrelationsId(KORRELATIONS_ID);
    }

}
