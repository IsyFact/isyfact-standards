package de.bund.bva.isyfact.batchrahmen.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Logback appender that stores log messages in a static list.
 * Used by tests to verify log output from code that resets the logback context
 * (e.g. via {@code initialisiereLogback()}), since appenders attached programmatically
 * before the reset would be removed. Configuring this appender in {@code logback-batch.xml}
 * ensures it is re-registered on every logback reconfiguration.
 */
public class InMemoryAppender extends AppenderBase<ILoggingEvent> {

    private static final List<ILoggingEvent> EVENTS = Collections.synchronizedList(new ArrayList<>());

    @Override
    protected void append(ILoggingEvent event) {
        EVENTS.add(event);
    }

    /** Clears all captured log events. Call this before the code under test. */
    public static void clear() {
        EVENTS.clear();
    }

    /** Returns a snapshot of all captured log messages. */
    public static List<String> getMessages() {
        synchronized (EVENTS) {
            List<String> messages = new ArrayList<>(EVENTS.size());
            for (ILoggingEvent event : EVENTS) {
                messages.add(event.getFormattedMessage());
            }
            return messages;
        }
    }
}
