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
 * DataSource-Wrapper, der null-Connections abfängt und eine PersistenzException wirft.
 *
 */
public class IsyDataSource extends DelegatingDataSource {

    /** Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(IsyDataSource.class);

    /** Fehlertext-Provider für die isy-persistence. */
    private static final FehlertextProvider FEHLERTEXT_PROVIDER = new PersistenzFehlertextProvider();

    /** Der SQLSTATE Code "connection exception". */
    private static final String SQLSTATE_CONNECTION_EXCEPTION = "08000";

    /** Erwartete Version des Datenbank-Schemas. */
    private String schemaVersion;

    /**
     * Durchzuführende Aktion, falls das DB-Schema nicht der erwarteten Version entspricht.<br>
     * warn: es wird eine Warnung in das Log geschrieben.<br>
     * fail: es wird eine Exception geworfen.
     */
    private String invalidSchemaVersionAction;

    /**
     * Dieses Flag zeigt an, dass diese DataSource für die Anwendung nicht absolut notwendig ist. Ein Beispiel
     * wäre die DataSource für ein Archivschema, welches für den regulären Betrieb nicht essentiell ist.<br>
     * false: Ist die Datenbank beim Hochfahren nicht erreichbar, wird eine Exception geworfen und die
     * DataSource wird nicht erzeugt<br>
     * true: Ist die Datenbank beim Hochfahren nicht erreichbar, wird ein Log-Eintrag auf dem Level WARN
     * geschrieben. Die DataSource wird jedoch erzeugt und die Anwendung kann starten. Wird von dieser
     * DataSource später eine Verbindung angefordert, wirft sie jedoch eine Exception. Dies bleibt bis zum
     * Neustart bestehen.
     */
    private boolean nonCriticalDataSource;

    /**
     * Dieses Flag ist true, wenn {@link nonCriticalDataSource} auf true gesetzt war und die DataSource trotz
     * fehlgeschlagener Schemaprüfung erstellt wurde. In diesem Fall dürfen keine Connections herausgegeben
     * werden.
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
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        if (this.schemaVersion == null || "".equals(this.schemaVersion.trim())) {
            return;
        }

        String ermittelteSchemaVersion = "unbekannt";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String query =
                "select version_nummer from m_schema_version where version_nummer = ? and status = 'gueltig'";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, this.schemaVersion);
            rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                ermittelteSchemaVersion = rs.getString(1);
            }
            if (!this.schemaVersion.equals(ermittelteSchemaVersion)) {
                if ("warn".equals(this.invalidSchemaVersionAction)) {
                    LOG.warn(EreignisSchluessel.FALSCHE_SCHEMA_VERSION,
                        "Die Version des Datenbankschemas entspricht nicht der "
                            + "erwarteten Version ( {} ).", this.schemaVersion);
                } else {
                    throw new PersistenzException(FehlerSchluessel.FALSCHE_DB_SCHEMAVERSION,
                        this.schemaVersion);
                }
            }
        } catch (SQLException e) {
            if (this.nonCriticalDataSource) {
                this.initializationFailed = true;
                LOG.warn(FehlerSchluessel.DB_BEIM_HOCHFAHREN_NICHT_VERFUEGBAR,
                    FEHLERTEXT_PROVIDER.getMessage(FehlerSchluessel.DB_BEIM_HOCHFAHREN_NICHT_VERFUEGBAR), e);
            } else {
                throw new PersistenzException(FehlerSchluessel.PRUEFEN_DER_SCHEMAVERSION_FEHLGESCHLAGEN, e);
            }
        } catch (PersistenzException e1) {
            throw e1;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOG.warn(EreignisSchluessel.DB_VERBINDUNG_NICHT_GESCHLOSSEN,
                        "Das Ergebnis der SQL Anfrage konnte nicht geschlossen werden. Grund {}", e.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    LOG.warn(EreignisSchluessel.DB_VERBINDUNG_NICHT_GESCHLOSSEN,
                        "Die SQL Anfrage konnte nicht geschlossen werden. Grund: {}", e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    LOG.warn(EreignisSchluessel.DB_VERBINDUNG_NICHT_GESCHLOSSEN,
                        "Die Datenbankverbindung konnte nicht geschlossen werden. Grund: {}", e1.getMessage());
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

    /**
     * Setzt das Feld 'nonCriticalDataSource'.
     * @param nonCriticalDataSource
     *            Neuer Wert für nonCriticalDataSource
     */
    public void setNonCriticalDataSource(boolean nonCriticalDataSource) {
        this.nonCriticalDataSource = nonCriticalDataSource;
    }

}
