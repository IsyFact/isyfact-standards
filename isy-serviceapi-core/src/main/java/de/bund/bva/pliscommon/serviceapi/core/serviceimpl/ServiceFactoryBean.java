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
package de.bund.bva.pliscommon.serviceapi.core.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.pliscommon.serviceapi.common.konstanten.EreignisSchluessel;
import de.bund.bva.pliscommon.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.pliscommon.sicherheit.annotation.SicherheitAttributeSource;

/**
 * Factory-Bean, die eine generische Implementierung einer RemoteBean-Schnittstelle erzeugt.
 *
 */
public class ServiceFactoryBean extends TransactionProxyFactoryBean {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ServiceFactoryBean.class);

    /** Konfigurierte Interceptor. */
    private final List<Object> interceptors = new ArrayList<Object>();

    /** Die RemoteBean-Schnittstelle. */
    private Class<?> remoteBeanInterface;

    /** Die Target-Bean. */
    private Object target;

    /** Die benötigten Rechte. */
    private Map<String, String[]> benoetigtesRecht;

    /**
     * Ob die Konfiguration des Services sofort validiert werden soll. Wenn nicht, treten Konfigurationsfehler
     * erst beim Aufruf des Services auf.
     */
    private boolean validateConfiguration = true;

    /**
     * Setzt die RemoteBean-Schnittstelle, die durch die Service-Implementierung umgesetzt werden soll.
     *
     * @param remoteBeanInterface
     *            die RemoteBean-Schnittstelle
     */
    public void setRemoteBeanInterface(Class<?> remoteBeanInterface) {
        if (!remoteBeanInterface.isInterface()) {
            throw new IllegalArgumentException(remoteBeanInterface.getName() + " ist kein Interface");
        }
        setProxyInterfaces(new Class<?>[] { remoteBeanInterface });
        this.remoteBeanInterface = remoteBeanInterface;
    }

    @Override
    public void setPreInterceptors(Object[] preInterceptors) {
        super.setPreInterceptors(preInterceptors);
        this.interceptors.addAll(Arrays.asList(preInterceptors));
    }

    @Override
    public void setPostInterceptors(Object[] postInterceptors) {
        super.setPostInterceptors(postInterceptors);
        this.interceptors.addAll(Arrays.asList(postInterceptors));
    }

    @Override
    public void setTarget(Object target) {
        super.setTarget(target);
        this.target = target;
    }

    public void setBenoetigtesRecht(Map<String, String[]> benoetigtesRecht) {
        this.benoetigtesRecht = benoetigtesRecht;
    }

    public void setValidateConfiguration(boolean validateConfiguration) {
        this.validateConfiguration = validateConfiguration;
    }

    @Override
    public void afterPropertiesSet() {

        if (this.benoetigtesRecht != null && this.benoetigtesRecht.size() > 0) {
            GesichertInterceptor gesichert = getInterceptor(GesichertInterceptor.class);
            SicherheitAttributeSource sicherheitAttributeSource = gesichert.getSicherheitAttributeSource();
            if (!(sicherheitAttributeSource instanceof MethodMapSicherheitAttributeSource)) {
                throw new IllegalArgumentException("Die Konfiguration der benötigten Rechte erfordert eine "
                    + MethodMapSicherheitAttributeSource.class.getSimpleName() + " im "
                    + GesichertInterceptor.class.getName() + ", konfiguriert ist aber eine "
                    + sicherheitAttributeSource.getClass().getName());
            }
            MethodMapSicherheitAttributeSource methodMapSicherheitAttributeSource =
                (MethodMapSicherheitAttributeSource) sicherheitAttributeSource;

            for (Map.Entry<String, String[]> entry : this.benoetigtesRecht.entrySet()) {
                if (entry.getKey().contains(".")) {
                    throw new IllegalArgumentException("Ungültiger Punkt in Methoden-Pattern '"
                        + entry.getKey() + "' in Konfiguration des Service "
                        + this.remoteBeanInterface.getName());
                }
                methodMapSicherheitAttributeSource.addGesichertMethod(this.remoteBeanInterface.getName()
                    + "." + entry.getKey(), entry.getValue());
            }
        }

        if (this.validateConfiguration) {
            LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.VALIDIERUNG_KONFIGURATION,
                "Validiere Konfiguration für Service-Implementierung {}", this.remoteBeanInterface.getName());

            for (Object interceptor : this.interceptors) {
                if (interceptor instanceof Validatable) {
                    ((Validatable) interceptor).validateConfiguration(this.remoteBeanInterface, this.target);
                }
            }
        }

        super.afterPropertiesSet();

    }

    /**
     * Ermittelt einen Interceptor eines bestimmten Typs.
     *
     * @param <T>
     *            der gesuchte Typ
     * @param interceptorClass
     *            der gesuchte Typ
     * @return der Interceptor oder null
     */
    private <T> T getInterceptor(Class<T> interceptorClass) {
        for (Object interceptor : this.interceptors) {
            if (interceptorClass.isInstance(interceptor)) {
                return interceptorClass.cast(interceptor);
            }
        }
        return null;
    }
}
