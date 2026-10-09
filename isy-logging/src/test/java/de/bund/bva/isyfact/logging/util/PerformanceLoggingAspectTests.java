package de.bund.bva.isyfact.logging.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PerformanceLoggingAspectTests {

    private final LogHelper logHelper = mock(LogHelper.class);

    private PerformanceLoggingAspect performanceLoggingAspect;
    private ProceedingJoinPoint pjp;

    @BeforeEach
    void setup() {
        pjp = mock(ProceedingJoinPoint.class);
        this.performanceLoggingAspect = new PerformanceLoggingAspect(logHelper);
    }

    @Test
    void shouldLogPerformance(){
        preparePerformanceLoggingEnabledTest();

        assertDoesNotThrow(() -> this.performanceLoggingAspect.loggeDauer(pjp));

        verify(logHelper, atLeastOnce()).loggeDauer(any(), any(), anyLong(), anyBoolean());
    }

    @Test
    void shouldNotLogPerformance() {
        this.performanceLoggingAspect.setEnabled(false);

        assertDoesNotThrow(() -> this.performanceLoggingAspect.loggeDauer(pjp));

        verify(logHelper, never()).loggeDauer(any(), any(), anyLong(), anyBoolean());
    }

    @Test
    void shouldLogAndRethrow() throws Throwable {
        preparePerformanceLoggingEnabledTest();
        when(pjp.proceed()).thenThrow(Throwable.class);

        assertThrows(Throwable.class, () -> this.performanceLoggingAspect.loggeDauer(pjp));

        verify(logHelper, atLeastOnce()).loggeDauer(any(), any(), anyLong(), anyBoolean());
    }

    private void preparePerformanceLoggingEnabledTest() {
        this.performanceLoggingAspect.setEnabled(true);
        var methodSignature = mock(MethodSignature.class);
        when(pjp.getTarget()).thenReturn(mock(AwfTestClass.class));
        when(pjp.getSignature()).thenReturn(methodSignature);
    }

    private static class AwfTestClass{

    }


}
