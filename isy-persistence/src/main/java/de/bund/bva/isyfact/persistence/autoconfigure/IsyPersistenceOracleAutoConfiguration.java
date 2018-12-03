package de.bund.bva.isyfact.persistence.autoconfigure;

import java.sql.SQLException;
import javax.sql.DataSource;

import de.bund.bva.isyfact.persistence.config.OracleDataSourceProperties;
import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;
import de.bund.bva.isyfact.persistence.health.OracleHealthIndicator;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring-Konfiguration der Persistenzschicht für Oracle.
 */
@Configuration
@ConditionalOnProperty("oracle.datasource.databaseurl")
@EnableConfigurationProperties
public class IsyPersistenceOracleAutoConfiguration {

    /**
     * Erzeugt eine Bean für den Health-Check.
     * @return Bean oracleHealthIndicator.
     */
    @Bean
    public OracleHealthIndicator oracleHealthIndicator() {
        return new OracleHealthIndicator();
    }

    /**
     * Erzeugt eine neue Bean für die Oracle Data Source Properties.
     *
     * @return Bean oracleDataSourceProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "oracle.datasource")
    public OracleDataSourceProperties oracleDataSourceProperties() {
        return new OracleDataSourceProperties();
    }

    /**
     * Erzeugt eine neue Bean für die Oracle Data-Source
     *
     * @param dsProps
     *            Bean mit den Data-Source-Properties.
     *
     * @return Bean appDataSource.
     *
     * @throws SQLException
     *             falls beim Erzeugen der Bean ein Datenbank-Fehler auftritt.
     */
    @Bean
    public DataSource appDataSource(OracleDataSourceProperties dsProps) throws SQLException {
        PoolDataSource target = PoolDataSourceFactory.getPoolDataSource();
        target.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
        target.setConnectionPoolName(dsProps.getPoolName());
        target.setUser(dsProps.getDatabaseUsername());
        target.setPassword(dsProps.getDatabasePassword());
        target.setURL(dsProps.getDatabaseUrl());
        target.setInitialPoolSize(dsProps.getPoolInitialSize());
        target.setSQLForValidateConnection(dsProps.getPoolValidationQuery());
        target.setTimeoutCheckInterval(dsProps.getPoolTimeoutCheckInterval());
        target.setMaxIdleTime(dsProps.getPoolMaxIdleTime());
        target.setMinPoolSize(dsProps.getPoolMinActive());
        target.setMaxPoolSize(dsProps.getPoolMaxActive());
        target.setConnectionWaitTimeout(dsProps.getPoolWaitTimeout());
        target.setInactiveConnectionTimeout(dsProps.getPoolInactiveTimeout());
        target.setTimeToLiveConnectionTimeout(dsProps.getPoolTimeToLiveTimeout());
        target.setAbandonedConnectionTimeout(dsProps.getPoolAbandonedTimeout());
        target.setMaxConnectionReuseTime(dsProps.getPoolMaxReuseTime());
        target.setMaxConnectionReuseCount(dsProps.getPoolMaxReuseCount());
        target.setValidateConnectionOnBorrow(dsProps.isPoolValidateOnBorrow());
        target.setMaxStatements(dsProps.getPoolStatementCache());

        target.setConnectionProperty("oracle.net.disableOob", Boolean.toString(dsProps.isJdbcDisableOob()));
        target.setConnectionProperty("oracle.net.CONNECT_TIMEOUT",
            Integer.toString(dsProps.getJdbcTimeoutConnect()));
        target.setConnectionProperty("oracle.jdbc.ReadTimeout",
            Integer.toString(dsProps.getJdbcTimeoutRead()));
        target.setConnectionProperty("defaultRowPrefetch", Integer.toString(dsProps.getJdbcRowPrefetch()));

        IsyDataSource ds = new IsyDataSource();
        ds.setTargetDataSource(target);
        ds.setSchemaVersion(dsProps.getSchemaVersion());
        ds.setInvalidSchemaVersionAction(dsProps.getSchemaInvalidVersionAction());

        return ds;
    }
}
