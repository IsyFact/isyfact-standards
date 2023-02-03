package de.bund.bva.isyfact.persistence.autoconfigure;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bund.bva.isyfact.persistence.config.OracleDataSourceProperties;
import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Oracle persistence layer configuration.
 */
@Configuration
@ConditionalOnProperty(prefix = "isy.persistence.oracle.datasource", name = "database-url")
@EnableConfigurationProperties
public class IsyPersistenceOracleAutoConfiguration {

    /**
     * Oracle datasource health check.
     * @return bean {@code oracleDataSourceHealthIndicator}
     */
    @Bean
    @ConditionalOnMissingBean(DataSourceHealthIndicator.class)
    public DataSourceHealthIndicator oracleDataSourceHealthIndicator(@Qualifier("appDataSource") DataSource dataSource) {
        DataSourceHealthIndicator dataSourceHealthIndicator = new DataSourceHealthIndicator();
        dataSourceHealthIndicator.setDataSource(dataSource);
        dataSourceHealthIndicator.setQuery("select BANNER from V$VERSION where BANNER like 'Oracle%'");

        return dataSourceHealthIndicator;
    }

    /**
     * Oracle datasource properties.
     * @return bean {@code oracleDataSourceProperties}
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.persistence.oracle.datasource")
    public OracleDataSourceProperties oracleDataSourceProperties() {
        return new OracleDataSourceProperties();
    }

    /**
     * Oracle datasource.
     * @param dsProps datasource properties
     * @return bean {@code appDataSource}.
     * @throws SQLException if a database error occurs during bean creation
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
