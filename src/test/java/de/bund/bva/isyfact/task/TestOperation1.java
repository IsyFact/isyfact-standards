package de.bund.bva.isyfact.task;

import de.bund.bva.isyfact.task.model.impl.OperationImpl;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TestOperation1 extends OperationImpl {

    public TestOperation1() {
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(LocalDateTime.now() + " running");
            }
            setHasBeenExecutedSuccessfully(true);
        } catch(Throwable e) {
            setErrorMessage(e.getMessage());
            setHasBeenExecutedSuccessfully(false);
        }
    }
}
