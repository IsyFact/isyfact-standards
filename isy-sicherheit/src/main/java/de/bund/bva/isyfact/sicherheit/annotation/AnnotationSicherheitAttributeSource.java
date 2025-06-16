package de.bund.bva.isyfact.sicherheit.annotation;

import java.lang.reflect.Method;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;

/**
 * Ermittelt die benötigten Rechte für eine Service-Operation über die Annotation {@link Gesichert}.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class AnnotationSicherheitAttributeSource implements SicherheitAttributeSource {

    @Override
    public String[] getBenoetigeRechte(Method method, Class<?> targetClass) {
        Gesichert gesichert = ermittleGesichertAnnotation(method, targetClass);
        pruefeTagKombination(gesichert);
        return gesichert.value();
    }

    private Gesichert ermittleGesichertAnnotation(Method method, Class<?> targetClass) {

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
        Gesichert gesichert = specificMethod.getAnnotation(Gesichert.class);
        if (gesichert != null) {
            return gesichert;
        }

        // Second try is the transaction attribute on the target class.
        gesichert = specificMethod.getDeclaringClass().getAnnotation(Gesichert.class);
        if (gesichert != null) {
            return gesichert;
        }

        if (specificMethod != method) {
            // Fallback is to look at the original method.
            gesichert = method.getAnnotation(Gesichert.class);
            if (gesichert != null) {
                return gesichert;
            }

            // Last fallback is the class of the original method.
            return method.getDeclaringClass().getAnnotation(Gesichert.class);
        }

        return null;
    }

    /**
     * Prüft, ob mindestens ein Recht gefordert wird.
     *
     * @param gesichert
     *            Angabe, wie die Methode abzusichern ist.
     * @throws FehlerhafteServiceKonfigurationRuntimeException
     *             falls kein Recht gefordert wird.
     */
    private void pruefeTagKombination(Gesichert gesichert) {
        if (gesichert == null || gesichert.value().length == 0) {
            throw new FehlerhafteServiceKonfigurationRuntimeException();
        }
        for (String recht : gesichert.value()) {
            if (recht == null || recht.isEmpty()) {
                throw new FehlerhafteServiceKonfigurationRuntimeException();
            }
        }
    }
}
