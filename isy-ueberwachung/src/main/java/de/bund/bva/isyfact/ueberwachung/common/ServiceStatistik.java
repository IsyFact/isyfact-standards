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
package de.bund.bva.isyfact.ueberwachung.common;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.bund.bva.isyfact.datetime.util.DateTimeUtil;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.exception.service.PlisBusinessToException;
import de.bund.bva.isyfact.serviceapi.annotations.FachlicherFehler;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;

/**
 * Diese Klasse implementiert eine Überwachungs-MBean für Services. Sie liefert die Überwachungsoptionen,
 * welche jeder Service der PLIS anbieten muss.
 *
 */
public class ServiceStatistik implements MethodInterceptor, InitializingBean {
    /**
     * Standard-Wert fuer Anzahl Suchen, anhand derer der Durchschnitt berechnet wird.
     */
    private static final int ANZAHL_AUFRUFE_FUER_DURCHSCHNITT = 10;

    /**
     * Logger.
     */
    private static final IsyLogger LOGISY = IsyLoggerFactory.getLogger(ServiceStatistik.class);

    /**
     * Die maximale Tiefe bei der rekursiven Prüfung auf fachliche Fehler bei der erweiterten fachlichen
     * Fehlerprüfung.
     */
    private static final int MAXTIEFE = 10;

    /**
     * Gibt an, ob die Rückgabeobjektstrukturen auf fachliche Fehler überprüft werden sollen. Kann
     * Auswirkungen auf die Performance haben.
     */
    private boolean fachlicheFehlerpruefung;

    /**
     * Dauern der letzten Such-Aufrufe (in Millisekunden).
     */
    private List<Long> letzteSuchdauern = new LinkedList<>();

    /**
     * Merker für die Minute, in der Werte der letzten Minute ermittelt wurden.
     */
    private volatile LocalDateTime letzteMinute = DateTimeUtil.localDateTimeNow();

    /**
     * Anzahl der nicht fehlerhaften Aufrufe, die in der durch letzteMinute bezeichneten Minute durchgeführt
     * wurden.
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

    public ServiceStatistik(MeterRegistry meterRegistry, Tags tags) {
        Gauge.builder("anzahlAufrufe.LetzteMinute", this, ServiceStatistik::getAnzahlAufrufeLetzteMinute)
            .tags(tags)
            .description("Liefert die Anzahl der nicht fehlerhaften Aufrufe in der letzten Minute")
            .register(meterRegistry);

        Gauge.builder("anzahlFehler.LetzteMinute", this, ServiceStatistik::getAnzahlFehlerLetzteMinute)
            .tags(tags)
            .description("Liefert die Anzahl der fehlerhaften Aufrufe in der letzten Minute")
            .register(meterRegistry);

        Gauge.builder("anzahlFachlicheFehler.LetzteMinute", this, ServiceStatistik::getAnzahlFachlicheFehlerLetzteMinute)
            .tags(tags)
            .description("Liefert die Anzahl der fachlich fehlerhaften Aufrufe in der letzten Minute")
            .register(meterRegistry);

        Gauge.builder("durchschnittsDauer.LetzteAufrufe", this, ServiceStatistik::getDurchschnittsDauerLetzteAufrufe)
            .tags(tags)
            .description("Liefert die durchschnittliche Dauer der letzten 10 Aufrufe in ms")
            .register(meterRegistry);
    }

    /**
     * Berechnet die aktuelle Minute der Systemzeit.
     *
     * @return Der Minuten-Anteil der aktuellen Systemzeit
     */
    private static LocalDateTime getAktuelleMinute() {
        return DateTimeUtil.localDateTimeNow().truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * Gibt an, ob die Rückgabeobjektstrukturen auf fachliche Fehler überprüft werden sollen. Kann
     * Auswirkungen auf die Performance haben.
     *
     * @param fachlicheFehlerpruefung <code>true</code> wenn die Rückgabeobjektstruktur auf fachliche Fehler hin untersucht werden
     *                                         soll, ansonsten <code>false</code>.
     */
    public void setFachlicheFehlerpruefung(boolean fachlicheFehlerpruefung) {
        this.fachlicheFehlerpruefung = fachlicheFehlerpruefung;
    }

    /**
     * Diese Methode zählt einen Aufruf der Komponente für die Statistik. Für die Statistik wird die Angabe
     * der Dauer und ob der Aufruf fehlerhaft war benötigt.
     *
     * @param dauer               Die Dauer des Aufrufs in Millisekunden.
     * @param erfolgreich         Kennzeichen, ob der Aufruf erfolgreich war (<code>true</code>) oder ein technischer Fehler
     *                            aufgetreten ist (<code>false</code>).
     * @param fachlichErfolgreich Kennzeichen, ob der Aufruf fachlich erfolgreich war (<code>true</code>) oder ein fachlicher
     *                            Fehler aufgetreten ist (<code>false</code>).
     */
    public synchronized void zaehleAufruf(long dauer, boolean erfolgreich, boolean fachlichErfolgreich) {
        aktualisiereZeitfenster();
        anzahlAufrufeAktuelleMinute++;

        if (!erfolgreich) {
            anzahlFehlerAktuelleMinute++;
        }

        if (!fachlichErfolgreich) {
            anzahlFachlicheFehlerAktuelleMinute++;
        }

        if (letzteSuchdauern.size() == ANZAHL_AUFRUFE_FUER_DURCHSCHNITT) {
            letzteSuchdauern.remove(ANZAHL_AUFRUFE_FUER_DURCHSCHNITT - 1);
        }

        letzteSuchdauern.add(0, dauer);
    }

    /**
     * Diese Methode veranlasst, dass das Zeitfenster für die Zähler der Fehler und Aufrufe in der aktuellen
     * und letzten Minute aktualisiert wird. Falls eine Minute verstrichen ist, werden die Werte der aktuellen
     * Minute in die der Zähler für die letzte Minute kopiert. Die Zähler für die aktuelle Minute werden auf 0
     * gesetzt. Die Methode sorg dafür, dass dieser Vorgang nur einmal pro Minute ausgeführt werden kann.
     */
    private synchronized void aktualisiereZeitfenster() {
        LocalDateTime aktuelleMinute = getAktuelleMinute();
        if (!aktuelleMinute.isEqual(letzteMinute)) {
            if (ChronoUnit.MINUTES.between(letzteMinute, aktuelleMinute) > 1) {
                // keine infos von letzter Minute
                anzahlAufrufeLetzteMinute = 0;
                anzahlFehlerLetzteMinute = 0;
                anzahlFachlicheFehlerLetzteMinute = 0;
            } else {
                anzahlAufrufeLetzteMinute = anzahlAufrufeAktuelleMinute;
                anzahlFehlerLetzteMinute = anzahlFehlerAktuelleMinute;
                anzahlFachlicheFehlerLetzteMinute = anzahlFachlicheFehlerAktuelleMinute;
            }

            anzahlAufrufeAktuelleMinute = 0;
            anzahlFehlerAktuelleMinute = 0;
            anzahlFachlicheFehlerAktuelleMinute = 0;
            letzteMinute = aktuelleMinute;
        }
    }

    /**
     * Liefert die durchschnittliche Dauer der letzten 10 Aurufe. Definiert eine Methode für das
     * Management-Interface dieser MBean.
     *
     * @return Die durchschnittliche Dauer der letzten 10 Aufrufe in ms.
     */
    private long getDurchschnittsDauerLetzteAufrufe() {
        long result = 0;
        if (!letzteSuchdauern.isEmpty()) {
            // Kopiere Liste um konkurrierende Änderungen zu vermeiden
            // Explizit keine Synchronisierung, um die Anwendungsperformance
            // nicht zu verschlechtern.
            Long[] dauern = letzteSuchdauern.toArray(new Long[0]);
            for (long dauer : dauern) {
                result += dauer;
            }
            result /= letzteSuchdauern.size();
        }
        return result;
    }

    /**
     * Liefert die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen kein Fehler aufgetreten ist.
     * Definiert eine Methode für das Management-Interface dieser MBean.
     *
     * @return Die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen kein Fehler aufgetreten ist.
     */
    private int getAnzahlAufrufeLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlAufrufeLetzteMinute;
    }

    /**
     * Liefert die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein Fehler aufgetreten ist.
     * Definiert eine Methode für das Management-Interface dieser MBean.
     *
     * @return Die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein Fehler aufgetreten ist.
     */
    private int getAnzahlFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlFehlerLetzteMinute;
    }

    /**
     * Liefert die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein fachlicher Fehler
     * aufgetreten ist.
     *
     * @return Die Anzahl der in der letzten Minute gezählten Aufrufe, bei denen ein fachlicher Fehler
     * aufgetreten ist.
     */
    private int getAnzahlFachlicheFehlerLetzteMinute() {
        aktualisiereZeitfenster();
        return anzahlFachlicheFehlerLetzteMinute;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Instant start = DateTimeUtil.getClock().instant();
        boolean erfolgreich = false;
        boolean fachlichErfolgreich = false;

        try {
            Object result = invocation.proceed();
            erfolgreich = true;
            if (fachlicheFehlerpruefung) {
                fachlichErfolgreich = !sindFachlicheFehlerVorhanden(result);
            } else {
                fachlichErfolgreich = true;
            }
            return result;
        } catch (PlisBusinessToException t) {
            // BusinessExceptions werden nicht als technischer Fehler gezählt.
            erfolgreich = true;
            throw t;
        } finally {
            long aufrufDauer = ChronoUnit.MILLIS.between(start, DateTimeUtil.getClock().instant());
            zaehleAufruf(aufrufDauer, erfolgreich, fachlichErfolgreich);
        }
    }

    /**
     * Prüft ob im Rückgabeobjekt fachliche Fehler enthalten waren. Das Rückgabeobjekt muss eine Collection
     * enthalten, die mit @FachlicheFehlerListe annotiert ist.
     *
     * @param result Das Rückgabeobjekt des Aufrufs.
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
     * @param result Das Objekt
     * @param clazz  Die Klasse des Objekts durchsucht werden soll (optional). Kann leergelassen werden beim
     *               Start, kann aber genutzt werden um auf Oberklassen eines Objekts zu prüfen.
     * @param tiefe  tiefe Gibt die aktuelle Tiefe des Aufrufs an. Muss erhöht werden wenn man die
     *               Klassenstruktur nach unten durchläuft.
     * @return <code>true</code> wenn Fehler gefunden, ansonsten <code>false</code>
     */
    boolean pruefeObjektAufFehler(final Object result, Class<?> clazz, int tiefe) {
        // Wenn max. Tiefe erreicht, nicht weiter prüfen
        if (tiefe > MAXTIEFE) {
            LOGISY.trace("Max. Tiefe erreicht, prüfe nicht weiter auf fachliche Fehler");
            return false;
        }

        // Wenn keine Klasse übergeben, selber ermitteln
        Class<?> clazzToScan = clazz;
        if (clazzToScan == null) {
            clazzToScan = result.getClass();
        }

        List<Field> objectFields = Arrays.stream(clazzToScan.getDeclaredFields())
                                         .filter(field -> !ClassUtils.isPrimitiveOrWrapper(field.getType()) && !field.getType().isEnum())
                                         .collect(Collectors.toList());

        LOGISY.trace("{} Analysiere Objekt {} (Klasse {}) {} Felder gefunden.",
            String.join("", Collections.nCopies(tiefe, "-")), result.toString(), clazzToScan.getSimpleName(),
            objectFields.size());

        boolean fehlerGefunden = false;

        for (Field field : objectFields) {
            LOGISY.trace("{} {}.{}, Type {}", String.join("", Collections.nCopies(tiefe, "-")),
                clazzToScan.getSimpleName(), field.getName(), field.getType().getSimpleName());
            field.setAccessible(true);
            try {
                // Prüfe einzelne Klassenfelder (non-Collection) auf annotierten Typ und Vorhandensein
                if (fieldIsNotACollection(field)) {
                    Object fieldObject = field.get(result);

                    if (fieldObject != null) {
                        if (fieldObject.getClass().isAnnotationPresent(FachlicherFehler.class)) {
                            // Fachliches Fehlerobjekt gefunden
                            return true;
                        }

                        // Wenn kein String, dann prüfe rekursiv Objektstruktur
                        if (fieldObject.getClass() != String.class) {
                            fehlerGefunden = pruefeObjektAufFehler(fieldObject, null, tiefe + 1) || fehlerGefunden;
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

        // Die Klassen-Hierachie rekursiv nach oben prüfen
        if (typeHasSuperClass(clazzToScan)) {
            LOGISY.trace("{}> Climb up class hierarchy! Source {}, Target {}",
                String.join("", Collections.nCopies(tiefe, "-")), clazzToScan.getSimpleName(),
                clazzToScan.getSuperclass());
            fehlerGefunden =
                // Aufruf mit gleicher Tiefe, da Vererbung nach oben durchlaufen wird
                pruefeObjektAufFehler(result, clazzToScan.getSuperclass(), tiefe) || fehlerGefunden;
        }

        return fehlerGefunden;
    }

    private boolean typeHasSuperClass(Class clazz) {
        return clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class);
    }

    private boolean fieldIsNotACollection(Field field) {
        return !Collection.class.isAssignableFrom(field.getType());
    }

    @Override
    public void afterPropertiesSet() {
        LOGISY.debug("ServiceStatistik " + (fachlicheFehlerpruefung ?
            " mit erweiterter fachlicher Fehlerprüfung " :
            "") + " initialisiert.");
    }
}
