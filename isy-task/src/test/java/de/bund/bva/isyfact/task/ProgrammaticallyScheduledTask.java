package de.bund.bva.isyfact.task;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.task.annotation.ManualTask;
import de.bund.bva.isyfact.task.demo.ScheduledTasks;

@Component
public class ProgrammaticallyScheduledTask implements Runnable {

    private static final IsyLogger logger = IsyLoggerFactory.getLogger(ScheduledTasks.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    @ManualTask
    public void run() {
        for (int i = 0; i < 3; i++) {
            try {
                MILLISECONDS.sleep(100);
                logger.info(LogKategorie.JOURNAL, "EISYTA99994", "Manual Task {} :: Execution Time - {}", i, dateTimeFormatter.format(LocalDateTime.now()));
            } catch (InterruptedException e) {
                logger.debug("Thread unterbrochen");
                return;
            }
        }
    }
}
