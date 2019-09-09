package de.bund.bva.isyfact.task.test.config;

import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.task.TestTaskMitException;
import de.bund.bva.isyfact.task.model.TaskMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
public class TestTaskExceptionConfig {

    @Bean
    public TestTaskMitException taskMitException(TaskMonitor monitor) {
        return new TestTaskMitException(monitor);
    }

    @Bean
    public TaskMonitor taskMonitor() {
        return new TaskMonitor();
    }

    @Bean
    public MBeanExporter mBeanExporter(TaskMonitor monitor) {
        MBeanExporter mBeanExporter = new MBeanExporter();
        mBeanExporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        mBeanExporter.setAssembler(new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource()));
        mBeanExporter.setAutodetect(false);

        Map<String, Object> mBeans = new HashMap<>();
        String key = "de.bund.bva.isyfact.taskRunner:type=TaskMonitor,name=\"TaskMitException\"";

        mBeans.put(key, monitor);

        mBeanExporter.setBeans(mBeans);

        return mBeanExporter;
    }
}
