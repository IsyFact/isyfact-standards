package de.bund.bva.isyfact.ueberwachung.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.autoconfigure.IsyLoggingAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.metrics.impl.DefaultServiceStatistik;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = MonitorTestServiceCalls_TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MetricsTest {

    private static final Instant START = Instant.now();

    @BeforeAll
    public static void setupClock() {
        DateTimeUtil.setClock(Clock.fixed(START, ZoneId.systemDefault()));
    }

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private TestService testService;

    @Test
    public void metrics_available() {
        assertThat(meterRegistry.getMeters())
                .map(Meter::getId)
                .map(Meter.Id::getName)
                .contains(
                        "anzahlAufrufe",
                        "anzahlBusinessExceptions",
                        "anzahlTechnicalExceptions",
                        "durchschnittsDauer.LetzteAufrufe",
                        "jvm.classes.loaded"
                )
                .anyMatch(s -> s.startsWith("jvm.buffer."))
                .anyMatch("jvm.classes.loaded"::equals)
                .anyMatch(s -> s.startsWith("jvm.gc."))
                .anyMatch(s -> s.startsWith("jvm.memory."))
                .anyMatch(s -> s.startsWith("jvm.threads."))
                .anyMatch(s -> s.startsWith("process.cpu."))
                .anyMatch("process.uptime"::equals)
                .anyMatch(s -> s.startsWith("system.cpu."))
                .anyMatch(s -> s.startsWith("tomcat."));
    }

    @Test
    public void serviceStats_available() {
        testService.call1();
        testService.call1();

        assertThat(meterRegistry.get("anzahlAufrufe").tag("serviceMethod", "call1").gauge().value())
            .isEqualTo(2.0);
        assertThat(meterRegistry.get("anzahlAufrufe").tag("serviceMethod", "call2").gauge().value())
            .isZero();
    }

    @Test
    public void serviceStats_durationStats() {
        final Duration[] durations = { Duration.ofMillis(10), Duration.ofMillis(20), Duration.ofMillis(30) };

        for (Duration duration : durations) {
            resetClock();
            testService.call3(() -> moveClockBy(duration));
        }

        final Duration meanDuration = Arrays.stream(durations)
                .reduce(Duration.ZERO, Duration::plus)
                .dividedBy(durations.length);

        assertThat(meterRegistry.get("durchschnittsDauer.LetzteAufrufe")
                .tag("serviceMethod", "call3")
                .timeGauge()
                .value(TimeUnit.MILLISECONDS)
        ).isEqualTo(meanDuration.toMillis());
    }

    private static void moveClockBy(Duration amountToAdd) {
        DateTimeUtil.setClock(Clock.fixed(START.plus(amountToAdd), ZoneId.systemDefault()));
    }

    private static void resetClock() {
        moveClockBy(Duration.ZERO);
    }
}

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = IsyLoggingAutoConfiguration.class)
class MonitorTestServiceCalls_TestConfig {

    @Bean
    TestService testService() {
        return new TestService();
    }

    @Bean
    DefaultServiceStatistik serviceStatistikCall1() {
        return new DefaultServiceStatistik("serviceMethod", "call1");
    }

    @Bean
    Advisor serviceStatistikCall1MonitorAdvice() {
        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* " + TestService.class.getCanonicalName() + ".call1(..))");
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, serviceStatistikCall1());
        advisor.setOrder(1000);
        return advisor;
    }

    @Bean
    DefaultServiceStatistik serviceStatistikCall2() {
        return new DefaultServiceStatistik("serviceMethod", "call2");
    }

    @Bean
    Advisor serviceStatistikCall2MonitorAdvice() {
        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* " + TestService.class.getCanonicalName() + ".call2(..))");
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, serviceStatistikCall2());
        advisor.setOrder(1000);
        return advisor;
    }

    @Bean
    DefaultServiceStatistik serviceStatistikCall3() {
        return new DefaultServiceStatistik("serviceMethod", "call3");
    }

    @Bean
    Advisor serviceStatistikCall3MonitorAdvice() {
        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* " + TestService.class.getCanonicalName() + ".call3(..))");
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, serviceStatistikCall3());
        advisor.setOrder(1000);
        return advisor;
    }
}

class TestService {
    void call1() {
    }

    void call2() {}

    void call3(Runnable action) {
        action.run();
    }
}
