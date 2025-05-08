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
 * Implementation of {@link MethodMappingSource}, which uses reflection to determine the appropriate target method.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ReflectiveMethodMappingSource implements MethodMappingSource {

    /**
     * Method Cache.
     */
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
     * Checks if a possible target method matches the called method. This implementation checks for
     * Name match and number of parameters.
     *
     * @param calledMethod  the called method
     * @param possibleMatch a possible target method
     * @return {@code true} on match
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
     * Returns the method signature as a string.
     *
     * @param method the method
     * @return the method signature as a string.
     */
    protected String getMethodSignatureString(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

}
