package de.bund.bva.isyfact.batchrahmen;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Test configuration that builds a database without the BATCHSTATUS_KONFIGURATIONSPARAMETER
 * table (only BATCHSTATUS). This reproduces the state described in ticket IFS-5834:
 * a batch without the {@code Batchrahmen.MaxWiederholungen} feature must also be runnable
 * without this table.
 */
@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableAutoConfiguration
public class AnwendungOhneKonfigurationsParameterTestConfig {

    @Bean
    public DataSource appDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:./target/isy-batchrahmen-ohne-konfigurationsparameter;MODE=Oracle");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan("de.bund.bva.isyfact.batchrahmen.persistence.rahmen");
        em.setDataSource(dataSource);
        em.setJpaDialect(new HibernateJpaDialect());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
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

    @Bean
    public SchemaInitializer schemaInitializer(DataSource dataSource) {
        return new SchemaInitializer(dataSource);
    }

    /**
     * Creates only the BATCHSTATUS table when the context starts - without the
     * configuration parameter table - to reproduce the ticket case.
     */
    public static class SchemaInitializer implements InitializingBean {

        private final DataSource dataSource;

        public SchemaInitializer(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void afterPropertiesSet() throws SQLException {
            // Delete leftover H2 database files from previous test runs to ensure clean state
            String dbPath = "./target/isy-batchrahmen-ohne-konfigurationsparameter";
            for (String suffix : new String[]{"mv.db", "trace.db", "db"}) {
                File file = new File(dbPath + "." + suffix);
                if (file.exists()) {
                    file.delete();
                }
            }

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                    new ClassPathResource("sql/tabellen-erzeugen-if2-ohne-konfigurationsparameter.sql"));
            populator.execute(dataSource);
        }
    }
}