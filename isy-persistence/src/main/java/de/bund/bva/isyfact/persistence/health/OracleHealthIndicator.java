package de.bund.bva.isyfact.persistence.health;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

/**
 * Health-Indicator für die Prüfung der Verbindung zur Oracle-Datenbank. Im Erfolgsfall wird die Version der
 * Oracle-Datenbank zurückgegeben.
 */
public class OracleHealthIndicator extends AbstractHealthIndicator {

    @PersistenceContext
    private EntityManager em;

    private static final String VERSION_QUERY = "select BANNER from V$VERSION where BANNER like 'Oracle%'";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            String version = getVersion(em.createNativeQuery(VERSION_QUERY));
            builder.up().withDetail("Oracle Version", version);
        } catch (Exception ex) {
            builder.down(ex);
        }
    }

    private String getVersion(Query query) {
        String version = "";
        List result = query.getResultList();
        if (result != null && !result.isEmpty()) {
            version = result.get(0).toString();
        }
        return version;
    }
}
