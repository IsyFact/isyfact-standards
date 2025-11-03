package de.bund.bva.isyfact.ueberwachung.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import de.bund.bva.isyfact.ueberwachung.config.LoadbalancerSecurityConfiguration;

@EnableConfigurationProperties
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ SecurityFilterChain.class, HttpSecurity.class })
@Import(LoadbalancerSecurityConfiguration.class)
public class IsyLoadbalancerSecurityAutoConfiguration {
}