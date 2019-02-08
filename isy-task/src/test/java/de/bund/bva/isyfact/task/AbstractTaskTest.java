package de.bund.bva.isyfact.task;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractTaskTest {

    @Autowired
    protected TaskScheduler taskScheduler;

    protected String getMBeanAttribute(String taskName, String attributeName) throws Exception {
        ArrayList<MBeanServer> mBeanServerList = MBeanServerFactory.findMBeanServer(null);
        MBeanServer mBeanServer = mBeanServerList.get(0);

        Hashtable<String, String> table = new Hashtable<>();
        table.put("type", "TaskMonitor");
        table.put("name", "\"" + taskName + "\"");
        ObjectName testObjectName = new ObjectName("de.bund.bva.isyfact.taskRunner", table);

        Object attribute = mBeanServer.getAttribute(testObjectName, attributeName);
        return attribute == null ? null : attribute.toString();
    }

    protected boolean isTaskRunning(String taskId) {
        return taskScheduler.getLaufendeTasks().stream()
            .anyMatch(t -> t.getTaskKonfiguration().getTaskId().equals(taskId));
    }
}
