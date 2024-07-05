package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;
import de.bund.bva.isyfact.persistence.exception.FehlerSchluessel;

/**
 * Test to check if the DataSource configured in application-h2.properties was initialized with the initializer in
 * {@link TestDataSourceInitializationConfig} before the schema validation in {@link IsyDataSource} kicks in.
 */
class DataSourceInitializerTest {

    ApplicationContextRunner contextRunner = new ApplicationContextRunner(AnnotationConfigApplicationContext::new)
            .withInitializer(applicationContext -> {
                applicationContext
                    .getEnvironment().addActiveProfile("h2");
            })
            // read application-h2.properties
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withConfiguration(AutoConfigurations.of(IsyPersistenceAutoConfiguration.class));

    @Test
    void testSchemaValidationFailsWhenDataSourceNotInitialized() {
        contextRunner.run(context -> {
            assertThat(context).hasFailed()
                .getFailure()
                .hasMessageContaining(FehlerSchluessel.PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN);
        });
    }

    @Test
    void testSchemaValidationSuccessfulWhenDataSourceInitialized() throws SQLException {
        contextRunner
            .withUserConfiguration(TestDataSourceInitializationConfig.class)
            .run(context -> {
                assertThat(context).hasNotFailed();
                IsyDataSource dataSource = context.getBean(IsyDataSource.class);
                try (Statement statement = dataSource.getConnection().createStatement()) {
                    ResultSet resultSet = statement.executeQuery("select * from M_SCHEMA_VERSION");
                    resultSet.next();
                    String versionNummer = resultSet.getString("VERSION_NUMMER");

                    assertThat(versionNummer).isEqualTo("4.1.0");

                    // reset the database, so that it is not initialized for other tests
                    statement.execute("drop all objects");
                }
            });
    }

    /**
     * Test configuration for initializing the data source before its schema is validated.
     */
    static class TestDataSourceInitializationConfig {

        @Bean
        DataSourceScriptDatabaseInitializer dataSourceInitializer(@Qualifier("dataSource") DataSource dataSource) {
            DatabaseInitializationSettings initializationSettings = new DatabaseInitializationSettings();
            initializationSettings.setContinueOnError(true);
            initializationSettings.setSchemaLocations(Arrays.asList("classpath:/sql.persistence/reset.sql", "classpath:/sql.persistence/create_version_table.sql"));
            initializationSettings.setDataLocations(Collections.singletonList("classpath:/sql.persistence/set_schema_version.sql"));
            return new DataSourceScriptDatabaseInitializer(dataSource, initializationSettings);
        }

    }

}
