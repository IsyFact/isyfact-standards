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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.Ordered;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.pliscommon.sicherheit.Berechtigungsmanager;
import de.bund.bva.pliscommon.sicherheit.Sicherheit;
import de.bund.bva.pliscommon.sicherheit.common.exception.AutorisierungTechnicalException;
import de.bund.bva.pliscommon.sicherheit.common.exception.SicherheitTechnicalRuntimeException;
import de.bund.bva.pliscommon.sicherheit.common.konstanten.SicherheitFehlerSchluessel;

/**
 * Interceptor, der eine Service-Methode absichert.
 *
 */
public class GesichertInterceptor implements MethodInterceptor, Ordered {

    /** Logger. */
    private static IsyLogger LOG = IsyLoggerFactory.getLogger(GesichertInterceptor.class);

    /** Zugriff auf die Komponete zur Autorisierung. */
    private Sicherheit<?> sicherheit;

    /**
     * Ermittelt die benötigten Rechte zu einer Methode. Default: Rechte werden aus der Annotation @Gesichert
     * ermittelt.
     */
    private SicherheitAttributeSource sicherheitAttributeSource = new AnnotationSicherheitAttributeSource();

    @Required
    public void setSicherheit(Sicherheit<?> sicherheit) {
        this.sicherheit = sicherheit;
    }

    public SicherheitAttributeSource getSicherheitAttributeSource() {
        return this.sicherheitAttributeSource;
    }

    public void setSicherheitAttributeSource(SicherheitAttributeSource sicherheitAttributeSource) {
        this.sicherheitAttributeSource = sicherheitAttributeSource;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            Class<?> targetClass =
                (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

            String[] benoetigeRechte =
                this.sicherheitAttributeSource.getBenoetigeRechte(invocation.getMethod(), targetClass);

            // Prüfe, ob der Benutzer alle geforderten Rechte hat.
            LOG.debugFachdaten("Prüfe auf Rechte {}", join(benoetigeRechte));
            Berechtigungsmanager berechtigungsmanager = this.sicherheit.getBerechtigungsManager();
            for (String recht : benoetigeRechte) {
                berechtigungsmanager.pruefeRecht(recht);
                LOG.debugFachdaten("Recht {} vorhanden.", recht);
            }
        } catch (SicherheitTechnicalRuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new AutorisierungTechnicalException(
                SicherheitFehlerSchluessel.MSG_AUTORISIERUNG_TECHNISCH_FEHLGESCHLAGEN, t);
        }

        return invocation.proceed();
    }

    // XXX: Use Guava Joiner ?
    private String join(String... benoetigeRechte) {
        if (benoetigeRechte == null || benoetigeRechte.length == 0) {
            return "(keine)";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < benoetigeRechte.length; i++) {
            String recht = benoetigeRechte[i];
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("'");
            sb.append(recht);
            sb.append("'");
        }
        return sb.toString();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
