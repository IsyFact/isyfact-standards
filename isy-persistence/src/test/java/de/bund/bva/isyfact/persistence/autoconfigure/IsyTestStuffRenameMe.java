package de.bund.bva.isyfact.persistence.autoconfigure;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.bund.bva.isyfact.persistence.config.DatabaseTestConfiguration;
import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

@RunWith(SpringRunner.class)
@ActiveProfiles({ "h2" })
@SpringBootTest(classes = {
    DatabaseTestConfiguration.class,
    IsyPersistenceOracleAutoConfiguration.class
}, properties = {"isy.persistence.oracle.datasource.databaseurl=irgendwas",
"isy.persistence.oracle.datasource.databaseUsername=asdf",
"isy.persistence.oracle.datasource.databasePassword=pass"})

public class IsyTestStuffRenameMe {

    @Autowired
    @Qualifier("appDataSource")
    DataSource dataSource;

    @Test
    public void testDataBaseInitBeforeVersionValidationError(/*@Qualifier("dataSource") DataSource dataSource*/){
        //        Map<String, Object> properties = new HashMap<>();
        //
        //        properties.put("isy.logging.anwendung.name", "test");
        //        properties.put("isy.logging.anwendung.typ", "test");
        //        properties.put("isy.logging.anwendung.version", "test");
        //
        //        ConfigurableApplicationContext context = new SpringApplicationBuilder()
        //            .sources(TestConfig.class)
        //            .properties(properties)
        //            .run();
        //
        //        IsyDataSource isyDataSource = new IsyPersistenceOracleAutoConfiguration()
        //            .appDataSource(dataSource, new DatabaseProperties());
        DataSourceScriptDatabaseInitializer scriptDBInitializer = new DataSourceScriptDatabaseInitializer(
            dataSource,
            new DatabaseInitializationSettings());
        boolean b = scriptDBInitializer.initializeDatabase();
    }
}
