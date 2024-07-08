package de.bund.bva.isyfact.logging.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.logging.AbstractLogTest;
import de.bund.bva.isyfact.logging.autoconfigure.IsyLoggingAutoConfiguration;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielKlasse2;
import de.bund.bva.isyfact.logging.hilfsklassen.TestZielParameterPerson;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestZielKlasse.class,
    TestZielKlasse2.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=IsyLogging",
    "isy.logging.anwendung.typ=Test",
    "isy.logging.anwendung.version=0.0.0",
    "isy.logging.component.loggedauer=true",
    "isy.logging.component.loggeaufruf=true"
})
@ImportAutoConfiguration(IsyLoggingAutoConfiguration.class)
public class LogMethodInterceptorAnnotationTest extends AbstractLogTest {

    @Autowired
    private TestZielKlasse testZielKlasse;

    @Autowired
    private TestZielKlasse2 testZielKlasse2;

    @Test
    public void testBoundaryLogAdvice() throws Throwable {
        testZielKlasse.setzeName(new TestZielParameterPerson("Nachname"), "Name");
        pruefeLogdatei("testAufrufErfolgreichInterceptor");
    }

    @Test
    public void testComponentLogAdvice() throws Throwable {
        testZielKlasse2.setzeName(new TestZielParameterPerson("Nachname"), "Name");
        pruefeLogdatei("testAufrufErfolgreichComponentInterceptor");
    }
}
