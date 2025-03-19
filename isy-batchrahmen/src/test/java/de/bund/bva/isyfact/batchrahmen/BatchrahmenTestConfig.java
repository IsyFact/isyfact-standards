package de.bund.bva.isyfact.batchrahmen;

import de.bund.bva.isyfact.batchrahmen.batch.AufrufKontextTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.BasicTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.ErrorTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.GesicherterTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.GesicherterTestBatch2;
import de.bund.bva.isyfact.batchrahmen.batch.InfiniteTestBatch;
import de.bund.bva.isyfact.batchrahmen.batch.ReturnCodeTestBatch;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.Batchrahmen;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.impl.BatchrahmenImpl;
import de.bund.bva.isyfact.batchrahmen.core.rahmen.jmx.BatchRahmenMBean;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class BatchrahmenTestConfig {

    @Bean
    public BatchRahmenMBean batchRahmenMBean() {
        return new BatchRahmenMBean();
    }

    @Bean
    public Batchrahmen batchrahmen(JpaTransactionManager jpaTransactionManager, AufrufKontextVerwalter aufrufKontextVerwalter, AufrufKontextFactory aufrufKontextFactory,
                Sicherheit sicherheit) {
        BatchrahmenImpl batchrahmen = new BatchrahmenImpl(sicherheit);
        batchrahmen.setTransactionManager(jpaTransactionManager);
        batchrahmen.setAufrufKontextFactory(aufrufKontextFactory);
        batchrahmen.setAufrufKontextVerwalter(aufrufKontextVerwalter);
        batchrahmen.setJmxBean(batchRahmenMBean());

        return batchrahmen;
    }

    @Bean
    public BasicTestBatch basicTestBatch() {
        return new BasicTestBatch();
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

    @Bean
    public AufrufKontextTestBatch aufrufKontextTestBatch() {
        return new AufrufKontextTestBatch();
    }
}
