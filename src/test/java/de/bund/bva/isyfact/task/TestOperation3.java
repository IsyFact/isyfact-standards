package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.impl.OperationImpl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation3 extends OperationImpl {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestOperation3.class);

    @Override
    public void execute() {
        LOG.info(LogKategorie.JOURNAL, "OP3", "{} running Operation 3", LocalDateTime.now());
        setHasBeenExecutedSuccessfully(true);
    }
}
