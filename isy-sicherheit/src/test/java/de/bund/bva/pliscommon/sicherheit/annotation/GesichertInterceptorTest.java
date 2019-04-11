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

import de.bund.bva.pliscommon.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.Service2Intf;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.ServiceImpl;
import de.bund.bva.pliscommon.sicherheit.annotation.bean.ServiceIntf;
import de.bund.bva.pliscommon.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.pliscommon.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;
import de.bund.bva.pliscommon.sicherheit.config.SicherheitTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.util.SimpleMethodInvocation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SicherheitTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
properties = {"isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test"})
public class GesichertInterceptorTest {

    @Autowired
    private ServiceIntf testBean;

    @Autowired
    private Service2Intf test2Bean;

    @SuppressWarnings("rawtypes")
    @Autowired
    private AufrufKontextVerwalterStub aufrufKontextVerwalter;

    @Autowired
    private GesichertInterceptor interceptor;

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
    
    @Test(expected = FehlerhafteServiceKonfigurationRuntimeException.class)
    public void testKeineGesichertAnnotation() throws NoSuchMethodException {
        AnnotationSicherheitAttributeSource anno = new AnnotationSicherheitAttributeSource();
        try {
            anno.getBenoetigeRechte(this.getClass().getMethod("testKeineGesichertAnnotation"),
                this.getClass());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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

    @Test
    public void testPositiv_klasseGesichertDurch_RechtA() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.test2Bean.gesichertDurch_RechtA();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_klasseGesichertDurch_RechtA() {
        this.test2Bean.gesichertDurch_RechtA();
    }

    @Test
    public void testPositiv_klasseGesichertDurch_RechtB() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_B" });
        this.test2Bean.gesichertDurch_RechtB();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_klasseGesichertDurch_RechtB() {
        this.aufrufKontextVerwalter.getAufrufKontext().setRolle(new String[] { "Rolle_A" });
        this.test2Bean.gesichertDurch_RechtB();
    }

    @Test(expected = AutorisierungFehlgeschlagenException.class)
    public void testNegativ_statischeMethodeGesichert() throws Throwable{
        SimpleMethodInvocation invocation = new SimpleMethodInvocation(testBean, ServiceImpl.class.getMethod("gesichertDurch_RechtA"), new Object[]{} );
        interceptor.invoke(invocation);
    }
}
