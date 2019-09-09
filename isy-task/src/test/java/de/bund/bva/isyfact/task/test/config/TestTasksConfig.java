package de.bund.bva.isyfact.task.test.config;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.task.TestTask1;
import de.bund.bva.isyfact.task.TestTask2;
import de.bund.bva.isyfact.task.TestTask3;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
public class TestTasksConfig {

    @Bean
    public TestTask1 testTask1() {
        return new TestTask1();
    }

    @Bean
    public TestTask2 testTask2(@Qualifier("testTask2Monitor") TaskMonitor monitor) {
        return new TestTask2(monitor);
    }

    @Bean
    public TaskMonitor testTask2Monitor() {
        return new TaskMonitor();
    }

    @Bean
    public TestTask3 testTask3(@Qualifier("testTask3Monitor") TaskMonitor monitor) {
        return new TestTask3(monitor);
    }

    @Bean
    public TaskMonitor testTask3Monitor() {
        return new TaskMonitor();
    }

    @Bean
    public MBeanExporter mBeanExporter(@Qualifier("testTask2Monitor") TaskMonitor monitor2,
        @Qualifier("testTask3Monitor") TaskMonitor monitor3) {
        MBeanExporter mBeanExporter = new MBeanExporter();
        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        mBeanExporter.setAssembler(new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource()));
        mBeanExporter.setAutodetect(false);

        Map<String, Object> mBeans = new HashMap<>();
        String key = "de.bund.bva.isyfact.taskRunner:type=TaskMonitor,name=&quot;TaskTest2&quot;";

        mBeans.put(key, monitor2);

        key = "de.bund.bva.isyfact.taskRunner:type=TaskMonitor,name=&quot;TaskTest3&quot;";

        mBeans.put(key, monitor3);

        mBeanExporter.setBeans(mBeans);

        return mBeanExporter;
    }

    @Bean
    public TestTask1 testTaskOnceInitialDelay() {
        return new TestTask1();
    }
}
