package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.HashSet;

import jakarta.persistence.EntityManagerFactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

class ObjectRelationalMappingTest {
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
    @Configuration
    @AutoConfigureDataJpa
    @EntityScan(basePackageClasses = BatchStatus.class)
    static class DaoTestConfig {
        @Bean
        BatchStatusDao batchStatusDao(EntityManagerFactory emf) {
            return new BatchStatusDao(emf);
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
