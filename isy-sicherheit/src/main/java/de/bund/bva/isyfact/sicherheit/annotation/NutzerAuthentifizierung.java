package de.bund.bva.isyfact.sicherheit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kennzeichnet eine Methode, für die ein fester, technischer Nutzer authentifiziert werden soll.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface NutzerAuthentifizierung {

    /**
     * Der Konfigurationsschlüssel der betrieblichen Konfiguration, unter dem die Benutzerdaten zur
     * Authentifizierung abgelegt sind.
     */
    String benutzer();

}
