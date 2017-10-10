package de.bund.bva.isyfact.task;

import java.time.LocalDateTime;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.model.AbstractTask;

public class TestTask2 extends AbstractTask {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTask2.class);

    public TestTask2(TaskMonitor monitor) {
        super(monitor);
    }

    @Override
    public void execute() {
        LOG.info(LogKategorie.JOURNAL, "OP2", "{} running Task 2", LocalDateTime.now());
    }
}
