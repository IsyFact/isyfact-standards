package de.bund.bva.isyfact.ueberwachung.metrics.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.datetime.test.TestClock;
import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class,
                webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"isy.logging.anwendung.name=Test",
                              "isy.logging.anwendung.typ=Test",
                              "isy.logging.anwendung.version=0.1"})
public class TestDefaultServiceStatistik {

    @Autowired
    private DefaultServiceStatistik serviceStatistik;

    @Autowired
    private MeterRegistry meterRegistry;

    private final Random random = new SecureRandom();

    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestDefaultServiceStatistik.class);

    /**
     * Tests ZaehleAufruf in the first minute
     */
    @Test
    public void testZaehleAufrufErsteMinute() {
        LOG.debug("Setze Zeit auf Anfang einer vollen Minute");
        DateTimeUtil.setClock(TestClock.at(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));

        final int anzahlAufrufe = 10;
        for (int count = 0; count < anzahlAufrufe; count++) {
            Duration dauer = Duration.ofMillis(random.nextInt(10000));
            boolean erfolgreich = random.nextBoolean();
            boolean fachlichErfolgreich = random.nextBoolean();
            serviceStatistik.zaehleAufruf(dauer, erfolgreich, fachlichErfolgreich);
            LOG.debug("Rufe MBean.zaehleAufruf auf mit ({},{},{})", dauer, erfolgreich, fachlichErfolgreich);
        }

        Gauge anzahlAufrufeLetzteMinute = meterRegistry.get("anzahlAufrufe.LetzteMinute").gauge();
        Gauge anzahlFehlerLetzteMinute = meterRegistry.get("anzahlFehler.LetzteMinute").gauge();
        Gauge durchschnittsDauerLetzteZehnAufrufe =  meterRegistry.get("durchschnittsDauer.LetzteAufrufe").gauge();

        double durchschnittsDauerLetzteZehnAufrufeReferenz = durchschnittsDauerLetzteZehnAufrufe.value();

        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute.value());
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :{}",
            anzahlFehlerLetzteMinute.value());
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :{}",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        // the statistics from the current minute are not yet populated
        assertEquals(0, anzahlAufrufeLetzteMinute.value(), 0.0);
        assertEquals(0, anzahlFehlerLetzteMinute.value(), 0.0);
        assertTrue(durchschnittsDauerLetzteZehnAufrufeReferenz > 0.0);

        LOG.debug("Stelle Uhr um 1 Minute nach vorn");
        ((TestClock)DateTimeUtil.getClock()).advanceBy(Duration.ofMinutes(1));

        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute.value());
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :{}",
            anzahlFehlerLetzteMinute.value());
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :{}",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        assertEquals(anzahlAufrufe, anzahlAufrufeLetzteMinute.value(), 0.0);
        assertTrue(anzahlFehlerLetzteMinute.value() > 0.0);
        assertEquals(durchschnittsDauerLetzteZehnAufrufeReferenz, durchschnittsDauerLetzteZehnAufrufe.value(), 0.0);
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Prüfungen für 1. Minute erfolgreich");
    }

    /**
     * Tests ZaehleAufruf without MBean call in the first minute .
     */
    @Test
    public void testZaehleAufrufNachAbstand() {
        // wait till the minute starts.
        LOG.debug("Setze Zeit auf Anfang einer vollen Minute");
        DateTimeUtil.setClock(TestClock.at(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        final int anzahlAufrufe = 10;
        for (int count = 0; count < anzahlAufrufe; count++) {
            Duration dauer = Duration.ofMillis(random.nextInt(10000));
            boolean erfolgreich = random.nextBoolean();
            boolean fachlichErfolgreich = random.nextBoolean();
            serviceStatistik.zaehleAufruf(dauer, erfolgreich, fachlichErfolgreich);
            LOG.debug("Rufe MBean.zaehleAufruf auf mit ({},{},{})", dauer, erfolgreich, fachlichErfolgreich);
        }

        Gauge anzahlAufrufeLetzteMinute = meterRegistry.get("anzahlAufrufe.LetzteMinute").gauge();
        Gauge anzahlFehlerLetzteMinute = meterRegistry.get("anzahlFehler.LetzteMinute").gauge();
        Gauge durchschnittsDauerLetzteZehnAufrufe =  meterRegistry.get("durchschnittsDauer.LetzteAufrufe").gauge();

        double durchschnittsDauerLetzteZehnAufrufeReferenz = durchschnittsDauerLetzteZehnAufrufe.value();

        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :{}",
            anzahlFehlerLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :{}",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        // the statistics from the current minute are not yet populated
        assertEquals(0, anzahlAufrufeLetzteMinute.value(), 0.0);
        assertEquals(0, anzahlFehlerLetzteMinute.value(), 0.0);
        assertTrue(durchschnittsDauerLetzteZehnAufrufeReferenz > 0.0);

        // wait 120 seconds and check the values. Since there were no calls in the last minute
        // number of calls and number of erroneous calls should be zero.
        LOG.debug("Stelle Uhr um 2 Minuten nach vorn");
        ((TestClock)DateTimeUtil.getClock()).advanceBy(Duration.ofMinutes(2));

        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :",
            anzahlFehlerLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        assertEquals(0, anzahlAufrufeLetzteMinute.value(), 0.0);
        assertEquals(0, anzahlFehlerLetzteMinute.value(), 0.0);
        assertEquals(durchschnittsDauerLetzteZehnAufrufeReferenz, durchschnittsDauerLetzteZehnAufrufe.value(), 0.0);
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Prüfungen für 2. Minute erfolgreich");
    }
}
