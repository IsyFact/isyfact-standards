package de.bund.bva.isyfact.datetime.test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * @author Björn Saxe, msg systems ag
 */
public class TestClock extends Clock {

    private Instant instant;

    private ZoneId zoneId;

    @Override
    public ZoneId getZone() {
        return null;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return null;
    }

    @Override
    public Instant instant() {
        return null;
    }
}
