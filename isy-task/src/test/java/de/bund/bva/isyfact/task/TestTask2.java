package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.AbstractTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;

public class TestTask2 extends AbstractTask {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTask2.class);

    public TestTask2(TaskMonitor monitor) {
        super(monitor);
    }

    @Override
    public void execute() {
        LOG.info(LogKategorie.JOURNAL, "OP2", "{} running Task 2", DateTimeUtil.localDateTimeNow());
    }
}
