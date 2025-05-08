package de.bund.bva.isyfact.sicherheit.annotation;

import java.lang.reflect.Method;
import java.util.UUID;

import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.config.NutzerAuthentifizierungProperties;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.sicherheit.common.exception.AnnotationFehltRuntimeException;

/**
 * MethodInterceptor, der einen Benutzer authentifiziert und dabei einen AufrufKontext erzeugt. Die zur
 * Authentifizierung nötigen Benutzerdaten werden aus der Konfiguration gelesen, der Konfigurationsschlüssel
 * muss über {@link NutzerAuthentifizierung} an der Methode annotiert sein. Außerdem wird die erzeugte
 * Korrelations-ID für das Logging gesetzt.
 *
 * <p>
 * Diese Form der Authentifizierung ist für Zugangsschichten vorgesehen, die keine Benutzerdaten von außen
 * erhalten, z.B. Workflow, TimerTasks, etc.
 * </p>
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class NutzerAuthentifizierungInterceptor<K extends AufrufKontext> implements MethodInterceptor {

    /** Der AufrufKontextVerwalter. */
    private final AufrufKontextVerwalter<K> aufrufKontextVerwalter;

    /* Die Benutzerkennungen */
    private final NutzerAuthentifizierungProperties properties;

    /** Die Querschnittskomponente Sicherheit. */
    private final Sicherheit<K> sicherheit;

    public NutzerAuthentifizierungInterceptor(AufrufKontextVerwalter<K> aufrufKontextVerwalter,
        NutzerAuthentifizierungProperties properties, Sicherheit<K> sicherheit) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
        this.properties = properties;
        this.sicherheit = sicherheit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean korrelationsIdErzeugt = false;
        try {
            String korrelationsId = MdcHelper.liesKorrelationsId();
            if (korrelationsId == null || korrelationsId.isEmpty()) {
                korrelationsId = UUID.randomUUID().toString();
                MdcHelper.pushKorrelationsId(korrelationsId);
                korrelationsIdErzeugt = true;
            }
            authentifiziereNutzer(invocation, korrelationsId);
            return invocation.proceed();
        } finally {
            this.aufrufKontextVerwalter.setAufrufKontext(null);
            if (korrelationsIdErzeugt) {
                MdcHelper.entferneKorrelationsId();
            }
        }
    }

    /**
     * Erzeugt und befüllt einen Aufrufkontext. Die Daten werden aus der Konfiguration gelesen.
     *
     * @param invocation
     *            die gerufene Methode
     * @param korrelationsId
     *            die Korrelations-ID
     */
    private void authentifiziereNutzer(MethodInvocation invocation, String korrelationsId) {
        Class<?> targetClass =
            (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

        NutzerAuthentifizierung ann = ermittleAuthAnnotation(invocation.getMethod(), targetClass);
        if (ann == null) {
            throw new AnnotationFehltRuntimeException(NutzerAuthentifizierung.class.getSimpleName(),
                invocation.getMethod().toString());
        }

        String konfigSchluesselPraefix = ann.benutzer();
        String kennung = properties.getBenutzer().get(konfigSchluesselPraefix).getKennung();
        String passwort = properties.getBenutzer().get(konfigSchluesselPraefix).getPasswort();
        String bhknz = properties.getBenutzer().get(konfigSchluesselPraefix).getBhknz();

        // Benutzer authentifizieren und AufrufKontextVerwalter befüllen
        // sicherheit.getBerechtigungsManagerUndAuthentifiziereNutzer(kennung, passwort, bhknz, null,
        // korrelationsId);
        AufrufKontextImpl aufrufKontextImpl = new AufrufKontextImpl();
        aufrufKontextImpl.setDurchfuehrenderBenutzerKennung(kennung);
        aufrufKontextImpl.setDurchfuehrenderBenutzerPasswort(passwort);
        aufrufKontextImpl.setDurchfuehrendeBehoerde(bhknz);
        aufrufKontextImpl.setKorrelationsId(korrelationsId);
        this.sicherheit.getBerechtigungsManagerUndAuthentifiziere((K) aufrufKontextImpl);
    }

    /**
     * Ermittelt die NutzerAuthentifizierung-Annotation an der gerufenen Methode oder Klasse.
     *
     * @param method
     *            die gerufene Methode
     * @param targetClass
     *            die Zielklasse
     * @return die Annotation oder null
     */
    private NutzerAuthentifizierung ermittleAuthAnnotation(Method method, Class<?> targetClass) {

        // Strategie für die Ermittlung der Annotation ist aus AnnotationTransactionAttributeSource
        // übernommen.

        // Ignore CGLIB subclasses - introspect the actual user class.
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        NutzerAuthentifizierung nutzerAuth = specificMethod.getAnnotation(NutzerAuthentifizierung.class);
        if (nutzerAuth != null) {
            return nutzerAuth;
        }

        // Second try is the transaction attribute on the target class.
        nutzerAuth = specificMethod.getDeclaringClass().getAnnotation(NutzerAuthentifizierung.class);
        if (nutzerAuth != null) {
            return nutzerAuth;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            nutzerAuth = method.getAnnotation(NutzerAuthentifizierung.class);
            if (nutzerAuth != null) {
                return nutzerAuth;
            }

            // Last fallback is the class of the original method.
            return method.getDeclaringClass().getAnnotation(NutzerAuthentifizierung.class);
        }

        return null;
    }
}
