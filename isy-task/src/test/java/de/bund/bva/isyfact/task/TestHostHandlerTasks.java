package de.bund.bva.isyfact.task;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

import io.micrometer.core.annotation.Timed;

@Component
public class TestHostHandlerTasks {

    public static final String SCHLUESSEL = "ISYTA99999";

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestHostHandlerTasks.class);

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    @Timed(value = "testHostHandlerTasks-scheduledTaskWithCorrectHostname")
    public void scheduledTaskWithCorrectHostname() {
        LOG.info(LogKategorie.JOURNAL, SCHLUESSEL, "test task - scheduled - correct host - executed at {}", DateTimeUtil.localDateTimeNow());
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void scheduledTaskWithWrongHostname() {
        LOG.info(LogKategorie.JOURNAL, SCHLUESSEL, "test task - scheduled - wrong host - executed at {}", DateTimeUtil.localDateTimeNow());
    }

}
