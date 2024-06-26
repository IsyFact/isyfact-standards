package de.bund.bva.isyfact.batchrahmen;

import jakarta.annotation.Nullable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import de.bund.bva.isyfact.batchrahmen.batch.BasicAuthenticationTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.BasicTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.ErrorTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.GesicherterTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.GesicherterTestBatch2;
import de.bund.bva.isyfact.batchrahmen.batch.InfiniteTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.ReturnCodeTestBatch;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.impl.BatchrahmenImpl;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.jmx.BatchRahmenMBean;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

@Configuration
public class BatchrahmenTestConfig {

    @Bean
    public BatchRahmenMBean batchRahmenMBean() {
        return new BatchRahmenMBean();
    }

    @Bean
    public Batchrahmen batchrahmen(PlatformTransactionManager platformTransactionManager, @Nullable Authentifizierungsmanager authentifizierungsmanager) {
        BatchrahmenImpl batchrahmen = new BatchrahmenImpl();
        batchrahmen.setTransactionManager(platformTransactionManager);
        batchrahmen.setJmxBean(batchRahmenMBean());
        batchrahmen.setAuthentifizierungsmanager(authentifizierungsmanager);

        return batchrahmen;
    }

    @Bean
    public BasicTestBatch basicTestBatch() {
        return new BasicTestBatch();
    }

    @Bean
    public BasicAuthenticationTestBatch basicAuthenticationTestBatch() {
        return new BasicAuthenticationTestBatch();
    }

    @Bean
    public InfiniteTestBatch infiniteTestBatch() {
        return new InfiniteTestBatch();
    }

    @Bean
    public ErrorTestBatch errorTestBatch() {
        return new ErrorTestBatch();
    }

    @Bean
    public GesicherterTestBatch gesicherterTestBatch() {
        return new GesicherterTestBatch();
    }

    @Bean
    public GesicherterTestBatch2 gesicherterTestBatch2() {
        return new GesicherterTestBatch2();
    }

    @Bean
    public ReturnCodeTestBatch returnCodeTestBatch() {
        return new ReturnCodeTestBatch();
    }

}
