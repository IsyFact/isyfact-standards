package de.bund.bva.isyfact.ueberwachung.config;

import org.springframework.validation.annotation.Validated;

/**
 * Contains properties to configure a monitoring user.
 */
@Validated
public class ActuatorSecurityConfigurationProperties {

    /** Username for HTTP access to actuator endpoints. Can be {@code Null} but must not be empty. */
    private String username;

    /** Password for HTTP access to actuator endpoints. */
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
