package de.bund.bva.isyfact.task;

import java.util.concurrent.TimeUnit;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.AbstractTask;

public class TestTask1 extends AbstractTask {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTask1.class);

    @Override
    public void execute() {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
                LOG.info(LogKategorie.JOURNAL, "OP1", "{} running Task 1", DateTimeUtil.localDateTimeNow());
            } catch (InterruptedException e) {
                LOG.debug("Thread unterbrochen");
                return;
            }
        }
    }
}
