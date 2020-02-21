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
package de.bund.bva.isyfact.exception.service.bridge.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import de.bund.bva.isyfact.exception.BaseException;
import de.bund.bva.isyfact.exception.FehlertextProvider;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.exception.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.exception.service.PlisToException;

/**
 * Diese Klasse bietet Methoden zur Uebernahme von Inhalten von Anwendungsexceptions in Schnittstellen bzw.
 * Transportexceptions.
 * <p> *
 */
public class BridgeExceptionMapper {

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(BridgeExceptionMapper.class);

    public BridgeExceptionMapper() {



    }

    /**
     * erzeugt und füllt eine TransportExcpetion-Klasse vom übergebenen Typ mit den Werten aus der übergebenen
     * AnwendungsException.
     *
     * @param <T>
     *            Typ der zu erzeugenden TransportExcpetion
     * @param exception
     *            Die originale BaseException
     * @param transportExceptionClass
     *            die fachliche oder technische TransportException
     * @return Die TransportException.
     * @throws IllegalArgumentException
     *             falls <em>null</em> als Wert für den Parameter <code>transportExceptionClass</code>
     *             übergeben wurde.
     */
    public static <T extends PlisToException> T mapException(BaseException exception,
                                                         Class<T> transportExceptionClass) {

        // IllegalArgumentException, wenn die Transport-Exception nicht gesetzt wurde.
        if (transportExceptionClass == null) {
            throw new IllegalArgumentException("null ist kein zulaessiger Wert fuer eine TransportException");
        }

        try {
            Constructor<T> con =
                transportExceptionClass.getConstructor(String.class, String.class, String.class);

            // Exception erzeugen
            T ex = con.newInstance(exception.getFehlertext(), exception.getAusnahmeId(),
                exception.getUniqueId());

            return ex;
        } catch (Throwable t) {
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                "Die TransportException ({}) konnte nicht mit den Werten aus der  AnwendungsException ({}), mit den Werten AusnahmeId: {}, Fehlertext: {} und UUID: {} gefuellt werden! Die TransportException implementiert nicht den benoetigten Konstruktor mit den Parametern: String message, String ausnahmeId, String uniqueId",
                t, transportExceptionClass, exception.getClass(),
                exception.getAusnahmeId(), exception.getFehlertext(), exception.getUniqueId());
            throw new IllegalArgumentException(
                "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                    + "Parametern: String message, String ausnahmeId, String uniqueId");
        }
    }

    /**
     * erzeugt und füllt eine TransportExcpetion-Klasse vom übergebenen Typ mit den Werten aus der übergebenen
     * AnwendungsException.
     *
     * @param <T>
     *            Typ der zu erzeugenden TransportExcpetion
     * @param technicalRuntimeException
     *            Die original TechnicalRuntimeException
     * @param transportExceptionClass
     *            die fachliche oder technische TransportException
     * @return Die TransportException.
     * @throws IllegalArgumentException
     *             falls <em>null</em> als Wert für den Parameter <code>transportExceptionClass</code>
     *             übergeben wurde oder die transportException keinen Konstruktor mit den Parametern
     *             (String.class, String.class, String.class).
     */
    public static <T extends PlisToException> T mapException(
        TechnicalRuntimeException technicalRuntimeException, Class<T> transportExceptionClass) {

        // IllegalArgumentException, wenn die Transport-Exception nicht gesetzt wurde.
        if (transportExceptionClass == null) {
            throw new IllegalArgumentException("null ist kein zulaessiger Wert fuer eine TransportException");
        }

        try {
            Constructor<T> con =
                transportExceptionClass.getConstructor(String.class, String.class, String.class);

            // Exception erzeugen
            T ex = con.newInstance(technicalRuntimeException.getFehlertext(),
                technicalRuntimeException.getAusnahmeId(), technicalRuntimeException.getUniqueId());

            return ex;
        } catch (Throwable t) {
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                "Die TransportException ({}) konnte nicht mit den Werten aus der AnwendungsException ({}), "
                    + "mit den Werten AusnahmeId: {}, Fehlertext: {} und UUID: {} gefuellt werden! "
                    + "Die TransportException implementiert nicht den benoetigten Konstruktor mit den Parametern: "
                    + "String message, String ausnahmeId, String uniqueId",
                t, transportExceptionClass, technicalRuntimeException.getClass(),
                technicalRuntimeException.getAusnahmeId(), technicalRuntimeException.getFehlertext(),
                technicalRuntimeException.getUniqueId());
            throw new IllegalArgumentException(
                "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                    + "Parametern: String message, String ausnahmeId, String uniqueId");
        }
    }

    /**
     * erstellt eine Transport-Exception aus den &uumlbergebenen Werten.
     * @param <T>
     *            Typ der zu erzeugenden Exception
     * @param ausnahmeId
     *            Die Ausnahme-ID
     * @param fehlertextProvider
     *            Der Fehlertext-Provider
     * @param transportExceptionClass
     *            Die fachliche oder technische TransportException
     * @param parameter
     *            Variable Anzahl an Parameterwerten. Parameterwert f&uml;r die mögliche Variable in einer
     *            Fehler-Nachricht.
     * @return Die TransportException.
     * @throws IllegalArgumentException
     *             In Ausnahmefällen kann diese Exception auftreten, sofern eine der folgenden Bedingungen
     *             erfüllt ist:
     *             <li><em>null</em> als Wert für den Parameter <code>transportExceptionClass</code> übergeben
     *             wurde
     *             <li><code>fehlertextProvider</code>
     *             <li>die TransportException keinen Konstruktor mit den Parametern (String.class,
     *             String.class, String.class) besitzt
     *             <li>die TransportException ein Interface oder eine abstrakte Implementierung ist
     *             <li>der Zugriff auf die TransportException gegen die Java-Sicherheitsrichtlinien verstösst
     *             <li>eine Exception innerhalb des Konstruktors der zu erzeugenden TransportException
     *             auftritt
     */
    public static <T extends PlisToException> T createToException(String ausnahmeId,
                                                              FehlertextProvider fehlertextProvider, Class<T> transportExceptionClass, String... parameter) {

        // IllegalArgumentException, wenn die Transport-Exception nicht gesetzt wurde.
        if (transportExceptionClass == null) {
            throw new IllegalArgumentException("null ist kein zulaessiger Wert fuer die TransportException");
        }

        Constructor<T> toException;
        T ex = null;
        String uuid = null;
        String fehlertext = null;
        try {
            // Konstruktor laden
            toException = transportExceptionClass.getConstructor(String.class, String.class, String.class);

            // Exception-Informationen erzeugen
            uuid = UUID.randomUUID().toString();
            fehlertext = fehlertextProvider.getMessage(ausnahmeId, parameter);

            // TransportException instanziieren
            ex = toException.newInstance(fehlertext, ausnahmeId, uuid);

            // TransportException zurueckgeben
            return ex;
        } catch (SecurityException se) {
            // Der Zugriff auf den Konstruktur verstoesst gegen die Sicherheitsrichtlinien von Java
            LOG.error(EreignisSchluessel.KONSTRUKTOR_SICHERHEITSRICHTLINIEN,
                "Der Zugriff auf den Konstruktur der TransportException {} verstoesst gegen die Java-Sicherheitsrichtlinien.",
                se, transportExceptionClass);
            throw new IllegalArgumentException(
                "Der Aufruf des Konstruktors der TransportException " + transportExceptionClass
                    + "fuehrte innerhalb des aufgerufenen Konstruktors zu einer Exception.");
        } catch (NoSuchMethodException nsme) {
            // Der Konstruktor ist nicht vorhanden
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                "Die TransportException {} konnte nicht erzeugt werden. Die TransportException"
                    + " implementiert nicht den benoetigten Konstruktor mit den Parametern: "
                    + "String message, String ausnahmeId, String uniqueId.",
                nsme, transportExceptionClass);
            throw new IllegalArgumentException(
                "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                    + "Parametern: String message, String ausnahmeId, String uniqueId.");
        } catch (IllegalArgumentException iae) {
            // Die Parameterwerte sind nicht zulaessig
            LOG.error(EreignisSchluessel.PARAMETER_FALSCH,
                "Die TransportException {} konnte nicht erzeugt werden. Die Parameterwerte ({}, {}, {}) entsprechen nicht den benoetigten "
                    + "Werten: String message, String ausnahmeId, String uniqueId.",
                iae, transportExceptionClass, fehlertext, ausnahmeId, uuid);
            throw new IllegalArgumentException(
                "Die Parameterwerte sind nicht zulaessig zur Erzeugung einer TransportException.");
        } catch (InstantiationException ie) {
            // Die übergebene Klasse ist ein Interface oder eine abstrakte Klasse
            LOG.error(EreignisSchluessel.TRANSPORT_EXCEPTION_INTERFACE_ABSTRAKT,
                "Die TransportException {} konnte nicht erzeugt werden. Sie ist entweder ein Interface oder aber"
                    + " eine abstrakte Klasse. Sie TransportException muss aber eine konkrete"
                    + " Implementierung sein",
                ie, transportExceptionClass);
            throw new IllegalArgumentException(
                "Die Uebergebene TransportException-Klasse ist ein Interface oder eine abstrakte Klasse.");
        } catch (IllegalAccessException iae) {
            // Der Konstruktor ist nicht vorhanden
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                "Die TransportException {} konnte nicht erzeugt werden. Die TransportException"
                    + " implementiert nicht den benoetigten Konstruktor mit den Parametern: "
                    + "String message, String ausnahmeId, String uniqueId.",
                iae, transportExceptionClass);
            throw new IllegalArgumentException(
                "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                    + "Parametern: String message, String ausnahmeId, String uniqueId");
        } catch (InvocationTargetException ite) {
            // Der Aufruf des Konstruktors fuehrte innerhalb des Konstruktors zu einer Exception
            LOG.error(EreignisSchluessel.KONSTRUKTOR_EXCEPTION,
                "Der Aufruf des Konstruktors der TransportException {} fuehrte innerhalb des aufgerufenen Konstruktors zu einer Exception.",
                ite, transportExceptionClass);
            throw new IllegalArgumentException(
                "Der Aufruf des Konstruktors der TransportException" + transportExceptionClass
                    + "fuehrte innerhalb des aufgerufenen Konstruktors zu einer Exception.");
        }
    }
}
