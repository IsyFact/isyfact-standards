package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.task.model.impl.OperationImpl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation3 extends OperationImpl {
    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(LocalDateTime.now() + " running Operation 3");
            }
            setHasBeenExecutedSuccessfully(true);
        } catch(Throwable e) {
            setErrorMessage(e.getMessage());
            setHasBeenExecutedSuccessfully(false);
        }
    }
}
