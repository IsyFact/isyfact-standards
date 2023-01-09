package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

class IsyPersistenceOracleAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IsyPersistenceOracleAutoConfiguration.class));

    @Test
    void testOraclePropertiesNichtGesetzt() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(IsyDataSource.class));
    }

    @Test
    void testOraclePropertiesUnvollstaendigGesetzt() {
        contextRunner.withPropertyValues("isy.persistence.oracle.datasource.database-url=test")
                .run(context -> assertThat(context).getFailure().isInstanceOf(UnsatisfiedDependencyException.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "isy.persistence.oracle.datasource.database-url=test",
            "isy.persistence.oracle.datasource.database_url=test",
            "isy.persistence.oracle.datasource.databaseUrl=test",
            "isy.persistence.oracle.datasource.databaseurl=test"
    })
    void testAlternativePropertySpelling(String property) {
        contextRunner.withPropertyValues(
                        property,
                        "isy.persistence.oracle.datasource.databaseUsername=test",
                        "isy.persistence.oracle.datasource.databasePassword=test"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(IsyPersistenceOracleAutoConfiguration.class);
                    assertThat(context).hasSingleBean(IsyDataSource.class);
                });

    }
}
