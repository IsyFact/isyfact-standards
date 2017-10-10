package de.bund.bva.isyfact.task;

import java.time.LocalDateTime;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.model.AbstractTask;

public class TestTask3 extends AbstractTask {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTask3.class);

    public TestTask3(TaskMonitor monitor) {
        super(monitor);
    }

    @Override
    public void execute() {
        LOG.info(LogKategorie.JOURNAL, "OP3", "{} running Task 3", LocalDateTime.now());
    }
}
