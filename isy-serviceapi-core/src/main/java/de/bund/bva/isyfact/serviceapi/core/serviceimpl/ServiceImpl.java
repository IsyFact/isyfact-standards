package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.support.AopUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import ma.glasnost.orika.MapperFacade;



/**
 * Generische Implementierung des Kernteils einer Service- oder Native-GUI-Komponente. Die Implementierung
 * ruft für einen RemoteBean-Aufruf die korrespondierende Methode einer Anwendungskern-Komponente. Dabei
 * werden alle Methodenparameter sowie das Methodenergebnis mit Hilfe eines Bean Mappers konvertiert.
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class ServiceImpl implements MethodInterceptor, Validatable {

    /** MethodMappingSource. */
    private MethodMappingSource methodMappingSource;

    /** MapperFacade. */
    private MapperFacade mapper;

    public void setMethodMappingSource(MethodMappingSource methodMappingSource) {
        this.methodMappingSource = methodMappingSource;
    }

    public void setMapper(MapperFacade mapper) {
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
        return mapErgebnis(internesErgebnis, externeMethode);
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
    private Object[] mapParameter(Object[] externeParameter, Method externeMethode, Method interneMethode) {
        Class<?>[] externeParameterTypen = externeMethode.getParameterTypes();
        Type[] interneParameterTypen = interneMethode.getGenericParameterTypes();

        List<Object> interneParameter = new ArrayList<>(externeParameter.length);
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
    private Object mapErgebnis(Object internesErgebnis, Method externeMethode) {
        return MappingHelper.map(mapper, internesErgebnis, externeMethode.getGenericReturnType());
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
