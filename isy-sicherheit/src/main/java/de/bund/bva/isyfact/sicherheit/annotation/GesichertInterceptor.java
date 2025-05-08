package de.bund.bva.isyfact.sicherheit.annotation;

import java.util.Arrays;
import java.util.stream.Collectors;

import de.bund.bva.isyfact.sicherheit.Berechtigungsmanager;
import de.bund.bva.isyfact.sicherheit.common.konstanten.SicherheitFehlerSchluessel;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.sicherheit.Sicherheit;
import de.bund.bva.isyfact.sicherheit.common.exception.AutorisierungTechnicalException;
import de.bund.bva.isyfact.sicherheit.common.exception.SicherheitTechnicalRuntimeException;

/**
 * Interceptor, der eine Service-Methode absichert.
 *
 * @deprecated since IsyFact 3.0.0 in favor of the isy-security module.
 */
@Deprecated
public class GesichertInterceptor implements MethodInterceptor, Ordered {

    /** Logger. */
    private static IsyLogger LOG = IsyLoggerFactory.getLogger(GesichertInterceptor.class);

    /** Zugriff auf die Komponete zur Autorisierung. */
    private final Sicherheit<?> sicherheit;

    /**
     * Ermittelt die benötigten Rechte zu einer Methode. Default: Rechte werden aus der Annotation @Gesichert
     * ermittelt.
     */
    private SicherheitAttributeSource sicherheitAttributeSource = new AnnotationSicherheitAttributeSource();

    public GesichertInterceptor(Sicherheit<?> sicherheit) {
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

    private String join(String... benoetigeRechte) {
        if (benoetigeRechte == null || benoetigeRechte.length == 0) {
            return "(keine)";
        } else {
            return Arrays.stream(benoetigeRechte).map(r -> "'" + r + "'").collect(Collectors.joining(", "));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
