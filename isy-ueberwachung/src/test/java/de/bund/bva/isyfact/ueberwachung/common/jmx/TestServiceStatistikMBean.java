/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.ueberwachung.common.jmx;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

import de.bund.bva.isyfact.datetime.test.TestClock;
import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import de.bund.bva.isyfact.ueberwachung.common.jmx.bean.Service;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;
import de.bund.bva.isyfact.ueberwachung.common.jmx.ServiceStatistikMBean;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;

/**
 * Tests für ServiceStatistikMBean.
 *
 * @author Capgemini, Thomas Timu
 * @version $Id: TestServiceStatistikMBean.java 144975 2015-08-18 13:07:56Z sdm_jmeisel $
 */
public class TestServiceStatistikMBean {
    /**
     * Logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(TestServiceStatistikMBean.class);

    /**
     * Testet ZaehleAufruf von ServiceStatistikMBean in Erste Minute.
     */
    @Test
    public void testZaehleAufrufErsteMinute() throws Exception {
        ServiceStatistikMBean mbean = new ServiceStatistikMBean();
        Random random = new SecureRandom();
        LOG.debug("Warte bis Anfang naechste Minute ...");
        DateTimeUtil.setClock(TestClock.at(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        int anzahlAufrufe = 10;
        int anzahlAufrufeLetzteMinute = 0;
        int anzahlFehlerLetzteMinute = 0;
        long durchschnittsDauerLetzteZehnAufrufe = 0;
        long durchschnittsDauerLetzteZehnAufrufeReferenz = 0;
        for (int count = 0; count < anzahlAufrufe; count++) {
            long dauer = random.nextInt(10000);
            boolean erfolgreich = random.nextBoolean();
            boolean fachlichErfolgreich = random.nextBoolean();
            mbean.zaehleAufruf(dauer, erfolgreich, fachlichErfolgreich);
            LOG.debug("Rufe MBean.zaehleAufruf auf mit ({},{},{})", dauer, erfolgreich, fachlichErfolgreich);
        }
        anzahlAufrufeLetzteMinute = mbean.getAnzahlAufrufeLetzteMinute();
        anzahlFehlerLetzteMinute = mbean.getAnzahlFehlerLetzteMinute();
        durchschnittsDauerLetzteZehnAufrufeReferenz = mbean.getDurchschnittsDauerLetzteAufrufe();
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :",
            anzahlFehlerLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        // the statistics from the current minute are not yet populated
        Assert.assertEquals(0, anzahlAufrufeLetzteMinute);
        Assert.assertEquals(0, anzahlFehlerLetzteMinute);
        Assert.assertTrue(durchschnittsDauerLetzteZehnAufrufeReferenz > 0);

        // wait 60 seconds and check the values.
        LOG.debug("Warte 1 Minute ...");
        ((TestClock)DateTimeUtil.getClock()).advanceBy(Duration.ofMinutes(1));
        anzahlAufrufeLetzteMinute = mbean.getAnzahlAufrufeLetzteMinute();
        anzahlFehlerLetzteMinute = mbean.getAnzahlFehlerLetzteMinute();
        durchschnittsDauerLetzteZehnAufrufe = mbean.getDurchschnittsDauerLetzteAufrufe();
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :",
            anzahlFehlerLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        Assert.assertEquals(anzahlAufrufe, anzahlAufrufeLetzteMinute);
        Assert.assertTrue(anzahlFehlerLetzteMinute > 0);
        Assert.assertEquals(durchschnittsDauerLetzteZehnAufrufe, durchschnittsDauerLetzteZehnAufrufeReferenz);
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Prüfungen für 1. Minute erfolgreich");
    }

    /**
     * Testet ZaehleAufruf von ServiceStatistikMBean mit 1. Minute ohne MBean Aufruf.
     */
    @Test
    public void testZaehleAufrufNachAbstand() throws Exception {
        ServiceStatistikMBean mbean = new ServiceStatistikMBean();
        Random random = new SecureRandom();
        // wait till the minute starts.
        LOG.debug("Warte bis Anfang naechste Minute ...");
        DateTimeUtil.setClock(TestClock.at(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)));
        int anzahlAufrufe = 10;
        int anzahlAufrufeLetzteMinute = 0;
        int anzahlFehlerLetzteMinute = 0;
        long durchschnittsDauerLetzteZehnAufrufe = 0;
        long durchschnittsDauerLetzteZehnAufrufeReferenz = 0;
        for (int count = 0; count < anzahlAufrufe; count++) {
            long dauer = random.nextInt(10000);
            boolean erfolgreich = random.nextBoolean();
            boolean fachlichErfolgreich = random.nextBoolean();
            mbean.zaehleAufruf(dauer, erfolgreich, fachlichErfolgreich);
            LOG.debug("Rufe MBean.zaehleAufruf auf mit (" + dauer + "," + erfolgreich + ","
                + fachlichErfolgreich + ")");
        }

        anzahlAufrufeLetzteMinute = mbean.getAnzahlAufrufeLetzteMinute();
        anzahlFehlerLetzteMinute = mbean.getAnzahlFehlerLetzteMinute();
        durchschnittsDauerLetzteZehnAufrufeReferenz = mbean.getDurchschnittsDauerLetzteAufrufe();
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :",
            anzahlFehlerLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        // the statistics from the current minute are not yet populated
        Assert.assertEquals(0, anzahlAufrufeLetzteMinute);
        Assert.assertEquals(0, anzahlFehlerLetzteMinute);
        Assert.assertTrue(durchschnittsDauerLetzteZehnAufrufeReferenz > 0);

        // wait 120 seconds and check the values. Since there were no calls in the last minute
        // number of calls and number of erroneous calls should be zero.
        LOG.debug("Warte 2 Minuten ...");
        ((TestClock)DateTimeUtil.getClock()).advanceBy(Duration.ofMinutes(2));
        anzahlAufrufeLetzteMinute = mbean.getAnzahlAufrufeLetzteMinute();
        anzahlFehlerLetzteMinute = mbean.getAnzahlFehlerLetzteMinute();
        durchschnittsDauerLetzteZehnAufrufe = mbean.getDurchschnittsDauerLetzteAufrufe();
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001,
            "AnzahlAufrufeLetzteMinute           :{}", anzahlAufrufeLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "AnzahlFehlerLetzteMinute            :",
            anzahlFehlerLetzteMinute);
        LOG.info(LogKategorie.METRIK, EreignisSchluessel.PLUEB00001, "DurchschnittsDauerLetzteZehnAufrufe :",
            durchschnittsDauerLetzteZehnAufrufeReferenz);
        Assert.assertEquals(0, anzahlAufrufeLetzteMinute);
        Assert.assertEquals(0, anzahlFehlerLetzteMinute);
        Assert.assertEquals(durchschnittsDauerLetzteZehnAufrufe, durchschnittsDauerLetzteZehnAufrufeReferenz);
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Prüfungen für 2. Minute erfolgreich");
    }
}
