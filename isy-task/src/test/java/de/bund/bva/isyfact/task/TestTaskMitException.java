package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.AbstractTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;

public class TestTaskMitException extends AbstractTask {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTaskMitException.class);

    private boolean werfeException = true;

    public TestTaskMitException(TaskMonitor monitor) {
        super(monitor);
    }

    @Override
    public void execute() {
        if (werfeException) {
            LOG.info(LogKategorie.JOURNAL, "OPException", "{} running Task mit Exception",
                DateTimeUtil.localDateTimeNow());
            werfeException = false;
            throw new RuntimeException("Exception von Task");
        } else {
            LOG.info(LogKategorie.JOURNAL, "OPException", "{} running Task ohne Exception",
                DateTimeUtil.localDateTimeNow());
        }
    }
}