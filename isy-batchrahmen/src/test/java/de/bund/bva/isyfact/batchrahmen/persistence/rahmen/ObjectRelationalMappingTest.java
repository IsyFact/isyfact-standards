package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.HashSet;

import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

class ObjectRelationalMappingTest {

    @Configuration
    @AutoConfigureDataJpa
    @EntityScan(basePackageClasses = BatchStatus.class)
    static class DaoTestConfig {
        @Bean
        BatchStatusDao batchStatusDao(EntityManagerFactory emf) {
            return new BatchStatusDao(emf);
        }
    }

    private static class OrmTestMethods {
        @Test
        @Transactional
        void testOrm(@Autowired BatchStatusDao dao, @Autowired EntityManagerFactory emf) {
            final BatchStatus input = new BatchStatus();
            input.setBatchId("id1");
            input.setBatchStatus("status1");
            input.setBatchName("name1");
            input.setDatumLetzterAbbruch(Timestamp.from(LocalDateTime.of(2020, Month.FEBRUARY, 26, 21, 13).toInstant(ZoneOffset.UTC)));
            input.setDatumLetzterErfolg(Timestamp.from(LocalDateTime.of(2020, Month.FEBRUARY, 26, 22, 13).toInstant(ZoneOffset.UTC)));
            input.setDatumLetzterStart(Timestamp.from(LocalDateTime.of(2020, Month.FEBRUARY, 26, 23, 13).toInstant(ZoneOffset.UTC)));
            input.setSatzNummerLetztesCommit(10);
            input.setKonfigurationsParameter(new HashSet<>());

            dao.createBatchStatus(input);
            EntityManagerFactoryUtils.getTransactionalEntityManager(emf).flush();

            assertThat(dao.leseBatchStatus(input.getBatchId())).usingRecursiveComparison().isEqualTo(input);
        }
    }

    @Nested
    @SpringBootTest(
            webEnvironment = SpringBootTest.WebEnvironment.NONE,
            classes = DaoTestConfig.class,
            properties = {
                    "spring.main.banner-mode = off",
                    "spring.sql.init.schema-locations = classpath:sql/tabellen-erzeugen.sql",
                    "spring.jpa.hibernate.ddl-auto = none"
            }
    )
    class CamalCaseToUnderscoresNamingTest extends OrmTestMethods {
    }

    @Nested
    @SpringBootTest(
            webEnvironment = SpringBootTest.WebEnvironment.NONE,
            classes = DaoTestConfig.class,
            properties = {
                    "spring.main.banner-mode = off",
                    "spring.sql.init.schema-locations = classpath:sql/tabellen-erzeugen.sql",
                    "spring.jpa.hibernate.ddl-auto = none",
                    "spring.jpa.hibernate.naming.physical-strategy = org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
                    "spring.jpa.mapping-resources = resources/isy-batchrahmen/hibernate/hibernate-mapping.xml"
            }
    )
    class PhysicalNamingTest extends OrmTestMethods {
    }

    @Nested
    @SpringBootTest(
            webEnvironment = SpringBootTest.WebEnvironment.NONE,
            classes = DaoTestConfig.class,
            properties = {
                    "spring.main.banner-mode = off",
                    "spring.sql.init.schema-locations = classpath:sql/tabellen-erzeugen-if2.sql",
                    "spring.jpa.hibernate.ddl-auto = none",
                    "spring.jpa.mapping-resources = resources/isy-batchrahmen/hibernate/hibernate-mapping-if2.xml",
            }
    )
    class CamalCaseToUnderscoresNamingOldBatchStatusTableTest extends OrmTestMethods {
    }
}
