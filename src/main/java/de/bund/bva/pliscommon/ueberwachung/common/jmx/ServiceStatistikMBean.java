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
package de.bund.bva.pliscommon.ueberwachung.common.jmx;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisToException;
import de.bund.bva.pliscommon.serviceapi.annotations.FachlicherFehler;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Diese Klasse implementiert eine Überwachungs-MBean für Services. Sie liefert die Überwachungsoptionen,
 * welche jeder Service der PLIS anbieten muss.
 *
 * @author sd&m Simon Spielmann
 * @version $Id: ServiceStatistikMBean.java 144975 2015-08-18 13:07:56Z sdm_jmeisel $
 */
@ManagedResource(description = "Diese MBean liefert Überwacht die Aufrufe eines Services.")
public class ServiceStatistikMBean implements MethodInterceptor, InitializingBean {
    /**
     * Standard-Wert fuer Anzahl Suchen, anhand derer der Durchschnitt berechnet wird.
     */
    private static final int ANZAHL_AUFRUFE_FUER_DURCHSCHNITT = 10;

    /**
     * Gibt an, ob die Rückgabeobjektstrukturen auf fachliche Fehler überprüft werden sollen. Kann
     * Auswirkungen auf die Performance haben.
     */
    private boolean erweiterteFachlicheFehlerpruefung;

    /**
     * Gibt an, ob die Rückgabeobjektstrukturen auf fachliche Fehler überprüft werden sollen. Kann
     * Auswirkungen auf die Performance haben.
     *
     * @param fachlicheFehlerpruefungAktiviert
     *            <code>true</code> wenn die Rückgabeobjektstruktur auf fachliche Fehler hin untersucht werden
     *            soll, ansonsten <code>false</code>.
     */
    public void setErweiterteFachlicheFehlerpruefung(boolean fachlicheFehlerpruefungAktiviert) {
        this.erweiterteFachlicheFehlerpruefung = fachlicheFehlerpruefungAktiviert;
    }

    /**
     * Dauern der letzten Such-Aufrufe (in Millisekunden).
     */
    private List<Long> letzteSuchdauern = new LinkedList<Long>();

    /**
     * Merker für die Minute, in der Werte der letzten Minute ermittelt wurden.
     */
    private volatile int letzteMinute;

    /**
     * Anzahl der nicht fehlerhaften Aufrufe, die in der durch letzteMinute bezeichneten Minute durchgeführt
     * wurden.
     *
     */
    private volatile int anzahlAufrufeLetzteMinute;

    /**
     * Anzahl der nicht fehlerhaften Aufrufe, die in der aktuellen Minute durchgeführt wurden.
     */
    private volatile int anzahlAufrufeAktuelleMinute;

    /**
     * Anzahl der Aufrufe, die in der durch letzteMinute bezeichneten Minute durchgeführt wurden, bei denen
     * ein techinscher Fehler aufgetreten ist.
     */
    private volatile int anzahlFehlerLetzteMinute;

    /**
     * Anzahl der Aufrufe, die in der aktuellen Minute durchgeführt wurden, bei denen ein techinscher Fehler
     * aufgetreten ist.
     */
    private volatile int anzahlFehlerAktuelleMinute;

    /**
     * Die Anzahl der fachlichen Fehler in der aktuellen Minute. Ein fachlicher Fehler liegt vor, wenn
     * entweder eine Exception vom Typ PlusBusinessException geworfen wurde oder die zurückgegebene
     * Fehlerliste Einträge enthielt.
     */
    private volatile int anzahlFachlicheFehlerLetzteMinute;

    /**
     * Die Anzahl der fachlichen Fehler in der letzten Minute. Ein fachlicher Fehler liegt vor, wenn entweder
     * eine Exception vom Typ PlusBusinessException geworfen wurde oder die zurückgegebene Fehlerliste
     * Einträge enthielt.
     */
    private volatile int anzahlFachlicheFehlerAktuelleMinute;

    /** Fehlercode einer eventuell geworfenen Exception. */
    private String fehlerCode;

    /**
     * Logger.
     */
    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(ServiceStatistikMBean.class);

    /**
     * Die maximale Tiefe bei der rekursiven Prüfung auf fachliche Fehler bei der erweiterten fachlichen
     * Fehlerprüfung.
     */
    private static final int MAXTIEFE = 10;

    /**
     * Diese Methode zählt einen Aufruf der Komponente für die Statistik. Für die Statistik wird die Angabe
     * der Dauer und ob der Aufruf fehlerhaft war benötigt.
     * @param dauer
     *            Die Dauer des Aufrufs in Millisekunden.
     * @param erfolgreich
     *            Kennzeichen, ob der Aufruf erfolgreich war (<code>true</code>) oder ein technischer Fehler
     *            aufgetreten ist (<code>false</code>).
     * @param fachlichErfolgreich
     *            Kennzeichen, ob der Aufruf fachlich erfolgreich war (<code>true</code>) oder ein fachlicher
     *            Fehler aufgetreten ist (<code>false</code>).
     */
    public synchronized void zaehleAufruf(long dauer, boolean erfolgreich, boolean fachlichErfolgreich) {
        aktualisiereZeitfenster();
        this.anzahlAufrufeAktuelleMinute++;
        if (!erfolgreich) {
            this.anzahlFehlerAktuelleMinute++;
        }
        if (!fachlichErfolgreich) {
            this.anzahlFachlicheFehlerAktuelleMinute++;
        }
        if (this.letzteSuchdauern.size() == ANZAHL_AUFRUFE_FUER_DURCHSCHNITT) {
            this.letzteSuchdauern.remove(ANZAHL_AUFRUFE_FUER_DURCHSCHNITT - 1);
        }
        this.letzteSuchdauern.add(0, dauer);
    }

    /**
     * Diese Methode veranlasst, dass das Zeitfenster für die Zähler der Fehler und Aufrufe in der aktuellen
     * und letzten Minute aktualisiert wird. Falls eine Minute verstrichen ist, werden die Werte der aktuellen
     * Minute in die der Zähler für die letzte Minut kopiert. Die Zähler für die aktuelle Minute werden auf 0
     * gesetzt. Die Methode sorg dafür, dass dieser Vorgang nur einmal pro Minute ausgeführt werden kann.
     */
    private synchronized void aktualisiereZeitfenster() {
        int aktuelleMinute = getAktuelleMinute();
        if (aktuelleMinute != this.letzteMinute) {
            if ((aktuelleMinute - this.letzteMinute) > 1) {
                // keine infos von letzten Minute
                this.anzahlAufrufeLetzteMinute = 0;
                this.anzahlFehlerLetzteMinute = 0;
                this.anzahlFachlicheFehlerLetzteMinute = 0;
            } else {
                this.anzahlAufrufeLetzteMinute = this.anzahlAufrufeAktuelleMinute;
                this.anzahlFehlerLetzteMinute = this.anzahlFehlerAktuelleMinute;
                this.anzahlFachlicheFehlerLetzteMinute = this.anzahlFachlicheFehlerAktuelleMinute;
            }

            this.anzahlAufrufeAktuelleMinute = 0;
            this.anzahlFehlerAktuelleMinute = 0;
            this.anzahlFachlicheFehlerAktuelleMinute = 0;
            this.letzteMinute = aktuelleMinute;
        }
    }

    /**
     * Berechnet die aktuelle Minute der Systemzeit.
     * @return Der Minuten-Anteil der aktuellen Systemzeit
     */
    private static final int getAktuelleMinute() {
        return (int) (System.currentTimeMillis() / 60000);
    }

    /**
     * Liefert die durchschnittliche Dauer der letzten 10 Aurufe. Definiert eine Methode für das
     * Management-Interface dieser MBean.
     * @return Die durchschnittliche Dauer der letzten 10 Aufrufe in ms.
     */
    @ManagedAttribute(description = "Liefert die durchschnittliche Dauer der letzten 10 Aufrufe in ms.")
    public long getDurchschnittsDauerLetzteAufrufe() {
        long result = 0;
        if (this.letzteSuchdauern.size() > 0) {
            // Kopiere Liste um konkurrierende Änderungen zu vermeiden
            // Explizit keine Synchronisierung, um die Anwendungsperformance
            // nicht zu verschlechtern.
            Long[] dauern = this.letzteSuchdauern.toArray(new Long[0]);
            for (long dauer : dauern) {
                result += dauer;
            }
            result /= this.letzteSuchdauern.size();
        }
        return result;
    }

    /**
     * Liefert die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen kein Fehler aufgetreten ist.
     * Definiert eine Methode für das Management-Interface dieser MBean.
     * @return Die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen kein Fehler aufgetreten ist.
     */
    @ManagedAttribute(description = "Liefert die Anzahl der nicht fehlerhaften Aufrufe in der letzten Minute")
    public int getAnzahlAufrufeLetzteMinute() {
        aktualisiereZeitfenster();
        return this.anzahlAufrufeLetzteMinute;
    }

    /**
     * Liefert die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein Fehler aufgetreten ist.
     * Definiert eine Methode für das Management-Interface dieser MBean.
     * @return Die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein Fehler aufgetreten ist.
     */
    @ManagedAttribute(description = "Liefert die Anzahl der fehlerhaften Aufrufe in der letzten Minute")
    public int getAnzahlFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return this.anzahlFehlerLetzteMinute;
    }

    /**
     * Liefert die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein fachlicher Fehler
     * aufgetreten ist.
     * @return Die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein fachlicher Fehler
     *         aufgetreten ist.
     */
    @ManagedAttribute(
        description = "Liefert die Anzahl der fachlich fehlerhaften Aufrufe in der letzten Minute")
    public int getAnzahlFachlicheFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return this.anzahlFachlicheFehlerLetzteMinute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startZeit = System.currentTimeMillis();
        boolean erfolgreich = false;
        boolean fachlichErfolgreich = false;

        try {
            Object result = invocation.proceed();
            erfolgreich = true;
            if (this.erweiterteFachlicheFehlerpruefung) {
                fachlichErfolgreich = !sindFachlicheFehlerVorhanden(result);
            }
            return result;
        } catch (PlisBusinessToException t) {
            // BusinessExceptions werden nicht als technischer Fehler gezählt.
            erfolgreich = true;
            this.fehlerCode = t.getAusnahmeId();
            throw t;
        } catch (PlisToException t) {
            this.fehlerCode = t.getAusnahmeId();
            throw t;
        } finally {
            long aufrufDauer = System.currentTimeMillis() - startZeit;
            zaehleAufruf(aufrufDauer, erfolgreich, fachlichErfolgreich);
        }
    }

    /**
     * Prüft ob im Rückgabeobjekt fachliche Fehler enthalten waren. Das Rückgabeobjekt muss eine Collection
     * enthalten, die mit @FachlicheFehlerListe annotiert ist.
     *
     * @param result
     *            Das Rückgabeobjekt des Aufrufs.
     * @return true bei Fehlern, sonst false
     */
    private boolean sindFachlicheFehlerVorhanden(final Object result) {
        return pruefeObjektAufFehler(result, null, 1);
    }

    /**
     * Durchsucht eine Klasse nach Fehlerobjekten, die nicht null sind, oder Fehlercollections, die nicht leer
     * sind. Fehlerobjekten sind mit {link FachlicherFehler} annotiert.
     *
     * Durchsucht Oberklassen & untergeordnete Objektstrukturen ebenfalls rekursiv.
     *
     * @param result
     *            Das Objekt
     * @param clazz
     *            Die Klasse des Objekts durchsucht werden soll (optional). Kann leergelassen werden beim
     *            Start, kann aber genutzt werden um auf Oberklassen eines Objekts zu prüfen.
     * @param tiefe
     *            tiefe Gibt die aktuelle Tiefe des Aufrufs an. Muss erhöht werden wenn man die
     *            Klassenstruktur nach unten durchläuft.
     * @return <code>true</code> wenn Fehler gefunden, ansonsten <code>false</code>
     */
    boolean pruefeObjektAufFehler(final Object result, Class<?> clazz, int tiefe) {
        boolean fehlerGefunden = false;
        Class<?> clazzToScan = clazz;
        // Wenn keine Klasse übergeben, selber ermitteln
        if (clazzToScan == null) {
            clazzToScan = result.getClass();
        }

        // Wenn max. Tiefe erreicht, nicht weiter prüfen
        if (tiefe > MAXTIEFE) {
            LOGISY.trace("Max. Tiefe erreicht, prüfe nicht weiter auf fachliche Fehler");
            return false;
        }

        Field[] fields = clazzToScan.getDeclaredFields();

        LOGISY.trace("{} Analysiere Objekt {} (Klasse {}) {} Felder gefunden.",
            StringUtils.repeat("-", tiefe), result.toString(), clazzToScan.getSimpleName(), fields.length);

        for (Field field : fields) {
            if (!ClassUtils.isPrimitiveOrWrapper(field.getType()) && !field.getType().isEnum()) {
                LOGISY.trace("{} {}.{}, Type {}", StringUtils.repeat("-", tiefe),
                    clazzToScan.getSimpleName(), field.getName(), field.getType().getSimpleName());
                field.setAccessible(true);
                try {
                    // Prüfe einzelne Klassenfelder (non-Collection) auf annotierten Typ und Vorhandensein
                    if (!Collection.class.isAssignableFrom(field.getType())) {
                        if (field.get(result) != null) {
                            Object fieldObject = field.get(result);
                            if (fieldObject.getClass().isAnnotationPresent(FachlicherFehler.class)) {
                                // Fachliches Fehlerobjekt gefunden
                                return true;
                            }

                            // Wenn kein String, dann prüfe rekursiv Objektstruktur
                            if (fieldObject.getClass() != String.class) {
                                fehlerGefunden =
                                    pruefeObjektAufFehler(fieldObject, null, tiefe + 1) ? true
                                        : fehlerGefunden;
                            }
                        }
                    } else {
                        // Collection, prüfen ob fachliche Fehlerliste
                        ParameterizedType type = (ParameterizedType) field.getGenericType();
                        Class<?> collectionTypeArgument = (Class<?>) type.getActualTypeArguments()[0];
                        if (collectionTypeArgument.isAnnotationPresent(FachlicherFehler.class)) {
                            // Ist Fehlerliste, prüfen ob nicht leer
                            Collection<?> collection = (Collection<?>) field.get(result);
                            if (collection != null && !collection.isEmpty()) {
                                // Fachliche Fehler in Fehlerliste gefunden
                                return true;
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Nichts tun, Feld wird ignoriert
                    LOGISY.debug("Feldzugriffsfehler: {}", e.getMessage());
                }
            }
        }

        // Die Klassen-Hierachie rekursiv nach oben prüfen
        if (clazzToScan.getSuperclass() != null && !clazzToScan.getSuperclass().equals(Object.class)) {
            LOGISY.trace("{}> Climb up class hierarchy! Source {}, Target {}",
                StringUtils.repeat("-", tiefe), clazzToScan.getSimpleName(), clazzToScan.getSuperclass());
            fehlerGefunden =
            // Aufruf mit gleicher Tiefe, da Vererbung nach oben durchlaufen wird
                pruefeObjektAufFehler(result, clazzToScan.getSuperclass(), tiefe) ? true : fehlerGefunden;
        }

        return fehlerGefunden;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        LOGISY.debug("ServiceStatistikMBean "
            + (this.erweiterteFachlicheFehlerpruefung ? " mit erweiterter fachlicher Fehlerprüfung " : "")
            + " initialisiert.");
    }

    /**
     * Lädt den {@link AufrufKontextTo} aus den Parametern der aufgerufenen Funktion.
     *
     * @param args
     *            die Argumente der Service-Operation
     *
     * @return das AufrufKontextTo Objekt
     */
    private AufrufKontextTo leseAufrufKontextTo(Object[] args) {
        if (ArrayUtils.isNotEmpty(args) && args[0] instanceof AufrufKontextTo) {
            return (AufrufKontextTo) args[0];
        } else {
            return null;
        }
    }

}
