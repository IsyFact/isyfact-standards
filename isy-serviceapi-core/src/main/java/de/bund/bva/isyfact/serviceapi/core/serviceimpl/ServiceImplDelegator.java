package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

/**
 * Delegiert an eine konkrete Service-Implementierung einer Service- oder Native-GUI-Komponente. Diese Klasse
 * wird genutzt, um in einer teilweise generischen Service-Komponente einen konkret implementierten Kernteil
 * zu nutzen.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ServiceImplDelegator implements MethodInterceptor, Validatable {

    /** Methoden-Cache. */
    private final Map<MethodHashKey, Method> methodCache = new ConcurrentHashMap<MethodHashKey, Method>();

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object target = invocation.getThis();
        Class<?> targetClass = AopUtils.getTargetClass(target);

        Method externeMethode = invocation.getMethod();
        Method interneMethode = getServiceImplMethod(externeMethode, targetClass);

        // Implementierung aufrufen
        try {
            Object ergebnis = interneMethode.invoke(target, invocation.getArguments());
            return ergebnis;
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Ermittelt die aufzurufende Zielmethode der konkreten Serviceimplementierung. Deren Signatur muss mit
     * der RemoteBean-Methode übereinstimmen, d.h. Name, Parametertypen und Rückgabetyp müssen gleich sein.
     * 
     * @param remoteBeanMethod
     *            die Service-Methode der RemoteBean-Schnittstelle
     * @param targetClass
     *            die Zielklasse des Service-Implementierung, die die Zielmethode implementiert
     * @return die Methode der target-Bean, die für die Serviceoperation aufzurufen ist
     */
    Method getServiceImplMethod(Method remoteBeanMethod, Class<?> targetClass) {
        MethodHashKey key = new MethodHashKey(remoteBeanMethod, targetClass);
        Method method = methodCache.get(key);
        if (method != null) {
            return method;
        }

        for (Class<?> intf : targetClass.getInterfaces()) {
            for (Method possibleMatch : intf.getMethods()) {
                if (remoteBeanMethod.getName().equals(possibleMatch.getName())
                    && Arrays.equals(remoteBeanMethod.getGenericParameterTypes(),
                        possibleMatch.getGenericParameterTypes())
                    && remoteBeanMethod.getGenericReturnType().equals(possibleMatch.getGenericReturnType())) {
                    return possibleMatch;
                }
            }
        }

        throw new IllegalArgumentException("Keine Zielmethode für "
            + getMethodSignatureString(remoteBeanMethod) + " in " + targetClass.getName());
    }

    /**
     * Liefert die Methodensignatur als String.
     * 
     * @param method
     *            die Methode
     * @return die Methodensignatur als String.
     */
    protected String getMethodSignatureString(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    /**
     * {@inheritDoc}
     */
    public void validateConfiguration(Class<?> remoteBeanInterface, Object target) {
        Class<?> targetClass = AopUtils.getTargetClass(target);

        for (Method method : remoteBeanInterface.getMethods()) {
            getServiceImplMethod(method, targetClass);
        }
    }

}
