package de.bund.bva.isyfact.task.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import de.bund.bva.isyfact.task.AbstractTaskTest;
import de.bund.bva.isyfact.task.config.IsyTaskConfigurationProperties;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import de.bund.bva.isyfact.task.test.config.TestConfig;
import de.bund.bva.isyfact.task.test.config.TestTaskExceptionConfig;
import de.bund.bva.isyfact.task.test.util.MemoryLogAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {TestConfig.class, TestTaskExceptionConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {"isy.logging.anwendung.name=test",
                "isy.logging.anwendung.typ=test",
                "isy.logging.anwendung.version=test",
                "logging.level.root=info",
                "isy.task.tasks.taskMitException.benutzer=TestUser1",
                "isy.task.tasks.taskMitException.passwort=TestPasswort1",
                "isy.task.tasks.taskMitException.bhkz=BHKZ1",
                "isy.task.tasks.taskMitException.ausfuehrung=FIXED_RATE",
                "isy.task.tasks.taskMitException.initial-delay=1s",
                "isy.task.tasks.taskMitException.fixed-rate=3s",
                "isy.task.watchdog.restart-interval=1s"})
public class TestCompletionWatchdogLogging extends AbstractTaskTest {
    @Autowired
    private IsyTaskConfigurationProperties configurationProperties;

    @Autowired
    private TaskMonitor taskMonitor;

    private MemoryLogAppender memoryLogAppender;

    @Before
    public void setup() throws Exception {
        configurationProperties.getDefault().setHost(InetAddress.getLocalHost().getHostName());
        // get Logger for TaskSchedulerImpl.class
        Logger logger = (Logger) LoggerFactory.getLogger(TaskSchedulerImpl.class);
        memoryLogAppender = new MemoryLogAppender();
        memoryLogAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(memoryLogAppender);
        memoryLogAppender.start();
    }

    @After
    public void cleanUp() {
        memoryLogAppender.clearLog();
        memoryLogAppender.stop();
    }

    @Test
    public void taskMitExceptionLoggtExceptionStacktrace() throws Exception {
        taskScheduler.starteKonfigurierteTasks();
        SECONDS.sleep(2);
        taskScheduler.shutdownMitTimeout(10);

        assertThat(memoryLogAppender.containsLog("Exception von Task", Level.WARN)).isTrue();
        ILoggingEvent event = memoryLogAppender.findByMessageAndLevel("Exception von Task", Level.WARN).get(0);
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getClassName())
                .isEqualTo(java.util.concurrent.ExecutionException.class.getName());
    }
}
