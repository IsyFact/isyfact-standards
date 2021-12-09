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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Implementierung der {@link MethodMappingSource}, die per Reflection die passende Zielmethode ermittelt.
 *
 */
public class ReflectiveMethodMappingSource implements MethodMappingSource {

    /** Methoden-Cache. */
    private final Map<MethodHashKey, Method> methodCache = new ConcurrentHashMap<>();

    @Override
    public Method getTargetMethod(Method calledMethod, Class<?> targetClass) {
        MethodHashKey key = new MethodHashKey(calledMethod, targetClass);
        Method method = methodCache.get(key);
        if (method != null) {
            return method;
        }

        Map<Method, Method> matchesMap = new HashMap<>();
        for (Class<?> intf : targetClass.getInterfaces()) {
            for (Method possibleMatch : intf.getMethods()) {
                if (isMatch(calledMethod, possibleMatch)) {
                    matchesMap.put(AopUtils.getMostSpecificMethod(possibleMatch, targetClass), possibleMatch);
                }
            }
        }
        Collection<Method> matches = matchesMap.values();

        if (matchesMap.size() == 1) {
            Method targetMethod = matches.iterator().next();
            methodCache.put(key, targetMethod);
            return targetMethod;
        } else if (matchesMap.size() > 1) {
            throw new IllegalArgumentException("Mehr als eine mögliche Zielmethode für "
                + getMethodSignatureString(calledMethod) + " in "
                + Arrays.toString(targetClass.getInterfaces()) + ": " + matches);
        } else {
            throw new IllegalArgumentException("Keine Zielmethode für "
                + getMethodSignatureString(calledMethod) + " in "
                + Arrays.toString(targetClass.getInterfaces()));
        }
    }

    /**
     * Prüft, ob eine mögliche Zielmethode zur gerufenen Methode passt. Diese Implementierung prüft auf
     * Namensgleichheit und Anzahl der Parameter.
     *
     * @param calledMethod
     *            die gerufene Methode
     * @param possibleMatch
     *            eine mögliche Zielmethode
     * @return {@code true} bei Übereinstimmung
     */
    protected boolean isMatch(Method calledMethod, Method possibleMatch) {
        if (!calledMethod.getName().equals(possibleMatch.getName())) {
            return false;
        }

        int noOfParameters = calledMethod.getParameterTypes().length;
        for (Class<?> parType : calledMethod.getParameterTypes()) {
            if (skipParameter(parType)) {
                noOfParameters--;
            }
        }

        return noOfParameters == possibleMatch.getParameterTypes().length;
    }

    @Override
    public boolean skipParameter(Class<?> parameterType) {
        return AufrufKontextTo.class.isAssignableFrom(parameterType);
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

}
