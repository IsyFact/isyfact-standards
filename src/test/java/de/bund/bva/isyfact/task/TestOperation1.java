package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.model.impl.OperationImpl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation1 extends OperationImpl {

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestOperation1.class);

    @Override
    public void execute() {
        try {
            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(1);
                LOG.info(LogKategorie.JOURNAL, "OP1", "{} running Operation 1", LocalDateTime.now());
            }
            setHasBeenExecutedSuccessfully(true);
        } catch (Throwable e) {
            setErrorMessage(e.getMessage());
            setHasBeenExecutedSuccessfully(false);
        }
    }
}
