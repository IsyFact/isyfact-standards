package de.bund.bva.isyfact.persistence.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

/**
 * Properties für die Konfiguration der Oracle-Data-Source. Default-Werte werden den Attributen bei der
 * Definition direkt zugewiesen.
 */
@Validated
public class OracleDataSourceProperties {

    /**
     * Von der Anwendung erwartete Version des Datenbankschemas. Wenn keine Version angegeben wird, dann
     * erfolgt keine Prüfung der Schema-Version.
     */
    private String schemaVersion;

    /**
     * Durchzuführende Aktion, falls die Erwartete Version des Datenbankschemas abweicht. <br>
     * Erlaubte Werte: warn = Im Log wird eine Warnung ausgeben <br>
     * fail = Es wird eine Exception geworfen und die Anwendung startet nicht (Standardwert).
     */
    private String schemaInvalidVersionAction = "fail";

    /** Name des pools. */
    private String poolName;

    /** Name des Datenbankbenutzers. */
    @NotNull
    private String databaseUsername;

    /** Kennwort für den Datenbankbenutzer. */
    @NotNull
    private String databasePassword;

    /** Connection-String für die Datenbankverbindung. */
    @NotEmpty
    private String databaseUrl;

    /** Anzahl der initialen Connections im Connection Pool. */
    private int poolInitialSize = 2;

    /**
     * Eine SQL Abfrage, die in Verbindung mit dem poolValidateConnectionOnBorrow Attribut verwendet wird. Die
     * SQL Abfrage sollte kompatibel mit dem Datenbank-Backend und dem verwendeten JDBC Treiber sein.
     * Standardwert ist null.
     */
    private String poolValidationQuery;

    /**
     * Dieses Intervall gibt an wie oft die Erreichung der Timeouts geprüft wird. Dabei geht es um Timeouts
     * wie: poolInactiveTimeout, poolAbandonedTimeout, und poolTimeToLiveTimeout. Wird dieser Parameter auf 0
     * gesetzt, wird dadurch die Verarbeitung aller Verbindungstimeouts gestoppt.
     */
    private int poolTimeoutCheckInterval = 30;

    /**
     * Setzt die maximale Wartezeit für die verfügbaren Verbindungen im Connection-Pool. Dies hat den gleichen
     * Effekt wie poolInactiveTimeout.
     */
    private int poolMaxIdleTime;

    /** Anzahl der minimal offenen Verbindungen im Connection-Pool. */
    private int poolMinActive = 2;

    /**
     * Anzahl der maximal möglichen Verbindungen im Connection-Pool. Muss auf die Last des Systems angepasst
     * werden.
     */
    private int poolMaxActive = 50;

    /**
     * Zeit in Sekunden, nach der bei Nichtverfügbarkeit einer neue Verbindung ein Fehler geworfen wird.
     */
    private int poolWaitTimeout = 100;

    /**
     * Die maximale Zeit (in Millisekunden), welcher ein Prozess auf das Holen einer Datenbankverbindung aus
     * dem Connection-Pool wartet. (Bei dem Wert '-1' wartet ein Prozess unendlich lange.
     */
    private int poolMaxWait = -1;

    /**
     * Zeit in Sekunden, nach der eine bereitstehende und ungültige Verbindung geschlossen und aus dem Pool
     * entfernt wird.
     */
    private int poolInactiveTimeout = 60;

    /**
     * Zeit in Sekunden, nach der eine ausgeliehene Verbindung wieder zwangsweise zurück in den Pool geholt
     * wird. Offene Transaktionen werden zurückgerollt. Standard ist 0 (deaktiviert).
     */
    private int poolTimeToLiveTimeout;

    /**
     * Zeit in Sekunden, nach der eine ungenutzte aber verliehene Verbindung wieder in den Pool geholt wird.
     * Offene Transaktionen werden zurückgerollt. Standard ist 0 (deaktiviert).
     */
    private int poolAbandonedTimeout;

    /**
     * Zeit in Sekunden, nach der eine physikalische Verbindung im Pool geordnet abgebaut wird. Sie wird erst
     * abgebaut, wenn die Verbindung nicht mehr genutzt wird und zurück im Pool ist. Kann genutzt werden, wenn
     * bspw. Firewalls nach einer zeitlichen Beschränkung Verbindungen schliessen. Standard ist 0,
     * deaktiviert.
     */
    private int poolMaxReuseTime;

    /**
     * Maximale Anzahl, die eine Verbindung ausgeliehen werden kann, bevor sie endgueltig abgebaut wird.
     * Standard 0 (deaktiviert).
     */
    private int poolMaxReuseCount;

    /**
     * Aktiviert/deaktiviert die Pruefung von Datenbankverbindungen vor ihrer Benutzung
     * (validateConnectionOnBorrow).
     */
    private boolean poolValidateOnBorrow = true;

    /**
     * Anzahl der Statements, die pro Verbindung gecacht werden sollen (Statement Cache). Standard ist 0
     * (deaktiviert).
     */
    private int poolStatementCache;

    /**
     * Der Wert fuer oracle.net.CONNECT_TIMEOUT des Oracle JDBC Treibers. Der Timeout bestimmt die maximale
     * Zeit in ms, welche zum Aufbau einer Netzwerkverbindung zum Datenbankserver gewartet wird.
     */
    private int jdbcTimeoutConnect = 10000;

    /**
     * Der Wert fuer oracle.jdbc.ReadTimeout des Oracle JDBC Treibers. Der Timeout bestimmt die maximale Zeit
     * in ms, welche auf Socketebene zum Lesen von Daten gewartet wird. Dadurch koennen abgebrochene TCP
     * Verbindungen erkannt werden.
     */
    private int jdbcTimeoutRead;

    /**
     * Verbindungen können im regulären band (inband) oder asynchron (out-of-band) beendet werden.
     * Standardmäßig passiert das per OOB. Kann bei Problemen deaktiviert werden.
     */
    private boolean jdbcDisableOob = true;

    /**
     * Die Anzahl an Zeilen, die der JDBC Treiber direkt vom Server vorlädt. Damit können Server-Roundtrips
     * beim Auslesen der Queryergebnisse minmiert werden.
     */
    private int jdbcRowPrefetch = 200;

    public String getSchemaVersion() {
        return this.schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getSchemaInvalidVersionAction() {
        return this.schemaInvalidVersionAction;
    }

    public void setSchemaInvalidVersionAction(String schemaInvalidVersionAction) {
        this.schemaInvalidVersionAction = schemaInvalidVersionAction;
    }

    public String getPoolName() {
        return this.poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getDatabaseUsername() {
        return this.databaseUsername;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return this.databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getDatabaseUrl() {
        return this.databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public int getPoolInitialSize() {
        return this.poolInitialSize;
    }

    public void setPoolInitialSize(int poolInitialSize) {
        this.poolInitialSize = poolInitialSize;
    }

    public String getPoolValidationQuery() {
        return this.poolValidationQuery;
    }

    public void setPoolValidationQuery(String poolValidationQuery) {
        this.poolValidationQuery = poolValidationQuery;
    }

    public int getPoolTimeoutCheckInterval() {
        return this.poolTimeoutCheckInterval;
    }

    public void setPoolTimeoutCheckInterval(int poolTimeoutCheckInterval) {
        this.poolTimeoutCheckInterval = poolTimeoutCheckInterval;
    }

    public int getPoolMaxIdleTime() {
        return this.poolMaxIdleTime;
    }

    public void setPoolMaxIdleTime(int poolMaxIdleTime) {
        this.poolMaxIdleTime = poolMaxIdleTime;
    }

    public int getPoolMinActive() {
        return this.poolMinActive;
    }

    public void setPoolMinActive(int poolMinActive) {
        this.poolMinActive = poolMinActive;
    }

    public int getPoolMaxActive() {
        return this.poolMaxActive;
    }

    public void setPoolMaxActive(int poolMaxActive) {
        this.poolMaxActive = poolMaxActive;
    }

    public int getPoolWaitTimeout() {
        return this.poolWaitTimeout;
    }

    public void setPoolWaitTimeout(int poolWaitTimeout) {
        this.poolWaitTimeout = poolWaitTimeout;
    }

    public int getPoolMaxWait() {
        return this.poolMaxWait;
    }

    public void setPoolMaxWait(int poolMaxWait) {
        this.poolMaxWait = poolMaxWait;
    }

    public int getPoolInactiveTimeout() {
        return this.poolInactiveTimeout;
    }

    public void setPoolInactiveTimeout(int poolInactiveTimeout) {
        this.poolInactiveTimeout = poolInactiveTimeout;
    }

    public int getPoolTimeToLiveTimeout() {
        return this.poolTimeToLiveTimeout;
    }

    public void setPoolTimeToLiveTimeout(int poolTimeToLiveTimeout) {
        this.poolTimeToLiveTimeout = poolTimeToLiveTimeout;
    }

    public int getPoolAbandonedTimeout() {
        return this.poolAbandonedTimeout;
    }

    public void setPoolAbandonedTimeout(int poolAbandonedTimeout) {
        this.poolAbandonedTimeout = poolAbandonedTimeout;
    }

    public int getPoolMaxReuseTime() {
        return this.poolMaxReuseTime;
    }

    public void setPoolMaxReuseTime(int poolMaxReuseTime) {
        this.poolMaxReuseTime = poolMaxReuseTime;
    }

    public int getPoolMaxReuseCount() {
        return this.poolMaxReuseCount;
    }

    public void setPoolMaxReuseCount(int poolMaxReuseCount) {
        this.poolMaxReuseCount = poolMaxReuseCount;
    }

    public boolean isPoolValidateOnBorrow() {
        return this.poolValidateOnBorrow;
    }

    public void setPoolValidateOnBorrow(boolean poolValidateOnBorrow) {
        this.poolValidateOnBorrow = poolValidateOnBorrow;
    }

    public int getPoolStatementCache() {
        return this.poolStatementCache;
    }

    public void setPoolStatementCache(int poolStatementCache) {
        this.poolStatementCache = poolStatementCache;
    }

    public int getJdbcTimeoutConnect() {
        return this.jdbcTimeoutConnect;
    }

    public void setJdbcTimeoutConnect(int jdbcTimeoutConnect) {
        this.jdbcTimeoutConnect = jdbcTimeoutConnect;
    }

    public int getJdbcTimeoutRead() {
        return this.jdbcTimeoutRead;
    }

    public void setJdbcTimeoutRead(int jdbcTimeoutRead) {
        this.jdbcTimeoutRead = jdbcTimeoutRead;
    }

    public boolean isJdbcDisableOob() {
        return this.jdbcDisableOob;
    }

    public void setJdbcDisableOob(boolean jdbcDisableOob) {
        this.jdbcDisableOob = jdbcDisableOob;
    }

    public int getJdbcRowPrefetch() {
        return this.jdbcRowPrefetch;
    }

    public void setJdbcRowPrefetch(int jdbcRowPrefetch) {
        this.jdbcRowPrefetch = jdbcRowPrefetch;
    }

}
