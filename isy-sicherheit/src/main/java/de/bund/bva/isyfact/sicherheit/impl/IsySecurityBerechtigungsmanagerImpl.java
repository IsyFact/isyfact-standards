package de.bund.bva.isyfact.sicherheit.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Recht;
import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.Sicherheit;

/**
 * Mit IsyFact 3 kommt eine neue Bibliothek isy-security. Die alte Bibliothek isy-sicherheit ist deprecated.
 * Komponenten (wie beispielsweise SchlüsselverzeichnisClient), die bereits auf isy-security umgestellt sind,
 * funktionieren nicht mehr ohne weiteres, wenn isy-sicherheit anstatt isy-security genutzt wird.
 * Im folgenden werden Beans für die Interfaces von isy-security erstellt, die aber durch isy-sicherheit
 * implementiert werden.
 *
 * @deprecated Muss beim Umstellung von isy-sicherheit auf isy-security entfernt werden.
 */
@Deprecated
public class IsySecurityBerechtigungsmanagerImpl implements Berechtigungsmanager {

    private final Sicherheit<AufrufKontext> sicherheit;

    public IsySecurityBerechtigungsmanagerImpl(Sicherheit<AufrufKontext> sicherheit) {
        this.sicherheit = sicherheit;
    }

    @Override
    public void pruefeRecht(String recht) throws AccessDeniedException {
        this.sicherheit.getBerechtigungsManager().pruefeRecht(recht);
    }

    @Override
    public boolean hatRecht(String recht) {
        return this.sicherheit.getBerechtigungsManager().hatRecht(recht);
    }

    @Override
    public Set<String> getRollen() {
        return this.sicherheit.getBerechtigungsManager()
            .getRollen()
            .stream()
            .map(Rolle::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRechte() {
        return this.sicherheit.getBerechtigungsManager()
            .getRechte()
            .stream()
            .map(Recht::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public Object getTokenAttribute(String key) {
        return null;
    }
}
