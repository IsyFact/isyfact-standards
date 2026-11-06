package de.bund.bva.isyfact.batchrahmen;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Holder for the two test configurations of the {@code Batchrahmen.MaxWiederholungen}
 * feature, one for each shipped SQL schema script. Each configuration loads its script into
 * a separate in-memory H2 database and maps the entity with the matching JPA naming strategy.
 */
public final class AnwendungMitKonfigurationsParameterTestConfig {

    private AnwendungMitKonfigurationsParameterTestConfig() {
    }

    @Configuration
    @EnableTransactionManagement
    @EnableAspectJAutoProxy
    @EnableAutoConfiguration
    public static class TabellenErzeugenIf2Config {


        private static final DataSource SHARED_DATA_SOURCE = createDataSource();

        private static boolean schemaInitialized = false;

        private static DataSource createDataSource() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setUrl(
                "jdbc:h2:mem:isy-batchrahmen-mit-konfigurationsparameter;MODE=Oracle;DB_CLOSE_DELAY=-1");
            return dataSource;
        }

        @Bean
        public DataSource appDataSource() {
            return SHARED_DATA_SOURCE;
        }

        @Bean
        public DataSourceInitializer dataSourceInitializer() {
            if (schemaInitialized) {
                DataSourceInitializer initializer = new DataSourceInitializer();
                initializer.setDataSource(SHARED_DATA_SOURCE);
                return initializer;
            }
            schemaInitialized = true;

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("sql/tabellen-erzeugen-if2.sql"));
            populator.setContinueOnError(false);

            DataSourceInitializer initializer = new DataSourceInitializer();
            initializer.setDataSource(SHARED_DATA_SOURCE);
            initializer.setDatabasePopulator(populator);
            return initializer;
        }

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(SHARED_DATA_SOURCE);
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setPackagesToScan("de.bund.bva.isyfact.batchrahmen.persistence.rahmen");
            em.setDataSource(SHARED_DATA_SOURCE);
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
    }

    @Configuration
    @EnableTransactionManagement
    @EnableAspectJAutoProxy
    @EnableAutoConfiguration
    public static class TabellenErzeugenConfig {

        private static final DataSource SHARED_DATA_SOURCE = createDataSource();

        private static boolean schemaInitialized = false;

        private static DataSource createDataSource() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setUrl(
                "jdbc:h2:mem:isy-batchrahmen-tabellen-erzeugen;MODE=Oracle;DB_CLOSE_DELAY=-1");
            return dataSource;
        }

        @Bean
        public DataSource appDataSource() {
            return SHARED_DATA_SOURCE;
        }

        @Bean
        public DataSourceInitializer dataSourceInitializer() {
            if (schemaInitialized) {
                DataSourceInitializer initializer = new DataSourceInitializer();
                initializer.setDataSource(SHARED_DATA_SOURCE);
                return initializer;
            }
            schemaInitialized = true;

            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("sql/tabellen-erzeugen.sql"));
            populator.setContinueOnError(false);

            DataSourceInitializer initializer = new DataSourceInitializer();
            initializer.setDataSource(SHARED_DATA_SOURCE);
            initializer.setDatabasePopulator(populator);
            return initializer;
        }

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(SHARED_DATA_SOURCE);
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setPackagesToScan("de.bund.bva.isyfact.batchrahmen.persistence.rahmen");
            em.setDataSource(SHARED_DATA_SOURCE);
            em.setJpaDialect(new HibernateJpaDialect());
            em.getJpaPropertyMap().put(
                "hibernate.physical_naming_strategy",
                CamelCaseToUnderscoresNamingStrategy.class.getName()
            );

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
    }
}