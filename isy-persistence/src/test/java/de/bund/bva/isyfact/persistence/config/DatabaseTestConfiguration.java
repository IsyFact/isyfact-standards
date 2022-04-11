package de.bund.bva.isyfact.persistence.config;

import java.util.Arrays;
import java.util.Collections;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import de.bund.bva.isyfact.persistence.autoconfigure.IsyPersistenceOracleAutoConfiguration;
import de.bund.bva.isyfact.persistence.datetime.attributeconverter.PeriodAttributeConverterTest;


@Profile("!batch")
/*@SpringBootTest(classes = {
    IsyPersistenceOracleAutoConfiguration.class,
    DatabaseTestConfiguration.class
})*/
@EnableTransactionManagement
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)//<
//@DatabaseSetup("testPeriodSetup.xml")
public class DatabaseTestConfiguration {

    @Bean
    //@Profile("h2")
    //@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
    DataSourceScriptDatabaseInitializer dataSourceInitializer(/*@Qualifier("dataSource")*/ DataSource dataSource) {
        DatabaseInitializationSettings initializationSettings = new DatabaseInitializationSettings();
        initializationSettings.setContinueOnError(true);
        initializationSettings.setSchemaLocations(Arrays.asList(
            "classpath:/sql.persistence/reset.sql",
            "file:src/main/skripte/db-versioning-templates/db-schema-name/db-install-x.y.z_0/04-1_version_erzeugen.sql",
            "file:src/main/skripte/db-versioning-templates/db-schema-name/db-install-x.y.z_0/04-2_tabellen_erzeugen.sql"
            //"classpath:/sql/bnvzqk/tables.sql",//TODO make copy / custom version of this if necessary
            //"classpath:/sql/bnvzqk/procedures.sql"//TODO make copy / custom version of this if necessary
        ));
        /*initializationSettings.setDataLocations(Collections.singletonList(
            "classpath:/sql/bnvzqk/set_schema_version.sql"//TODO make copy / custom version of this if necessary
        ));*/
        return new DataSourceScriptDatabaseInitializer(dataSource, initializationSettings);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("de.bund.bva.isyfact.persistence.datetime.attributeconverter");
        em.setDataSource(dataSource);
        em.setJpaDialect(new HibernateJpaDialect());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setShowSql(false);
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}