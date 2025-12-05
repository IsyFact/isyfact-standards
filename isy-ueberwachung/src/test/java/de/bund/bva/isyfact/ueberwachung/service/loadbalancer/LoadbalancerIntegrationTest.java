package de.bund.bva.isyfact.ueberwachung.service.loadbalancer;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.Appender;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;
import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestMethodOrder(MethodOrderer.MethodName.class)
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {IsyHealthAutoConfiguration.class, LoadbalancerIntegrationTest.TestConfig.class},
        properties = {
                "isy.logging.anwendung.name=LoadbalancerIntegrationTest",
                "isy.logging.anwendung.version=1.0.0-SNAPSHOT",
                "isy.logging.anwendung.typ=Integrationstest"
        }
)
public class LoadbalancerIntegrationTest {

    @Autowired
    WebTestClient webClient;

    @Test
    //Test returns forbidden because isAlive file is not found in temp test tomcat
    public void testDoGetIsAlive() throws ServletException, IOException {
        urlCall("/Loadbalancer").expectStatus().isForbidden();

    }

    @Test
    void test1_initialerStatusUnknown() {
        urlCall("/xxxLoadbalancer").expectStatus().isNotFound();
    }


    //Test url without auth
    private WebTestClient.ResponseSpec urlCall(String endpoint) {
        return webClient.get().uri(endpoint).exchange();
    }


    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {

    }
}
