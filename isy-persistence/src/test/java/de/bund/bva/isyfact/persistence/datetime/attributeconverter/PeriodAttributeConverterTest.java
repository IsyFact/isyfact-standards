package de.bund.bva.isyfact.persistence.datetime.attributeconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Period;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;
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

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import de.bund.bva.isyfact.persistence.datetime.attributeconverter.test.TestPeriodEntity;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PeriodAttributeConverterTest.TestConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@Transactional
@DatabaseSetup("testPeriodSetup.xml")
public class PeriodAttributeConverterTest {

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

    @Test
    @ExpectedDatabase(value = "testPeriodExpectedWriteNull.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumnNull() throws Exception {
        TestPeriodEntity testEntity = new TestPeriodEntity();
        testEntity.setId(2);
        testEntity.setPeriod(null);

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttributeNull() throws Exception {
        TestPeriodEntity testEntity = entityManager.find(TestPeriodEntity.class, 1L);

        assertNull(testEntity.getPeriod());
    }

    @Test
    @ExpectedDatabase(value = "testPeriodExpectedWritePeriod.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void convertToDatabaseColumn() throws Exception {
        TestPeriodEntity testEntity = new TestPeriodEntity();
        testEntity.setId(2);
        testEntity.setPeriod(Period.of(3, 4, 5));

        entityManager.persist(testEntity);
    }

    @Test
    public void convertToEntityAttribute() throws Exception {
        TestPeriodEntity testEntity = entityManager.find(TestPeriodEntity.class, 0L);

        assertEquals(Period.of(10, 20, 30), testEntity.getPeriod());
    }
}