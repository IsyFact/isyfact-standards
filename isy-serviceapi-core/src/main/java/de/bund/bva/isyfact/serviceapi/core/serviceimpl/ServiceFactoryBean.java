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
package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import de.bund.bva.isyfact.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.isyfact.sicherheit.annotation.SicherheitAttributeSource;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Factory bean that creates a generic implementation of a RemoteBean interface.
 *
 */
public class ServiceFactoryBean extends TransactionProxyFactoryBean {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Configured interceptors. */
    private final List<Object> interceptors = new ArrayList<Object>();

    /** The RemoteBean interface. */
    private Class<?> remoteBeanInterface;

    /** The target bean. */
    private Object target;

    /** The required permissions. */
    private Map<String, String[]> benoetigtesRecht;

    /**
     * Whether the service configuration should be validated immediately. If not, configuration errors
     * will only occur when the service is called.
     */
    private boolean validateConfiguration = true;

    /**
     * Sets the RemoteBean interface that the service implementation should realize.
     *
     * @param remoteBeanInterface
     *            the RemoteBean interface
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
        if (this.benoetigtesRecht != null && !this.benoetigtesRecht.isEmpty()) {
            GesichertInterceptor gesichert = getInterceptor(GesichertInterceptor.class);

            // Check if the secured interceptor actually exists
            MethodMapSicherheitAttributeSource methodMap = getMethodMapSicherheitAttributeSource(gesichert);
            for (Map.Entry<String, String[]> entry : this.benoetigtesRecht.entrySet()) {
                if (entry.getKey().contains(".")) {
                    throw new IllegalArgumentException("Ungültiger Punkt in Methoden-Pattern '"
                            + entry.getKey() + "' in Konfiguration des Service "
                            + this.remoteBeanInterface.getName());
                }
                methodMap.addGesichertMethod(this.remoteBeanInterface.getName() + "." + entry.getKey(), entry.getValue());
            }
        }

        if (this.validateConfiguration) {
            validateConfiguration();
        }

        super.afterPropertiesSet();
    }

    private static MethodMapSicherheitAttributeSource getMethodMapSicherheitAttributeSource(GesichertInterceptor gesichert) {
        if (gesichert == null) {
            throw new IllegalStateException("GesichertInterceptor ist nicht konfiguriert.");
        }

        SicherheitAttributeSource sicherheitAttributeSource = gesichert.getSicherheitAttributeSource();
        if (!(sicherheitAttributeSource instanceof MethodMapSicherheitAttributeSource)) {
            throw new IllegalArgumentException("Die Konfiguration der benötigten Rechte erfordert eine "
                    + MethodMapSicherheitAttributeSource.class.getSimpleName() + " im "
                    + GesichertInterceptor.class.getName() + ", konfiguriert ist aber eine "
                    + sicherheitAttributeSource.getClass().getName());
        }

        return (MethodMapSicherheitAttributeSource) sicherheitAttributeSource;
    }

    private void validateConfiguration() {
        for (Object interceptor : this.interceptors) {
            if (interceptor instanceof Validatable) {
                ((Validatable) interceptor).validateConfiguration(this.remoteBeanInterface, this.target);
            }
        }
    }

    /**
     * Identifies an interceptor of a specific type.
     *
     * @param <T>
     *            the type sought
     * @param interceptorClass
     *            the type sought
     * @return the interceptor or null
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
