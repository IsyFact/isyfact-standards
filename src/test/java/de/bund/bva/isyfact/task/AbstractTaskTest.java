package de.bund.bva.isyfact.task;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import de.bund.bva.isyfact.task.konstanten.KonfigurationSchluessel;
import de.bund.bva.isyfact.task.konstanten.KonfigurationStandardwerte;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.endsWith;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public abstract class AbstractTaskTest {

    @Autowired
    protected Konfiguration konfiguration;

    @Autowired
    protected TaskScheduler taskScheduler;

    @Before
    public void setUp() throws Exception {
        when(konfiguration.getAsString(KonfigurationSchluessel.DATETIME_PATTERN,
            KonfigurationStandardwerte.DEFAULT_DATETIME_PATTERN)).thenReturn("dd.MM.yyyy HH:mm:ss.SSS");
        when(konfiguration.getAsInteger("isyfact.task.standard.amount_of_threads")).thenReturn(100);
        when(konfiguration.getAsString(endsWith("host")))
            .thenReturn(InetAddress.getLocalHost().getHostName());
    }

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
}
