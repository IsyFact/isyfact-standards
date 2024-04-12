package de.bund.bva.isyfact.task.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.bund.bva.isyfact.task.util.TaskCounterBuilder;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class TestTaskRunAssertion {

    public static void assertTaskSuccess(String className, String annotatedMethodName,
        MeterRegistry registry, String exceptionName){

        Counter successCounter = TaskCounterBuilder.successCounter(className, annotatedMethodName, registry);
        Counter failureCounter = TaskCounterBuilder.failureCounter(className, annotatedMethodName,
            exceptionName, registry);

        assertTrue(successCounter.count() > 0);
        assertEquals(0, failureCounter.count(), 0.0);
    }

    public static void assertTaskFailure(String className, String annotatedMethodName,
        MeterRegistry registry, String exceptionName){

        Counter successCounter = TaskCounterBuilder.successCounter(className, annotatedMethodName, registry);
        Counter failureCounter = TaskCounterBuilder.failureCounter(className, annotatedMethodName,
            exceptionName, registry);

        assertEquals(0, successCounter.count(), 0.0);
        assertTrue(failureCounter.count() > 0);
    }

}
