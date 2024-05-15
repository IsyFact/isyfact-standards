package de.bund.bva.isyfact.polling.annotation;

import de.bund.bva.isyfact.polling.PollingVerwalter;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollingAktionInterceptorTest {

    @Mock
    private PollingVerwalter pollingVerwalter;

    @Mock
    private MethodInvocation methodInvocation;

    @InjectMocks
    private PollingAktionInterceptor interceptor;

    private static final String POLLING_CLUSTER = "testCluster";

    @BeforeEach
    void setup() throws Throwable {
        lenient().when(methodInvocation.proceed()).thenReturn(null);
    }

    @Test
    void testInvokeWithNullTarget() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("myMethod");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(null);

        assertDoesNotThrow(() -> interceptor.invoke(methodInvocation));
        verify(pollingVerwalter, never()).aktualisiereZeitpunktLetztePollingAktivitaet(anyString());
    }

    @Test
    void testInvokeWithPollingAktion() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("myMethod");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(new MyClass());

        interceptor.invoke(methodInvocation);

        verify(pollingVerwalter).aktualisiereZeitpunktLetztePollingAktivitaet("testCluster");
    }

    @Test
    void testInvokeWithoutPollingAktion() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("myMethodWithoutAnnotation");
        when(methodInvocation.getMethod()).thenReturn(method);

        interceptor.invoke(methodInvocation);

        verify(pollingVerwalter, never()).aktualisiereZeitpunktLetztePollingAktivitaet(anyString());
    }

    @Test
    void testInvokeWithExceptionInProceed() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("myMethod");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(new MyClass());
        doThrow(new RuntimeException("Test exception")).when(methodInvocation).proceed();

        assertThrows(RuntimeException.class, () -> interceptor.invoke(methodInvocation));

        verify(pollingVerwalter, never()).aktualisiereZeitpunktLetztePollingAktivitaet(anyString());
    }

    @Test
    void testReturnValues() throws Throwable {
        Method method = MyClass.class.getDeclaredMethod("methodWithReturnValue");
        when(methodInvocation.getMethod()).thenReturn(method);
        when(methodInvocation.getThis()).thenReturn(new MyClass());
        when(methodInvocation.proceed()).thenReturn("Expected Return");

        Object result = interceptor.invoke(methodInvocation);

        Assertions.assertEquals("Expected Return", result, "The interceptor should return the value from proceed()");
    }

    static class MyClass {
        @PollingAktion(pollingCluster = POLLING_CLUSTER)
        void myMethod() {
        }

        void myMethodWithoutAnnotation() {
        }

        String methodWithReturnValue() {
            return "Expected Return";
        }
    }
}
