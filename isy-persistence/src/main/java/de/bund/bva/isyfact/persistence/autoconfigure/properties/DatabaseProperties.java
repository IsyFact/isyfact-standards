package de.bund.bva.isyfact.persistence.autoconfigure.properties;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class DatabaseProperties extends DataSourceProperties {

    /** Action to take if the database schema does not match the expected version. */
    private String invalidSchemaVersionAction;

    /** Expected version of database schema. */
    private String schemaVersion;

    public String getInvalidSchemaVersionAction() {
        return invalidSchemaVersionAction;
    }

    public void setInvalidSchemaVersionAction(String invalidSchemaVersionAction) {
        this.invalidSchemaVersionAction = invalidSchemaVersionAction;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

}
