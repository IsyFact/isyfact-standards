package de.bund.bva.isyfact.sicherheit.annotation;

import de.bund.bva.isyfact.aufrufkontext.stub.AufrufKontextVerwalterStub;
import de.bund.bva.isyfact.sicherheit.annotation.bean.ServiceIntf;
import de.bund.bva.isyfact.sicherheit.common.exception.AutorisierungFehlgeschlagenException;
import de.bund.bva.isyfact.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;
import de.bund.bva.isyfact.sicherheit.config.SicherheitTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SicherheitTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {"isy.logging.anwendung.name=test", "isy.logging.anwendung.typ=test", "isy.logging.anwendung.version=test"})
public class GesichertTest {

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
