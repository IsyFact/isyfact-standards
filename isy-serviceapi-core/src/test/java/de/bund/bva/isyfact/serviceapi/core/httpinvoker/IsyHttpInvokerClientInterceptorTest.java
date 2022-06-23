package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.util.LogHelper;
import de.bund.bva.isyfact.serviceapi.core.aufrufkontext.DefaultAufrufKontextToResolver;
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

    private static final String KORRELATIONS_ID = "korrelationsId";

    private static final String REMOTE_SYSTEM = "remoteSystem";

    private static final String REGEX_WITHOUT_KORRELATIONS = "[a-z0-9-]{36}";

    @Before
    public void init() throws Throwable {
        isyHttpInvokerClientInterceptor = new IsyHttpInvokerClientInterceptor();
        toStringMethod = Object.class.getMethod("toString");

        when(logHelper.ermittleAktuellenZeitpunkt()).thenReturn(1L).thenReturn(2L);
        when(methodInvocation.getArguments()).thenReturn(new Object[] {aufrufKontextTo});
        when(methodInvocation.getMethod()).thenReturn(toStringMethod);

        isyHttpInvokerClientInterceptor.setAufrufKontextToResolver(new DefaultAufrufKontextToResolver());
        isyHttpInvokerClientInterceptor.setLogHelper(logHelper);
        isyHttpInvokerClientInterceptor.setRemoteSystemName(REMOTE_SYSTEM);
    }

    @Test
    public void invokeMitIsyFactLogging() throws Throwable {
        isyHttpInvokerClientInterceptor.invoke(methodInvocation);

        verify(aufrufKontextTo, atLeast(1)).setKorrelationsId(anyString());
        verify(logHelper).loggeNachbarsystemAufruf(any(IsyLogger.class), eq(toStringMethod), eq(REMOTE_SYSTEM),
                eq(null));
        verify(logHelper).loggeNachbarsystemErgebnis(any(IsyLogger.class), eq(toStringMethod), eq(REMOTE_SYSTEM),
                eq(null), eq(true));
        verify(logHelper).loggeNachbarsystemDauer(any(IsyLogger.class), eq(toStringMethod), eq(1L),
                eq(REMOTE_SYSTEM), eq(null), eq(true));
    }

    @Test
    public void invokeResetKorrelationId() throws Throwable {
        isyHttpInvokerClientInterceptor.invoke(methodInvocation);

        // The korrelationsSetter will be called one time with String
        verify(aufrufKontextTo, times(2)).setKorrelationsId(any());

        InOrder inOrder = inOrder(aufrufKontextTo);

        // korrelationsId = UUID
        inOrder.verify(aufrufKontextTo).setKorrelationsId(ArgumentMatchers.matches(REGEX_WITHOUT_KORRELATIONS));

        // Reset of korrelationsId
        inOrder.verify(aufrufKontextTo).setKorrelationsId(null);
    }
}
