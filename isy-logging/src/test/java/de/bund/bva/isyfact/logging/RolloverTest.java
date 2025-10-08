package de.bund.bva.isyfact.logging;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;


/**
 * Tests for the log-rotation.
 */
public class RolloverTest extends AbstractLogTest {

    /**
     * Testing that in the log rotation timestamp not the time zone of the system but UTC is used
     * 
     * @throws Exception
     *             if an exception during the test occurs.
     */
    @Test
    public void utcTest() throws Exception {

        // determine the appender
        IsyLocationAwareLoggerImpl logger = (IsyLocationAwareLoggerImpl) IsyLoggerFactory.getLogger(this
                .getClass());
        Logger logbackLogger = (Logger) logger.getLogger();
        // only Root-Logger has an appender
        Logger rootLogger = logbackLogger.getLoggerContext().getLogger("ROOT");
        RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>) rootLogger
                .getAppender("DATEI_ANWENDUNG");

        Calendar jetzt = Calendar.getInstance();
        TimeZone zeitzoneSystem = jetzt.getTimeZone();
        TimeZone zeitzoneUTC = TimeZone.getTimeZone("UTC");

        // Verify that the test is being run on a system that does not use UTC. Generally, it is
        // good if a system runs under UTC, but then the test is not
        // meaningful, since the goal is to verify that Logback always uses UTC,
        // even if the system time zone is different. If necessary, the test must be skipped or the
        // system time must be adjusted.
        Assertions.assertNotSame(zeitzoneUTC.getID(), zeitzoneSystem.getID(),
                "Die Zeitzone des Systems ist UTC. Dies ist kein Fehler, "
                + "führt jedoch dazu, dass der Test nicht aussagekräftig ist. ");

        // Determine and delete the rotation target file
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH");
        dateFormat.setTimeZone(zeitzoneUTC);
        String rolloverString = dateFormat.format(jetzt.getTime());
        String logdateiRolliertPfad = LOG_VERZEICHNIS
                + LOG_DATEI.replace(".log", "_" + rolloverString + ".log");
        File logdateiRolliert = new File(logdateiRolliertPfad);
        if (logdateiRolliert.exists()) {
            logdateiRolliert.delete();
        }

        logger.debug("TEST");

        // Rolling: This is a bit cumbersome, since you can't force logback to roll. Therefore,
        // the next time to be rolled is manipulated in logback using reflection.
        TimeBasedRollingPolicy<?> triggeringPolicy = (TimeBasedRollingPolicy<?>) appender
                .getTriggeringPolicy();
        TimeBasedFileNamingAndTriggeringPolicyBase<?> timeBasedFileNamingAndTriggeringPolicy = (TimeBasedFileNamingAndTriggeringPolicyBase<?>) triggeringPolicy
                .getTimeBasedFileNamingAndTriggeringPolicy();
        Field nextCheckField = TimeBasedFileNamingAndTriggeringPolicyBase.class.getDeclaredField("atomicNextCheck");
        nextCheckField.setAccessible(true);
        nextCheckField.set(timeBasedFileNamingAndTriggeringPolicy, new AtomicLong(jetzt.getTimeInMillis()));
        triggeringPolicy.isTriggeringEvent(null, null);
        appender.rollover();

        // rolled log file with correct timestamp must exist
        Assertions.assertTrue(
                logdateiRolliert.exists(),
                "Die erwartete rotierte Logdatei existiert nicht: " + logdateiRolliert.getAbsolutePath());

    }
}
