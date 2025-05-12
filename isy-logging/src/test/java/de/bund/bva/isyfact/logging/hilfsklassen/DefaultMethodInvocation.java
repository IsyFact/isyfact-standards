package de.bund.bva.isyfact.logging.hilfsklassen;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Hilfsklasse zum Testen von AOP-MethodInvocations.
 * 
 */
public class DefaultMethodInvocation implements MethodInvocation {

    /** Die aufzurufende Methode. */
    private Method method;

    /**
     * Die Parameter der aufzurufenden Methode.
     */
    private Object[] arguments;

    /**
     * Das Zielobjekt, auf dem die Methode aufgerufen wird.
     */
    private Object target;

    /**
     * 
     * Konstruktor der Klasse. Initialisiert die übergebenen Parameter.
     * 
     * @param target
     *            Zielobjekt.
     * @param method
     *            aufzurufende Methode.
     * @param arguments
     *            zu verwendende Parameter.
     */
    public DefaultMethodInvocation(Object target, Method method, Object... arguments) {
        this.arguments = arguments;
        this.method = method;
        this.target = target;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.aopalliance.intercept.Invocation#getArguments()
     */
    public Object[] getArguments() {
        return arguments;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.aopalliance.intercept.Joinpoint#proceed()
     */
    public Object proceed() throws Throwable {
        return method.invoke(target, arguments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.aopalliance.intercept.Joinpoint#getThis()
     */
    public Object getThis() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.aopalliance.intercept.Joinpoint#getStaticPart()
     */
    public AccessibleObject getStaticPart() {
        // Nicht relevant
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.aopalliance.intercept.MethodInvocation#getMethod()
     */
    public Method getMethod() {
        return method;
    }

}
