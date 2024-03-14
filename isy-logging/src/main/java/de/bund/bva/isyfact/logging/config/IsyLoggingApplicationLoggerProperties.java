package de.bund.bva.isyfact.logging.config;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

/**
 * Properties für die Konfiguration des Application-Loggers von Isy-Logging.
 */
@Validated
public class IsyLoggingApplicationLoggerProperties {

    /** Name der Anwendung. Wird beim Start ausgegeben. */
    private String name;

    /** Typ der Anwendung. Wird beim Start ausgegeben. */
    private String typ;

    /** Versionsnummer der Anwendung. Wird beim Start ausgegeben. */
    private String version;

    @NotEmpty
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotEmpty
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @NotEmpty
    public String getTyp() {
        return this.typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

}
