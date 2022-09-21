package de.bund.bva.isyfact.ueberwachung.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.autoconfigure.IsyLoggingAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.common.impl.DefaultServiceStatistik;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
public class MetricsTest {

    private static final Instant START = Instant.now();

    @BeforeClass
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
                        "anzahlAufrufe.LetzteMinute",
                        "anzahlFachlicheFehler.LetzteMinute",
                        "anzahlFehler.LetzteMinute",
                        "durchschnittsDauer.LetzteAufrufe",
                        "jvm.classes.loaded"
                )
                .anyMatch(s -> s.startsWith("jvm.buffer."))
                .anyMatch("jvm.classes.loaded"::equals)
                .anyMatch(s -> s.startsWith("jvm.gc."))
                .anyMatch(s -> s.startsWith("jvm.memory."))
                .anyMatch(s -> s.startsWith("jvm.threads."))
                .anyMatch(s -> s.startsWith("process.cpu."))
                .anyMatch(s -> s.startsWith("process.files."))
                .anyMatch("process.uptime"::equals)
                .anyMatch(s -> s.startsWith("system.cpu."))
                .anyMatch(s -> s.startsWith("tomcat."));
    }

    @Test
    public void serviceStats_available() {

        testService.call1();
        testService.call1();

        assertThat(meterRegistry.get("anzahlAufrufe.LetzteMinute").gauges())
                .extracting(Gauge::value)
                .containsOnly(0.0);

        DateTimeUtil.setClock(Clock.fixed(START.plus(1, ChronoUnit.MINUTES), ZoneId.systemDefault()));
        assertThat(meterRegistry.get("anzahlAufrufe.LetzteMinute").tag("serviceMethod", "call1").gauge().value())
                .isEqualTo(2.0);
        assertThat(meterRegistry.get("anzahlAufrufe.LetzteMinute").tag("serviceMethod", "call2").gauge().value())
                .isZero();

        DateTimeUtil.setClock(Clock.fixed(START.plus(2, ChronoUnit.MINUTES), ZoneId.systemDefault()));
        assertThat(meterRegistry.get("anzahlAufrufe.LetzteMinute").gauges())
                .extracting(Gauge::value)
                .containsOnly(0.0);
    }
}

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = IsyLoggingAutoConfiguration.class)
class TestConfig {

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
}

class TestService {
    void call1() {
    }

    void call2() {
    }
}
