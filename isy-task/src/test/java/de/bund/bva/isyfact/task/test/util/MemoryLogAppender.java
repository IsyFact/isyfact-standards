package de.bund.bva.isyfact.task.test.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.List;
import java.util.stream.Collectors;

public class MemoryLogAppender extends ListAppender<ILoggingEvent> {

    public void clearLog() {
        this.list.clear();
    }

    public boolean containsLog(String message, Level level) {
        return this.list.stream()
                .anyMatch(event -> event.getMessage().contains(message)
                        && event.getLevel().equals(level));
    }

    public List<ILoggingEvent> findByMessageAndLevel(String message, Level level) {
        return this.list.stream()
                .filter(event -> event.getMessage().contains(message)
                        && event.getLevel().equals(level))
                .collect(Collectors.toList());
    }
}
