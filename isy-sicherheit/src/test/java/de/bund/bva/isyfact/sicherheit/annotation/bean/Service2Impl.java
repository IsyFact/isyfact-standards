package de.bund.bva.isyfact.sicherheit.annotation.bean;

import de.bund.bva.isyfact.sicherheit.annotation.Gesichert;

/**
 * Ein Bean, welches abgesicherte Methoden ohne Funktionalität bereitstellt.
 * <p>
 * Es dient zum Testen, dass die korrekten Exceptions geworfen werden.
 *
 *
 */
@Gesichert("Recht_A")
public class Service2Impl implements Service2Intf {

    /**
     * {@inheritDoc}
     */
    @Override
    public void gesichertDurch_RechtA() {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Gesichert("Recht_B")
    public void gesichertDurch_RechtB() {
        // noop
    }

}
