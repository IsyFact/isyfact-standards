package de.bund.bva.isyfact.logging;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;

import de.bund.bva.isyfact.logging.impl.IsyLocationAwareLoggerImpl;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;



/**
 * Tests zum Prüfen der Log-Rotation.
 */
public class RolloverTest extends AbstractLogTest {

    /**
     * Prüft, dass bei der Logrotation für den Zeitstempel nicht die Zeitzone des Systems, sondern UTC
     * verwendet wird.
     * 
     * @throws Exception
     *             falls ein Fehler im Testfall auftritt.
     */
    @Test
    public void utcTest() throws Exception {

        // Zunächst wird der Appernder ermittelt
        IsyLocationAwareLoggerImpl logger = (IsyLocationAwareLoggerImpl) IsyLoggerFactory.getLogger(this
                .getClass());
        Logger logbackLogger = (Logger) logger.getLogger();
        // Nur der Root-Logger hat einen Appender
        Logger rootLogger = logbackLogger.getLoggerContext().getLogger("ROOT");
        RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>) rootLogger
                .getAppender("DATEI_ANWENDUNG");

        Calendar jetzt = Calendar.getInstance();
        TimeZone zeitzoneSystem = jetzt.getTimeZone();
        TimeZone zeitzoneUTC = TimeZone.getTimeZone("UTC");

        // Prüfung, dass der Test in einem System ausgeführt wird, das nicht UTC verwendet. Grundsätzlich ist
        // es natürlich gut, wenn ein System unter UTC läuft, allerdings ist der Test dann nicht
        // aussagekräftig, da ja gerade überprüft werden soll, dass durch Logback immer UTC verwendet wird,
        // auch wenn die Zeitzone der Systemzeit eine andere ist. Ggf, muss der Test übersprungen oder die
        // Systemzeit des Systems angepasst werden.
        Assert.assertNotSame("Die Zeitzone des Systems ist UTC. Dies ist kein Fehler, "
                + "führt jedoch dazu, dass der Test nicht aussagekräftig ist. ", zeitzoneUTC.getID(),
                zeitzoneSystem.getID());

        // Zieldatei der Rotation ermitteln und löschen
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

        // Rolliereren: Dies ist etwas umständlich, da man logback nicht dazu zwingen kann zu rollieren. Daher
        // wird hier in Logback per Reflection die nächste zu rollierende Zeit manipuliert.
        TimeBasedRollingPolicy<?> triggeringPolicy = (TimeBasedRollingPolicy<?>) appender
                .getTriggeringPolicy();
        TimeBasedFileNamingAndTriggeringPolicyBase<?> timeBasedFileNamingAndTriggeringPolicy = (TimeBasedFileNamingAndTriggeringPolicyBase<?>) triggeringPolicy
                .getTimeBasedFileNamingAndTriggeringPolicy();
        Field nextCheckField = TimeBasedFileNamingAndTriggeringPolicyBase.class.getDeclaredField("atomicNextCheck");
        nextCheckField.setAccessible(true);
        nextCheckField.set(timeBasedFileNamingAndTriggeringPolicy, new AtomicLong(jetzt.getTimeInMillis()));
        triggeringPolicy.isTriggeringEvent(null, null);
        appender.rollover();

        // Rollierte Datei muss existieren
        Assert.assertTrue(
                "Die erwartete rotierte Logdatei existiert nicht: " + logdateiRolliert.getAbsolutePath(),
                logdateiRolliert.exists());

    }
}
