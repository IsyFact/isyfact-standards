/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.persistence.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.bund.bva.isyfact.persistence.common.EreignisSchluessel;
import de.bund.bva.isyfact.persistence.exception.FehlerSchluessel;
import de.bund.bva.isyfact.persistence.exception.PersistenzException;
import de.bund.bva.isyfact.persistence.exception.PersistenzFehlertextProvider;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.exception.FehlertextProvider;

/**
 * DataSource wrapper that catches null connections and throws a PersistenceException.
 *
 */
public class IsyDataSource extends DelegatingDataSource {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyDataSource.class);

    /** Error text provider for isy-persistence. */
    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new PersistenzFehlertextProvider();

    /** The SQLSTATE code "connection exception". */
    private static final String SQLSTATE_CONNECTION_EXCEPTION = "08000";

    /** Expected version of the database schema. */
    private String schemaVersion;

    /**
     * Action to perform if the DB schema does not match the expected version.<br>
     * warn: a warning is logged.<br>
     * fail: an exception is thrown.
     */
    private String invalidSchemaVersionAction;

    /**
     * This flag indicates that this DataSource is not absolutely necessary for the application. An example
     * would be the DataSource for an archive schema, which is not essential for regular operation.<br>
     * false: If the database is not reachable at startup, an exception is thrown, and the
     * DataSource is not created<br>
     * true: If the database is not reachable at startup, a log entry at the WARN level
     * is written. However, the DataSource is created, and the application can start. If a connection from this
     * DataSource is later requested, it will throw an exception. This remains until the
     * restart.
     */
    private boolean nonCriticalDataSource;

    /**
     * This flag is true if {@link nonCriticalDataSource} was set to true and the DataSource was created despite
     * failed schema verification. In this case, no connections should be issued.
     */
    private boolean initializationFailed;

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.initializationFailed) {
            throw new SQLException(
                FEHLERTEXT_PROVIDER
                    .getMessage(FehlerSchluessel.KEINE_CONNECTION_WEGEN_FEHLERHAFTER_INITIALISIERUNG),
                SQLSTATE_CONNECTION_EXCEPTION);
        }
        Connection conn = super.getConnection();
        if (conn == null) {
            throw new PersistenzException(FehlerSchluessel.KEINE_DB_CONNECTION_VERFUEGBAR);
        }

        return conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (this.initializationFailed) {
            throw new SQLException(
                FEHLERTEXT_PROVIDER
                    .getMessage(FehlerSchluessel.KEINE_CONNECTION_WEGEN_FEHLERHAFTER_INITIALISIERUNG),
                SQLSTATE_CONNECTION_EXCEPTION);
        }
        Connection conn = super.getConnection(username, password);
        if (conn == null) {
            throw new PersistenzException(FehlerSchluessel.KEINE_DB_CONNECTION_VERFUEGBAR);
        }

        return conn;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (this.schemaVersion == null || "".equals(this.schemaVersion.trim())) {
            return;
        }

        String ermittelteSchemaVersion = getSchemaVersionFromDatabase();
        if (!this.schemaVersion.equals(ermittelteSchemaVersion)) {
            handleSchemaVersionMismatch();
        }
    }

    private String getSchemaVersionFromDatabase() {
        String ermittelteSchemaVersion = "unbekannt";
        String query = "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.schemaVersion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs != null && rs.next()) {
                    ermittelteSchemaVersion = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        return ermittelteSchemaVersion;
    }

    private void handleSchemaVersionMismatch() {
        if ("warn".equals(this.invalidSchemaVersionAction)) {
            LOG.warn(EreignisSchluessel.FALSCHE_SCHEMA_VERSION,
                    "Die Version des Datenbankschemas entspricht nicht der erwarteten Version ( {} ).", this.schemaVersion);
        } else {
            throw new PersistenzException(FehlerSchluessel.FALSCHE_DB_SCHEMAVERSION,
                    this.schemaVersion);
        }
    }

    private void handleSQLException(SQLException e) throws PersistenzException {
        if (this.nonCriticalDataSource) {
            this.initializationFailed = true;
            LOG.warn(FehlerSchluessel.DB_BEIM_HOCHFAHREN_NICHT_VERFUEGBAR,
                    FEHLERTEXT_PROVIDER.getMessage(FehlerSchluessel.DB_BEIM_HOCHFAHREN_NICHT_VERFUEGBAR), e);
        } else {
            throw new PersistenzException(FehlerSchluessel.PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN, e);
        }
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * Sets the 'invalidSchemaVersionAction' field.
     * @param invalidSchemaVersionAction
     *            New value for invalidSchemaVersionAction
     */
    public void setInvalidSchemaVersionAction(String invalidSchemaVersionAction) {
        if (invalidSchemaVersionAction != null) {
            this.invalidSchemaVersionAction = invalidSchemaVersionAction.toLowerCase();
        } else {
            this.invalidSchemaVersionAction = null;
        }
    }

    /**
     * Sets the 'nonCriticalDataSource' field.
     * @param nonCriticalDataSource
     *            New value for nonCriticalDataSource
     */
    public void setNonCriticalDataSource(boolean nonCriticalDataSource) {
        this.nonCriticalDataSource = nonCriticalDataSource;
    }

}
