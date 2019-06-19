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
 * 
 */
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
