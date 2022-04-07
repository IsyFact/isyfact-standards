package de.bund.bva.isyfact.persistence.autoconfigure;

import java.sql.SQLException;
import javax.sql.DataSource;

import de.bund.bva.isyfact.persistence.autoconfigure.properties.DatabaseProperties;
import de.bund.bva.isyfact.persistence.config.OracleDataSourceProperties;
import de.bund.bva.isyfact.persistence.datasource.IsyDataSource;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring-Konfiguration der Persistenzschicht für Oracle.
 */
@Configuration
@ConditionalOnProperty(name = "isy.persistence.oracle.datasource.databaseurl")
@EnableConfigurationProperties
public class IsyPersistenceOracleAutoConfiguration {

    /**
     * Erzeugt eine Bean für den Health-Check.
     * @return Bean oracleDataSourceHealthIndicator
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
     * Erzeugt eine neue Bean für die Oracle Data Source Properties.
     *
     * @return Bean oracleDataSourceProperties.
     */
    @Bean
    @ConfigurationProperties(prefix = "isy.persistence.oracle.datasource")
    public OracleDataSourceProperties oracleDataSourceProperties() {
        return new OracleDataSourceProperties();
    }

    /**
     * Erzeugt eine neue Bean für die Oracle Data-Source
     *
     * @ param dsProps
     *            Bean mit den Data-Source-Properties.
     *
     * @ return Bean appDataSource.
     *
     * @ throws SQLException
     *             falls beim Erzeugen der Bean ein Datenbank-Fehler auftritt.
     */
    /*@Bean
    public DataSource appDataSource(OracleDataSourceProperties dsProps) throws SQLException {
        PoolDataSource target = PoolDataSourceFactory.getPoolDataSource();
        // dbProperties
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

        // dataSourceProperties
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
    }*/
    @Primary
    @Bean
    @DependsOnDatabaseInitialization
    public IsyDataSource appDataSource(@Qualifier("dataSource") DataSource dataSource,
        DatabaseProperties databaseProperties) {
        IsyDataSource plisDataSource = new IsyDataSource();
        plisDataSource.setSchemaVersion(databaseProperties.getSchemaVersion());
        plisDataSource.setInvalidSchemaVersionAction(databaseProperties.getInvalidSchemaVersionAction());
        plisDataSource.setTargetDataSource(dataSource);
        return plisDataSource;
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "isy.persistence.datasource")
    public DatabaseProperties databaseProperties() {
        return new DatabaseProperties();
    }

    @Bean("dataSource")
    @ConfigurationProperties(prefix = "isy.persistence.datasource.oracleucp")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}
