package de.bund.bva.isyfact.persistence.datetime.attributeconverter.test;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AbstractJpaTest.TestConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@Transactional
public abstract class AbstractJpaTest {

    @Autowired
    protected TestEntityManager entityManager;

    @EnableTransactionManagement
    @AutoConfigureTestEntityManager
    @AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
    static class TestConfig {
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

    @After
    public void commit() {
        entityManager.flush();
    }
}