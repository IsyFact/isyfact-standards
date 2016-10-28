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
package de.bund.bva.pliscommon.persistence.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.persistence.common.EreignisSchluessel;
import de.bund.bva.pliscommon.persistence.exception.FehlerSchluessel;
import de.bund.bva.pliscommon.persistence.exception.PersistenzException;

/**
 * DataSource-Wrapper, der null-Connections abfängt und eine PersistenzException wirft.
 *
 */
public class PlisDataSource extends DelegatingDataSource {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(PlisDataSource.class);

    /** Erwartete Version des Datenbank-Schemas. */
    private String schemaVersion;

    /**
     * Durchzuführende Aktion, falls das DB-Schema nicht der erwarteten Version entspricht.<br>
     * warn: es wird eine Warnung in das Log geschrieben.<br>
     * fail: es wird eine Exception geworfen.
     */
    private String invalidSchemaVersionAction;

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() throws SQLException {
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
        Connection conn = super.getConnection(username, password);
        if (conn == null) {
            throw new PersistenzException(FehlerSchluessel.KEINE_DB_CONNECTION_VERFUEGBAR);
        }

        return conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (this.schemaVersion == null || "".equals(this.schemaVersion.trim())) {
            return;
        }

        String ermittelteSchemaVersion = "unbekannt";
        Connection conn = null;
        try {
            conn = getConnection();
            String query =
                "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.schemaVersion);
            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                ermittelteSchemaVersion = rs.getString(1);
            }
            if (!this.schemaVersion.equals(ermittelteSchemaVersion)) {
                if ("warn".equals(this.invalidSchemaVersionAction)) {
                    LOG.warn(EreignisSchluessel.FALSCHE_SCHEMA_VERSION,
                        "Die Version des Datenbankschemas entspricht nicht der "
                            + "erwarteten Version ( {} ).",
                        this.schemaVersion);
                } else {
                    throw new PersistenzException(FehlerSchluessel.FALSCHE_DB_SCHEMAVERSION,
                        this.schemaVersion);
                }
            }
        } catch (SQLException e) {
            throw new PersistenzException(FehlerSchluessel.PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN, e);
        } catch (PersistenzException e1) {
            throw e1;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    LOG.warn(EreignisSchluessel.DB_VERBINDUNG_NICHT_GESCHLOSSEN,
                        "Die Datenbankverbindung konnte nicht geschlossen werden. Grund: {}",
                        e1.getMessage());
                }
            }
        }
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * Setzt das Feld 'invalidSchemaVersionAction'.
     * @param invalidSchemaVersionAction
     *            Neuer Wert für invalidSchemaVersionAction
     */
    public void setInvalidSchemaVersionAction(String invalidSchemaVersionAction) {
        if (invalidSchemaVersionAction != null) {
            this.invalidSchemaVersionAction = invalidSchemaVersionAction.toLowerCase();
        } else {
            this.invalidSchemaVersionAction = null;
        }
    }

}
