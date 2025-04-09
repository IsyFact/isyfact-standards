package de.bund.bva.isyfact.task.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

import io.micrometer.core.annotation.Timed;

@Component
public class ScheduledTasks {
    private static final IsyLogger logger = IsyLoggerFactory.getLogger(ScheduledTasks.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Timed(value = "task.scheduledTasks.scheduleTaskWithFixedRate")
    @Scheduled(fixedRate = 2000)
    public void scheduleTaskWithFixedRate() {
        logger.info(LogKategorie.JOURNAL, "EISYTA99991", "Fixed Rate Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
    }

    @Timed(value = "task.scheduledTasks.scheduleTaskWithFixedDelay")
    @Scheduled(fixedDelay = 2000)
    public void scheduleTaskWithFixedDelay() {
        logger.info(LogKategorie.JOURNAL, "EISYTA99992", "Fixed Delay Task :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ex) {
            logger.error("ISYTA99999", "Ran into an error!", ex);
            throw new IllegalStateException(ex);
        }
    }

    @Timed(value = "task.scheduledTasks.scheduleTaskWithInitialDelay")
    @Scheduled(fixedRate = 2000, initialDelay = 5000)
    public void scheduleTaskWithInitialDelay() {
        logger.info(LogKategorie.JOURNAL, "EISYTA99993", "Fixed Rate Task with Initial Delay :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        if (new Random().nextInt(10) == 0) {
            throw new RuntimeException();
        }
    }

    @Timed(value = "task.scheduledTasks.scheduleTaskWithCronExpression")
    @Scheduled(cron = "0 * * * * ?")
    public void scheduleTaskWithCronExpression() {
        logger.info(LogKategorie.JOURNAL, "EISYTA99994", dateTimeFormatter.format(LocalDateTime.now()));
    }
}
