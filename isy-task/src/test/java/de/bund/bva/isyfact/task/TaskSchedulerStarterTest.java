package de.bund.bva.isyfact.task;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

// Kein Spring Boot Test, da manuell das Erstellen und Schließen von Kontexten simuliert wird,
// um Fehler wie in IFS-362 zu vermeiden (unbeabsichtes mehrfaches aufrufen bei Child-Kontexten)
public class TaskSchedulerStarterTest {
    private static TaskScheduler taskScheduler;

    // Prüft ob der starteKonfigurierteTasks ausgeführt wird,
    // sobald die TaskSchedulerStarter-Bean im Kontext vorhanden ist.
    @Test
    public void starterContextListenerOneContext() {
        taskScheduler = Mockito.mock(TaskScheduler.class);
        AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(ParentContext.class);
        Mockito.verify(taskScheduler).starteKonfigurierteTasks();
        Mockito.verifyNoMoreInteractions(taskScheduler);
    }

    // Prüft ob der starteKonfigurierteTasks einmalig ausgeführt wird,
    // auch wenn in der zwischenzeit Kind-Kontexte gestartet/stoppt werden
    @Test
    public void starterContextListenerWithChildContext() {
        taskScheduler = Mockito.mock(TaskScheduler.class);
        AnnotationConfigApplicationContext parent =
            new AnnotationConfigApplicationContext(ParentContext.class);
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext(ChildContext.class);
        child.setParent(parent);
        child.publishEvent(new ContextRefreshedEvent(child));

        //Funktion wurde nur einmal aufgerufen, auch wenn Kind-Kontext refreshed wurde
        Mockito.verify(taskScheduler, Mockito.times(1)).starteKonfigurierteTasks();

        Mockito.verifyNoMoreInteractions(taskScheduler);

    }

    // Prüft das Herunterfahren des Schedulers, wenn der Kontext geschlossen wird
    @Test
    public void contextListenerClosingTest() throws InterruptedException {
        taskScheduler = Mockito.mock(TaskScheduler.class);
        AnnotationConfigApplicationContext parent =
            new AnnotationConfigApplicationContext(ParentContext.class);
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
        TaskSchedulerStarter taskSchedulerStarter() {
            return new TaskSchedulerStarter(taskScheduler);
        }
    }

    public static class ChildContext {
    }



}
