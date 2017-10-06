package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.Operation;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation1 implements Operation {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestOperation1.class);

    @Override
    public void execute() {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
                LOG.info(LogKategorie.JOURNAL, "OP1", "{} running Operation 1", LocalDateTime.now());
            } catch (InterruptedException e) {
                LOG.debug("Thread unterbrochen");
                return;
            }
        }
    }
}
