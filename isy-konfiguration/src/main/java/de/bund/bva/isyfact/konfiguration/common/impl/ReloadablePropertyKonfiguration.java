/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.konfiguration.common.impl;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import de.bund.bva.isyfact.konfiguration.common.Konfiguration;
import de.bund.bva.isyfact.konfiguration.common.KonfigurationChangeListener;
import de.bund.bva.isyfact.konfiguration.common.ReloadableKonfiguration;
import de.bund.bva.isyfact.konfiguration.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.logging.util.MdcHelper;

/**
 * Diese Klasse bietet einen typsicheren Zugriff auf eine Konfiguration {@link Konfiguration}. Dazu wird an
 * die Implementierung {@link PropertyKonfiguration} delegiert. <br/>
 * Die Methode {@link #checkAndUpdate()} kann regelmäßig aufgerufen werden, um Konfigurationsänderungen zu
 * erfassen. <br/>
 * Um Konfigurationsänderungen in der Anwendung mitzubekommen, sollten Konfigurationswerte nicht in
 * Instanzvariablen sondern direkt per getAsXYZ aus dieser Klasse ausgelesen werden. <br/>
 * Falls das nicht möglich ist, kann sich eine interessierte Komponente per
 * {@link #addKonfigurationChangeListener(KonfigurationChangeListener)} als Listener für
 * Konfigurationsänderungen registrieren.
 *
 * Falls die Konfiguration geändert wurde, werden jedem registrierten {@link KonfigurationChangeListener} die
 * geänderten Konfigurationsschlüssel mitgeteilt.
 *
 */
public class ReloadablePropertyKonfiguration implements ReloadableKonfiguration {

    /**
     * Logger der Klasse.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ReloadablePropertyKonfiguration.class);

    /**
     * Diese Klasse sorgt für das Laden der Properties.
     */
    private ReloadablePropertyProvider propertyProvider;

    /**
     * Diese Klasse kapselt den typsicheren Zugriff auf die per {@link #propertyProvider} geladenen
     * Properties.
     */
    private PropertyKonfiguration propertyKonfiguration;

    /**
     * Lister der registrierten ChangeListener, die über Konfigänderungen informiert werden wollen.
     */
    private List<KonfigurationChangeListener> konfigurationChangeListener;

    /**
     * Erzeugt eine neue Konfiguration eine Menge von Spring Location-Patterns.
     *
     * @see PropertyKonfiguration#PropertyKonfiguration(String...)
     */
    public ReloadablePropertyKonfiguration(String... locationPatterns) {
        this.propertyProvider = new ReloadablePropertyProvider(locationPatterns);
        this.konfigurationChangeListener = new LinkedList<>();
        this.propertyKonfiguration =
            new PropertyKonfiguration(this.propertyProvider.getProperties());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean checkAndUpdate() {
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.KONFIGURATION_DATEI_GEAENDERT,
            "Prüfe auf geänderte Konfigurationsdateien.");
        boolean korrelationsIdGesetzt = false;
        if (MdcHelper.liesKorrelationsId() == null) {
            MdcHelper.pushKorrelationsId(UUID.randomUUID().toString());
            korrelationsIdGesetzt = true;
        }

        boolean neueVersionGeladen = this.propertyProvider.checkAndUpdate();
        if (neueVersionGeladen) {
            LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.KONFIGURATION_DATEI_GEAENDERT,
                "Mindestens eine Konfigurationsdatei wurde geändert.");
            Properties neueProperties = this.propertyProvider.getProperties();
            Properties aktuelleProperties = this.propertyKonfiguration.getProperties();
            this.propertyKonfiguration = new PropertyKonfiguration(neueProperties);
            fireKonfigurationChanged(aktuelleProperties, neueProperties);
        }

        if (korrelationsIdGesetzt) {
            MdcHelper.entferneKorrelationsId();
        }
        return neueVersionGeladen;
    }

    /**
     * Informiere alle registrierten Listener. Die geänderten Schlüssel werden aus den angegebenen Properties
     * ermittelt.
     * @param bisherigeProperties
     *            Die bisherige Konfiguration.
     * @param neueProperties
     *            Die neue Konfiguration.
     */
    private void fireKonfigurationChanged(Properties bisherigeProperties, Properties neueProperties) {
        Set<String> geaenderteSchluessel =
            unmodifiableSet(ermittleGeaenderteSchluessel(bisherigeProperties, neueProperties));
        for (String schluessel : geaenderteSchluessel) {
            LOG.debug("Konfigurationsparameter {} wurde geändert.", schluessel);
        }
        if (!geaenderteSchluessel.isEmpty()) {
            synchronized (this.konfigurationChangeListener) {
                for (KonfigurationChangeListener listener : this.konfigurationChangeListener) {
                    LOG.debug("Informiere {} über Konfigurationsänderung.",
                        listener.getClass().toString());
                    listener.onKonfigurationChanged(geaenderteSchluessel);
                }
            }
        }
    }

    /**
     * Hilfsmethode um alle geänderten Parameter aus den Properties a und b zu ermitteln. Die Liste enthält
     * alle Schlüssel, die entweder einen unterschiedlichen Wert in A und B haben, oder nur in einer der
     * beiden Properties enthalten sind.
     * @param a
     *            Properties A
     * @param b
     *            Properties B
     * @return Liste aller nicht identischen Schlüssel.
     */
    @SuppressWarnings("unchecked")
    private Set<String> ermittleGeaenderteSchluessel(Properties a, Properties b) {
        Set<Entry<?, ?>> changedEntries = new HashSet<>(a.entrySet());
        changedEntries.removeAll(b.entrySet());
        return (Set<String>) changedEntries.stream().map(Entry::getKey).collect(toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addKonfigurationChangeListener(KonfigurationChangeListener listener) {
        if (listener != null) {
            synchronized (this.konfigurationChangeListener) {
                if (!this.konfigurationChangeListener.contains(listener)) {
                    this.konfigurationChangeListener.add(listener);
                } else {
                    LOG.info(LogKategorie.JOURNAL,
                        EreignisSchluessel.KONFIGURATION_LISTENER_NICHT_HINZUGEFUEGT,
                        "Listener wurde nicht hinzugefügt, da die gleiche Instanz bereits registriert ist.");
                }
            }

        } else {
            LOG.debug("Listener ist null und wurde nicht hinzugefügt.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeKonfigurationChangeListener(KonfigurationChangeListener listener) {
        synchronized (this.konfigurationChangeListener) {
            this.konfigurationChangeListener.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAsBoolean(String schluessel, boolean defaultWert) {
        return this.propertyKonfiguration.getAsBoolean(schluessel, defaultWert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getAsBoolean(String schluessel) {
        return this.propertyKonfiguration.getAsBoolean(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAsDouble(String schluessel, double defaultWert) {
        return this.propertyKonfiguration.getAsDouble(schluessel, defaultWert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAsDouble(String schluessel) {
        return this.propertyKonfiguration.getAsDouble(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAsInteger(String schluessel, int defaultWert) {
        return this.propertyKonfiguration.getAsInteger(schluessel, defaultWert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAsInteger(String schluessel) {
        return this.propertyKonfiguration.getAsInteger(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getAsLong(String schluessel, long defaultWert) {
        return this.propertyKonfiguration.getAsLong(schluessel, defaultWert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getAsLong(String schluessel) {
        return this.propertyKonfiguration.getAsLong(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(String schluessel, String defaultWert) {
        return this.propertyKonfiguration.getAsString(schluessel, defaultWert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(String schluessel) {
        return this.propertyKonfiguration.getAsString(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsRawString(String schluessel, String defaultWert) {
        return this.propertyKonfiguration.getAsRawString(schluessel, defaultWert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsRawString(String schluessel) {
        return this.propertyKonfiguration.getAsRawString(schluessel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getSchluessel() {
        return this.propertyKonfiguration.getSchluessel();
    }

    @Override
    public boolean hasKonfigurationChangeListener(KonfigurationChangeListener listener) {
        if (listener != null) {
            return this.konfigurationChangeListener.contains(listener);
        } else {
            return false;
        }
    }

}
