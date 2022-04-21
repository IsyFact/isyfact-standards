package de.bund.bva.isyfact.persistence.autoconfigure.properties;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * Extension of {@link DataSourceProperties} with support for the validation properties required by
 * {@link de.bund.bva.isyfact.persistence.datasource.IsyDataSource}.
 */
public class DatabaseProperties extends DataSourceProperties {

    /**
     * Action to take if the database schema does not match the expected version.
     * <p>
     * Allowed values:
     * <ul>
     *     <li>warn = A warning message will be printed to the log
     *     <li>fail = An exception will be thrown and the application won't start (default)
     * </ul>
     */
    private String schemaInvalidVersionAction = "fail";

    /**
     * The version of the database schema expected by the application.
     * <p>
     * If empty, no schema validation will be performed.
     */
    private String schemaVersion;

    public String getSchemaInvalidVersionAction() {
        return schemaInvalidVersionAction;
    }

    public void setSchemaInvalidVersionAction(String schemaInvalidVersionAction) {
        this.schemaInvalidVersionAction = schemaInvalidVersionAction;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

}
