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
package de.bund.bva.isyfact.sicherheit.annotation;

import java.lang.reflect.Method;
import java.util.UUID;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.config.NutzerAuthentifizierungProperties;
import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontext;
import de.bund.bva.isyfact.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.isyfact.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.isyfact.sicherheit.common.exception.AnnotationFehltRuntimeException;

/**
 * MethodInterceptor which authenticates a user and creates an AufrufKontext. User data, which is needed for
 * authentication, is read from configuration. The configuration key needs to be annotated via
 * {@link NutzerAuthentifizierung}. Additionally, Korrelation-ID is set for logging.
 *
 * <p>
 * This kind of authentication is used for access layers that don't get user data from outside of the system,
 * like Workflow, TimerTask, etc.
 * </p>
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class NutzerAuthentifizierungInterceptor<K extends AufrufKontext> implements MethodInterceptor {

    /** AufrufKontextVerwalter. */
    private final AufrufKontextVerwalter<K> aufrufKontextVerwalter;

    /** Benutzerkennungen. */
    private final NutzerAuthentifizierungProperties properties;

    /** The cross-sectional component Sicherheit. */
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
            SecurityContextHolder.clearContext();
            if (korrelationsIdErzeugt) {
                MdcHelper.entferneKorrelationsId();
            }
        }
    }

    /**
     * Creates and cultivates Aufrufkontext. Data is read from config.
     *
     * @param invocation
     *            called method
     * @param korrelationsId
     *            Korrelations-ID
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

        AufrufKontextImpl aufrufKontextImpl = new AufrufKontextImpl();
        aufrufKontextImpl.setDurchfuehrenderBenutzerKennung(kennung);
        aufrufKontextImpl.setDurchfuehrenderBenutzerPasswort(passwort);
        aufrufKontextImpl.setDurchfuehrendeBehoerde(bhknz);
        aufrufKontextImpl.setKorrelationsId(korrelationsId);
        this.sicherheit.getBerechtigungsManagerUndAuthentifiziere((K) aufrufKontextImpl);
    }

    /**
     * Determines a NutzerAuthentifizierung annotation of the passed method or class.
     *
     * @param method
     *            called method
     * @param targetClass
     *            target class
     * @return NutzerAuthentifizierung or null
     */
    private NutzerAuthentifizierung ermittleAuthAnnotation(Method method, Class<?> targetClass) {

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
