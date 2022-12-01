package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;
import de.bund.bva.isyfact.persistence.exception.FehlerSchluessel;

/**
 * Test to check if the DataSource configured in application-h2.properties was initialized with the initializer in
 * {@link TestDataSourceInitializationConfig} before the schema validation in {@link IsyDataSource} kicks in.
 */
public class DataSourceInitializerTest {

    @Test
    public void testSchemaValidationFailsWhenDataSourceNotInitialized() {
        try {
            new SpringApplicationBuilder()
                    .sources(IsyPersistenceAutoConfiguration.class)
                    .profiles("h2")
                    .run();
            fail();
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains(FehlerSchluessel.PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN));
        }
    }

    @Test
    public void testSchemaValidationSuccessfulWhenDataSourceInitialized() throws SQLException {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(IsyPersistenceAutoConfiguration.class, TestDataSourceInitializationConfig.class)
                .profiles("h2")
                .run();

        IsyDataSource dataSource = context.getBean(IsyDataSource.class);
        try (Statement statement = dataSource.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from M_SCHEMA_VERSION");
            resultSet.next();
            String versionNummer = resultSet.getString("VERSION_NUMMER");

            assertEquals("4.1.0", versionNummer);

            // reset the database, so that it is not initialized for other tests
            statement.execute("drop all objects");
        }
    }

    /**
     * Test configuration for initializing the data source before its schema is validated.
     */
    static class TestDataSourceInitializationConfig {

        @Bean
        DataSourceScriptDatabaseInitializer dataSourceInitializer(@Qualifier("dataSource") DataSource dataSource) {
            DatabaseInitializationSettings initializationSettings = new DatabaseInitializationSettings();
            initializationSettings.setContinueOnError(true);
            initializationSettings.setSchemaLocations(Arrays.asList(
                    "classpath:/sql.persistence/reset.sql",
                    "file:src/main/skripte/db-versioning-templates/db-schema-name/db-install-x.y.z_0/04-1_version_erzeugen.sql",
                    "file:src/main/skripte/db-versioning-templates/db-schema-name/db-install-x.y.z_0/04-2_tabellen_erzeugen.sql"
            ));
            initializationSettings.setDataLocations(Collections.singletonList(
                    "classpath:/sql.persistence/set_schema_version.sql"
            ));
            return new DataSourceScriptDatabaseInitializer(dataSource, initializationSettings);
        }

    }

}
