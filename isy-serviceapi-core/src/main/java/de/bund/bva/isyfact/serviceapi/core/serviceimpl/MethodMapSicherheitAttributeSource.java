package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PatternMatchUtils;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.sicherheit.annotation.SicherheitAttributeSource;
import de.bund.bva.isyfact.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;

/**
 * Stellt die benötigten Rechte pro Methode in einer Map bereit.
 *
 * <p>
 * Diese Klasse ist als Kopie der Spring MethodMapTransactionAttributeSource entstanden.
 * </p>
 * @deprecated This module is deprecated and will be removed in a future release.
 * It is recommended to use <a href="https://isyfact.github.io/isyfact-standards-doku/latest/isy-service-rest/konzept/master.html">REST according to IsyFacts REST Concept</a> instead.
 */
@Deprecated
public class MethodMapSicherheitAttributeSource implements SicherheitAttributeSource, BeanClassLoaderAware,
    InitializingBean {

    /** Isy-Logger. */
    private static final IsyLogger LOG = IsyLoggerFactory
        .getLogger(MethodMapSicherheitAttributeSource.class);

    /** Map from method name to attribute value. */
    private Map<String, String[]> methodMap;

    /** Class loader. */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /** Map from Method to benötigte Rechte. */
    private final Map<Method, String[]> sicherheitAttributeMap = new HashMap<>();

    /** Map from Method to name pattern used for registration. */
    private final Map<Method, String> methodNameMap = new HashMap<>();

    /**
     * Set a name/attribute map, consisting of "FQCN.method" method names (e.g.
     * "com.mycompany.mycode.MyClass.myMethod") and String[] instances.
     * <p>
     * Intended for configuration via setter injection, typically within a Spring bean factory. Relies on
     * {@link #afterPropertiesSet()} being called afterwards.
     * @param methodMap
     *            said {@link Map} from method name to attribute value
     */
    public void setMethodMap(Map<String, String[]> methodMap) {
        this.methodMap = methodMap;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * Eagerly initializes the specified {@link #setMethodMap(java.util.Map) "methodMap"}, if any.
     * @see #initMethodMap(java.util.Map)
     */
    @Override
    public void afterPropertiesSet() {
        initMethodMap(this.methodMap);
    }

    /**
     * Initialize the specified {@link #setMethodMap(java.util.Map) "methodMap"}, if any.
     * @param methodMap
     *            Map from method names to <code>String[]</code> instances
     * @see #setMethodMap
     */
    protected void initMethodMap(Map<String, String[]> methodMap) {
        if (methodMap != null) {
            for (Map.Entry<String, String[]> entry : methodMap.entrySet()) {
                addGesichertMethod(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Add an attribute for a "gesichert" method.
     * <p>
     * Method names can end or start with "*" for matching multiple methods.
     * @param name
     *            class and method name, separated by a dot
     * @param attr
     *            attribute associated with the method
     * @throws IllegalArgumentException
     *             in case of an invalid name
     */
    public void addGesichertMethod(String name, String[] attr) {
        Assert.notNull(name, "Name must not be null");
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("'" + name
                + "' is not a valid method name: format is FQN.methodName");
        }
        String className = name.substring(0, lastDotIndex);
        String methodName = name.substring(lastDotIndex + 1);
        Class clazz = ClassUtils.resolveClassName(className, this.beanClassLoader);
        addGesichertMethod(clazz, methodName, attr);
    }

    /**
     * Add an attribute for a "gesichert" method. Method names can end or start with "*" for matching multiple
     * methods.
     * @param clazz
     *            target interface or class
     * @param mappedName
     *            mapped method name
     * @param attr
     *            attribute associated with the method
     */
    public void addGesichertMethod(Class<?> clazz, String mappedName, String[] attr) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(mappedName, "Mapped name must not be null");
        String name = clazz.getName() + '.' + mappedName;

        Method[] methods = clazz.getDeclaredMethods();
        List<Method> matchingMethods = new ArrayList<>();
        for (Method method : methods) {
            if (isMatch(method.getName(), mappedName)) {
                matchingMethods.add(method);
            }
        }
        if (matchingMethods.isEmpty()) {
            throw new IllegalArgumentException("Couldn't find method '" + mappedName + "' on class ["
                + clazz.getName() + "]");
        }

        // register all matching methods
        for (Method method : matchingMethods) {
            String regMethodName = this.methodNameMap.get(method);
            if (regMethodName == null
                || (!regMethodName.equals(name) && regMethodName.length() <= name.length())) {
                // No already registered method name, or more specific
                // method name specification now -> (re-)register method.
                if (regMethodName != null) {
                    LOG
                        .debug(
                            "Replacing attribute for gesichert method [{}]: current name '{}' is more specific than '{}'",
                            method, name, regMethodName);
                }
                this.methodNameMap.put(method, name);
                addGesichertMethod(method, attr);
            } else {
                LOG
                    .debug(
                        "Keeping attribute for gesichert method [{}]: current name '{}' is not more specific than '{}'",
                        method, name, regMethodName);
            }
        }
    }

    /**
     * Add an attribute for a "gesichert" method.
     * @param method
     *            the method
     * @param attr
     *            attribute associated with the method
     */
    public void addGesichertMethod(Method method, String[] attr) {
        Assert.notNull(method, "Method must not be null");
        Assert.notNull(attr, "rechte must not be null");
        LOG.debug("Adding gesichert method [{}] with attribute [{}]", method, attr);
        this.sicherheitAttributeMap.put(method, attr);
    }

    /**
     * Return if the given method name matches the mapped name.
     * <p>
     * The default implementation checks for "xxx*", "*xxx" and "*xxx*" matches, as well as direct equality.
     * @param methodName
     *            the method name of the class
     * @param mappedName
     *            the name in the descriptor
     * @return if the names match
     * @see org.springframework.util.PatternMatchUtils#simpleMatch(String, String)
     */
    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getBenoetigeRechte(Method method, Class<?> targetClass) {
        String[] benoetigteRechte = this.sicherheitAttributeMap.get(method);
        if (benoetigteRechte == null || benoetigteRechte.length == 0) {
            throw new FehlerhafteServiceKonfigurationRuntimeException();
        }
        return benoetigteRechte;
    }

}
