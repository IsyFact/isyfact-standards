package de.bund.bva.isyfact.sicherheit.impl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.security.core.Berechtigungsmanager;
import de.bund.bva.isyfact.security.core.Security;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;
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
public class IsySecurityImpl implements Security {

    private final Sicherheit<AufrufKontext> sicherheit;

    public IsySecurityImpl(Sicherheit<AufrufKontext> sicherheit) {
        this.sicherheit = sicherheit;
    }

    @Override
    public Optional<Authentifizierungsmanager> getAuthentifizierungsmanager() {
        return Optional.empty();
    }

    @Override
    public Berechtigungsmanager getBerechtigungsmanager() {
        return new IsySecurityBerechtigungsmanagerImpl(this.sicherheit);
    }

    @Override
    public Set<String> getAlleRollen() {
        final Set<Rolle> rollen = this.sicherheit.getAlleRollen();
        return rollen
            .stream()
            .map(Rolle::getId)
            .collect(Collectors.toSet());
    }
}
