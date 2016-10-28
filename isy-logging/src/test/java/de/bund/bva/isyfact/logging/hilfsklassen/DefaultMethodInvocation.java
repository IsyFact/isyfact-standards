package de.bund.bva.isyfact.logging.hilfsklassen;

/*
 * #%L
 * isy-logging
 * %%
 * 
 * %%
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
 * #L%
 */

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
