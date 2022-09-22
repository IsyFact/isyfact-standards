package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.ueberwachung.metrics.ServiceStatistik;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * Metrics autoconfiguration.
 */
@Configuration
public class IsyMetricsAutoConfiguration {

    /** {@link MeterBinder} which registers all available {@link ServiceStatistik} beans. */
    @Bean
    @ConditionalOnBean(ServiceStatistik.class)
    public MeterBinder serviceStatistics(ObjectProvider<ServiceStatistik> serviceStatsBeans) {
        return registry -> serviceStatsBeans.stream().forEach(stats -> registerServiceStatsGauges(registry, stats));
    }

    /** Register a {@link ServiceStatistik} bean in the Micrometer registry. */
    public static void registerServiceStatsGauges(MeterRegistry registry, ServiceStatistik stats) {
        Gauge.builder("anzahlAufrufe.LetzteMinute", stats, ServiceStatistik::getAnzahlAufrufeLetzteMinute)
                .tags(stats.getTags())
                .description("Liefert die Anzahl der nicht fehlerhaften Aufrufe in der letzten Minute")
                .register(registry);

        Gauge.builder("anzahlFehler.LetzteMinute", stats, ServiceStatistik::getAnzahlFehlerLetzteMinute)
                .tags(stats.getTags())
                .description("Liefert die Anzahl der fehlerhaften Aufrufe in der letzten Minute")
                .register(registry);

        Gauge.builder("anzahlFachlicheFehler.LetzteMinute", stats, ServiceStatistik::getAnzahlFachlicheFehlerLetzteMinute)
                .tags(stats.getTags())
                .description("Liefert die Anzahl der fachlich fehlerhaften Aufrufe in der letzten Minute")
                .register(registry);

        TimeGauge.builder("durchschnittsDauer.LetzteAufrufe", stats, TimeUnit.MILLISECONDS, ServiceStatistik::getDurchschnittsDauerLetzteAufrufe)
                .tags(stats.getTags())
                .description("Liefert die durchschnittliche Dauer der letzten 10 Aufrufe in ms")
                .register(registry);
    }
}
