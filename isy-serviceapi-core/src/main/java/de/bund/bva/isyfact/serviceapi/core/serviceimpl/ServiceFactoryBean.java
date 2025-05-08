package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import de.bund.bva.isyfact.serviceapi.common.konstanten.EreignisSchluessel;
import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.sicherheit.annotation.GesichertInterceptor;
import de.bund.bva.isyfact.sicherheit.annotation.SicherheitAttributeSource;



/**
 * Factory-Bean, die eine generische Implementierung einer RemoteBean-Schnittstelle erzeugt.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ServiceFactoryBean extends TransactionProxyFactoryBean {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(ServiceFactoryBean.class);

    /** Konfigurierte Interceptor. */
    private final List<Object> interceptors = new ArrayList<>();

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
            if (gesichert != null) {
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
