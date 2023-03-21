package de.bund.bva.isyfact.serviceapi.core.httpinvoker;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;

import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

/**
 * Implements the {@link RemoteInvocationFactory} to cast the {@link MethodInvocation} to {@link RemoteInvocation} with
 * a {@link AufrufKontextTo} using {@link CreateAufrufKontextToStrategy}, if {@code null} is passed.
 * Can be used by the class {@link org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor}.
 */
public class AufrufKontextToRemoteInvocationFactory implements RemoteInvocationFactory {

    /**
     * The strategy of how to create the {@link AufrufKontextTo}.
     */
    private final CreateAufrufKontextToStrategy createAufrufKontextToStrategy;

    public AufrufKontextToRemoteInvocationFactory(CreateAufrufKontextToStrategy createAufrufKontextToStrategy) {
        this.createAufrufKontextToStrategy = createAufrufKontextToStrategy;
    }

    @Override
    public RemoteInvocation createRemoteInvocation(MethodInvocation invocation) {

        Method methode = invocation.getMethod();
        Optional<Class<?>> firstParameterOptional = Arrays.stream(methode.getParameterTypes()).findFirst();

        if (firstParameterOptional.isPresent()) {
            String classnameOfParameter = firstParameterOptional.get().getName();
            // Since the value should only be exchanged if null is passed as parameter, instanceof does not work
            String aufrufKontextToRegex = String.format(".+\\.%s$", AufrufKontextTo.class.getSimpleName());

            Object[] arguments = invocation.getArguments();

            // IFS documentation states that the AufrufKontextTo must ALWAYS be the first parameter for HTTPInvoker.
            if (classnameOfParameter.matches(aufrufKontextToRegex) && (arguments.length > 0 && arguments[0] == null)) {
                arguments[0] = createAufrufKontextToStrategy.create();
            }
        }

        return new RemoteInvocation(invocation);
    }
}
