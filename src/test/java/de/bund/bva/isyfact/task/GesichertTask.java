package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.AbstractTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.pliscommon.sicherheit.annotation.Gesichert;

/**
 * @author Björn Saxe, msg systems ag
 */
public class GesichertTask extends AbstractTask {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(GesichertTask.class);

    public GesichertTask(TaskMonitor monitor) {
        super(monitor);
    }

    @Override
    @Gesichert("Recht1")
    public void execute() {
        LOG.info(LogKategorie.JOURNAL, "GesichertTask", "{} running Task GesichertTask",
            DateTimeUtil.localDateTimeNow());
    }
}