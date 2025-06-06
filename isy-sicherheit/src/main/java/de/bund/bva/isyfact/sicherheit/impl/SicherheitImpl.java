package de.bund.bva.isyfact.sicherheit.impl;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextFactory;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.Rolle;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import de.bund.bva.isyfact.sicherheit.config.IsySicherheitConfigurationProperties;
import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.accessmgr.AuthentifzierungErgebnis;
import de.bund.bva.isyfact.sicherheit.common.exception.AuthentifizierungTechnicalException;
import de.bund.bva.isyfact.sicherheit.common.exception.InitialisierungsException;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.InitializingBean;

/**
 * Diese Klasse dient als Implementierung der Komponente Sicherheit und kann als Bean in anderen Anwendungen
 * und Komponenten verwendet werden. Es wird das Interface Sicherheit implementiert, über welches
 * Berechtigungsmanager instanziiert werden können.
 *
 * @param <K> Typ des Aufrufkontextes
 * @param <E> Typ des Ergebnisses der Authentifizierung
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class SicherheitImpl<K extends AufrufKontext, E extends AuthentifzierungErgebnis>
    implements Sicherheit<K>, InitializingBean {

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(SicherheitImpl.class);

    private static final String CACHE_ALIAS = "de.bund.bva.isyfact.sicherheit.authentifizierung";

    /** Pfad zum Rollen-Rechte-Mapping. */
    private final String rollenRechteDateiPfad;

    /** Zugriff auf den AufrufKontextVerwalter. */
    private final AufrufKontextVerwalter<K> aufrufKontextVerwalter;

    /** Referenz auf den AccessManager für den Zugriff auf Rollen/Rechte der Benutzer. */
    private final AccessManager<K, E> accessManager;

    /**
     * Cache für Authentifizierungsinformationen, so dass der AccessManager entlastet
     * wird.
     */
    private final Cache<Object, E> authentifizierungCache;

    /** Das ausgelesene Rollenrechtemapping. */
    private RollenRechteMapping mapping;

    /** Zugriff auf die Aufrufkontext-Factory. */
    private AufrufKontextFactory<K> aufrufKontextFactory;

    private boolean cacheAktiviert = true;

    public SicherheitImpl(String rollenRechteDateiPfad, AufrufKontextVerwalter<K> aufrufKontextVerwalter,
        AufrufKontextFactory<K> aufrufKontextFactory, AccessManager<K, E> accessManager,
        IsySicherheitConfigurationProperties properties) {
        this.rollenRechteDateiPfad = rollenRechteDateiPfad;
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
        this.accessManager = accessManager;
        this.aufrufKontextFactory = aufrufKontextFactory;

        authentifizierungCache = setupCache(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Berechtigungsmanager getBerechtigungsManager() throws AuthentifizierungTechnicalException {

        // Der AufrufKontextverwalter muss durch die nutzende Anwendung korrekt bereitgestellt werden
        // Ist das nicht der Fall muss davon ausgegangen werden, dass die Authentifizierung fehlgeschlagen ist
        // und der Aufrufer dies bisher technisch nicht behandelt hat.
        if (this.aufrufKontextVerwalter == null) {
            throw new AuthentifizierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR);
        }

        // Der AufrufKontext muss durch die nutzende Anwendung korrekt bereitgestellt werden
        // Ist das nicht der Fall muss davon ausgegangen werden, dass die Authentifizierung fehlgeschlagen ist
        // und der Aufrufer dies bisher technisch nicht behandelt hat.
        K aufrufKontext = this.aufrufKontextVerwalter.getAufrufKontext();
        if (aufrufKontext == null) {
            throw new AuthentifizierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR);
        }

        // im Regelfall kommen die Rollen mit dem Aufrufkontext mit
        // falls nicht authentifiziere den Nutzer
        if (!aufrufKontext.isRollenErmittelt()) {
            return getBerechtigungsManagerUndAuthentifiziere(aufrufKontext);
        }
        return erzeugeBerechtigungsmanager(aufrufKontext);
    }

    @SuppressWarnings("unchecked")
    private Cache<Object, E> setupCache(IsySicherheitConfigurationProperties properties) {
        if (properties.getTtl() == 0) {
            cacheAktiviert = false;
            return null;
        } else {
            CacheConfiguration<Object, AuthentifzierungErgebnis> cacheConfiguration =
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, AuthentifzierungErgebnis.class,
                        ResourcePoolsBuilder.heap(properties.getMaxelements())).withExpiry(
                    Expirations.timeToLiveExpiration(Duration.of(properties.getTtl(), TimeUnit.SECONDS)))
                    .build();
            CacheManager cacheManager =
                CacheManagerBuilder.newCacheManagerBuilder().withCache(CACHE_ALIAS, cacheConfiguration)
                    .build(true);

            return (Cache<Object, E>) cacheManager
                .getCache(CACHE_ALIAS, Object.class, AuthentifzierungErgebnis.class);
        }
    }

    /**
     * Erzeugt einen Berechtigungsmanager anhand eines Aufrufkontextes.
     *
     * @param aufrufKontext Aufrufkontext
     * @return einen neuen Berechtigungmanager.
     */
    private Berechtigungsmanager erzeugeBerechtigungsmanager(K aufrufKontext) {
        BerechtigungsmanagerImpl berechtigungsmanager =
            new BerechtigungsmanagerImpl(aufrufKontext.getRolle());
        berechtigungsmanager.setRollenRechteMapping(this.mapping);
        return berechtigungsmanager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leereCache() {
        authentifizierungCache.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Berechtigungsmanager getBerechtigungsManagerUndAuthentifiziere(
        K unauthentifizierterAufrufKontext) {
        // Der AufrufKontext muss bereitgestellt werden, da dieser die zur Authentifizierung benötigten Daten
        // bereithält.
        if (unauthentifizierterAufrufKontext == null) {
            throw new AuthentifizierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUFRUFKONTEXT_NICHT_VERFUEGBAR);
        }

        Optional<E> authentifizierungErgebnis;
        if (cacheAktiviert) {
            authentifizierungErgebnis = authentifiziereKontextMitCache(unauthentifizierterAufrufKontext);
        } else {
            authentifizierungErgebnis = authentifiziereKontext(unauthentifizierterAufrufKontext);
        }

        aktualisiereAufrufKontextAusAuthentifizierungErgebnis(
            unauthentifizierterAufrufKontext.getKorrelationsId(), authentifizierungErgebnis.orElseThrow(
                () -> new AuthentifizierungTechnicalException("AccessManager hat kein Ergebnis geliefert.")));

        return erzeugeBerechtigungsmanager(this.aufrufKontextVerwalter.getAufrufKontext());
    }

    private Optional<E> authentifiziereKontext(K unauthentifizierterAufrufKontext) {
        Optional<E> authentifizierungErgebnis = Optional.empty();
        try {
            authentifizierungErgebnis =
                Optional.ofNullable(accessManager.authentifiziere(unauthentifizierterAufrufKontext));
        } finally {
            authentifizierungErgebnis.ifPresent(accessManager::logout);
        }
        return authentifizierungErgebnis;
    }

    private Optional<E> authentifiziereKontextMitCache(K unauthentifizierterAufrufKontext) {
        Object cacheKey = accessManager.erzeugeCacheSchluessel(unauthentifizierterAufrufKontext);

        Optional<E> authentifizierungErgebnis = Optional.ofNullable(authentifizierungCache.get(cacheKey));

        if (!authentifizierungErgebnis.isPresent()) {
            authentifizierungErgebnis = authentifiziereKontext(unauthentifizierterAufrufKontext);
            authentifizierungErgebnis.ifPresent(ergebnis -> authentifizierungCache.put(cacheKey, ergebnis));
        }
        return authentifizierungErgebnis;
    }

    /**
     * Schreibt Daten aus einer Benutzersession in den Aufrufkontext.
     *
     * @param correlationId            Korrelations-ID des Service-Aufrufs
     * @param authentifzierungErgebnis Ergebnis der Authentifizierung
     */
    private void aktualisiereAufrufKontextAusAuthentifizierungErgebnis(String correlationId,
        E authentifzierungErgebnis) {

        // Erzeuge Aufrufkontext aufbauend auf den Session-Informationen, oder befülle vorhandenen
        K aufrufKontext = this.aufrufKontextVerwalter.getAufrufKontext();

        boolean neuerAufrufKontext = false;
        if (aufrufKontext == null) {
            aufrufKontext = this.aufrufKontextFactory.erzeugeAufrufKontext();
            this.aufrufKontextVerwalter.setAufrufKontext(aufrufKontext);
            neuerAufrufKontext = true;
        }

        this.accessManager.befuelleAufrufkontext(aufrufKontext, authentifzierungErgebnis);
        aufrufKontext.setKorrelationsId(correlationId);

        // Falls der AufrufKontext neu erzeugt wurde, löse Lifecycle aus.
        if (neuerAufrufKontext) {
            this.aufrufKontextFactory.nachAufrufKontextVerarbeitung(aufrufKontext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info(LogKategorie.SICHERHEIT, EreignisSchluessel.INITIALISIERUNG_SICHERHEIT,
            "Initialisiere Sicherheitskomponente mit RollenRechteDatei: {}", this.rollenRechteDateiPfad);
        if (this.rollenRechteDateiPfad == null || this.rollenRechteDateiPfad.isEmpty()) {
            throw new InitialisierungsException(SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT,
                "rollenRechteDateiPfad");
        }
        if (this.aufrufKontextVerwalter == null) {
            throw new InitialisierungsException(SicherheitFehlerSchluessel.MSG_INITIALISIERUNGS_ELEMENT_FEHLT,
                "aufrufKontextVerwalter");
        }

        XmlAccess access = new XmlAccess();
        this.mapping = access.parseRollenRechteFile(this.rollenRechteDateiPfad);

        LOG.debug("Initialisierung der Sicherheitskomponente beendet.");
    }

    @Override
    public Set<Rolle> getAlleRollen() {
        return this.mapping.getRollenRechteMapping().keySet();
    }

}
