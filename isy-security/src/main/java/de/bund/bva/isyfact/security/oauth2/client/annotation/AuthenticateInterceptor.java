package de.bund.bva.isyfact.security.oauth2.client.annotation;

import java.lang.reflect.Method;
import java.util.UUID;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import de.bund.bva.isyfact.logging.util.MdcHelper;
import de.bund.bva.isyfact.security.oauth2.client.Authentifizierungsmanager;

/**
 * MethodInterceptor that authenticates an OAuth 2.0 client and sets the authenticated principal in the Security Context.
 * The registration ID of the OAuth 2.0 client to authenticate is read from the parameter on the {@link Authenticate} annotation.
 * In addition, a correlation ID will be created (if not exists) for logging purposes.
 * <p>
 * This form of authentication is intended for access layers that rely on internal user authentication.
 * For example: Workflow, TimerTask, etc.
 * <p>
 * This class also implements the necessary Pointcut Advisor and should use {@link org.springframework.aop.Advisor}
 * as the return type of the factory method.
 */
public class AuthenticateInterceptor extends EmbeddedValueResolutionSupport implements MethodInterceptor, PointcutAdvisor, Ordered {

    /** Make sure the interceptor runs before Spring annotations like @Secured. */
    private int order = AuthorizationInterceptorsOrder.FIRST.getOrder();

    /** The manager used for authenticating the OAuth 2.0 client. */
    private final Authentifizierungsmanager authentifizierungsmanager;

    public AuthenticateInterceptor(Authentifizierungsmanager authentifizierungsmanager) {
        this.authentifizierungsmanager = authentifizierungsmanager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean correlationIdCreated = false;
        Authentication initialAuthentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            String correlationId = MdcHelper.liesKorrelationsId();
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
                MdcHelper.pushKorrelationsId(correlationId);
                correlationIdCreated = true;
            }
            authenticateOAuth2Client(invocation);
            return invocation.proceed();
        } finally {
            // reset the authenticated principal after method
            SecurityContextHolder.getContext().setAuthentication(initialAuthentication);
            if (correlationIdCreated) {
                MdcHelper.entferneKorrelationsId();
            }
        }
    }

    /**
     * Authenticates the OAuth 2.0 client based on the properties of the method annotation.
     *
     * @param invocation
     *         the method invocation
     */
    private void authenticateOAuth2Client(MethodInvocation invocation) {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

        Authenticate ann = detectAuthAnnotation(invocation.getMethod(), targetClass);
        Assert.notNull(ann, String.format("The annotation %s is missing on the method %s.",
                Authenticate.class.getSimpleName(), invocation.getMethod()));

        // resolve property placeholders in the annotation value (if present)
        String oauth2ClientRegistrationId = resolveEmbeddedValue(ann.oauth2ClientRegistrationId());

        // authenticates the client and sets the authenticated principal
        // will throw an exception if the ID does not exist or there's an error during authentication
        authentifizierungsmanager.authentifiziere(oauth2ClientRegistrationId);
    }

    /**
     * Detects the {@link Authenticate} annotation on the invoked method.
     *
     * @param method
     *         the invoked method
     * @param targetClass
     *         the target class
     * @return the annotation or {@code null}
     */
    private Authenticate detectAuthAnnotation(Method method, Class<?> targetClass) {
        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        // This also automatically resolves bridge methods.
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        // First try is the method in the target class.
        Authenticate ann = AnnotationUtils.findAnnotation(specificMethod, Authenticate.class);
        if (ann != null) {
            return ann;
        }

        // Fallback is to look at the original method.
        if (specificMethod != method) {
            ann = AnnotationUtils.findAnnotation(method, Authenticate.class);
            if (ann != null) {
                return ann;
            }
        }

        return null;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public Pointcut getPointcut() {
        // Authenticate is only allowed on methods and does not support inheritance
        return new AnnotationMatchingPointcut(null, Authenticate.class, false);
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }
}
