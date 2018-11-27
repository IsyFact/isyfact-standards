package de.bund.bva.isyfact.logging.util;

import de.bund.bva.isyfact.logging.AbstractLogTest;
import de.bund.bva.isyfact.logging.autoconfigure.IsyLoggingAutoConfiguration;
import de.bund.bva.isyfact.logging.config.IsyLoggingApplicationLoggerProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(IsyLoggingAutoConfiguration.class)
@SpringBootTest(classes = ApplicationListenerInitTest.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "isy.logging.anwendung.name=IsyLogging",
    "isy.logging.anwendung.typ=Test",
    "isy.logging.anwendung.version=0.0.0"
})
public class ApplicationListenerInitTest extends AbstractLogTest {

    @Autowired
    private IsyLoggingApplicationLoggerProperties properties;

    @Test
    public void populateIsyLoggingApplicationLoggerProperties() {
        assertEquals("IsyLogging", properties.getName());
        assertEquals("Test", properties.getTyp());
        assertEquals("0.0.0", properties.getVersion());
    }

    static class TestConfiguration {

    }
}
