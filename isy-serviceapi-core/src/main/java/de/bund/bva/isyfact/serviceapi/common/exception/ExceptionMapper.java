package de.bund.bva.isyfact.serviceapi.common.exception;

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
 * This class provides methods for transferring the contents of application exceptions to interfaces or transport exceptions.
 * <p>
 * It also provides the ability to throw an interface exception using an exception ID and a {@link FehlertextProvider}.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ExceptionMapper {

    /**
     * Isy-Logger.
     */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ExceptionMapper.class);

    /**
     * creates and populates a TransportExcpetion class of the passed type with the values from the passed ApplicationException.
     *
     * @param <T>Type                 of the TransportExcpetion to be created
     * @param exception               The original BaseException
     * @param transportExceptionClass the business or technical TransportException
     * @return The TransportException.
     * @throws IllegalArgumentException if {@code null} was passed as value for the parameter {@code transportExceptionClass}.
     */
    public static <T extends PlisToException> T mapException(BaseException exception,
                                                             Class<T> transportExceptionClass) {

        // IllegalArgumentException if the transport exception was not set.
        if (transportExceptionClass == null) {
            throw new IllegalArgumentException("null ist kein zulaessiger Wert fuer eine TransportException");
        }

        try {
            Constructor<T> con =
                    transportExceptionClass.getConstructor(String.class, String.class, String.class);

            // Create exception

            return con.newInstance(exception.getFehlertext(), exception.getAusnahmeId(),
                    exception.getUniqueId());
        } catch (Throwable t) {
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                    "Die TransportException ({}) konnte nicht mit den Werten aus der  AnwendungsException ({}), mit den Werten AusnahmeId: {}, Fehlertext: {} und UUID: {} gefuellt werden! Die TransportException implementiert nicht den benoetigten Konstruktor mit den Parametern: String message, String ausnahmeId, String uniqueId",
                    t, transportExceptionClass.getClass(), exception.getClass(),
                    exception.getAusnahmeId(), exception.getFehlertext(), exception.getUniqueId());
            throw new IllegalArgumentException(
                    "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                            + "Parametern: String message, String ausnahmeId, String uniqueId");
        }
    }

    /**
     * creates and populates a TransportExcpetion class of the passed type with the values from the passed ApplicationException.
     *
     * @param <T>Type                   of the TransportExcpetion to be created
     * @param technicalRuntimeException The original TechnicalRuntimeException
     * @param transportExceptionClass   the business or technical TransportException
     * @return The TransportException.
     * @throws IllegalArgumentException if {@code null} was passed as value for the parameter {@code transportExceptionClass}
     *                                  or the transportException does not have a constructor with the parameters (String.class, String.class, String.class).
     */
    public static <T extends PlisToException> T mapException(
            TechnicalRuntimeException technicalRuntimeException, Class<T> transportExceptionClass) {

        // IllegalArgumentException if the transport exception was not set.
        if (transportExceptionClass == null) {
            throw new IllegalArgumentException("null ist kein zulaessiger Wert fuer eine TransportException");
        }

        try {
            Constructor<T> con =
                    transportExceptionClass.getConstructor(String.class, String.class, String.class);

            // Create exception

            return con.newInstance(technicalRuntimeException.getFehlertext(),
                    technicalRuntimeException.getAusnahmeId(), technicalRuntimeException.getUniqueId());
        } catch (Throwable t) {
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                    "Die TransportException ({}) konnte nicht mit den Werten aus der AnwendungsException ({}), "
                            + "mit den Werten AusnahmeId: {}, Fehlertext: {} und UUID: {} gefuellt werden! "
                            + "Die TransportException implementiert nicht den benoetigten Konstruktor mit den Parametern: "
                            + "String message, String ausnahmeId, String uniqueId",
                    t, transportExceptionClass.getClass(), technicalRuntimeException.getClass(),
                    technicalRuntimeException.getAusnahmeId(), technicalRuntimeException.getFehlertext(),
                    technicalRuntimeException.getUniqueId());
            throw new IllegalArgumentException(
                    "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                            + "Parametern: String message, String ausnahmeId, String uniqueId");
        }
    }

    /**
     * creates a transport exception from the passed values.
     *
     * @param <T>Type                 of the exception to be created
     * @param ausnahmeId              The exception ID
     * @param fehlertextProvider      The Error Text Provider
     * @param transportExceptionClass The business or technical TransportException
     * @param parameter               Variable number of parameter values. Parameter value for the possible variable in an error message.
     * @return The TransportException.
     * @throws IllegalArgumentException In exceptional cases this exception can occur if one of the following conditions is met:
     *                                  <li>{@code null} was passed as the value for the parameter {@code transportExceptionClass}.</li>
     *                                  <li>{@code fehlertextProvider}</li>
     *                                  <li>the TransportException has no constructor with the parameters (String.class, String.class, String.class).</li>
     *                                  <li>the TransportException is an interface or an abstract implementation.</li>
     *                                  <li>access to the TransportException violates the Java security policy.</li>
     *                                  <li>an exception occurs within the constructor of the TransportException to be created.</li>
     */
    public static <T extends PlisToException> T createToException(String ausnahmeId,
                                                                  FehlertextProvider fehlertextProvider, Class<T> transportExceptionClass, String... parameter) {

        // IllegalArgumentException if the transport exception was not set.
        if (transportExceptionClass == null) {
            throw new IllegalArgumentException("null ist kein zulaessiger Wert fuer die TransportException");
        }

        String uuid = null;
        String fehlertext = null;
        try {
            // Load constructor
            Constructor<T> toException = transportExceptionClass.getConstructor(String.class, String.class, String.class);

            // Create exception information
            uuid = UUID.randomUUID().toString();
            fehlertext = fehlertextProvider.getMessage(ausnahmeId, parameter);

            // Instantiate TransportException

            // Return TransportException
            return toException.newInstance(fehlertext, ausnahmeId, uuid);
        } catch (SecurityException se) {
            // Accessing the constructed structure violates Java's security policy.
            LOG.error(EreignisSchluessel.KONSTRUKTOR_SICHERHEITSRICHTLINIEN,
                    "Der Zugriff auf den Konstruktur der TransportException {} verstoesst gegen die Java-Sicherheitsrichtlinien.",
                    se, transportExceptionClass.getClass());
            throw new IllegalArgumentException(
                    "Der Aufruf des Konstruktors der TransportException " + transportExceptionClass.getClass()
                            + "fuehrte innerhalb des aufgerufenen Konstruktors zu einer Exception.");
        } catch (NoSuchMethodException nsme) {
            // The constructor is not present
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                    "Die TransportException {} konnte nicht erzeugt werden. Die TransportException"
                            + " implementiert nicht den benoetigten Konstruktor mit den Parametern: "
                            + "String message, String ausnahmeId, String uniqueId.",
                    nsme, transportExceptionClass.getClass());
            throw new IllegalArgumentException(
                    "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                            + "Parametern: String message, String ausnahmeId, String uniqueId.");
        } catch (IllegalArgumentException iae) {
            // The parameter values are not allowed
            LOG.error(EreignisSchluessel.PARAMETER_FALSCH,
                    "Die TransportException {} konnte nicht erzeugt werden. Die Parameterwerte ({}, {}, {}) entsprechen nicht den benoetigten "
                            + "Werten: String message, String ausnahmeId, String uniqueId.",
                    iae, transportExceptionClass.getClass(), fehlertext, ausnahmeId, uuid);
            throw new IllegalArgumentException(
                    "Die Parameterwerte sind nicht zulaessig zur Erzeugung einer TransportException.");
        } catch (InstantiationException ie) {
            // The passed class is an interface or an abstract class
            LOG.error(EreignisSchluessel.TRANSPORT_EXCEPTION_INTERFACE_ABSTRAKT,
                    "Die TransportException {} konnte nicht erzeugt werden. Sie ist entweder ein Interface oder aber"
                            + " eine abstrakte Klasse. Sie TransportException muss aber eine konkrete"
                            + " Implementierung sein",
                    ie, transportExceptionClass.getClass());
            throw new IllegalArgumentException(
                    "Die Uebergebene TransportException-Klasse ist ein Interface oder eine abstrakte Klasse.");
        } catch (IllegalAccessException iae) {
            // The constructor is not present
            LOG.error(EreignisSchluessel.KONSTRUKTOR_NICHT_IMPLEMENTIERT,
                    "Die TransportException {} konnte nicht erzeugt werden. Die TransportException"
                            + " implementiert nicht den benoetigten Konstruktor mit den Parametern: "
                            + "String message, String ausnahmeId, String uniqueId.",
                    iae, transportExceptionClass.getClass());
            throw new IllegalArgumentException(
                    "Die TransportException implementiert nicht den benoetigten Konstruktor mit den "
                            + "Parametern: String message, String ausnahmeId, String uniqueId");
        } catch (InvocationTargetException ite) {
            // The call of the constructor led to an exception inside the constructor
            LOG.error(EreignisSchluessel.KONSTRUKTOR_EXCEPTION,
                    "Der Aufruf des Konstruktors der TransportException {} fuehrte innerhalb des aufgerufenen Konstruktors zu einer Exception.",
                    ite, transportExceptionClass.getClass());
            throw new IllegalArgumentException(
                    "Der Aufruf des Konstruktors der TransportException" + transportExceptionClass.getClass()
                            + "fuehrte innerhalb des aufgerufenen Konstruktors zu einer Exception.");
        }
    }
}
