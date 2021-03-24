package de.bund.bva.isyfact.task.autoconfigure;

import de.bund.bva.isyfact.task.TaskScheduler;
import de.bund.bva.isyfact.task.TaskSchedulerStarter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/*Testet, ob der TaskSchedulerStarter autostart=false wirklich nicht erstellt wird */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IsyTaskAutoConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = { "isy.task.autostart=false" })
public class TaskSchedulerStarterAutoConfigOffTest {

    @Autowired(required = false)
    private TaskSchedulerStarter taskSchedulerStarter;

    @Autowired(required = false)
    private IsyTaskAutoConfiguration isyTaskAutoConfiguration;

    @MockBean
    private TaskScheduler taskScheduler;

    @Test
    public void testTaskSchedulerStarterNichtVorhanden() {
        assertNotNull(isyTaskAutoConfiguration);
        assertNull(taskSchedulerStarter);
        verify(taskScheduler, never()).starteKonfigurierteTasks();
    }

}
