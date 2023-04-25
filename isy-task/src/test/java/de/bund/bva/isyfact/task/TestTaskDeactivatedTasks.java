package de.bund.bva.isyfact.task;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

@Component
public class TestTaskDeactivatedTasks {

    public static final String SCHLUESSEL = "ISYTA99999";

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTaskDeactivatedTasks.class);

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void scheduledTaskDeactivated() {
        LOG.info(LogKategorie.JOURNAL, SCHLUESSEL, "test task - scheduled - deactivated - executed at {}", DateTimeUtil.localDateTimeNow());
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void scheduledTaskActivated() {
        LOG.info(LogKategorie.JOURNAL, SCHLUESSEL, "test task - scheduled - activated - executed at {}", DateTimeUtil.localDateTimeNow());
    }

}
