package de.bund.bva.isyfact.logging.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class AspectJUtilsTest {

    @Test
    void shouldCreateAspectOf() {
        Object aspect = AspectJUtils.aspectOf(PerformanceLoggingAspect.class);

        assertEquals(PerformanceLoggingAspect.class, aspect.getClass());
    }

    @Test
    void shouldThrowIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> AspectJUtils.aspectOf(null));
    }
}
