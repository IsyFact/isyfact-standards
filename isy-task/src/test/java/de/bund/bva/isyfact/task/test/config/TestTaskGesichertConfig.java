package de.bund.bva.isyfact.task.test.config;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.task.GesichertTask;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
public class TestTaskGesichertConfig {

    @Bean
    public GesichertTask gesichertTask1(@Qualifier("taskMonitor1") TaskMonitor monitor) {
        return new GesichertTask(monitor);
    }

    @Bean
    public TaskMonitor taskMonitor1() {
        return new TaskMonitor();
    }

    @Bean
    public GesichertTask gesichertTask2(@Qualifier("taskMonitor2") TaskMonitor taskMonitor) {
        return new GesichertTask(taskMonitor);
    }

    @Bean
    public TaskMonitor taskMonitor2() {
        return new TaskMonitor();
    }

    @Bean
    public MBeanExporter mBeanExporter(@Qualifier("taskMonitor1") TaskMonitor monitor1,
        @Qualifier("taskMonitor2") TaskMonitor monitor2) {
        MBeanExporter mBeanExporter = new MBeanExporter();
        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        mBeanExporter.setAssembler(new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource()));
        mBeanExporter.setAutodetect(false);

        Map<String, Object> mBeans = new HashMap<>();
        String key = "de.bund.bva.isyfact.taskRunner:type=TaskMonitor,name=\"GesichertTask1\"";

        mBeans.put(key, monitor1);

        key = "de.bund.bva.isyfact.taskRunner:type=TaskMonitor,name=\"GesichertTask2\"";

        mBeans.put(key, monitor2);

        mBeanExporter.setBeans(mBeans);

        return mBeanExporter;
    }
}
