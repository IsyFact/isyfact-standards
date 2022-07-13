package de.bund.bva.isyfact.ueberwachung.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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

import de.bund.bva.isyfact.logging.autoconfigure.IsyLoggingAutoConfiguration;
import de.bund.bva.isyfact.ueberwachung.common.ServiceStatistik;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = TestConfig.class)
public class MetricsTest {

    @Autowired
    private MeterRegistry meterRegistry;

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
}

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = IsyLoggingAutoConfiguration.class)
class TestConfig {
    @Bean
    ServiceStatistik serviceStatistikMBean(MeterRegistry meterRegistry) {
        return new ServiceStatistik(meterRegistry, Tags.of("testKey", "testValue"));
    }

    @Bean
    TestService testService() {
        return new TestService();
    }

    @Bean
    Advisor testServiceMonitorAdvice(ServiceStatistik serviceStatistik) {
        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("target(" + TestService.class.getCanonicalName() + ")");
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, serviceStatistik);
        advisor.setOrder(1000);
        return advisor;
    }
}

class TestService {
    void call() {
    }
}
