package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletRegistration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.ueberwachung.config.LoadbalancerSecurityConfiguration;
import de.bund.bva.isyfact.ueberwachung.config.LoadbalancerServletConfigurationProperties;
import de.bund.bva.isyfact.ueberwachung.service.loadbalancer.LoadbalancerServlet;

@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(ServletRegistration.class)
public class IsyLoadbalancerAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "isy.ueberwachung.loadbalancer")
    public LoadbalancerServletConfigurationProperties loadbalancerServletConfigurationProperties() {
        return new LoadbalancerServletConfigurationProperties();
    }

    @Bean
    public ServletRegistrationBean<LoadbalancerServlet> loadbalancerservlet(
            LoadbalancerServletConfigurationProperties properties) {
        ServletRegistrationBean<LoadbalancerServlet> loadbalancerServlet =
                new ServletRegistrationBean<>(new LoadbalancerServlet());
        loadbalancerServlet.setLoadOnStartup(1);
        loadbalancerServlet.addUrlMappings(LoadbalancerSecurityConfiguration.LOADBALANCER_SERVLET_PATH);

        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("isAliveFileLocation", properties.getIsAliveFileLocation());

        loadbalancerServlet.setInitParameters(initParameters);

        return loadbalancerServlet;
    }
}
