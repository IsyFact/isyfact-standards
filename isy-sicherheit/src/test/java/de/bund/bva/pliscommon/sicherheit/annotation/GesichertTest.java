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
package de.bund.bva.pliscommon.sicherheit.annotation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.ServiceIntf;
import de.bund.bva.pliscommon.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;

@ContextConfiguration(locations = "/resources/spring/application_interceptor.xml")
public class GesichertTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ServiceIntf testBean;

    @SuppressWarnings("rawtypes")
    @Autowired
    private AufrufKontextVerwalterStub aufrufKontextVerwalter;

    @Before
    public void resetRollen() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] {});
    }

    @Test(expected = FehlerhafteServiceKonfigurationRuntimeException.class)
    public void testGesichertDurch_leeresRecht() {
        this.testBean.gesichertDurch_leeresRecht();
    }

    @Test(expected = FehlerhafteServiceKonfigurationRuntimeException.class)
    public void testGesichertDurch_Nichts() {
        this.testBean.gesichertDurch_Nichts();
    }

    @Test
    public void testPositiv_gesichertDurch_RechtA() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.testBean.gesichertDurch_RechtA();
    }

    @Test
    public void testPositiv_gesichertDurch_RechtB() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_B" });
        this.testBean.gesichertDurch_RechtB();
    }

    @Test
    public void testPositiv_gesichertDurch_RechtAundB() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_ABC" });
        this.testBean.gesichertDurch_RechtAundB();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_gesichertDurch_RechtA() {
        this.testBean.gesichertDurch_RechtA();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_gesichertDurch_RechtB() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.testBean.gesichertDurch_RechtB();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_gesichertDurch_RechtAundB_1() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.testBean.gesichertDurch_RechtAundB();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_gesichertDurch_RechtAundB_2() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_B" });
        this.testBean.gesichertDurch_RechtAundB();
    }
}
