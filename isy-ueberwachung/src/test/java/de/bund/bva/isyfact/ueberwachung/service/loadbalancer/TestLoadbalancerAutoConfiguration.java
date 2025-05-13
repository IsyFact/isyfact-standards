package de.bund.bva.isyfact.ueberwachung.service.loadbalancer;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.ueberwachung.config.LoadbalancerServletConfigurationProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestLoadbalancerAutoConfiguration.TestConfig.class, properties = {"isy.logging.autoconfiguration.enabled=false"})
public class TestLoadbalancerAutoConfiguration {

    @Autowired
    private ServletRegistrationBean<LoadbalancerServlet> registrationBean;

    @Autowired
    private LoadbalancerServletConfigurationProperties loadbalancerServletConfigurationProperties;

    @Test
    public void testAutowiring() {
        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getServlet());
    }

    @Test
    public void isAliveDefaultConfigTest() {
        assertEquals("/WEB-INF/classes/config/isAlive", loadbalancerServletConfigurationProperties.getIsAliveFileLocation());
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {}

}
