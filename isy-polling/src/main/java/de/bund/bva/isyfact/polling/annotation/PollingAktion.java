package de.bund.bva.isyfact.polling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 

/**
 * Kennzeichnet eine Methode, die eine Polling-Aktion durchführt.
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PollingAktion {

    /**
     * Name des Polling-Clusters, für den eine Polling-Aktion durchgeführt wird.
     */
    String pollingCluster();

}



