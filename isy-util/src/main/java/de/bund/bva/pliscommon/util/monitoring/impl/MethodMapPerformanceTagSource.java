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
package de.bund.bva.pliscommon.util.monitoring.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.PatternMatchUtils;

import de.bund.bva.pliscommon.util.monitoring.PerformanceTagSource;

/**
 * In der Regel wird das Performance-Monitoring per AOP konfiguriert. Dabei muss abhängig von dem gerade
 * gemessenen Programm-Code ein Performance-Tag vergeben werden. Diese Bean liefert einen Tag-Namen abhängig
 * von der aktuell ausgeführten Methode, analog zu
 * org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource.
 * 
 * 
 */
public class MethodMapPerformanceTagSource implements PerformanceTagSource, InitializingBean {
    /** Hält eine Map der Methoden. */
    private Map<String, String> methodMap;

    /** Hält eine Map der Methodentypen. */
    private Map<Method, String> methodTypeMap = new HashMap<Method, String>();

    /** Der ClassLoader. */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /** Der logger. */
    private static final Logger LOG = Logger.getLogger(MethodMapPerformanceTagSource.class);

    /**
     * {@inheritDoc}
     */
    public String getTag(Method method, Class<? extends Object> targetClass) {
        return methodTypeMap.get(method);
    }

    /**
     * Initialisiert die Map welche zu jeder Methode den Performance-Tag liefert.
     * @see org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource#initMethodMap(Map)
     */
    @SuppressWarnings("unchecked")
    private void initMethodMap() {
        for (Map.Entry<String, String> entry : methodMap.entrySet()) {
            int lastDotIndex = entry.getKey().lastIndexOf(".");
            if (lastDotIndex == -1) {
                throw new IllegalArgumentException("'" + entry.getKey()
                    + "' is not a valid method name: format is FQN.methodName");
            }
            String className = entry.getKey().substring(0, lastDotIndex);
            String methodName = entry.getKey().substring(lastDotIndex + 1);
            Class<? extends Object> clazz = ClassUtils.resolveClassName(className, this.beanClassLoader);
            addMethod(clazz, methodName, entry.getValue());
        }
    }

    /**
     * @see org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource
     *      #addTransactionalMethod(Class, String,
     *      org.springframework.transaction.interceptor.TransactionAttribute)
     * @param clazz
     *            target interface or Class<? extends Object>
     * @param mappedName
     *            mapped method name
     * @param tag
     *            attribute associated with the method
     */
    private void addMethod(Class<? extends Object> clazz, String mappedName, String tag) {
        String name = clazz.getName() + '.' + mappedName;

        Method[] methods = clazz.getDeclaredMethods();
        List<Method> matchingMethods = new ArrayList<Method>();
        for (int i = 0; i < methods.length; i++) {
            if (isMatch(methods[i].getName(), mappedName)) {
                matchingMethods.add(methods[i]);
            }
        }
        if (matchingMethods.isEmpty()) {
            throw new IllegalArgumentException("Couldn't find method '" + mappedName
                + "' on Class<? extends Object> [" + clazz.getName() + "]");
        }

        // register all matching methods
        for (Iterator<Method> it = matchingMethods.iterator(); it.hasNext();) {
            Method method = it.next();
            String regMethodName = this.methodTypeMap.get(method);
            if (regMethodName == null
                || (!regMethodName.equals(name) && regMethodName.length() <= name.length())) {
                // No already registered method name, or more specific
                // method name specification now -> (re-)register method.
                if (LOG.isDebugEnabled() && regMethodName != null) {
                    LOG.debug("Replacing attribute for transactional method [" + method + "]: current name '"
                        + name + "' is more specific than '" + regMethodName + "'");
                }
                this.methodTypeMap.put(method, name);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Adding transactional method [" + method + "] with tag [" + tag + "]");
                }
                this.methodTypeMap.put(method, tag);
            } else {
                if (LOG.isDebugEnabled() && regMethodName != null) {
                    LOG.debug("Keeping attribute for transactional method [" + method + "]: current name '"
                        + name + "' is not more specific than '" + regMethodName + "'");
                }
            }
        }
    }

    /**
     * @param methodName
     *            the method name of the Class
     * @param mappedName
     *            the name in the descriptor
     * @return if the names match
     * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
     */
    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        initMethodMap();
    }

    /**
     * Setzt die name/attribut map, mit "FQCN.method" method names (z.B.
     * "com.mycompany.mycode.MyClass.myMethod") und Tag-Namen.
     * Siehe auch org.springframework.transaction.interceptor.MethodMapTransactionAttributeSource#setMethodMap(Map)
     * @param methodMap
     *            Die Map für die Methoden
     */
    public void setMethodMap(Map<String, String> methodMap) {
        this.methodMap = methodMap;
    }

}
