package de.bund.bva.isyfact.polling.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public abstract class AbstractPollingTest {

    /**
     * Überprüft den JMX-Status und gibt Informationen auf der Konsole aus.
     * @return true, falls JMX aktiv ist, false sonst. 
     */
    public boolean pruefeJMXStatus() throws Exception {

        if (System.getProperty("com.sun.management.jmxremote") == null) {
            System.out.println("JMX remote ist disabled");
            return false;
        } else {
            System.out.println("JMX remote is enabled");
            if (System.getProperty("com.sun.management.jmxremote.port") != null) {
                System.out.println("JMX running on port "
                    + Integer.parseInt(
                    System.getProperty("com.sun.management.jmxremote.port")));
            }
        }

        return true;
    }
}
