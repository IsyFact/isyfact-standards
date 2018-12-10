package de.bund.bva.isyfact.batchrahmen;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import de.bund.bva.isyfact.batchrahmen.sicherheit.AccessManagerStub;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextFactoryImpl;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextVerwalterImpl;
import de.bund.bva.pliscommon.konfiguration.common.impl.ReloadablePropertyKonfiguration;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.pliscommon.sicherheit.impl.SicherheitImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class AnwendungTestConfig {

    @Bean
    public DataSource appDataSource() {
        /*
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .addScript("/sql/tabellen-erzeugen.sql").build();
        */
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:~/isy-batchrahmen;MODE=Oracle");

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
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setShowSql(false);
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        return jpaTransactionManager;
    }

    @Bean
    public AccessManagerStub accessManagerStub() {
        return new AccessManagerStub();
    }

    @Bean
    public AufrufKontextFactory aufrufKontextFactory() {
        return new AufrufKontextFactoryImpl<>();
    }

    @Bean
    public AufrufKontextVerwalter aufrufKontextVerwalter() {
        return new AufrufKontextVerwalterImpl();
    }

    @Bean
    public Sicherheit sicherheit(AccessManagerStub accessManagerStub, AufrufKontextFactory aufrufKontextFactory, AufrufKontextVerwalter aufrufKontextVerwalter) {
        SicherheitImpl sicherheit = new SicherheitImpl();

        sicherheit.setAccessManager(accessManagerStub);
        sicherheit.setAufrufKontextFactory(aufrufKontextFactory);
        sicherheit.setAufrufKontextVerwalter(aufrufKontextVerwalter);
        sicherheit.setKonfiguration(new ReloadablePropertyKonfiguration(new String[] {}));
        sicherheit.setRollenRechteDateiPfad("/resources/sicherheit/rollenrechte.xml");

        return sicherheit;
    }

    @Bean
    public GesichertInterceptor gesichertInterceptor(Sicherheit sicherheit) {
        GesichertInterceptor gesichertInterceptor = new GesichertInterceptor();
        gesichertInterceptor.setSicherheit(sicherheit);

        return gesichertInterceptor;
    }

    @Bean
    public Advisor gesichertPointAdvisor(GesichertInterceptor gesichertInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(de.bund.bva.pliscommon.sicherheit.annotation.Gesichert) || @within(de.bund.bva.pliscommon.sicherheit.annotation.Gesichert)");
        return new DefaultPointcutAdvisor(pointcut, gesichertInterceptor);
    }
}
