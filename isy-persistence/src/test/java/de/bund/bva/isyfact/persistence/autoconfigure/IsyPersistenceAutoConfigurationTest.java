package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

class IsyPersistenceAutoConfigurationTest {

    ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
            .withConfiguration(AutoConfigurations.of(IsyPersistenceAutoConfiguration.class));


    @Test
    void testDataSourceUrlPropertyNichtGesetzt() {
        contextRunner.withPropertyValues(
            "isy.logging.anwendung.name=test",
            "isy.logging.anwendung.typ=test",
            "isy.logging.anwendung.version=test"
        ).run(context -> {
            assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(
                () ->
                context.getBean(IsyDataSource.class)
            );
        });
    }

    @Test
    void testPropertiesFehlerhaft() {
        contextRunner
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
            "isy.persistence.datasource.url=test",
            "spring.sql.init.enabled=false"
        ).run(context -> {
            assertThat(context).hasFailed()
                .getFailure()
                .cause()
                .isInstanceOf(UnsatisfiedDependencyException.class);
        });
    }

    @Test
    void testExpectCyclicReferenceException() {
        contextRunner.withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                "isy.logging.anwendung.name=test",
                "isy.logging.anwendung.typ=test",
                "isy.logging.anwendung.version=test",
                // h2 profile properties
                "isy.persistence.datasource.url=jdbc:h2:mem:isypersistence;TRACE_LEVEL_FILE=4;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                "isy.persistence.datasource.username=isypersistence",
                "isy.persistence.datasource.password=password",
                "isy.persistence.datasource.driver-class-name=org.h2.Driver",
                "isy.persistence.datasource.type=org.springframework.jdbc.datasource.SimpleDriverDataSource",
                "isy.persistence.datasource.schema-version=4.1.0",
                "isy.persistence.datasource.schema-invalid-version-action=fail",
                "spring.jpa.properties.hibernate.hbm2ddl.auto=none"
            )
            .run(context -> {
                assertThat(context).hasFailed()
                    .getFailure()
                    .cause()
                        .hasRootCauseInstanceOf(BeanCurrentlyInCreationException.class)
                        .hasMessageContaining("Is there an unresolvable circular reference");
            });
    }

    @Test
    void testValidAutoConfiguration() {
        contextRunner.withUserConfiguration(
                TestConfig.class,
                DataSourceInitializerTest.TestDataSourceInitializationConfig.class
            )
            .withPropertyValues(
                "isy.logging.anwendung.name=test",
                "isy.logging.anwendung.typ=test",
                "isy.logging.anwendung.version=test",
                // fix cyclic reference
                "spring.sql.init.mode=never",
                // h2 profile properties
                "isy.persistence.datasource.url=jdbc:h2:mem:isypersistence;TRACE_LEVEL_FILE=4;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                "isy.persistence.datasource.username=isypersistence",
                "isy.persistence.datasource.password=password",
                "isy.persistence.datasource.driver-class-name=org.h2.Driver",
                "isy.persistence.datasource.type=org.springframework.jdbc.datasource.SimpleDriverDataSource",
                "isy.persistence.datasource.schema-version=4.1.0",
                "isy.persistence.datasource.schema-invalid-version-action=fail",
                "spring.jpa.properties.hibernate.hbm2ddl.auto=none"
            ).run(context -> {
                assertThat(context)
                    .hasNotFailed()
                    .hasSingleBean(IsyDataSource.class)
                    .hasBean("dataSource")
                    .hasBean("appDataSource");
            });
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }

}
