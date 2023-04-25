package de.bund.bva.isyfact.task;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;

@Component
public class TestTaskAuthenticationTasks {

    public static final String SCHLUESSEL = "ISYTA99999";

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestTaskAuthenticationTasks.class);

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    @Secured("Recht1")
    public void scheduledTaskSecured() {
        LOG.info(LogKategorie.JOURNAL, SCHLUESSEL, "test task - scheduled - secured - executed at {}", DateTimeUtil.localDateTimeNow());
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    @Secured("Recht1")
    public void scheduledTaskSecuredInsufficientRights() {
        LOG.info(LogKategorie.JOURNAL, SCHLUESSEL, "test task - scheduled - insufficient rights - executed at {}", DateTimeUtil.localDateTimeNow());
    }

}
