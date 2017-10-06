package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.impl.OperationImpl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation2 extends OperationImpl {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestOperation2.class);

    @Override
    public void execute() {
        LOG.info(LogKategorie.JOURNAL, "OP2", "{} running Operation 2", LocalDateTime.now());
    }
}
