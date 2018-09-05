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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.dozer.Mapper;
import org.springframework.aop.support.AopUtils;

/**
 * Generische Implementierung des Kernteils einer Service- oder Native-GUI-Komponente. Die Implementierung
 * ruft für einen RemoteBean-Aufruf die korrespondierende Methode einer Anwendungskern-Komponente. Dabei
 * werden alle Methodenparameter sowie das Methodenergebnis mithilfe von Dozer konvertiert.
 * 
 */
public class ServiceImpl implements MethodInterceptor, Validatable {

    /** Methoden-Mapping. */
    private MethodMappingSource methodMappingSource;

    public void setMethodMappingSource(MethodMappingSource methodMappingSource) {
        this.methodMappingSource = methodMappingSource;
    }

    /** Der Dozer Bean-Mapper. */
    private Mapper mapper;

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object target = invocation.getThis();
        Class<?> targetClass = AopUtils.getTargetClass(target);

        Method externeMethode = invocation.getMethod();
        Method interneMethode = methodMappingSource.getTargetMethod(externeMethode, targetClass);

        // Aufrufparameter abbilden
        Object[] interneParameter = mapParameter(invocation.getArguments(), externeMethode, interneMethode);

        // Implementierung aufrufen
        Object internesErgebnis;
        try {
            internesErgebnis = interneMethode.invoke(target, interneParameter);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }

        // Ergebnis abbilden
        Object externesErgebnis = mapErgebnis(internesErgebnis, externeMethode);

        return externesErgebnis;
    }

    /**
     * Mappt die Parameter der externen Methode in die Zieltypen der Parameter der internen Methode.
     * 
     * @param externeParameter
     *            die externen Parameter
     * @param externeMethode
     *            die externe Methode, zu der die Parameter gehören
     * @param interneMethode
     *            die entsprechende interne Methode, zu dessen Parametertypen die externen Parameter gemappt
     *            werden sollen.
     * @return die gemappten externen Parameter auf die Parametertypen der internen Methode.
     */
    Object[] mapParameter(Object[] externeParameter, Method externeMethode, Method interneMethode) {
        Class<?>[] externeParameterTypen = externeMethode.getParameterTypes();
        Type[] interneParameterTypen = interneMethode.getGenericParameterTypes();

        List<Object> interneParameter = new ArrayList<Object>(externeParameter.length);
        int j = 0;
        for (int i = 0; i < externeParameter.length; i++) {
            if (!methodMappingSource.skipParameter(externeParameterTypen[i])) {
                Object internerParameter =
                    MappingHelper.map(mapper, externeParameter[i], interneParameterTypen[j]);
                interneParameter.add(internerParameter);
                j++;
            }
        }
        return interneParameter.toArray();
    }

    /**
     * Mappt das Ergebnis der internen Methode auf den Rückgabetyp der externen Methode.
     * 
     * @param internesErgebnis
     *            das Ergebnis der internen Methode
     * @param externeMethode
     *            die externe Methode
     * @return der Ergebniswert für die externe Methode
     */
    Object mapErgebnis(Object internesErgebnis, Method externeMethode) {
        Object externesErgebnis =
            MappingHelper.map(mapper, internesErgebnis, externeMethode.getGenericReturnType());
        return externesErgebnis;
    }

    /**
     * {@inheritDoc}
     */
    public void validateConfiguration(Class<?> remoteBeanInterface, Object target) {
        Class<?> targetClass = AopUtils.getTargetClass(target);

        for (Method method : remoteBeanInterface.getMethods()) {
            methodMappingSource.getTargetMethod(method, targetClass);
        }
    }

}
