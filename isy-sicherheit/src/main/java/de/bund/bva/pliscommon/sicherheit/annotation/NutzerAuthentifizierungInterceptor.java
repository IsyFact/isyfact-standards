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
package de.bund.bva.pliscommon.sicherheit.annotation;

import java.lang.reflect.Method;
import java.util.UUID;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontext;
import de.bund.bva.pliscommon.aufrufkontext.AufrufKontextVerwalter;
import de.bund.bva.pliscommon.aufrufkontext.impl.AufrufKontextImpl;
import de.bund.bva.pliscommon.konfiguration.common.Konfiguration;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.common.exception.AnnotationFehltRuntimeException;

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
 */
public class NutzerAuthentifizierungInterceptor<K extends AufrufKontext> implements MethodInterceptor {

    /** Der AufrufKontextVerwalter. */
    private AufrufKontextVerwalter<K> aufrufKontextVerwalter;

    /** Die Konfiguration. */
    private Konfiguration konfiguration;

    /** Die Querschnittskomponente Sicherheit. */
    private Sicherheit<K> sicherheit;

    @Required
    public void setAufrufKontextVerwalter(AufrufKontextVerwalter<K> aufrufKontextVerwalter) {
        this.aufrufKontextVerwalter = aufrufKontextVerwalter;
    }

    @Required
    public void setKonfiguration(Konfiguration konfiguration) {
        this.konfiguration = konfiguration;
    }

    @Required
    public void setSicherheit(Sicherheit<K> sicherheit) {
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
            if ((korrelationsId == null) || StringUtils.EMPTY.equals(korrelationsId)) {
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

        String konfigSchluesselPraefix = ann.konfigurationSchluesselBenutzer();
        String kennung = this.konfiguration.getAsString(konfigSchluesselPraefix + ".kennung");
        String passwort = this.konfiguration.getAsString(konfigSchluesselPraefix + ".passwort");
        String bhknz = this.konfiguration.getAsString(konfigSchluesselPraefix + ".bhknz");

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
