package de.bund.bva.isyfact.ueberwachung.actuate.health;

import org.springframework.util.Assert;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.task.model.AbstractTask;

/**
 * Dieser Task aktualisiert den Cache in {@link IsyHealthEndpoint}.
 */
public class IsyHealthTask extends AbstractTask {

    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(IsyHealthTask.class);

    private final IsyHealthEndpoint isyHealthEndpoint;

    public IsyHealthTask(IsyHealthEndpoint isyHealthEndpoint) {
        Assert.notNull(isyHealthEndpoint, "IsyHealtHEndpoint must not be null");
        this.isyHealthEndpoint = isyHealthEndpoint;
    }

    @Override
    public void execute() {
        LOGISY.debug("Starte Health Caching");

        isyHealthEndpoint.aktualisiereCache();

        LOGISY.debug("Health Cache aktualisiert");
    }

}
