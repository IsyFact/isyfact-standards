package de.bund.bva.isyfact.task;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

public class TestTaskSchedulerStarter  {
    private static TaskScheduler taskScheduler;

    @Test
    public void starterContextListenerOneContext() {
        taskScheduler = Mockito.mock(TaskScheduler.class);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ParentContext.class);
        Mockito.verify(taskScheduler).starteKonfigurierteTasks();
        Mockito.verifyNoMoreInteractions(taskScheduler);
    }

    @Test
    public void starterContextListenerWithChildContext() {
        taskScheduler = Mockito.mock(TaskScheduler.class);
        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(ParentContext.class);
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext(ChildContext.class);
        child.setParent(parent);
        child.publishEvent(new ContextRefreshedEvent(child));

        //Funktion wurde nur einmal aufgerufen, auch wenn Kind-Kontext refreshed wurde
        Mockito.verify(taskScheduler, Mockito.times(1)).starteKonfigurierteTasks();

        Mockito.verifyNoMoreInteractions(taskScheduler);

    }

    @Test
    public void contextListenerClosingTest() throws InterruptedException{
        taskScheduler = Mockito.mock(TaskScheduler.class);
        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(ParentContext.class);
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext(ChildContext.class);
        child.setParent(parent);
        child.close();
        parent.close();

        Mockito.verify(taskScheduler, Mockito.times(1)).starteKonfigurierteTasks();
        Mockito.verify(taskScheduler).shutdownMitTimeout(Mockito.anyLong());
        Mockito.verifyNoMoreInteractions(taskScheduler);
    }

    public static class ParentContext {

        @Bean
        TaskSchedulerStarter taskSchedulerStarter(){
            return new TaskSchedulerStarter(taskScheduler);
        }
    }

    public static class ChildContext {
    }

}
