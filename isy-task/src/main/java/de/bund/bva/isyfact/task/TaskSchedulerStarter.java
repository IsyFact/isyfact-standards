package de.bund.bva.isyfact.task;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * Die Klasse stellt Listener zum Starten des TaskSchedulers beim Laden der Applikation zur Verfügung.
 */
public class TaskSchedulerStarter implements ApplicationContextAware {

    /** Scheduler, der gestartet werden soll. */
    private final TaskScheduler taskScheduler;

    /** Der Applikationskontext des Starters. Er reagiert nur auf Events des Kontexts. */
    private ApplicationContext applicationContext;

    public TaskSchedulerStarter(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @EventListener
    public void starteTasks(ContextRefreshedEvent event) {
        //wird benötigt, damit der Listener nicht auf Kind-Kontexte reagiert
        //nur auf den eigenen
        if (applicationContext.equals(event.getApplicationContext())) {
            taskScheduler.starteKonfigurierteTasks();
        }
    }

    @EventListener
    public void stoppeTasks(ContextClosedEvent event) throws InterruptedException {
        if (applicationContext.equals(event.getApplicationContext())) {
            taskScheduler.shutdownMitTimeout(5);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
