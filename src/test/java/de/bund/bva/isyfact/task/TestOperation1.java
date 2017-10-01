package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.task.model.impl.OperationImpl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation1 extends OperationImpl {

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(LocalDateTime.now() + " running Operation 1");
            }
            setHasBeenExecutedSuccessfully(true);
        } catch(Throwable e) {
            setErrorMessage(e.getMessage());
            setHasBeenExecutedSuccessfully(false);
        }
    }
}
