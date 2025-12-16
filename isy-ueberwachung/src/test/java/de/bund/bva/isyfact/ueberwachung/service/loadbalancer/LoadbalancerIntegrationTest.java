package de.bund.bva.isyfact.ueberwachung.service.loadbalancer;

import de.bund.bva.isyfact.ueberwachung.autoconfigure.IsyHealthAutoConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;
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
    public void testDoGetIsAlive() {
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
